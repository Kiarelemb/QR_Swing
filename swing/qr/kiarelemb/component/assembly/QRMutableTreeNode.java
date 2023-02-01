package swing.qr.kiarelemb.component.assembly;

import swing.qr.kiarelemb.component.basic.QRTree;
import swing.qr.kiarelemb.inter.QRActionRegister;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-01-28 23:15
 **/
public class QRMutableTreeNode extends DefaultMutableTreeNode {
	private boolean expendable = true;
	private boolean collapsable = true;

	/**
	 * {@link DefaultMutableTreeNode#allowsChildren} 为 {@code false}，但当有子节点时，自动设置为 {@code true}
	 */
	public QRMutableTreeNode(String text) {
		super(text, false);
	}

	public QRMutableTreeNode addChild(String text) {
		QRMutableTreeNode node = new QRMutableTreeNode(text);
		add(node);
		return node;
	}

	@Override
	public void add(MutableTreeNode newChild) {
		allowsChildren = true;
		super.add(newChild);
	}

	public void addAndExpend(QRTree tree, MutableTreeNode newChild) {
		add(newChild);
		TreePath treePath = tree.getTreePath(this);
		tree.expandPath(treePath);
	}

	public void addClickAction(QRTree tree, QRActionRegister ar, boolean positionVague) {
		TreePath treePath = tree.getTreePath(this);
		if (treePath != null) {
			tree.addTreeNodeListener();
			tree.addTreeNodeClickAction(treePath, ar, positionVague);
		}
	}

	public boolean expendable() {
		return expendable;
	}

	public void setExpendable(boolean expendable) {
		this.expendable = expendable;
		if (!expendable && !this.collapsable) {
			this.collapsable = true;
		}
	}

	public boolean collapsable() {
		return collapsable;
	}

	public void setCollapsable(boolean collapsable) {
		this.collapsable = collapsable;
		if (!collapsable && !this.expendable) {
			this.expendable = true;
		}
	}
}
