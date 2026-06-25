package swing.qr.kiarelemb.inter.listener.add;

import swing.qr.kiarelemb.event.QRTreeExpansionEvent;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.listener.QRTreeWillExpandListener;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRTreeWillExpandListenerAdd
 * @description TODO
 * @create 2026/6/4 22:02
 */
public interface QRTreeWillExpandListenerAdd {

	/**
	 * 添加 {@link QRTreeWillExpandListener} 事件
	 */
	void addTreeWillExpandListener();

	/**
	 * 为 {@link QRTreeWillExpandListener} 事件添加操作
	 *
	 * @param ar 操作
	 */
	void addTreeWillAction(QRTreeWillExpandListener.TYPE type, QRActionRegister<QRTreeExpansionEvent> ar);
}