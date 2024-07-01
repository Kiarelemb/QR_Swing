package swing.qr.kiarelemb.inter.listener.add;

import swing.qr.kiarelemb.inter.QRActionRegister;

public interface QRTextSelectionEndListenerAdd {

	/**
     * 添加 {@link swing.qr.kiarelemb.listener.QRTextSelectionEndListener} 事件
	 */
	void addSelectionEndListener();

	/**
     * 为 {@link swing.qr.kiarelemb.listener.QRTextSelectionEndListener} 事件添加操作
	 *
	 * @param ar 操作
	 */
	void addSelectionEndAction(QRActionRegister ar);
}