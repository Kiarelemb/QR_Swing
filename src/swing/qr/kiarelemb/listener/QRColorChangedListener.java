package swing.qr.kiarelemb.listener;

import swing.qr.kiarelemb.event.QRColorChangedEvent;
import swing.qr.kiarelemb.inter.listener.QRColorChangedLis;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-30 16:02
 **/
public class QRColorChangedListener extends QRAction implements QRColorChangedLis {
	@Override
	public final void colorChanged(QRColorChangedEvent e) {
		action(e);
	}
}