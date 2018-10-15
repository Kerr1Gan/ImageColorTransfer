package com.starwin.app.window;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class AddColorWindow extends JFrame {
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JPanel contentPanel;
    private JLabel empty;
    private JPanel colorOffsetPanel;
    private JTextField colorText;

    public AddColorWindow() {
        init();
    }

    private void init() {
        setTitle("设置需要替换的颜色");
        empty = new JLabel();
        JLabel label = new JLabel("例如:{原始颜色RGB值} {替换后颜色RGB值} -> 255 0 0 0 255 0。换行支持多个颜色替换", JLabel.CENTER);
        contentPanel = new JPanel();
        // 创建一个 5 行 10 列的文本区域
        textArea = new JTextArea();
        // 设置自动换行
        textArea.setLineWrap(true);

        JButton button = new JButton("确定");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddColorWindow.this.dispose();
            }
        });


        // 创建凸起边框
        Border border = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        textArea.setBorder(border);
        scrollPane = new JScrollPane(textArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        contentPanel.add(empty);
        contentPanel.add(label);
        contentPanel.add(scrollPane);


        // add text field
        colorText = new JTextField(8);
        JLabel text = new JLabel("颜色偏差值(可选)：");
        colorOffsetPanel = new JPanel();
        colorOffsetPanel.add(text);
        colorOffsetPanel.add(colorText);
        contentPanel.add(colorOffsetPanel);

        // add positive button
        contentPanel.add(button);
        this.setContentPane(contentPanel);
    }

    @Override
    public void doLayout() {
        empty.setPreferredSize(new Dimension(getWidth(), 10));
        //label.setPreferredSize(new Dimension(getWidth(), label.getPreferredSize().height));
        contentPanel.setSize(getWidth(), getHeight());
        scrollPane.setPreferredSize(new Dimension(getWidth() - 100, getHeight() - 200));
        colorOffsetPanel.setPreferredSize(new Dimension(getWidth(), colorOffsetPanel.getPreferredSize().height));
        super.doLayout();
    }

    public String getColorOffsetText() {
        return colorText.getText();
    }

    public String getColorAreaText() {
        return textArea.getText();
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
    }
}
