package swing.qr.kiarelemb.listener;

import swing.qr.kiarelemb.event.QRRedoUndoEvent;
import swing.qr.kiarelemb.inter.listener.QRRedoUndoLis;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-01-04 16:52
 **/
public class QRRedoUndoListener extends QRAction implements QRRedoUndoLis {
	@Override
	public final void redoUndoAction(QRRedoUndoEvent event) {
		action(event);
	}
}