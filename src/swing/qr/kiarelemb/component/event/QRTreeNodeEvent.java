package swing.qr.kiarelemb.component.event;

import swing.qr.kiarelemb.component.basic.QRTree;

import javax.swing.tree.TreePath;
import java.util.EventObject;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-01-28 23:37
 **/
public class QRTreeNodeEvent extends EventObject {
	private final TreePath clickedPath;
	private final TreePath clickedNearestPath;

	public QRTreeNodeEvent(QRTree tree, TreePath clickedPath, TreePath clickedNearestPath) {
		super(tree);
		this.clickedPath = clickedPath;
		this.clickedNearestPath = clickedNearestPath;
	}

	public TreePath clickedPath() {
		return clickedPath;
	}

	public TreePath clickedNearestPath() {
		return clickedNearestPath;
	}
}
