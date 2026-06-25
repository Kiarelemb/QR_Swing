package swing.qr.kiarelemb.utils;

import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.*;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className MessageLabel
 * @description 一个可以换行的标签
 * @create 2026/6/5 15:57
 */
public final class QRMessageLabel extends QRLabel {
	private java.util.List<String> lines = java.util.Collections.emptyList();

	public void setMessage(String message) {
		int fontSize = QRColorsAndFonts.DEFAULT_FONT_MENU.getSize();
		Font font = QRColorsAndFonts.createFont(fontSize);
		java.util.List<String> wrappedLines = wrapLines(message, font);
		while (fontSize > 10 && wrappedLines.size() * getFontMetrics(font).getHeight() > getHeight()) {
			font = QRColorsAndFonts.createFont(--fontSize);
			wrappedLines = wrapLines(message, font);
		}
		setFont(font);
		this.lines = wrappedLines;
		super.setText(null);
		repaint();
	}

	private java.util.List<String> wrapLines(String message, Font font) {
		java.util.List<String> result = new java.util.ArrayList<>();
		FontMetrics fm = getFontMetrics(font);
		int maxWidth = Math.max(1, getWidth());
		String[] paragraphs = message.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1);
		for (String paragraph : paragraphs) {
			if (paragraph.isEmpty()) {
				result.add("");
			} else {
				wrapParagraph(paragraph, fm, maxWidth, result);
			}
		}
		return result;
	}

	private void wrapParagraph(String paragraph, FontMetrics fm, int maxWidth, java.util.List<String> result) {
		char[] chars = paragraph.toCharArray();
		int start = 0;
		while (start < chars.length) {
			int len = 1;
			while (start + len <= chars.length && fm.charsWidth(chars, start, len) <= maxWidth) {
				len++;
			}
			int lineLength = Math.max(1, len - 1);
			result.add(new String(chars, start, lineLength));
			start += lineLength;
		}
	}

	@Override
	public void componentFresh() {
		setForeground(QRColorsAndFonts.DEFAULT_COLOR_LABEL);
		setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (lines.isEmpty()) {
			return;
		}
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setFont(getFont());
		g2.setColor(getForeground());
		FontMetrics fm = g2.getFontMetrics();
		int lineHeight = fm.getHeight();
		int y = (getHeight() - lineHeight * lines.size()) / 2 + fm.getAscent();
		for (String line : lines) {
			int x = switch (getHorizontalAlignment()) {
				case SwingConstants.LEFT -> 0;
				case SwingConstants.RIGHT -> getWidth() - fm.stringWidth(line);
				default -> (getWidth() - fm.stringWidth(line)) / 2;
			};
			g2.drawString(line, Math.max(0, x), y);
			y += lineHeight;
		}
		g2.dispose();
	}
}