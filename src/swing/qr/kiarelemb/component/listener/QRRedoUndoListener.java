package swing.qr.kiarelemb.component.listener;

import swing.qr.kiarelemb.component.event.QRRedoUndoEvent;
import swing.qr.kiarelemb.inter.listener.QRRedoUndoLis;

/**
 * @author Kiarelemb QR
 * @program: EnglishAnalyzer
 * @description:
 * @create 2023-01-04 16:52
 **/
public class QRRedoUndoListener extends QRAction implements QRRedoUndoLis {
	@Override
	public final void redoUndoAction(QRRedoUndoEvent event) {
		action(event);
	}
}