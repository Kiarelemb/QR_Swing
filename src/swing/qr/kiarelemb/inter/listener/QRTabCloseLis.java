package swing.qr.kiarelemb.inter.listener;

import swing.qr.kiarelemb.event.QRTabbedPaneCloseEvent;

import java.util.EventListener;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-27 14:39
 **/
public interface QRTabCloseLis extends EventListener {

	void tabCloseButtonAction(QRTabbedPaneCloseEvent e);
}