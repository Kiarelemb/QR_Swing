package swing.qr.kiarelemb.component.utils;

import swing.qr.kiarelemb.component.basic.QRButton;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.window.basic.QRFrame;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRCloseButton
 * @description 一个符号为 x 的按钮
 * @create 2024/3/31 22:37
 */
public class QRCloseButton extends QRButton {
	public QRCloseButton() {
		super(QRFrame.CLOSE_MARK);
		disableListener();
		addMouseMotionListener();
	}

	@Override
	protected void mouseEnter(MouseEvent e) {
		//进入关闭按钮时，鼠标仍然是默认状态
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public void componentFresh() {
		super.componentFresh();
		setForeground(QRColorsAndFonts.MENU_COLOR);
		setFont(QRColorsAndFonts.PROCESS_BUTTON_FONT.deriveFont(11f).deriveFont(Font.BOLD));
	}
}