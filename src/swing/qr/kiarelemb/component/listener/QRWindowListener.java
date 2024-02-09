package swing.qr.kiarelemb.component.listener;

import swing.qr.kiarelemb.inter.QRActionRegister;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-24 15:10
 **/
public class QRWindowListener implements WindowListener {
	public enum TYPE {
		OPEN, CLOSING, CLOSED, ICONIFIED, DEICONIFIED, ACTIVATED, DEACTIVATED
	}

	private final LinkedList<QRActionRegister> open = new LinkedList<>();
	private final LinkedList<QRActionRegister> closing = new LinkedList<>();
	private final LinkedList<QRActionRegister> closed = new LinkedList<>();
	private final LinkedList<QRActionRegister> iconified = new LinkedList<>();
	private final LinkedList<QRActionRegister> deiconified = new LinkedList<>();
	private final LinkedList<QRActionRegister> activated = new LinkedList<>();
	private final LinkedList<QRActionRegister> deactivated = new LinkedList<>();

	public void add(TYPE type, QRActionRegister ar) {
		switch (type) {
			case CLOSING:
				this.closing.add(ar);
			case CLOSED:
				this.closed.add(ar);
			case ICONIFIED:
				this.iconified.add(ar);
			case DEICONIFIED:
				this.deiconified.add(ar);
			case ACTIVATED:
				this.activated.add(ar);
			case DEACTIVATED:
				this.deactivated.add(ar);
				break;
			default:
				this.open.add(ar);
		}
	}

	@Override
	public final void windowOpened(WindowEvent e) {
		for (QRActionRegister register : this.open) {
			register.action(e);
		}
	}

	@Override
	public final void windowClosing(WindowEvent e) {
		for (QRActionRegister register : this.closing) {
			register.action(e);
		}
	}

	@Override
	public final void windowClosed(WindowEvent e) {
		for (QRActionRegister register : this.closed) {
			register.action(e);
		}
	}

	@Override
	public final void windowIconified(WindowEvent e) {
		for (QRActionRegister register : this.iconified) {
			register.action(e);
		}
	}

	@Override
	public final void windowDeiconified(WindowEvent e) {
		for (QRActionRegister register : this.deiconified) {
			register.action(e);
		}
	}

	@Override
	public final void windowActivated(WindowEvent e) {
		for (QRActionRegister register : this.activated) {
			register.action(e);
		}
	}

	@Override
	public final void windowDeactivated(WindowEvent e) {
		for (QRActionRegister register : this.deactivated) {
			register.action(e);
		}
	}
}
