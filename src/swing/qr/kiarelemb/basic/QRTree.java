package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.assembly.QRMutableTreeNode;
import swing.qr.kiarelemb.assembly.QRToolTip;
import swing.qr.kiarelemb.combination.QRPopupMenu;
import swing.qr.kiarelemb.event.QRTreeExpansionEvent;
import swing.qr.kiarelemb.event.QRTreeNodeEvent;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.listener.add.*;
import swing.qr.kiarelemb.listener.*;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

/**
 * QR Swing 的主题树控件。
 *
 * <p>该类基于 {@link JTree}，统一了主题渲染、节点展开/折叠控制、树选择事件、
 * 节点点击事件、鼠标事件和展开前事件封装。设置窗口左侧导航、文件树、分类树等场景通常使用该类。</p>
 *
 * <p>如果节点使用 {@link QRMutableTreeNode}，可通过节点上的 {@code expendable/collapsable}
 * 控制是否允许用户展开或折叠。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRMutableTreeNode root = new QRMutableTreeNode("设置");
 * QRMutableTreeNode window = new QRMutableTreeNode("窗口");
 * root.add(window);
 *
 * QRTree tree = new QRTree(root);
 * tree.addTreeSelectionAction(event -> showPanel(event.getPath()));
 * tree.addTreeNodeClickAction(tree.getTreePath(window), event -> showWindowPanel(), true);
 * tree.expendAll();
 * </code></pre>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2023-01-28 21:39
 **/
public class QRTree extends JTree implements QRComponentUpdate, QRMouseListenerAdd, QRMouseMotionListenerAdd, QRTreeNodeListenerAdd, QRTreeWillExpandListenerAdd, QRTreeSelectionListenerAdd {

	protected final QRTreeCellRenderer renderer;
	protected QRPopupMenu popupMenu;
	private QRMouseMotionListener mouseMotionListener;
	private QRMouseListener mouseListener;
	private QRTreeNodeClickListener treeNodeClickListener;
	private QRTreeWillExpandListener treeWillExpandListener;
	private QRTreeSelectionListener treeSelectionListener;

	public QRTree(TreeNode root) {
		this();
		TreeModel model = new DefaultTreeModel(root, true);
		setModel(model);
	}

	public QRTree() {
		renderer = new QRTreeCellRenderer();
		setCellRenderer(renderer);
		setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
		setForeground(QRColorsAndFonts.TEXT_COLOR_FORE);
		setFont(QRColorsAndFonts.MENU_ITEM_DEFAULT_FONT);
		setRowHeight(30);
		setAutoscrolls(true);
		setOpaque(false);
		setUI(new QRTreeUI(this));

		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	/**
	 * @param node 节点
	 * @return 节点的 {@link TreePath}
	 */
	public TreePath getTreePath(TreeNode node) {
		TreeNode[] nodes = ((DefaultTreeModel) getModel()).getPathToRoot(node);
		return new TreePath(nodes);
	}

	/**
	 * 选中指定节点。
	 *
	 * @param node 要选中的节点
	 */
	public void select(TreeNode node) {
		TreePath path = getTreePath(node);
		setSelectionPath(path);
	}

	/**
	 * 展开指定节点。
	 *
	 * @param node 要展开的节点
	 */
	public void expend(TreeNode node) {
		TreePath path = getTreePath(node);
		expandPath(path);
	}

	/**
	 * 折叠指定节点。
	 *
	 * @param node 要折叠的节点
	 */
	public void collapse(TreeNode node) {
		TreePath path = getTreePath(node);
		collapsePath(path);
	}

	/**
	 * 将所有节点展开
	 */
	public void expendAll() {
		TreeModel model = getModel();
		if (model != null) {
			Object o = model.getRoot();
			nodeLoop(o, true);
		}
	}

	/**
	 * 将所有节点折叠
	 */
	public void collapseAll() {
		TreeModel model = getModel();
		if (model != null) {
			Object o = model.getRoot();
			nodeLoop(o, false);
		}
	}

	private void nodeLoop(Object o, boolean expend) {
		if (o instanceof TreeNode root) {
			Enumeration<? extends TreeNode> children = root.children();
			while (children.hasMoreElements()) {
				nodeLoop(children.nextElement(), expend);
			}
			if (expend) {
				if (root instanceof QRMutableTreeNode node) {
					if (!node.expendable()) {
						return;
					}
				}
				expandPath(getTreePath(root));
			} else {
				if (root instanceof QRMutableTreeNode node) {
					if (!node.collapsable()) {
						return;
					}
				}
				collapsePath(getTreePath(root));
			}
		}
	}

	//region 各种添加


	@Override
	public void addTreeSelectionListener() {
		if (this.treeSelectionListener == null) {
			this.treeSelectionListener = new QRTreeSelectionListener();
			this.treeSelectionListener.add(this::treeSelectionChanged);
			addTreeSelectionListener(this.treeSelectionListener);
		}
	}

	/**
	 * 添加树选择变化动作。
	 *
	 * <p>已自动安装内部树选择监听器。动作参数为 Swing 原生 {@link TreeSelectionEvent}。</p>
	 *
	 * @param ar 选择变化动作
	 */
	@Override
	public void addTreeSelectionAction(QRActionRegister<TreeSelectionEvent> ar) {
		if (this.treeSelectionListener == null) {
		    addTreeSelectionListener();
		}
		if (this.treeSelectionListener != null) {
			this.treeSelectionListener.add(ar);
		}
	}

	@Override
	public void addTreeWillExpandListener() {
		if (this.treeWillExpandListener == null) {
			this.treeWillExpandListener = new QRTreeWillExpandListener();
			this.treeWillExpandListener.add(QRTreeWillExpandListener.TYPE.EXPAND, this::treeWillExpand);
			this.treeWillExpandListener.add(QRTreeWillExpandListener.TYPE.COLLAPSE, this::treeWillCollapse);
		}
	}

	/**
	 * 添加树节点即将展开或折叠动作。
	 *
	 * <p>动作参数为 {@link QRTreeExpansionEvent}，可读取即将变化的 {@link TreePath}。</p>
	 *
	 * @param type 展开或折叠类型
	 * @param ar   动作
	 */
	@Override
	public void addTreeWillAction(QRTreeWillExpandListener.TYPE type, QRActionRegister<QRTreeExpansionEvent> ar) {
		if (this.treeWillExpandListener == null) {
		    addTreeWillExpandListener();
		}
		if (this.treeWillExpandListener != null) {
			this.treeWillExpandListener.add(type, ar);
		}
	}

	/**
	 * 安装节点点击监听器。
	 *
	 * <p>该监听器基于鼠标点击位置计算实际命中的节点和最近节点，并封装为
	 * {@link QRTreeNodeEvent} 分发。</p>
	 */
	@Override
	public void addTreeNodeListener() {
		if (this.treeNodeClickListener == null) {
			this.treeNodeClickListener = new QRTreeNodeClickListener();
			addMouseListener();
			addMouseAction(QRMouseListener.TYPE.CLICK, e -> {
				TreePath treePath = QRTree.this.getPathForLocation(e.getX(), e.getY());
				TreePath nearestTreePath = QRTree.this.getClosestPathForLocation(e.getX(), e.getY());
				QRTree.this.treeNodeClickListener.nodeClicked(new QRTreeNodeEvent(QRTree.this, treePath, nearestTreePath));
			});
		}
	}

	/**
	 * 添加单击事件
	 * 已自动添加 {@link #addTreeNodeListener()}
	 *
	 * <p>{@code positionVague} 为 false 时，只有点击位置正好命中 {@code path} 才触发；
	 * 为 true 时，点击该路径附近的最近节点也可触发，适合行高较大或希望提高容错的树导航。</p>
	 *
	 * @param path          目标节点路径
	 * @param ar            操作，参数为 {@link QRTreeNodeEvent}
	 * @param positionVague 是否允许最近节点匹配
	 */
	@Override
	public final void addTreeNodeClickAction(TreePath path, QRActionRegister<QRTreeNodeEvent> ar, boolean positionVague) {
		if (this.treeNodeClickListener == null) {
		    addTreeNodeListener();
		}
		if (this.treeNodeClickListener != null) {
			this.treeNodeClickListener.add(path, ar, positionVague);
		}
	}


	/**
	 * 添加鼠标移动事件
	 */
	@Override
	public final void addMouseMotionListener() {
		if (this.mouseMotionListener == null) {
			this.mouseMotionListener = new QRMouseMotionListener();
			this.mouseMotionListener.add(QRMouseMotionListener.TYPE.DRAG, this::mouseDrag);
			this.mouseMotionListener.add(QRMouseMotionListener.TYPE.MOVE, this::mouseMove);
			addMouseMotionListener(this.mouseMotionListener);
		}
	}

	/**
	 * 添加鼠标移动事件
	 *
	 * @param type 类型
	 * @param ar   操作
	 */
	@Override
	public final void addMouseMotionAction(QRMouseMotionListener.TYPE type, QRActionRegister<MouseEvent> ar) {
		if (this.mouseMotionListener == null) {
		    addMouseMotionListener();
		}
		if (this.mouseMotionListener != null) {
			this.mouseMotionListener.add(type, ar);
		}
	}

	/**
	 * 添加鼠标事件，在实例化时已添加
	 */
	@Override
	public final void addMouseListener() {
		if (this.mouseListener == null) {
			this.mouseListener = new QRMouseListener();
			this.mouseListener.add(QRMouseListener.TYPE.CLICK, this::mouseClick);
			this.mouseListener.add(QRMouseListener.TYPE.PRESS, this::mousePress);
			this.mouseListener.add(QRMouseListener.TYPE.RELEASE, this::mouseRelease);
			this.mouseListener.add(QRMouseListener.TYPE.ENTER, this::mouseEnter);
			this.mouseListener.add(QRMouseListener.TYPE.EXIT, this::mouseExit);
			addMouseListener(this.mouseListener);

		}
	}

	/**
	 * 添加鼠标事件
	 *
	 * @param type 类型
	 * @param ar   操作
	 */
	@Override
	public final void addMouseAction(QRMouseListener.TYPE type, QRActionRegister<MouseEvent> ar) {
		if (this.mouseListener == null) {
		    addMouseListener();
		}
		if (this.mouseListener != null) {
			this.mouseListener.add(type, ar);
		}
	}

	/**
	 * 为树创建并绑定右键菜单。重复调用返回同一实例。
	 *
	 * @return 绑定当前树的右键菜单
	 */
	public QRPopupMenu addPopupMenu() {
		return addPopupMenu(null);
	}

	/**
	 * 为树创建并绑定右键菜单，并在显示前执行回调。
	 *
	 * <p>回调可用于动态更新菜单状态。只有首次创建菜单时传入的回调会被绑定。</p>
	 *
	 * @param beforeShow 菜单显示前的回调，可为 null
	 * @return 绑定当前树的右键菜单
	 */
	public QRPopupMenu addPopupMenu(QRActionRegister<MouseEvent> beforeShow) {
		if (this.popupMenu == null) {
			this.popupMenu = QRPopupMenu.createAndBind(this, beforeShow);
		}
		return this.popupMenu;
	}
	//endregion

	//region 各种重写

	/**
	 * 重写前请先调用 {@link #addMouseMotionListener()}
	 */
	protected void mouseDrag(MouseEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addMouseMotionListener()}
	 */
	protected void mouseMove(MouseEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addMouseListener()}
	 */
	protected void mouseClick(MouseEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addMouseListener()}
	 */
	protected void mousePress(MouseEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addMouseListener()}
	 */
	protected void mouseRelease(MouseEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addMouseListener()}
	 */
	protected void mouseEnter(MouseEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addMouseListener()}
	 */
	protected void mouseExit(MouseEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addTreeWillExpandListener()}
	 */
	protected void treeWillExpand(QRTreeExpansionEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addTreeWillExpandListener()}
	 */
	protected void treeWillCollapse(QRTreeExpansionEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addTreeSelectionListener()}
	 */
	protected void treeSelectionChanged(TreeSelectionEvent e) {
	}

	//endregion

	//region 取得监听器

	public QRMouseMotionListener mouseMotionListener() {
		return mouseMotionListener;
	}

	public QRMouseListener mouseListener() {
		return mouseListener;
	}

	public QRTreeNodeClickListener treeNodeClickListener() {
		return treeNodeClickListener;
	}

	public QRTreeWillExpandListener treeWillExpandListener() {
		return treeWillExpandListener;
	}

	public QRTreeSelectionListener treeSelectionListener() {
		return treeSelectionListener;
	}

	//endregion

	@Override
	protected void setExpandedState(TreePath path, boolean state) {
		TreeModel model = getModel();
		if (path != null && model != null) {
			Object obj = path.getLastPathComponent();
			if (!model.isLeaf(obj)) {
				if (obj instanceof QRMutableTreeNode node) {
					if (!node.expendable()) {
						if (isExpanded(path)) {
							super.setExpandedState(path, false);
						}
						return;
					} else if (!node.collapsable()) {
						if (isCollapsed(path)) {
							super.setExpandedState(path, true);
						}
						return;
					}
				}
			}
		}
		super.setExpandedState(path, state);
	}

	@Override
	public void componentFresh() {
		setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
		renderer.componentFresh();
	}

	public static class QRTreeCellRenderer extends DefaultTreeCellRenderer implements QRComponentUpdate {

		public QRTreeCellRenderer() {
			setOpaque(false);
			closedIcon = null;
			openIcon = null;
			setLeafIcon(null);
			setDisabledIcon(null);
			componentFresh();
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			if (hasFocus) {
				hasFocus = false;
			}
//			DefaultMutableTreeNode va = (DefaultMutableTreeNode) value;
//			if (va instanceof QRMutableTreeNode node) {
//				if (!node.expendable() && expanded) {
//					expanded = false;
//				} else if (!node.collapsable() && !expanded) {
//					expanded = true;
//				}
//			}
			setBackgroundSelectionColor(new Color(0, 0, 0, 0));
			setBackgroundNonSelectionColor(new Color(0, 0, 0, 0));
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
//			label.setForeground(sel ? QRColorsAndFonts.CARET_COLOR : QRColorsAndFonts.BORDER_COLOR);
			// 树的文本颜色设置
			setForeground(sel ? QRColorsAndFonts.PRESS_COLOR : QRColorsAndFonts.TEXT_COLOR_FORE);
			return this;
//
//			// 得到每个节点的TreeNode

//
//			// 得到每个节点的text
//			String str = node.toString();
//			// 判断是哪个文本的节点设置对应的值（这里如果节点传入的是一个实体,则可以根据实体里面的一个类型属性来显示对应的图标）
//
//			//设置节点icon
//			if (Pattern.matches(".+_(.+)", str)) {
//				this.setIcon(new ImageIcon("image\\image.png"));
//			}

		}

		@Override
		public JToolTip createToolTip() {
			QRToolTip tip = new QRToolTip();
			tip.setComponent(tip);
			return tip;
		}

		@Override
		public void componentFresh() {
			setFont(QRColorsAndFonts.STANDARD_FONT_TEXT);
			setBackgroundNonSelectionColor(QRColorsAndFonts.FRAME_COLOR_BACK);
			setBackgroundSelectionColor(QRColorsAndFonts.LINE_COLOR);
			setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
		}
	}

	public static class QRTreeUI extends BasicTreeUI {
		public QRTreeUI(QRTree tree) {
			this.tree = tree;
			//线条的颜色
			setHashColor(QRColorsAndFonts.LINE_COLOR);
			setLeftChildIndent(20);
		}
	}
}
