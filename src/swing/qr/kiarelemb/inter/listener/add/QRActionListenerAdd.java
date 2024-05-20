package swing.qr.kiarelemb.inter.listener.add;

import swing.qr.kiarelemb.inter.QRActionRegister;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-01-09 16:56
 **/
public interface QRActionListenerAdd {
	/**
	 * 添加 {@link java.awt.event.ActionListener} 事件
	 */
	void addActionListener();

	/**
	 * 为 {@link java.awt.event.ActionListener} 事件添加操作
	 *
	 * @param ar 操作
	 */
	void addClickAction(QRActionRegister ar);
}