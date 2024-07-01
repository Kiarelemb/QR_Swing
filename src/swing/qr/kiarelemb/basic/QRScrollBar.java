package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.assembly.QRScrollBarUI;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-21 22:22
 **/
public class QRScrollBar extends JScrollBar implements QRComponentUpdate {
	private final QRScrollBarUI barUI;

	private QRScrollBar(boolean horizontal) {
		setOrientation(horizontal ? JScrollBar.HORIZONTAL : JScrollBar.VERTICAL);
		barUI = new QRScrollBarUI(horizontal);
		setUI(barUI);
		setUnitIncrement(30);
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				setCursor(Cursor.getDefaultCursor());
			}
		});
		componentFresh();
	}

	public void minusValue(int value) {
		int v = getValue();
		int m = getMinimum();
		int i = v - value;
		setValue(Math.max(m, i));
	}

	public void plusValue(int value) {
		int v = getValue();
		int m = getMaximum();
		int i = v + value;
		setValue(Math.min(m, i));
	}

	/**
	 * @param value 比例
	 */
	public void setValue(double value) {
		if (value <= 0) {
			setValue(0);
			return;
		}

		int maximum = getMaximum();
		if (value >= 1) {
			setValue(maximum);
		} else {
			int v = Math.min((int) (maximum * value), maximum);
			setValue(v);
		}
	}

	public QRScrollBarUI barUi() {
		return barUI;
	}

	@Override
	public void componentFresh() {
		setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
		barUI.componentFresh();
	}

	public static QRScrollBar getVerticalScrollBar() {
		return new QRScrollBar(false);
	}

	public static QRScrollBar getHorizontalScrollBar() {
		return new QRScrollBar(true);
	}
}