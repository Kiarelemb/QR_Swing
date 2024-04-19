package swing.qr.kiarelemb.component.listener;

import swing.qr.kiarelemb.component.QRComponentUtils;
import swing.qr.kiarelemb.inter.QRActionRegister;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-24 14:54
 **/
public class QRKeyListener implements KeyListener {
	public enum TYPE {
		TYPE, PRESS, RELEASE
	}

	private final LinkedList<QRActionRegister> type = new LinkedList<>();
	private final LinkedList<QRActionRegister> press = new LinkedList<>();
	private final LinkedList<QRActionRegister> release = new LinkedList<>();

	public void add(TYPE type, QRActionRegister ar) {
		switch (type) {
			case TYPE -> this.type.add(ar);
			case PRESS -> this.press.add(ar);
			case RELEASE -> this.release.add(ar);
		}
	}

	public void addType(QRActionRegister ar) {
		type.add(ar);
	}

	public void addPress(QRActionRegister ar) {
		press.add(ar);
	}

	public void addRelease(QRActionRegister ar) {
		release.add(ar);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		QRComponentUtils.runActions(type, e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		QRComponentUtils.runActions(press, e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		QRComponentUtils.runActions(release, e);
	}
}