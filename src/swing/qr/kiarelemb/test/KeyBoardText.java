package swing.qr.kiarelemb.test;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.component.utils.QRKeyBoardPanel;
import swing.qr.kiarelemb.window.basic.QRFrame;

import javax.swing.*;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className KeyBoardText
 * @description TODO
 * @create 2024/4/13 16:28
 */
public class KeyBoardText extends QRFrame {

    public KeyBoardText(String title) {
        super(title);
        //设置窗体标题居中
        setTitlePlace(SwingConstants.CENTER);
//		setTitleCenter();
        //设置单击关闭按钮后窗体淡化退出并结束程序
        setCloseButtonSystemExit();

        this.mainPanel.setLayout(null);


        QRKeyBoardPanel keyBoardPanel = new QRKeyBoardPanel();
        keyBoardPanel.setLocation(10, 10);


        this.mainPanel.add(keyBoardPanel);



        setSize(1400, 600);

    }

    public static void main(String[] args) {
        QRSwing.start("setting.properties");
        QRSwing.setWindowTitleMenu(true);

        QRSwing.setWindowRound(false);
        KeyBoardText window = new KeyBoardText("测试窗体");
        window.setBackgroundBorderAlpha(0.9f);
        //设置窗体可见
        window.setVisible(true);
    }
}