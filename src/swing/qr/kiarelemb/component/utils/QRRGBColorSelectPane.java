package swing.qr.kiarelemb.component.utils;

import method.qr.kiarelemb.utils.QRSleepUtils;
import method.qr.kiarelemb.utils.QRStringUtils;
import swing.qr.kiarelemb.component.basic.QRLabel;
import swing.qr.kiarelemb.component.basic.QRPanel;
import swing.qr.kiarelemb.component.basic.QRTextField;
import swing.qr.kiarelemb.component.event.QRColorChangedEvent;
import swing.qr.kiarelemb.component.listener.QRColorChangedListener;
import swing.qr.kiarelemb.component.listener.QRMouseListener;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 一个集成多个控件的颜色选择、显示、设置的面板
 * @create 2022-11-30 14:54
 **/
public class QRRGBColorSelectPane extends QRPanel {

	private final QRLabel showColorLabel;
	private final ColorTextField rt;
	private final ColorTextField gt;
	private final ColorTextField bt;
	private final QRColorChangedListener colorChangedListener;
	private Color color;

	private class ColorTextField extends QRTextField {
		protected boolean insertBlock;

		public ColorTextField(int value) {
			setText(String.valueOf(value));
			numbersOnly();
			setFont(QRColorsAndFonts.DEFAULT_FONT_MENU);
			setPreferredSize(new Dimension(50, 30));
			setHorizontalAlignment(CENTER);
			addDocumentListener();
		}

		@Override
		public void copy() {
//			String c = getColor(color);
//			QRSystemUtils.putTextToClipboard(c);
		}

		@Override
		public void paste() {
			pasteAction();
		}

		//        @Override
//        protected void focusLostAction() {
//        }

		@Override
		public void setText(String t) {
			insertBlock = true;
			try {
				super.setText(t);
			} catch (IllegalStateException ignore) {
//                ignore.printStackTrace();
			}
			insertBlock = false;
		}

		@Override
		protected boolean meetCondition() {
//            insertBlock = true;
			final String text = getText();
			if (QRStringUtils.isNumber(text)) {
				final int value = Integer.parseInt(text);
				if (value < 0) {
					setText("0");
				} else if (value > 255) {
					setText("255");
				}
			} else {
				setText("0");
			}
			colorChanged();
//            insertBlock = false;
			return super.meetCondition();
		}

		@Override
		protected void insertUpdate(DocumentEvent e) {
			if (insertBlock) {
				return;
			}
//            new Thread(() -> {
			insertBlock = true;
			QRSleepUtils.sleep(20);
			meetCondition();
			insertBlock = false;
		}

		@Override
		protected void removeUpdate(DocumentEvent e) {
			if (insertBlock) {
				return;
			}
			meetCondition();
		}

		public int getValue() {
			final String text = getText();
//			final int value = colorValueCheckBound(Integer.parseInt(text));
//			setText(value);
            return 0;
		}

		public void setValue(int value) {
//			setText(colorValueCheckBound(value));
		}

		private boolean valueInRange(int value) {
			return value >= 0 && value <= 255;
		}
	}

	public QRRGBColorSelectPane() {
		this(Color.WHITE, new QRLabel());
	}

	public QRRGBColorSelectPane(Color color) {
		this(color, new QRLabel());
	}

	public QRRGBColorSelectPane(Color color, QRLabel showColorLabel) {
//        setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
		this.showColorLabel = showColorLabel;
		this.color = color;
		showColorLabel.setOpaque(true);
		showColorLabel.setPreferredSize(new Dimension(30, 30));
		showColorLabel.setBackground(color);
		showColorLabel.setBorder(BorderFactory.createLineBorder(QRColorsAndFonts.LINE_COLOR, 1));
		setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
		rt = new ColorTextField(color.getRed());
		gt = new ColorTextField(color.getGreen());
		bt = new ColorTextField(color.getBlue());
		QRLabel rl = new QRLabel("r:");
		QRLabel gl = new QRLabel("g:");
		QRLabel bl = new QRLabel("b:");
		rl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
//		gl.setBorder(emptyBorder);
//		bl.setBorder(emptyBorder);
		rl.setTextCenter();
		gl.setTextCenter();
		bl.setTextCenter();
		colorChangedListener = new QRColorChangedListener();
		colorChangedListener.add(e -> colorChangedAction((QRColorChangedEvent) e));
		showColorLabel.addMouseListener();
		showColorLabel.addMouseAction(QRMouseListener.TYPE.CLICK, e -> {
			Color selected = JColorChooser.showDialog(QRRGBColorSelectPane.this, "选择颜色", QRRGBColorSelectPane.this.color);
			if (selected != null) {
				rt.setValue(selected.getRed());
				gt.setValue(selected.getGreen());
				bt.setValue(selected.getBlue());
				colorChanged();
			}
		});
		showColorLabel.addMouseAction(QRMouseListener.TYPE.ENTER, e -> showColorLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)));
		showColorLabel.addMouseAction(QRMouseListener.TYPE.EXIT, e -> showColorLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)));

		add(showColorLabel);
		add(rl);
		add(rt);
		add(gl);
		add(gt);
		add(bl);
		add(bt);
		setSize(250, 30);
	}

	private void colorChangedAction(QRColorChangedEvent e) {
		colorChangedAction(e.from(), e.to());
	}

	/**
	 * @param ar 其方法 {@link QRActionRegister#action(Object)} 的参数为 {@link QRColorChangedEvent} 的对象
	 */
	public void addColorChangedAction(QRActionRegister ar) {
		colorChangedListener.add(ar);
	}

	public void colorChanged() {
		if (rt.getText().isEmpty() || gt.getText().isEmpty() || bt.getText().isEmpty()) {
			return;
		}
		Color from = this.color;
		setColor(new Color(rt.getValue(), gt.getValue(), bt.getValue()));
		Color to = this.color;
		colorChangedListener.colorChanged(new QRColorChangedEvent(from, to));
	}

	public Color getColor() {
		return color;
	}

	public QRLabel showColorLabel() {
		return showColorLabel;
	}

	public void setColor(Color color) {
		this.color = color;
		rt.setValue(color.getRed());
		gt.setValue(color.getGreen());
		bt.setValue(color.getBlue());
		showColorLabel.setBackground(color);
	}

	/**
	 * 颜色改变事件
	 *
	 * @param from 原颜色
	 * @param to   改变后的颜色
	 */
	protected void colorChangedAction(Color from, Color to) {

	}

	private void pasteAction() {
//		final String text = QRSystemUtils.getSysClipboardText();
//		if (text != null) {
//			final int[] ints = QRStringUtils.splitWithNotNumber(text);
//			Color c = null;
//			if (ints.length == 3) {
//				c = new Color(colorValueCheckBound(ints[0]), colorValueCheckBound(ints[1]), colorValueCheckBound(ints[2]));
//			} else {
//				final String tmp = QRStringUtils.spaceClear(text, true);
//				if (tmp.length() == 7 && tmp.startsWith("#")) {
//					c = parseColor(tmp.substring(1));
//				}
//			}
//			if (c != null) {
//				setColor(c);
//			}
//		}
	}

	@Override
	public void componentFresh() {
		super.componentFresh();
		this.showColorLabel.setBorder(BorderFactory.createLineBorder(QRColorsAndFonts.LINE_COLOR, 1));
	}
}