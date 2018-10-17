package com.starwin.app;

import com.starwin.app.plugin.ProcessImageColor;
import com.starwin.app.utils.SelectPathHelper;
import com.starwin.app.window.AddColorWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainContentPanel extends JPanel {

    private JFrame jFrame;

    private List<String> selectedPath;

    private List<File> selectedFiles;

    private JLabel selectLabel;

    private JScrollPane scrollPane;

    private JLabel logLabel;

    private JScrollPane logLabelScrollPanel;

    private Thread mWatchThread;

    public MainContentPanel(JFrame frame) {
        this.jFrame = frame;
        selectedPath = new ArrayList<>();
        selectedFiles = new ArrayList<>();
        init();
    }

    private void init() {
        addMenu();
        addContent();
    }

    private void addMenu() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu menu = new JMenu("�ļ�");
        JMenu menu3 = new JMenu("����");
        jMenuBar.add(menu);
        jMenuBar.add(menu3);

        JMenuItem item = new JMenuItem("ѡ���ļ���·��");
        item.addActionListener(e -> {
            SelectPathHelper helper = new SelectPathHelper(selectedPath, selectedFiles);
            helper.show(jFrame, ((App) jFrame).getAppExecutors().networkIO());
            StringBuilder labelContent = new StringBuilder();
            for (String path : selectedPath) {
                labelContent.append(path);
                labelContent.append("<br>");
            }
            selectLabel.setText(String.format("<html><body>·����<br>%s</body></html>", labelContent));
            revalidate();
        });

        JMenuItem item2 = new JMenuItem("ɫֵ�滻");
        item2.addActionListener(e -> {

            AddColorWindow colorWindow = new AddColorWindow();
            colorWindow.setSize(800, 600);
            colorWindow.setLocationRelativeTo(jFrame);
            colorWindow.setVisible(true);
//            // ��ʾ��ɫѡȡ���Ի���, ����ѡȡ����ɫ���߳̽�������, ֱ���Ի��򱻹رգ�
//            Color color = JColorChooser.showDialog(jFrame, "ѡȡ��ɫ", null);
//            // ����û�ȡ����رմ���, �򷵻ص� color Ϊ null
//            if (color == null) {
//                return;
//            }
//            // ��ȡ��ɫ�� ARGB ��������ֵ
//            int alpha = color.getAlpha();
//            int red = color.getRed();
//            int green = color.getGreen();
//            int blue = color.getBlue();
//
//            System.out.println("A=" + String.format("%02x", alpha) + ", " +
//                    String.format("#%02x%02x%02x", red, green, blue));
            colorWindow.addWindowListener(new WindowAdapter() {
                boolean closeByUser = false;

                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    // ���X���������Closed
                    closeByUser = true;
                    e.getWindow().dispose();
                }

                @Override
                public void windowClosed(WindowEvent e) {
                    // ���ڵ���dispose������closingֱ����closed
                    if (closeByUser) {
                        return;
                    }
                    String offset = colorWindow.getColorOffsetText();
                    String colorArea = colorWindow.getColorAreaText();
                    String[] colorList = colorArea.split("\n");
                    initLog();
                    ((App) jFrame).getAppExecutors().networkIO().execute(() -> {
                        for (String path : selectedPath) {
                            List<String> command = new ArrayList<>();
                            command.add(path);
                            command.add("-offset");
                            command.add(offset);
                            for (int i = 0; i < colorList.length; i++) {
                                command.addAll(Arrays.asList(colorList[i].split(" ")));
                            }
                            ProcessImageColor.main(command.toArray(new String[]{}));
                        }
                    });
                }
            });

        });
        menu.add(item);
        menu.addSeparator();
        menu.add(item2);

        item = new JMenuItem("ͨ��ip��������");
        item.addActionListener((e) -> {
        });
        menu3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showDialog();
            }

            void showDialog() {
                // ����һ��ģ̬�Ի���
                final JDialog dialog = new JDialog(jFrame, "��װ�����", true);
                // ���öԻ���Ŀ��
                dialog.setSize(300, 150);
                // ���öԻ����С���ɸı�
                dialog.setResizable(false);
                // ���öԻ��������ʾ��λ��
                dialog.setLocationRelativeTo(jFrame);

                // ����һ����ǩ��ʾ��Ϣ����
                JLabel messageLabel = new JLabel("<html><body>1.�İ���<br>2.�����汾��<br>3.����firebase�ļ�<br>4.��logo��UI</body></html>");


                // �����Ի�����������, ������ڿ��Ը����Լ�����Ҫ����κ�������������ǲ���
                JPanel panel = new JPanel();

                // �����������
                panel.add(messageLabel);

                // ���öԻ�����������
                dialog.setContentPane(panel);
                // ��ʾ�Ի���
                dialog.setVisible(true);
            }
        });
        jFrame.setJMenuBar(jMenuBar);
    }

    private void addContent() {
        selectLabel = new JLabel();
        scrollPane = new JScrollPane(selectLabel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(30);
        this.add(scrollPane);

        logLabel = new JLabel();
        logLabelScrollPanel = new JScrollPane(logLabel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        logLabelScrollPanel.getVerticalScrollBar().setUnitIncrement(30);
        this.add(logLabelScrollPanel);
    }

    private void initLog() {
        if (mWatchThread != null) {
            return;
        }
        mWatchThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                SwingUtilities.invokeLater(() -> logLabel.setText(String.format("<html><body>%s</body></html>", ((App) jFrame).getOutputStream().toString().replace("\n", "<br>"))));
            }
//            try {
//                Path logPath = Paths.get(new File("").getAbsolutePath());
//                WatchService watcher = logPath.getFileSystem().newWatchService();
//                logPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
//                        StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
//                while (!Thread.currentThread().isInterrupted()) {
//                    WatchKey watchKey = watcher.take();
//                    List<WatchEvent<?>> events = watchKey.pollEvents();
//                    //File logFile = new File(Main.LOG_FILE_PATH);
//                    for (WatchEvent event : events) {
//                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
//                            System.out.println("Created: " + event.context().toString());
//                        }
//                        if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
//                            System.out.println("Delete: " + event.context().toString());
//                        }
//                        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
//                            System.out.println("Modify: " + event.context().toString());
//                        }
//                    }
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        });
        mWatchThread.start();
    }

    public void release() {
        if (mWatchThread != null) {
            mWatchThread.interrupt();
            mWatchThread = null;
        }
    }

    @Override
    public void doLayout() {
        scrollPane.setPreferredSize(new Dimension(getWidth() - 100, 150));
        logLabelScrollPanel.setPreferredSize(new Dimension(getWidth() - 100, getHeight() - 150 - 50));
        super.doLayout();
    }
}
