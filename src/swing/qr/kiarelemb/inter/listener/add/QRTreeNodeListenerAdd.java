package swing.qr.kiarelemb.inter.listener.add;

import swing.qr.kiarelemb.inter.QRActionRegister;

import javax.swing.tree.TreePath;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-01-28 23:42
 **/
public interface QRTreeNodeListenerAdd {
    /**
     * 添加 {@link swing.qr.kiarelemb.listener.QRTreeNodeClickListener} 事件
     */
    void addTreeNodeListener();

    /**
     * 为 {@link swing.qr.kiarelemb.listener.QRTreeNodeClickListener} 事件添加操作
     *
     * @param ar 操作
     */
    void addTreeNodeClickAction(TreePath path, QRActionRegister ar, boolean vague);
}