package swing.qr.kiarelemb.listener;

import swing.qr.kiarelemb.event.QRTabbedPaneCloseEvent;
import swing.qr.kiarelemb.inter.listener.QRTabCloseLis;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-27 14:39
 **/
public class QRTabCloseListener extends QRAction implements QRTabCloseLis {
	@Override
	public final void tabCloseButtonAction(QRTabbedPaneCloseEvent e) {
		action(e);
	}
}