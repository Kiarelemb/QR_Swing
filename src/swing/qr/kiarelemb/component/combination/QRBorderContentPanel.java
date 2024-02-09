package swing.qr.kiarelemb.component.combination;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.component.basic.QRPanel;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 带有圆角边框的内容面板
 * @create 2022-11-04 16:37
 **/
public class QRBorderContentPanel extends QRPanel {

	private boolean borderPaint = true;

	public QRBorderContentPanel() {
		super();
		setBorder(BorderFactory.createLineBorder(QRColorsAndFonts.BORDER_COLOR, 1));
	}

	@Override
	protected void paintBorder(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(QRColorsAndFonts.BORDER_COLOR);
		if (QRSwing.windowRound && this.borderPaint) {
			final int arc = 15;
			g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
		} else if (this.borderPaint) {
			g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	}

	@Override
	public void setBorder(Border border) {
		if (QRSwing.windowRound) {
			super.setBorder(BorderFactory.createEmptyBorder(3, 4, 4, 3));
		} else {
			super.setBorder(border);
		}
	}

	/**
	 * 是否设置边框圆角，{@code false} 则不画圆角
	 */
	public void setBorderPaint(boolean borderPaint) {
		this.borderPaint = borderPaint;
	}

	@Override
	public void componentFresh() {
		super.componentFresh();
		setBorder(BorderFactory.createLineBorder(QRColorsAndFonts.BORDER_COLOR, 1));
	}
}
