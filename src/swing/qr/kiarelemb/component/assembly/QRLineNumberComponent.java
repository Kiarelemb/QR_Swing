package swing.qr.kiarelemb.component.assembly;

import method.qr.kiarelemb.utils.QRFontUtils;
import swing.qr.kiarelemb.component.basic.QRTextPane;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * @author Kiarelemb QR
 * @program: EnglishAnalyzer
 * @description:
 * @create 2023-01-10 20:50
 **/
public class QRLineNumberComponent extends JComponent {
	public static final int LEFT_ALIGNMENT = 0;
	public static final int RIGHT_ALIGNMENT = 1;
	public static final int CENTER_ALIGNMENT = 2;
	/**
	 * 行号的边距
	 */
	public static int margin = 15;
	private int alignment = LEFT_ALIGNMENT;
	private final QRTextPane textPane;
	private int preAllLine = 1;
	/**
	 * 默认情况下，文本面板不论输入还是删除均会更新行号
	 */
	private boolean customAdjust = false;

	public QRLineNumberComponent(QRTextPane textPane) {
		super();
		this.textPane = textPane;
		adjustWidth();
		textPane.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				changedUpdate(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				changedUpdate(e);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				if (!customAdjust) {
					adjustWidth();
				}
			}
		});
	}

	public void adjustWidth() {
		int max = this.textPane.allLines();
		if (max != preAllLine) {
			SwingUtilities.invokeLater(() -> {
				int width = QRFontUtils.getTextInWidth(this.textPane, String.valueOf(max)) + 2 * margin;
				Dimension dimension = this.textPane.getPreferredSize();
				setPreferredSize(new Dimension(width, dimension.height));
				setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, QRColorsAndFonts.LINE_COLOR));
				revalidate();
				repaint();
				preAllLine = max;
			});
		}
	}

	public void setAlignment(int alignment) {
		if (alignment < LEFT_ALIGNMENT || alignment > CENTER_ALIGNMENT) {
			throw new IllegalArgumentException(String.valueOf(alignment));
		}
		this.alignment = alignment;
	}

	/**
	 * 设置为 {@code true} 之后，行号的更新需要自行调用 {@link #adjustWidth()}
	 *
	 * @param customAdjust 是否自定义行号的更新
	 */
	public void setCustomAdjust(boolean customAdjust) {
		this.customAdjust = customAdjust;
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paintComponent(g2d);
		int width = getWidth();
		//背景颜色
		g2d.setColor(QRColorsAndFonts.TEXT_COLOR_BACK);
		g2d.fillRect(0, 0, width, getHeight());
		//前景色
		g2d.setColor(QRColorsAndFonts.TEXT_COLOR_FORE);
		g2d.setFont(this.textPane.textFont);
		int allLines = this.textPane.allLines();

		int textPaneMargin = this.textPane.getMargin().left;
		for (int i = 1; i <= allLines; i++) {
			int offset = this.textPane.getLineStartOffset(i);
			Rectangle2D r = this.textPane.positionRectangle(offset);
			if (r == null) {
				continue;
			}
			Rectangle rect = r.getBounds();
			String text = String.valueOf(i);
			Rectangle stringBounds = this.textPane.textFont.getStringBounds(text, g2d.getFontRenderContext()).getBounds();
			int yPosition = rect.y + (rect.height - stringBounds.height) / 2 + textPaneMargin;
			int xPosition;
			switch (this.alignment) {
				case RIGHT_ALIGNMENT -> xPosition = width - stringBounds.width - margin;
				case CENTER_ALIGNMENT -> xPosition = (width - stringBounds.width) / 2;
				default -> xPosition = margin;
			}
			g2d.drawString(text, xPosition, yPosition);
		}
	}
}