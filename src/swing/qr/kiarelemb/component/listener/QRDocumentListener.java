package swing.qr.kiarelemb.component.listener;

import swing.qr.kiarelemb.component.QRComponentUtils;
import swing.qr.kiarelemb.inter.QRActionRegister;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.LinkedList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-24 14:20
 **/
public class QRDocumentListener implements DocumentListener {
	public enum TYPE {
		INSERT, REMOVE, CHANGED
	}

	private final LinkedList<QRActionRegister> insert = new LinkedList<>();
	private final LinkedList<QRActionRegister> remove = new LinkedList<>();
	private final LinkedList<QRActionRegister> changed = new LinkedList<>();

	public final void add(TYPE type, QRActionRegister ar) {
		switch (type) {
			case INSERT -> this.insert.add(ar);
			case REMOVE -> this.remove.add(ar);
			case CHANGED -> this.changed.add(ar);
		}
	}

	@Override
	public final void insertUpdate(DocumentEvent e) {
		QRComponentUtils.runActions(insert, e);

	}

	@Override
	public final void removeUpdate(DocumentEvent e) {
		QRComponentUtils.runActions(remove, e);
	}

	@Override
	public final void changedUpdate(DocumentEvent e) {
		QRComponentUtils.runActions(changed, e);
	}
}