package swing.qr.kiarelemb.test;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.basic.QRMenuItem;
import swing.qr.kiarelemb.basic.QRTextPane;
import swing.qr.kiarelemb.combination.QRMenuButton;
import swing.qr.kiarelemb.window.basic.QRFrame;

import javax.swing.*;
import java.awt.*;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-04 17:41
 **/
public class MenuTest extends QRFrame {

    public MenuTest(String title) {
        super(title);
        //设置窗体标题居中
        setTitlePlace(SwingConstants.CENTER);
        //设置单击关闭按钮后窗体淡化退出并结束程序
        setCloseButtonSystemExit();
        // 设置主面板的布局
        this.mainPanel.setLayout(new BorderLayout());

//        titleMenuPanel.setAutoExpend(true);

        QRButton fileMenu = titleMenuPanel.add("File");
        fileMenu.add(new QRMenuItem("New File", "ctrl N"));
        fileMenu.add(new QRMenuItem("Open File", "ctrl O"));
        addSeparator(fileMenu);
        fileMenu.add(new QRMenuItem("Save", "ctrl S"));
        fileMenu.add(new QRMenuItem("Save As"));
        addSeparator(fileMenu);
        fileMenu.add(new QRMenuItem("Close"));

        QRButton editMenu = titleMenuPanel.add("Edit");
        editMenu.add(new QRMenuItem("Undo", "ctrl Z"));
        editMenu.add(new QRMenuItem("Redo", "ctrl Y"));
        addSeparator(editMenu);
        editMenu.add(new QRMenuItem("Cut", "ctrl X"));
        editMenu.add(new QRMenuItem("Copy", "ctrl C"));
        editMenu.add(new QRMenuItem("Paste", "ctrl V"));

        QRButton viewMenu = titleMenuPanel.add("View");
        viewMenu.add(new QRMenuItem("Zoom In"));
        viewMenu.add(new QRMenuItem("Zoom Out"));
        viewMenu.add(new QRMenuItem("Reset Zoom"));

        this.mainPanel.add(new QRTextPane().addScrollPane());
//        QRComponentUtils.componentLoopToSetOpaque(this.mainPanel, true);
    }

    private void addSeparator(QRButton button) {
        if (button instanceof QRMenuButton menuButton) {
            menuButton.addSeparator();
        }
    }

    public static void main(String[] args) {
        QRSwing.start("res/settings.properties", "res/window.properties");
        // 设置菜单置于窗体标题栏
        QRSwing.setWindowTitleMenu(true);
        // 取消窗体圆角
        QRSwing.setWindowRound(false);
        MenuTest window = new MenuTest("测试窗体");
        // 设置窗体背景图遮罩透明度
        window.setBackgroundImageAlpha(0.5f);
        // 设置窗体背景图
//        window.setBackgroundImage("res/picture/background_image.png");
//        window.setBackgroundImage("/home/kylan/图片/背景图.png");
        //设置窗体可见
        window.setVisible(true);
    }
}