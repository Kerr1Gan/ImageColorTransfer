package com.starwin.app;


import com.starwin.app.executor.*;
import com.starwin.app.modules.AppModule;
import com.starwin.app.modules.DaggerAppComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

@Singleton
public class App extends JFrame {

    public static final String LOG_FILE_PATH = "system_out.txt";
    private ByteArrayOutputStream outputStream;

    @Inject
    AppExecutors appExecutors;

    @Inject
    public App(String[] args) {
        System.out.println("App init");
        DaggerAppComponent.builder().appModule(new AppModule(null)).build().inject(this);
    }

    public void run() {
        outputStream = new ByteArrayOutputStream();
        PrintStream cacheStream = new PrintStream(outputStream);
        System.setOut(cacheStream);
        this.setSize(1040, 800);
        this.setLocationRelativeTo(null);
        this.setTitle("马甲包工具");
        this.setLookAndFeel();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                ((MainContentPanel) App.this.getContentPane()).release();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                ((MainContentPanel) App.this.getContentPane()).release();
            }
        });
        this.setContentPane(new MainContentPanel(this));
        this.setVisible(true);
    }

    private void setLookAndFeel() {
        String lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lookAndFeelClassName);
        } catch (Exception e) {
        }
    }

    public AppExecutors getAppExecutors() {
        return this.appExecutors;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}
