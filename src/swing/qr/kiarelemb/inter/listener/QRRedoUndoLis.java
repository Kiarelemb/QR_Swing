package swing.qr.kiarelemb.inter.listener;

import swing.qr.kiarelemb.component.event.QRRedoUndoEvent;

import java.util.EventListener;

/**
 * @author Kiarelemb QR
 * @program: EnglishAnalyzer
 * @description:
 * @create 2023-01-04 16:51
 **/
public interface QRRedoUndoLis extends EventListener {
	void redoUndoAction(QRRedoUndoEvent event);
}
