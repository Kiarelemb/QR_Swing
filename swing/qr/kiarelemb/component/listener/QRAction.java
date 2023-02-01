package swing.qr.kiarelemb.component.listener;

import swing.qr.kiarelemb.inter.QRActionRegister;

import java.util.LinkedList;

public class QRAction {
	private final LinkedList<QRActionRegister> list = new LinkedList<>();

	public void add(QRActionRegister ar) {
		list.add(ar);
	}

	protected void action(Object e) {
		for (QRActionRegister register : list) {
			register.action(e);
		}
	}
}
