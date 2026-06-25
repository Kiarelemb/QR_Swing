package swing.qr.kiarelemb.utils;

import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className ImagePanel
 * @description 一个仅显示图片的面板，当无法显示时，会提供一个文本提示。
 * @create 2026/6/6 08:13
 */
public class QRImagePanel extends QRPanel {

	private final BufferedImage image;

	public QRImagePanel(BufferedImage image) {
		this.image = image;
		setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		try {
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if (image == null) {
				drawCenteredText(g2, "无法预览");
				return;
			}
			int margin = 10;
			int areaW = Math.max(1, getWidth() - margin * 2);
			int areaH = Math.max(1, getHeight() - margin * 2);
			double scale = Math.min((double) areaW / image.getWidth(), (double) areaH / image.getHeight());
			int drawW = Math.max(1, (int) Math.round(image.getWidth() * scale));
			int drawH = Math.max(1, (int) Math.round(image.getHeight() * scale));
			int x = (getWidth() - drawW) / 2;
			int y = (getHeight() - drawH) / 2;
			g2.drawImage(image, x, y, drawW, drawH, null);
		} finally {
			g2.dispose();
		}
	}

	private void drawCenteredText(Graphics2D g2, String text) {
		g2.setFont(QRColorsAndFonts.STANDARD_FONT_TEXT);
		FontMetrics fm = g2.getFontMetrics();
		g2.setColor(QRColorsAndFonts.TEXT_COLOR_FORE);
		g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, getHeight() / 2);
	}
}