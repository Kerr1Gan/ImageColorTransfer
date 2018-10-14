package com.starwin.app;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Main extends JFrame {

    public static final String LOG_FILE_PATH = "system_out.txt";
    public static ByteArrayOutputStream sOutputStream;

    public static void main(String[] args) {
        sOutputStream = new ByteArrayOutputStream();
        PrintStream cacheStream = new PrintStream(sOutputStream);
        System.setOut(cacheStream);//不打印到控制台

        Main frame = new Main();
        frame.setSize(1040, 800);
        frame.setLocationRelativeTo(null);
        frame.setTitle("马甲包工具");
        frame.setLookAndFeel();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                ((MainContentPanel) frame.getContentPane()).release();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                ((MainContentPanel) frame.getContentPane()).release();
            }
        });
        frame.setContentPane(new MainContentPanel(frame));
        frame.setVisible(true);
    }

    private void setLookAndFeel() {
        String lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lookAndFeelClassName);
        } catch (Exception e) {
        }
    }
}
