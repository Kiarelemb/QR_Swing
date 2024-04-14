package swing.qr.kiarelemb.component.utils;

import swing.qr.kiarelemb.component.basic.QRLabel;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 这是一个显示行号内容的标签，用设定的符号将多个数字分开
 * @create 2022-11-25 08:00
 **/
public class QRLineAndRowLabel extends QRLabel {
	private final String split;
	private int line;
	private int row;
	private int[] other;

	public QRLineAndRowLabel(String split) {
		this(split, 0, 0);
	}

	public QRLineAndRowLabel(String split, int line, int row, int... others) {
		this.split = split;
		setText(line, row, others);
	}

	public void setText(int line, int row, int... others) {
		this.line = line;
		this.row = row;
		this.other = others;
		StringBuilder sb = new StringBuilder(line + split + row);
		if (others != null) {
			for (int i : others) {
				sb.append(split).append(i);
			}
		}
		setText(sb.toString());
	}

	public String split() {
		return split;
	}

	public int line() {
		return line;
	}

	public int row() {
		return row;
	}

	public int[] other() {
		return other;
	}
}