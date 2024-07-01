package swing.qr.kiarelemb.inter.listener.add;

import swing.qr.kiarelemb.listener.QRKeyListener;
import swing.qr.kiarelemb.inter.QRActionRegister;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-01-08 13:49
 **/
public interface QRKeyListenerAdd {
	/**
	 * 添加 {@link java.awt.event.KeyListener} 事件
	 */
	void addKeyListener();

	/**
	 * 为 {@link java.awt.event.KeyListener} 事件添加操作
	 *
	 * @param type 类型
	 * @param ar   操作
	 */
	void addKeyListenerAction(QRKeyListener.TYPE type, QRActionRegister ar);
}