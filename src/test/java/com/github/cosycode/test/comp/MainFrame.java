package com.github.cosycode.test.comp;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

/**
 * <b>Description : </b> 主窗口程序
 *
 * @author CPF
 * Date: 2020/8/11 11:29
 */
@Slf4j
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new MainFrame();
            } catch (Exception e) {
                log.error("main error", e);
            }
        });
    }

    /**
     * Create the frame.
     */
    public MainFrame() {
        setTitle("EndPoint IO Transfer");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(400, 100, 800, 400);
        // 添加面板
        JPanel contentPane = new TableTestPanel();
        contentPane.setPreferredSize(new Dimension(700, 600));
        contentPane.setVisible(true);
        setContentPane(contentPane);
        pack();
    }


}
