package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.assembly.QRMutableTreeNode;
import swing.qr.kiarelemb.assembly.QRToolTip;
import swing.qr.kiarelemb.event.QRTreeNodeEvent;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.listener.add.QRMouseListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseMotionListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRTreeNodeListenerAdd;
import swing.qr.kiarelemb.listener.QRMouseListener;
import swing.qr.kiarelemb.listener.QRMouseMotionListener;
import swing.qr.kiarelemb.listener.QRTreeNodeClickListener;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-01-28 21:39
 **/
public class QRTree extends JTree implements QRComponentUpdate, QRMouseListenerAdd, QRMouseMotionListenerAdd, QRTreeNodeListenerAdd {

    protected final QRTreeCellRenderer renderer;
    private QRMouseMotionListener mouseMotionListener;
    private QRMouseListener mouseListener;
    private QRTreeNodeClickListener treeNodeClickListener;

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

    public void select(TreeNode node) {
        TreePath path = getTreePath(node);
        setSelectionPath(path);
    }

    public void expend(TreeNode node) {
        TreePath path = getTreePath(node);
        expandPath(path);
    }

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

    /**
     * 给树添加节点单击事件
     */
    @Override
    public void addTreeNodeListener() {
        if (this.treeNodeClickListener == null) {
            this.treeNodeClickListener = new QRTreeNodeClickListener();
            addMouseListener();
            addMouseAction(QRMouseListener.TYPE.CLICK, e -> {
                MouseEvent event = (MouseEvent) e;
                TreePath treePath = QRTree.this.getPathForLocation(event.getX(), event.getY());
                TreePath nearestTreePath = QRTree.this.getClosestPathForLocation(event.getX(), event.getY());
                QRTree.this.treeNodeClickListener.nodeClicked(new QRTreeNodeEvent(QRTree.this, treePath, nearestTreePath));
            });
        }
    }

    /**
     * 添加单击事件，调用前请添加 {@link QRTree#addTreeNodeListener()}
     *
     * @param ar 操作
     */
    @Override
    public final void addTreeNodeClickAction(TreePath path, QRActionRegister ar, boolean positionVague) {
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
            this.mouseMotionListener.add(QRMouseMotionListener.TYPE.DRAG, e -> mouseDrag((MouseEvent) e));
            this.mouseMotionListener.add(QRMouseMotionListener.TYPE.MOVE, e -> mouseMove((MouseEvent) e));
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
    public final void addMouseMotionAction(QRMouseMotionListener.TYPE type, QRActionRegister ar) {
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
            this.mouseListener.add(QRMouseListener.TYPE.CLICK, e -> mouseClick((MouseEvent) e));
            this.mouseListener.add(QRMouseListener.TYPE.PRESS, e -> mousePress((MouseEvent) e));
            this.mouseListener.add(QRMouseListener.TYPE.RELEASE, e -> mouseRelease((MouseEvent) e));
            this.mouseListener.add(QRMouseListener.TYPE.ENTER, e -> mouseEnter((MouseEvent) e));
            this.mouseListener.add(QRMouseListener.TYPE.EXIT, e -> mouseExit((MouseEvent) e));
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
    public final void addMouseAction(QRMouseListener.TYPE type, QRActionRegister ar) {
        if (this.mouseListener != null) {
            this.mouseListener.add(type, ar);
        }
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