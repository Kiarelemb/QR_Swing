package swing.qr.kiarelemb.component.listener;

import swing.qr.kiarelemb.component.event.QRItemEvent;
import swing.qr.kiarelemb.inter.listener.QRItemLis;

/**
 * @author Kiarelemb QR
 * @program: EnglishAnalyzer
 * @description:
 * @create 2022-12-10 21:28
 **/
public class QRItemListener extends QRAction implements QRItemLis {
	@Override
	public void itemChangedAction(QRItemEvent event) {
		action(event);
	}
}
