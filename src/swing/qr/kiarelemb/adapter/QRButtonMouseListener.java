package swing.qr.kiarelemb.adapter;

import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 按键事件类
 * @create 2022-11-04 16:11
 **/
public class QRButtonMouseListener extends MouseAdapter {
	public boolean isNotCloseButton = true;
	JButton btn;
	private Color defaultBackColor = QRColorsAndFonts.FRAME_COLOR_BACK;
	private Color enterColor;
	private Color pressColor;
//    private final Color beforeEnterColor;

	public QRButtonMouseListener(JButton btn) {
		this.btn = btn;
		this.enterColor = Color.RED;
		this.pressColor = QRColorsAndFonts.DEFAULT_COLOR_LABEL;
		isNotCloseButton = false;
//        beforeEnterColor = btn.getBackground();
	}

	public QRButtonMouseListener(JButton btn, Color enterColor, Color pressColor) {
		this.btn = btn;
		this.enterColor = enterColor;
		this.pressColor = pressColor;
	}

	public void setEnterColor(Color enterColor) {
		if (isNotCloseButton) {
			this.enterColor = enterColor;
		}
	}

	public void setPressColor(Color pressColor) {
		if (isNotCloseButton) {
			this.pressColor = pressColor;
		}
	}

	public void setBackColor(Color defaultBackColor) {
		this.defaultBackColor = defaultBackColor;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		btn.setBackground(enterColor);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		btn.setBackground(pressColor);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		btn.setBackground(enterColor);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		btn.setBackground(defaultBackColor);
	}
}
