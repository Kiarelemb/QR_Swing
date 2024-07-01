package swing.qr.kiarelemb.listener;

import swing.qr.kiarelemb.utils.QRComponentUtils;
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