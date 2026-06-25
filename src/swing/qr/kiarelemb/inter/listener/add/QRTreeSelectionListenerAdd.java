package swing.qr.kiarelemb.inter.listener.add;

import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.listener.QRTreeSelectionListener;

import javax.swing.event.TreeSelectionEvent;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRTreeSelectionListenerAdd
 * @description TODO
 * @create 2026/6/4 22:30
 */
public interface QRTreeSelectionListenerAdd {

	/**
	 * 添加 {@link QRTreeSelectionListener} 事件
	 */
	void addTreeSelectionListener();

	/**
	 * 为 {@link QRTreeSelectionListener} 事件添加操作
	 *
	 * @param ar 操作
	 */
	void addTreeSelectionAction(QRActionRegister<TreeSelectionEvent> ar);
}