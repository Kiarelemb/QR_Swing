package swing.qr.kiarelemb.component.listener;

import swing.qr.kiarelemb.component.QRComponentUtils;
import swing.qr.kiarelemb.inter.QRActionRegister;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-24 15:10
 **/
public class QRMouseMotionListener implements MouseMotionListener {
	public enum TYPE {
		DRAG, MOVE
	}

	private final LinkedList<QRActionRegister> drag = new LinkedList<>();
	private final LinkedList<QRActionRegister> move = new LinkedList<>();

	public void add(TYPE type, QRActionRegister ar) {
		if (type == TYPE.DRAG) {
			drag.add(ar);
		} else {
			move.add(ar);
		}
	}

	@Override
	public final void mouseDragged(MouseEvent e) {
		QRComponentUtils.runActions(drag, e);
	}

	@Override
	public final void mouseMoved(MouseEvent e) {
		QRComponentUtils.runActions(move, e);
	}
}