package swing.qr.kiarelemb.listener;

import swing.qr.kiarelemb.event.QRTreeNodeEvent;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.listener.QRTreeNodeLis;
import swing.qr.kiarelemb.utils.QRComponentUtils;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: {@link swing.qr.kiarelemb.basic.QRTree} 节点单击事件
 * @create 2022-11-27 14:06
 **/
public class QRTreeNodeClickListener extends QRAction<QRTreeNodeEvent> implements QRTreeNodeLis {
    private final Map<TreePath, ArrayList<QRActionRegister<QRTreeNodeEvent>>> precisionMap = new HashMap<>();
    private final Map<TreePath, ArrayList<QRActionRegister<QRTreeNodeEvent>>> vagueMap = new HashMap<>();

    @Override
    public final void nodeClicked(QRTreeNodeEvent e) {
        TreePath clickedPath = e.clickedPath();
        if (clickedPath != null) {
            runAction(precisionMap, clickedPath, e);
        } else {
            TreePath treePath = e.clickedNearestPath();
            runAction(vagueMap, treePath, e);
        }
    }

    /**
     * 添加节点的单击事件
     *
     * @param path  节点路径
     * @param ar    操作
     * @param vague 若为 {@code true}，则当不是恰好单击到节点，而是单击到其周围时，只要能获取到最近的节点，该操作就会执行。
     */
    public void add(TreePath path, QRActionRegister<QRTreeNodeEvent> ar, boolean vague) {
        {
            ArrayList<QRActionRegister<QRTreeNodeEvent>> list = precisionMap.computeIfAbsent(path, k -> new ArrayList<>());
            list.add(ar);
        }
        if (vague) {
            ArrayList<QRActionRegister<QRTreeNodeEvent>> list = vagueMap.computeIfAbsent(path, k -> new ArrayList<>());
            list.add(ar);
        }
    }

    @Deprecated
    @Override
    public final void add(QRActionRegister<QRTreeNodeEvent> ar) {
        throw new UnsupportedOperationException();
    }

    private void runAction(Map<TreePath, ArrayList<QRActionRegister<QRTreeNodeEvent>>> map, TreePath treePath, QRTreeNodeEvent e) {
        ArrayList<QRActionRegister<QRTreeNodeEvent>> list = map.get(treePath);
        QRComponentUtils.runActions(list, e);
    }
}