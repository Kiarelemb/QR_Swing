package swing.qr.kiarelemb.component.utils;

import swing.qr.kiarelemb.component.basic.QRLabel;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 一个进入后鼠标会变成手形的标签
 * @create 2023-01-31 15:13
 **/
public abstract class QRHandLabel extends QRLabel {
	public QRHandLabel() {
		addMouseListener();
	}

	public QRHandLabel(String text) {
		this();
		setText(text);
	}

	public QRHandLabel(Icon icon) {
		this();
		setIcon(icon);
	}

	@Override
	public void setText(String text) {
		super.setText("<HTML><U>" + text + "</U></HTML>");
	}

	public abstract void clickAction(MouseEvent e);

	@Override
	protected final void mouseClick(MouseEvent e) {
		clickAction(e);
	}

	@Override
	protected final void mouseEnter(MouseEvent e) {
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	@Override
	protected final void mouseExit(MouseEvent e) {
		setCursor(Cursor.getDefaultCursor());
	}

	@Override
	public void componentFresh() {
		super.componentFresh();
		setForeground(QRColorsAndFonts.BLUE_LIGHT);
	}
}
