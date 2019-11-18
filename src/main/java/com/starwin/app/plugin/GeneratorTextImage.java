package com.starwin.app.plugin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class GeneratorTextImage {

    public static void main(String[] args) {
        try {
            System.out.println("text " + args[0] + " outputPath " + args[1]);
            String text = args[0];
            String outputPath = args[1];
            BufferedImage temp = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = (Graphics2D) temp.getGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, 50, 50);
            g2d.setColor(Color.BLACK);
            g2d.drawString(text, 0, 20);
            ImageIO.write(temp, "png", new File(outputPath));
            g2d.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
