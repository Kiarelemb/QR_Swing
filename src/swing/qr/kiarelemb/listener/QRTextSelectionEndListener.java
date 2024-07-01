package swing.qr.kiarelemb.listener;

import swing.qr.kiarelemb.event.QRTextSelectionEndEvent;
import swing.qr.kiarelemb.inter.listener.QRTextSelectionEndLis;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-24 15:22
 **/
public class QRTextSelectionEndListener extends QRAction implements QRTextSelectionEndLis {
	@Override
	public final void selectionEnd(QRTextSelectionEndEvent e) {
		action(e);
	}
}