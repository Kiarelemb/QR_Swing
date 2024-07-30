package swing.qr.kiarelemb.assembly;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.inter.QRComponentUpdate;

import javax.swing.border.Border;
import java.awt.*;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-22 18:44
 **/
@Deprecated
public class QRBackgroundBorder implements Border, QRComponentUpdate {
    private final Image image;
    private boolean scale = true;
    private float alpha;

    public QRBackgroundBorder(Image image) {
        this.image = image;
        alpha = Math.max(0f, Math.min(1f, QRSwing.windowBackgroundImageAlpha));
    }

    @Override
    public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {

    }

    public float alpha() {
        return this.alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setScale(boolean scale) {
        this.scale = scale;
    }

    public boolean scale() {
        return scale;
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, 0, 0);
    }

    public boolean isBorderOpaque() {
        return true;
    }

    @Override
    public void componentFresh() {
    }
}