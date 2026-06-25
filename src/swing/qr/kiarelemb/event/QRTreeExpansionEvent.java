package swing.qr.kiarelemb.event;

import swing.qr.kiarelemb.basic.QRTree;

import javax.swing.tree.TreePath;
import java.util.EventObject;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRTreeExpansionEvent
 * @description TODO
 * @create 2026/6/4 21:37
 */
public class QRTreeExpansionEvent  extends EventObject {

	private final TreePath path;
	public QRTreeExpansionEvent(QRTree tree, TreePath path) {
		super(tree);
		this.path = path;
	}

	public TreePath getPath() {
		return path;
	}
}