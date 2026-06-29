package swing.qr.kiarelemb.combination;

import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.basic.QRScrollPane;
import swing.qr.kiarelemb.basic.QRTree;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 左右/上下树形导航内容面板。
 *
 * <p>该组件由一个 {@link QRTree} 和一个中心 {@link QRScrollPane} 组成。
 * 调用方把树节点和内容面板建立映射后，点击节点即可在中心区域显示对应面板。
 * 适合设置窗口、分类导航、帮助文档目录等场景。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRTreeTabbedPane pane = new QRTreeTabbedPane(tree, BorderLayout.WEST);
 * pane.setPositionVague(true);
 * pane.addTreeNodePointToPanel(windowNode, windowPanel);
 * pane.addTreeNodePointToPanel(shortcutNode, shortcutPanel);
 * </code></pre>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2023-01-29 14:39
 **/
public class QRTreeTabbedPane extends QRPanel {
    protected final QRTree tree;
    protected final QRScrollPane scrollPane;
    protected final Map<TreeNode, JPanel> map;
    protected String treePositionFromBorderLayout;
    protected boolean positionVague = false;

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
        super(false, new BorderLayout(5, 5));
        this.tree = tree;
        this.treePositionFromBorderLayout = treePositionFromBorderLayout;
        this.map = new HashMap<>();
        scrollPane = new QRScrollPane();
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

//	public JPanel addTreeNode(String nodeName){
//		tree.add
//	}

    /**
     * 批量添加树节点到内容面板的映射。
     *
     * @param map 节点与面板映射
     */
    public void addTreeNodePointToPanel(Map<TreeNode, ? extends JPanel> map) {
        this.map.putAll(map);
        map.forEach((this::putAction));
    }

    /**
     * 添加单个树节点到内容面板的映射。
     *
     * <p>添加后会为该节点注册点击动作，点击节点时把中心滚动区域切换到对应面板。</p>
     *
     * @param node  树节点
     * @param panel 对应内容面板
     */
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

    /**
     * 获取节点对应的内容面板。
     *
     * @param node 树节点
     * @return 内容面板，未映射时为 null
     */
    public JPanel getPanel(TreeNode node) {
        return map.get(node);
    }

    /**
     * 设置后，调用 {@link #componentFresh()} 以刷新
     *
     * @param treePositionFromBorderLayout {@link #tree} 的位置
     */
    public void setTreePositionFromBorderLayout(String treePositionFromBorderLayout) {
        this.treePositionFromBorderLayout = treePositionFromBorderLayout;
    }

    /**
     * @return 当前树所在的 BorderLayout 位置
     */
    public String treePositionFromBorderLayout() {
        return treePositionFromBorderLayout;
    }

    /**
     * 该项设置在此后的添加中生效，设置前的添加不生效
     *
     * @param positionVague 单击时是否允许位置模糊
     */
    public void setPositionVague(boolean positionVague) {
        this.positionVague = positionVague;
    }

    /**
     * @return 是否允许树节点点击使用最近节点模糊匹配
     */
    public boolean positionVague() {
        return positionVague;
    }

    /**
     * @return 内部树控件
     */
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
