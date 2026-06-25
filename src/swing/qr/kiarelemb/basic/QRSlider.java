package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-02-01 22:41
 **/
public class QRSlider extends JSlider implements QRComponentUpdate {
    public QRSlider() {
        super();
        setOpaque(false);
        setBorder(null);
        setFocusable(false);
        componentFresh();
    }

    public void setBoundValue(int min, int max) {
        setMinimum(min);
        setMaximum(max);
    }

    @Override
    public void componentFresh() {
        setFont(QRColorsAndFonts.DEFAULT_FONT_MENU);
        setForeground(QRColorsAndFonts.TEXT_COLOR_FORE);
        setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
    }
}