package swing.qr.kiarelemb.component.combination;

import swing.qr.kiarelemb.component.basic.QRPanel;
import swing.qr.kiarelemb.component.basic.QRScrollPane;
import swing.qr.kiarelemb.component.basic.QRTree;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.beans.BeanProperty;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-01-29 14:39
 **/
public class QRTreeTabbedPane extends QRPanel {
	private final QRTree tree;
	private final QRScrollPane scrollPane;
	private final Map<TreeNode, JPanel> map;
	private final String treePositionFromBorderLayout;
	private boolean positionVague = false;

	public QRTreeTabbedPane() {
		this(BorderLayout.WEST);
	}

	public QRTreeTabbedPane(QRTree tree) {
		this(tree, BorderLayout.WEST);
	}

	/**
	 * 参数 {@code treePositionFromBorderLayout} 的可用值如下：
	 * <ul>
	 * <li><code>BorderLayout.SOUTH</code>
	 * <li><code>BorderLayout.EAST</code>
	 * <li><code>BorderLayout.NORTH</code>
	 * <li><code>BorderLayout.WEST</code>
	 * </ul>
	 *
	 * @param treePositionFromBorderLayout 树的位置
	 */
	public QRTreeTabbedPane(String treePositionFromBorderLayout) {
		this(new QRTree(), treePositionFromBorderLayout);
	}

	/**
	 * 参数 {@code treePositionFromBorderLayout} 的可用值如下：
	 * <ul>
	 * <li><code>BorderLayout.SOUTH</code>
	 * <li><code>BorderLayout.EAST</code>
	 * <li><code>BorderLayout.NORTH</code>
	 * <li><code>BorderLayout.WEST</code>
	 * </ul>
	 *
	 * @param tree                         树
	 * @param treePositionFromBorderLayout 树的位置
	 */
	public QRTreeTabbedPane(QRTree tree, String treePositionFromBorderLayout) {
		super(false);
		this.tree = tree;
		this.treePositionFromBorderLayout = treePositionFromBorderLayout;
		this.map = new HashMap<>();
		scrollPane = new QRScrollPane();
		setLayout(new BorderLayout(5, 5));
		switch (treePositionFromBorderLayout) {
			case BorderLayout.SOUTH ->
					this.tree.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, QRColorsAndFonts.LINE_COLOR));
			case BorderLayout.EAST ->
					this.tree.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, QRColorsAndFonts.LINE_COLOR));
			case BorderLayout.NORTH ->
					this.tree.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, QRColorsAndFonts.LINE_COLOR));
			default -> {
				treePositionFromBorderLayout = BorderLayout.WEST;
				this.tree.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, QRColorsAndFonts.LINE_COLOR));
			}
		}
		add(this.tree, treePositionFromBorderLayout);
		add(this.scrollPane, BorderLayout.CENTER);
		tree.addTreeNodeListener();
	}

	public void addTreeNodePointToPanel(Map<TreeNode, ? extends JPanel> map) {
		this.map.putAll(map);
		map.forEach((this::putAction));
	}

	public void addTreeNodePointToPanel(TreeNode node, JPanel panel) {
		map.put(node, panel);
		putAction(node, panel);
	}

	/**
	 * 直接设置当前面板
	 *
	 * @param panel 要显示的内容面板
	 */
	public void jumpTo(QRPanel panel) {
		scrollPane.setViewportView(panel);
	}

	public JPanel getPanel(TreeNode node) {
		return map.get(node);
	}

	/**
	 * 该项设置在此后的添加中生效，设置前的添加不生效
	 *
	 * @param positionVague 单击时是否允许位置模糊
	 */
	public void setPositionVague(boolean positionVague) {
		this.positionVague = positionVague;
	}

	public boolean positionVague() {
		return positionVague;
	}

	public QRTree tree() {
		return tree;
	}

	private void putAction(TreeNode node, JPanel panel) {
		TreePath treePath = tree.getTreePath(node);
		tree.addTreeNodeClickAction(treePath, e -> scrollPane.setViewportView(panel), positionVague);
	}

	@Override
	public void componentFresh() {
		super.componentFresh();
		switch (treePositionFromBorderLayout) {
			case BorderLayout.SOUTH ->
					this.tree.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, QRColorsAndFonts.LINE_COLOR));
			case BorderLayout.EAST ->
					this.tree.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, QRColorsAndFonts.LINE_COLOR));
			case BorderLayout.NORTH ->
					this.tree.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, QRColorsAndFonts.LINE_COLOR));
			default -> this.tree.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, QRColorsAndFonts.LINE_COLOR));
		}
		this.map.forEach((node, panel) -> {
			if (panel instanceof QRComponentUpdate com) {
				if (scrollPane.getViewport().getView() instanceof QRPanel p) {
					if (p == panel) {
						return;
					}
				}
				com.componentFresh();
			}
		});
	}
}
