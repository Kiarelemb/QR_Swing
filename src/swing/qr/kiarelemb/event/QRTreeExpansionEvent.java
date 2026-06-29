package swing.qr.kiarelemb.event;

import swing.qr.kiarelemb.basic.QRTree;

import javax.swing.tree.TreePath;
import java.util.EventObject;

/**
 * QRTree 节点展开/折叠前事件。
 *
 * <p>该事件由 {@link swing.qr.kiarelemb.listener.QRTreeWillExpandListener}
 * 发出，用于在节点展开或折叠前通知调用方当前路径。</p>
 *
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRTreeExpansionEvent
 * @create 2026/6/4 21:37
 */
public class QRTreeExpansionEvent  extends EventObject {

	private final TreePath path;
	public QRTreeExpansionEvent(QRTree tree, TreePath path) {
		super(tree);
		this.path = path;
	}

	/**
	 * @return 即将展开或折叠的树路径
	 */
	public TreePath getPath() {
		return path;
	}
}
