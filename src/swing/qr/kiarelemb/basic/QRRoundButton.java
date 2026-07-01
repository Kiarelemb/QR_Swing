package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * 圆角按钮，继承自 {@link QRButton}，在父类基础上增加圆角背景与边框绘制。
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2022-11-21 19:13
 */
public class QRRoundButton extends QRButton {
    private final int ARC = 15;

    public QRRoundButton() {
        super();
    }

    public QRRoundButton(String text) {
        super(text);
    }

    @Override
    public void componentFresh() {
        super.componentFresh();
        setForeground(isEnabled() ? QRColorsAndFonts.MENU_COLOR : QRColorsAndFonts.DISABLED_COLOR_FORE);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        var model = getModel();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (!isEnabled()) {
                // Draw dimmed background
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2.setColor(QRColorsAndFonts.FRAME_COLOR_BACK);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, this.ARC, this.ARC);
                // Draw text and content at reduced opacity for disabled appearance
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                paintButtonContent(g2);
            } else {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, model.isRollover() ? 0.7f : 0.5f));
                if (model.isRollover() || model.isPressed()) {
                    g2.setColor(model.isPressed() ? QRColorsAndFonts.PRESS_COLOR : (model.isRollover() ? QRColorsAndFonts.ENTER_COLOR : QRColorsAndFonts.LINE_COLOR));
                } else {
                    g2.setColor(QRColorsAndFonts.LINE_COLOR);
                }
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, this.ARC, this.ARC);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
                paintButtonContent(g2);
            }
        } finally {
            g2.dispose();
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(QRColorsAndFonts.BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, this.ARC, this.ARC);
        super.paintBorder(g2);
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        setForeground(b ? QRColorsAndFonts.MENU_COLOR : QRColorsAndFonts.DISABLED_COLOR_FORE);
    }

    @Override
    public boolean contains(int x, int y) {
        return new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), this.ARC, this.ARC).contains(x, y);
    }
}
