package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;

/**
 * QR Swing 的主题滑块。
 *
 * <p>该类基于 {@link JSlider}，默认透明、无边框、不可获取焦点，并跟随 QR Swing
 * 主题刷新字体和颜色。常用于透明度、缩放比例、字号、数值设置等连续值配置。</p>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
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

    /**
     * 设置滑块最小值和最大值。
     *
     * @param min 最小值
     * @param max 最大值
     */
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
