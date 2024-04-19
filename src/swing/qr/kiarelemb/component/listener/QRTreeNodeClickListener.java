package swing.qr.kiarelemb.component.listener;

import swing.qr.kiarelemb.component.QRComponentUtils;
import swing.qr.kiarelemb.component.event.QRTreeNodeEvent;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.listener.QRTreeNodeLis;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: {@link swing.qr.kiarelemb.component.basic.QRTree} 节点单击事件
 * @create 2022-11-27 14:06
 **/
public class QRTreeNodeClickListener extends QRAction implements QRTreeNodeLis {
	private final Map<TreePath, ArrayList<QRActionRegister>> precisionMap = new HashMap<>();
	private final Map<TreePath, ArrayList<QRActionRegister>> vagueMap = new HashMap<>();

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
	 * @param path 节点路径
	 * @param ar 操作
	 * @param vague 若为 {@code true}，则当不是恰好单击到节点，而是单击到其周围时，只要能获取到最近的节点，该操作就会执行。
	 */
	public void add(TreePath path, QRActionRegister ar, boolean vague) {
		{
			ArrayList<QRActionRegister> list = precisionMap.computeIfAbsent(path, k -> new ArrayList<>());
			list.add(ar);
		}
		if (vague) {
			ArrayList<QRActionRegister> list = vagueMap.computeIfAbsent(path, k -> new ArrayList<>());
			list.add(ar);
		}
	}

	@Deprecated
	@Override
	public final void add(QRActionRegister ar) {
		throw new UnsupportedOperationException();
	}

	private void runAction(Map<TreePath, ArrayList<QRActionRegister>> map, TreePath treePath, QRTreeNodeEvent e) {
		ArrayList<QRActionRegister> list = map.get(treePath);
		QRComponentUtils.runActions(list, e);
	}
}