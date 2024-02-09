package swing.qr.kiarelemb.component.basic;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.*;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-02-01 22:41
 **/
public class QRSlider extends JSlider implements QRComponentUpdate {
	public QRSlider() {
		super();
		componentFresh();
	}

	public void setBoundValue(int min, int max) {
		setMinimum(min);
		setMaximum(max);
	}

	@Override
	protected void paintBorder(Graphics g) {
		final int arc = 15;
		Graphics2D g2 = (Graphics2D) g;
		if (hasFocus()) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(QRColorsAndFonts.BORDER_COLOR);
			g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
		}

	}

	@Override
	public void componentFresh() {
		setFont(QRColorsAndFonts.DEFAULT_FONT_MENU);
		setForeground(QRColorsAndFonts.TEXT_COLOR_FORE);
		setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
	}
}
