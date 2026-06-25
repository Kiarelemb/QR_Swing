package swing.qr.kiarelemb.event;

import swing.qr.kiarelemb.basic.QRTree;

import javax.swing.tree.TreePath;
import java.util.EventObject;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRTreeSelectionEvent
 * @description QRTree 节点选择事件
 * @create 2026/6/4 22:30
 */
public class QRTreeSelectionEvent extends EventObject {
	private final TreePath path;
	private final TreePath[] paths;
	private final TreePath oldLeadSelectionPath;
	private final TreePath newLeadSelectionPath;
	private final boolean addedPath;

	public QRTreeSelectionEvent(QRTree tree, TreePath path, TreePath[] paths, TreePath oldLeadSelectionPath, TreePath newLeadSelectionPath, boolean addedPath) {
		super(tree);
		this.path = path;
		this.paths = paths;
		this.oldLeadSelectionPath = oldLeadSelectionPath;
		this.newLeadSelectionPath = newLeadSelectionPath;
		this.addedPath = addedPath;
	}

	public TreePath getPath() {
		return path;
	}

	public TreePath[] getPaths() {
		return paths;
	}

	public TreePath getOldLeadSelectionPath() {
		return oldLeadSelectionPath;
	}

	public TreePath getNewLeadSelectionPath() {
		return newLeadSelectionPath;
	}

	public boolean isAddedPath() {
		return addedPath;
	}
}
