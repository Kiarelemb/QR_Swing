package swing.qr.kiarelemb.test;

import method.qr.kiarelemb.utils.QRRandomUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.basic.QRMenuItem;
import swing.qr.kiarelemb.basic.QRTextPane;
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
        // 用循环添加菜单和子菜单
        for (int i = 0; i < 4; i++) {
            QRButton button = titleMenuPanel.add("Menu " + i);
            for (int j = 0, size = QRRandomUtils.getRandomInt(2, 9); j < size; j++) {
                button.add(new QRMenuItem(String.format("Menu %s of Button %s", i, j)));
            }
        }
        this.mainPanel.add(new QRTextPane().addScrollPane());
//        QRComponentUtils.componentLoopToSetOpaque(this.mainPanel, true);
    }

    public static void main(String[] args) {
        QRSwing.start("res/settings.properties", "res/window.properties");
        // 设置菜单置于窗体标题栏
        QRSwing.setWindowTitleMenu(true);
        // 取消窗体圆角
        QRSwing.setWindowRound(false);
        MenuTest window = new MenuTest("测试窗体");
        // 设置窗体背景图遮罩透明度
        window.setBackgroundBorderAlpha(0.8f);
        // 设置窗体背景图
//        window.setBackgroundImage("res/picture/background_image.png");
        window.setBackgroundImage("/home/kylan/图片/背景图.png");
        //设置窗体可见
        window.setVisible(true);
    }
}