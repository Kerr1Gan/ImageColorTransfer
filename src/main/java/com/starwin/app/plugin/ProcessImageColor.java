package com.starwin.app.plugin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcessImageColor {

    private static final String TAG_OFFSET = "-offset";

    public static void main(String[] args) {

//        args = new String[]{".\\image", "142", "141", "152", "255", "0", "0", "87", "149", "234", "0", "255", "0"};
        if (args == null || args.length <= 0) {
            return;
        }

        String workPath = args[0];
        List<int[]> colorReplace = new ArrayList<>();
        int colorOffset = 0;
        for (int i = 1; i < args.length; ) {
            if (args[i].equals(TAG_OFFSET)) {
                try {
                    if (args[i + 1].length() == 0) {
                        i += 2;
                        continue;
                    }
                    colorOffset = Integer.parseInt(args[i + 1]);
                    i += 2;
                    continue;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                int[] array = new int[3];
                array[0] = Integer.parseInt(args[i]);
                array[1] = Integer.parseInt(args[i + 1]);
                array[2] = Integer.parseInt(args[i + 2]);
                colorReplace.add(array);
                i += 3;
            } catch (Exception e) {
                e.printStackTrace();
                i++;
            }
        }

        /**
         * 要处理的图片目录
         */
        File dir = new File(workPath);
        /**
         * 列出目录中的图片，得到数组
         */
        File[] files = dir.listFiles();
        if (!dir.isDirectory()) {
            files = new File[]{dir};
        }
        /**
         * 遍历数组
         */
        if (files == null) {
            return;
        }
        for (int x = 0; x < files.length; x++) {
            if (files[x].isDirectory()) {
                continue;
            }
            /**
             * 定义一个RGB的数组，因为图片的RGB模式是由三个 0-255来表示的 比如白色就是(255,255,255)
             */
            int[] rgb = new int[3];
            /**
             * 用来处理图片的缓冲流
             */
            BufferedImage bi = null;
            try {
                /**
                 * 用ImageIO将图片读入到缓冲中
                 */
                bi = ImageIO.read(files[x]);
                if (bi == null) {
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            /**
             * 得到图片的长宽
             */
            int width = bi.getWidth();
            int height = bi.getHeight();
            int minx = bi.getMinX();
            int miny = bi.getMinY();
            BufferedImage temp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            System.out.println("processing:" + files[x].getName());
            /**
             * 这里是遍历图片的像素，因为要处理图片的背色，所以要把指定像素上的颜色换成目标颜色
             * 这里 是一个二层循环，遍历长和宽上的每个像素
             */
            boolean hasChanged = false;
            for (int i = minx; i < width; i++) {
                for (int j = miny; j < height; j++) {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    /**
                     * 得到指定像素（i,j)上的RGB值，
                     */
                    int pixel = bi.getRGB(i, j);
                    /**
                     * 分别进行位操作得到 r g b上的值
                     */
                    rgb[0] = (pixel & 0xff0000) >> 16;
                    rgb[1] = (pixel & 0xff00) >> 8;
                    rgb[2] = (pixel & 0xff);
                    temp.setRGB(i, j, pixel);
                    pixel = bi.getRGB(i, j);
                    int alpha = (pixel & 0xff000000) >> 24;
                    for (int k = 0; k < colorReplace.size(); k += 2) {
                        int[] srcArray = colorReplace.get(k);
                        int[] destArray = colorReplace.get(k + 1);
                        if (containsInOffset(srcArray[0], rgb[0], colorOffset) && containsInOffset(srcArray[1], rgb[1], colorOffset) && containsInOffset(srcArray[2], rgb[2], colorOffset)) {
                            /**
                             * 这里是判断通过，则把该像素换成白色
                             */
                            pixel = (alpha << 24)/*alpha值*/ | (destArray[0] << 16) | (destArray[1] << 8) | (destArray[2]);
                            temp.setRGB(i, j, pixel);
                            hasChanged = true;
                        }
                    }
                }
            }
            System.out.println("processed:" + files[x].getName() + " " + (hasChanged ? "changed" : ""));
            System.out.println();
            /**
             * 将缓冲对象保存到新文件中
             */
            try {
                String name = files[x].getName().substring(0, files[x].getName().indexOf("."));
                ImageIO.write(temp, "PNG", new File(workPath, name + ".png"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        System.out.println("task end");
    }

    private static boolean containsInOffset(int srcColor, int destColor, int offset) {
        return Math.abs(destColor - srcColor) <= offset;
    }
}
