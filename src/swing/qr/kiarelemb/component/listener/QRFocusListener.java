package swing.qr.kiarelemb.component.listener;

import swing.qr.kiarelemb.component.QRComponentUtils;
import swing.qr.kiarelemb.inter.QRActionRegister;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.LinkedList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-27 13:14
 **/
public class QRFocusListener extends QRAction implements FocusListener {
	public enum TYPE {
		GAIN, LOST
	}

	private final LinkedList<QRActionRegister> gain = new LinkedList<>();
	private final LinkedList<QRActionRegister> lost = new LinkedList<>();

	public void add(QRFocusListener.TYPE type, QRActionRegister ar) {
		if (type == QRFocusListener.TYPE.GAIN) {
			gain.add(ar);
		} else {
			lost.add(ar);
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		QRComponentUtils.runActions(gain, e);
	}

	@Override
	public void focusLost(FocusEvent e) {
		QRComponentUtils.runActions(lost, e);
	}
}