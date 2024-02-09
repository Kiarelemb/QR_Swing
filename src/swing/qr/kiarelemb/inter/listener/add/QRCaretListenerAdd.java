package swing.qr.kiarelemb.inter.listener.add;

import swing.qr.kiarelemb.inter.QRActionRegister;

/**
 * @author Kiarelemb QR
 * @program: EnglishAnalyzer
 * @description:
 * @create 2023-01-08 13:45
 **/
public interface QRCaretListenerAdd {
	/**
	 * 添加 {@link javax.swing.event.CaretListener} 事件
	 */
	void addCaretListener();

	/**
	 * 为 {@link javax.swing.event.CaretListener} 事件添加操作
	 *
	 * @param ar 操作
	 */
	void addCaretListenerAction(QRActionRegister ar);
}
