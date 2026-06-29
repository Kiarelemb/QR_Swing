package swing.qr.kiarelemb.event;

import swing.qr.kiarelemb.basic.QRTree;

import javax.swing.tree.TreePath;
import java.util.EventObject;

/**
 * QRTree 节点点击事件。
 *
 * <p>{@link #clickedPath()} 表示鼠标点击位置真正命中的节点路径；
 * {@link #clickedNearestPath()} 表示距离点击位置最近的节点路径。两者可能不同：
 * 当用户点击在节点文字外侧但仍靠近某一行时，真实命中路径可能为 null，
 * 最近路径仍可用于模糊匹配。</p>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
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

    /**
     * @return 鼠标点击位置真实命中的节点路径，未命中节点时可能为 null
     */
    public TreePath clickedPath() {
        return clickedPath;
    }

    /**
     * @return 距离鼠标点击位置最近的节点路径，通常用于模糊位置匹配
     */
    public TreePath clickedNearestPath() {
        return clickedNearestPath;
    }
}
