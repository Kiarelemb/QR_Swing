package swing.qr.kiarelemb.test;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRTextPane;
import swing.qr.kiarelemb.inter.QRInternalScrollbarUpdate;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className TextPaneTest
 * @description TODO
 * @create 2024/7/14 下午8:04
 */
public class TextPaneTest extends QRTextPane implements QRInternalScrollbarUpdate {

    public static void main(String[] args) {
        QRSwing.start("setting.properties");
        QRSwing.setTheme("深色");
        QRSwing.setWindowTitleMenu(true);
        QRSwing.setWindowRound(false);
        QRSwing.registerGlobalKeyEvents();

    }

    public TextPaneTest() {
        setBackground(null);
        setOpaque(false);
        setLineWrap(true);
        setLineSpacing(0.8f);
    }
}