package swing.qr.kiarelemb.listener;

import swing.qr.kiarelemb.event.QREventObject;
import swing.qr.kiarelemb.inter.listener.QRTextSetLis;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 文本类控件设置文本时的事件
 * @create 2022-11-27 14:06
 **/
public class QRTextSetListener extends QRAction implements QRTextSetLis {
	@Override
	public final void setTextAction(QREventObject e) {
		action(e);
	}
}