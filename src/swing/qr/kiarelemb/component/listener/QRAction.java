package swing.qr.kiarelemb.component.listener;

import swing.qr.kiarelemb.component.QRComponentUtils;
import swing.qr.kiarelemb.inter.QRActionRegister;

import java.io.Serializable;
import java.util.LinkedList;

public class QRAction implements Serializable {
	private final LinkedList<QRActionRegister> list = new LinkedList<>();

	public void add(QRActionRegister ar) {
		list.add(ar);
	}

	protected void action(Object e) {
		QRComponentUtils.runActions(list, e);
	}
}