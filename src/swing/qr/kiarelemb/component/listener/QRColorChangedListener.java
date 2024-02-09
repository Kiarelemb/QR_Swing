package swing.qr.kiarelemb.component.listener;

import swing.qr.kiarelemb.component.event.QRColorChangedEvent;
import swing.qr.kiarelemb.inter.listener.QRColorChangedLis;

/**
 * @author Kiarelemb QR
 * @program: EnglishAnalyzer
 * @description:
 * @create 2022-11-30 16:02
 **/
public class QRColorChangedListener extends QRAction implements QRColorChangedLis {
	@Override
	public void colorChanged(QRColorChangedEvent e) {
		action(e);
	}
}
