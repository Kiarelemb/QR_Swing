package swing.qr.kiarelemb.test;

import method.qr.kiarelemb.utils.QRRandomUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.basic.QRComboBox;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.window.basic.QRFrame;

import javax.swing.*;
import java.awt.event.WindowEvent;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className ComboBoxText
 * @description TODO
 * @create 2024/4/14 22:12
 */
public class ComboBoxText extends QRFrame {
    QRComboBox box;

    public ComboBoxText(String title) {
        super(title);
        //设置窗体标题居中
        setTitleCenter();
        //设置单击关闭按钮后窗体淡化退出并结束程序
        setCloseButtonSystemExit();
        this.mainPanel.setLayout(null);


        box = new QRComboBox("22221", "30294", "2839432", "23489504");
        box.setBounds(40, 40, 180, 30);
        box.setEditable(true);
        box.boxUI.textField().setTextCenter();
        mainPanel.add(box);

        QRButton button = new QRButton("主题");
        button.addClickAction(event -> {
            String[] themes = QRColorsAndFonts.BASIC_THEMES;
            int i = QRRandomUtils.getRandomInt(themes.length);
            QRSwing.setTheme(themes[i]);
            ComboBoxText.this.componentFresh();
        });
        button.setBounds(80, 90, 50, 30);
        mainPanel.add(button);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        System.out.println(SwingUtilities.getWindowAncestor(box.boxUI.textField()));
        System.out.println(SwingUtilities.getWindowAncestor(box));
    }

    public static void main(String[] args) {
        QRSwing.start("setting.properties");
        QRFrame window = new ComboBoxText("这是一个测试窗体");
        window.setSize(400, 400);
        window.setLocationRelativeTo(null);
        //设置窗体可见
        window.setVisible(true);
    }
}