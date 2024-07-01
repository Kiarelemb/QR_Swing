package swing.qr.kiarelemb.assembly;

import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-21 22:07
 **/
public class QRScrollBarUI extends BasicScrollBarUI implements QRComponentUpdate {
	private final boolean horizontal;

	public QRScrollBarUI(boolean horizontal) {
		this.horizontal = horizontal;
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		Dimension preferredSize;
		if (horizontal) {
			preferredSize = new Dimension(0, 12);
			c.setPreferredSize(preferredSize);
		} else {
			preferredSize = new Dimension(12, 0);
			c.setPreferredSize(preferredSize);
		}
		return preferredSize;
	}

	@Override
	protected void paintDecreaseHighlight(Graphics g) {
	}

	@Override
	protected void paintIncreaseHighlight(Graphics g) {
	}

	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
	}

	@Override
	protected void configureScrollBarColors() {
		//滚动条的颜色
		thumbColor = QRColorsAndFonts.SCROLL_COLOR;
		thumbDarkShadowColor = thumbColor;
		//滚动条栏的背景色
//		trackColor = new Color(0, 0, 0, 0);

		if (horizontal) {
			setThumbBounds(0, 0, 10, 3);
		} else {
			setThumbBounds(0, 0, 3, 10);
		}
	}

	@Override
	protected JButton createDecreaseButton(int orientation) {
		//提供的这个按钮甚至能直接取消按钮的位置
		return new QRButton();
	}

	@Override
	protected JButton createIncreaseButton(int orientation) {
		return new QRButton();
	}

	@Override
	public void componentFresh() {
		thumbColor = QRColorsAndFonts.SCROLL_COLOR;
		thumbDarkShadowColor = thumbColor;

		//滚动条栏的背景色
//		trackColor = QRColorsAndFonts.TEXT_COLOR_BACK;
	}
}