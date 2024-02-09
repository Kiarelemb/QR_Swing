package swing.qr.kiarelemb.inter.listener;

import swing.qr.kiarelemb.component.event.QRTextSelectionEndEvent;

import java.util.EventListener;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-24 15:23
 **/
public interface QRTextSelectionEndLis extends EventListener {
	void selectionEnd(QRTextSelectionEndEvent e);
}
