package swing.qr.kiarelemb.combination;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 带有圆角边框的内容面板
 * @create 2022-11-04 16:37
 **/
public class QRBorderContentPanel extends QRPanel {

    private Image image;
    private boolean scale = true;
    private float alpha;

    public QRBorderContentPanel() {
        super(false);
        setBorder(BorderFactory.createLineBorder(QRColorsAndFonts.BORDER_COLOR, 3));
        addMouseListener();
        addMouseMotionListener();
        addMouseWheelListener();
    }

    public QRBorderContentPanel(LayoutManager layout) {
        this();
        setLayout(layout);
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image image() {
        return image;
    }

    public void setAlpha(float alpha) {
        this.alpha = Math.max(0f, Math.min(1f, alpha));
    }

    public float alpha() {
        return this.alpha;
    }

    public void setScale(boolean scale) {
        this.scale = scale;
    }

    public boolean scale() {
        return scale;
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(QRColorsAndFonts.BORDER_COLOR);
        if (QRSwing.windowRound) {
            final int arc = 15;
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        } else {
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }

    @Override
    public void paint(Graphics g) {

        if (image != null) {
            // 绘制背景图
            Graphics2D g2 = (Graphics2D) g;
            RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.addRenderingHints(rh);

            int w = image.getWidth(null);
            int h = image.getHeight(null);
            int width = getWidth();
            int height = getHeight();
            if (scale) {
                AffineTransform scale = new AffineTransform();
                double proportion = ((double) width) / w;
                scale.scale(proportion, proportion);
                g2.drawImage(image, scale, null);
            } else {
                for (int ix = 0; ix < width; ix += w) {
                    for (int iy = 0; iy < height; iy += h) {
                        g2.drawImage(image, ix, iy, null);
                    }
                }
            }
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        }
        super.paint(g);
    }

    @Override
    public void setBorder(Border border) {
        if (QRSwing.windowRound) {
            super.setBorder(BorderFactory.createEmptyBorder(3, 4, 4, 3));
        } else {
            super.setBorder(border);
        }
    }

    @Override
    public void componentFresh() {
        super.componentFresh();
        setBorder(BorderFactory.createLineBorder(QRColorsAndFonts.BORDER_COLOR, 3));
    }
}