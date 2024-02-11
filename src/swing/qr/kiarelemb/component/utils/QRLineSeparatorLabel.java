package swing.qr.kiarelemb.component.utils;

import swing.qr.kiarelemb.component.basic.QRLabel;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import java.awt.*;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-30 18:53
 **/
public class QRLineSeparatorLabel extends QRLabel {

	public static final int CENTER = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	private final Color lineColor;
	private final double portion;
	private final int position;
	private final boolean colorDefined;

	public QRLineSeparatorLabel() {
		this.lineColor = QRColorsAndFonts.LINE_COLOR;
		this.position = CENTER;
		this.portion = 0.5;
		this.colorDefined = true;
	}

	public QRLineSeparatorLabel(Color lineColor) {
		this.lineColor = lineColor;
		this.position = CENTER;
		this.portion = 0.5;
		this.colorDefined = false;
	}

	public QRLineSeparatorLabel(double portion) {
		this.lineColor = QRColorsAndFonts.LINE_COLOR;
		this.portion = portion;
		this.position = CENTER;
		this.colorDefined = true;
	}

	public QRLineSeparatorLabel(Color lineColor, int position) {
		this.lineColor = lineColor;
		this.position = position;
		this.portion = 0.5;
		this.colorDefined = false;
	}

	public QRLineSeparatorLabel(Color lineColor, double portion) {
		this.lineColor = lineColor;
		this.portion = portion;
		this.position = CENTER;
		this.colorDefined = false;
	}

	public QRLineSeparatorLabel(double portion, int position) {
		this.lineColor = QRColorsAndFonts.LINE_COLOR;
		this.portion = portion;
		this.position = position;
		this.colorDefined = true;
	}

	/**
	 * 分割线
	 *
	 * @param lineColor 分割线颜色
	 * @param portion   所占大小的比例
	 * @param position  位置
	 */
	public QRLineSeparatorLabel(Color lineColor, double portion, int position) {
		this.lineColor = lineColor;
		this.portion = portion;
		this.position = position;
		this.colorDefined = false;
	}

	@Override
	protected void paintComponent(Graphics g) {
		int width = getWidth();
		int length = (int) (width * portion);
		g.setColor(colorDefined ? QRColorsAndFonts.LINE_COLOR : lineColor);
		int x1;
		int x2;
		x1 = switch (position) {
			case LEFT -> 0;
			case RIGHT -> Math.max(width - length, 0);
			default -> Math.max((width - length) / 2, 0);
		};
		x2 = x1 + length;

		int y = getHeight() / 2;
		g.drawLine(x1, y, x2, y);
	}
}