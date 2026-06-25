package swing.qr.kiarelemb.test;

import method.qr.kiarelemb.utils.QRFontUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.utils.QRKeyBoardPanel;
import swing.qr.kiarelemb.window.basic.QRFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

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


        QRKeyBoardPanel keyBoardPanel = new QRKeyBoardPanel() {
            {
                for (QRLabel label : labelList) {
                    label.clear();
                }
            }

            @Override
            protected void labelComponentFresh(QRLabel label) {
                label.setOpaque(true);
                label.setBackground(Color.red);
            }

            @Override
            protected void labelPaint(QRLabel label, Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                String title = text[labelIndex(label)];
                Rectangle2D r = QRFontUtils.getStringBounds(title, QRSwing.globalFont);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        this.getClientProperty(RenderingHints.KEY_TEXT_ANTIALIASING));
                g2.setFont(QRSwing.globalFont);
                g2.setColor(QRColorsAndFonts.MENU_COLOR);
                float x = (float) (label.getWidth() / 2f - r.getWidth() / 2f);
                g2.drawString(title, x, 30);
            }
        };
        keyBoardPanel.setLocation(10, 10);
        this.mainPanel.add(keyBoardPanel);
        setSize(1400, 600);
    }
}