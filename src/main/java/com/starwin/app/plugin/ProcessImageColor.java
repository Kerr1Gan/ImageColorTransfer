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
         * Ҫ�����ͼƬĿ¼
         */
        File dir = new File(workPath);
        /**
         * �г�Ŀ¼�е�ͼƬ���õ�����
         */
        File[] files = dir.listFiles();
        if (!dir.isDirectory()) {
            files = new File[]{dir};
        }
        /**
         * ��������
         */
        if (files == null) {
            return;
        }
        for (int x = 0; x < files.length; x++) {
            if (files[x].isDirectory()) {
                continue;
            }
            /**
             * ����һ��RGB�����飬��ΪͼƬ��RGBģʽ�������� 0-255����ʾ�� �����ɫ����(255,255,255)
             */
            int[] rgb = new int[3];
            /**
             * ��������ͼƬ�Ļ�����
             */
            BufferedImage bi = null;
            try {
                /**
                 * ��ImageIO��ͼƬ���뵽������
                 */
                bi = ImageIO.read(files[x]);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            /**
             * �õ�ͼƬ�ĳ���
             */
            int width = bi.getWidth();
            int height = bi.getHeight();
            int minx = bi.getMinX();
            int miny = bi.getMinY();
            BufferedImage temp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            System.out.println("processing:" + files[x].getName());
            /**
             * �����Ǳ���ͼƬ�����أ���ΪҪ����ͼƬ�ı�ɫ������Ҫ��ָ�������ϵ���ɫ����Ŀ����ɫ
             * ���� ��һ������ѭ�����������Ϳ��ϵ�ÿ������
             */
            boolean hasChanged = false;
            for (int i = minx; i < width; i++) {
                for (int j = miny; j < height; j++) {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    /**
                     * �õ�ָ�����أ�i,j)�ϵ�RGBֵ��
                     */
                    int pixel = bi.getRGB(i, j);
                    /**
                     * �ֱ����λ�����õ� r g b�ϵ�ֵ
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
                             * �������ж�ͨ������Ѹ����ػ��ɰ�ɫ
                             */
                            pixel = (alpha << 24)/*alphaֵ*/ | (destArray[0] << 16) | (destArray[1] << 8) | (destArray[2]);
                            temp.setRGB(i, j, pixel);
                            hasChanged = true;
                        }
                    }
                }
            }
            System.out.println("processed:" + files[x].getName() + " " + (hasChanged ? "changed" : ""));
            System.out.println();
            /**
             * ��������󱣴浽���ļ���
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
