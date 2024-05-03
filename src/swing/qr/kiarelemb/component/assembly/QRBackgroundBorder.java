package swing.qr.kiarelemb.component.assembly;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.inter.QRComponentUpdate;

import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-22 18:44
 **/
public class QRBackgroundBorder implements Border, QRComponentUpdate {
	private final Image image;
	private boolean scale = true;
	private float alpha;

	public QRBackgroundBorder(Image image) {
		this.image = image;
		if (QRSwing.windowBackgroundImageAlpha < 0.0f || QRSwing.windowBackgroundImageAlpha > 1.0f) {
			alpha = 0.8f;
		} else {
			alpha = QRSwing.windowBackgroundImageAlpha;
		}
	}

	@Override
	public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
		Graphics2D g2 = (Graphics2D) graphics;
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

		if (scale) {
			AffineTransform scale = new AffineTransform();
			double proportion = ((double) width) / ((double) image.getWidth(null));
			scale.scale(proportion, proportion);
			g2.drawImage(image, scale, component);
		} else {
			int w = image.getWidth(null);
			for (int ix = x; ix < (x + width); ix += w) {
				int h = image.getHeight(null);
				for (int iy = y; iy < (y + height); iy += h) {
					g2.drawImage(image, ix, iy, component);
				}
			}
		}
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