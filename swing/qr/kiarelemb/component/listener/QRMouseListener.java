package swing.qr.kiarelemb.component.listener;

import swing.qr.kiarelemb.inter.QRActionRegister;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-24 15:15
 **/
public class QRMouseListener implements MouseListener {
	public enum TYPE {
		CLICK, PRESS, RELEASE, ENTER, EXIT
	}

	private final LinkedList<QRActionRegister> click = new LinkedList<>();
	private final LinkedList<QRActionRegister> press = new LinkedList<>();
	private final LinkedList<QRActionRegister> release = new LinkedList<>();
	private final LinkedList<QRActionRegister> enter = new LinkedList<>();
	private final LinkedList<QRActionRegister> exit = new LinkedList<>();

	public void add(TYPE type, QRActionRegister ar) {
		switch (type) {
			case CLICK -> this.click.add(ar);
			case PRESS -> this.press.add(ar);
			case RELEASE -> this.release.add(ar);
			case ENTER -> this.enter.add(ar);
			case EXIT -> this.exit.add(ar);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		for (QRActionRegister register : click) {
			register.action(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		for (QRActionRegister register : press) {
			register.action(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		for (QRActionRegister register : release) {
			register.action(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		for (QRActionRegister register : enter) {
			register.action(e);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		for (QRActionRegister register : exit) {
			register.action(e);
		}
	}


}
