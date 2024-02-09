package swing.qr.kiarelemb.component.listener;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-24 13:55
 **/
public class QRCaretListener extends QRAction implements CaretListener {
	@Override
	public final void caretUpdate(CaretEvent e) {
		action(e);
	}
}
