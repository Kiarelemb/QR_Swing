package swing.qr.kiarelemb.component.combination;

import swing.qr.kiarelemb.component.basic.QRButton;
import swing.qr.kiarelemb.inter.QRActionRegister;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-26 22:39
 **/
public class QRPopupItem extends QRButton {
	private final int index;

	public QRPopupItem(int index, String text) {
		super(text);
		this.index = index;
		addMouseListener();
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setHorizontalAlignment(SwingConstants.LEFT);
	}

	public QRPopupItem(int index, String text, QRActionRegister ar) {
		this(index, text);
		addActionListener(ar::action);
	}

	public int index() {
		return index;
	}
}
