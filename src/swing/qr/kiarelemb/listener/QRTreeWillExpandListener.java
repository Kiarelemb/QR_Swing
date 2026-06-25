package swing.qr.kiarelemb.listener;

import swing.qr.kiarelemb.event.QRTreeExpansionEvent;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.listener.QRTreeWillExpandLis;
import swing.qr.kiarelemb.utils.QRComponentUtils;

import java.util.LinkedList;
import java.util.Objects;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRTreeWillExpandListener
 * @description TODO
 * @create 2026/6/4 21:37
 */
public class QRTreeWillExpandListener extends QRAction<QRTreeWillExpandListener> implements QRTreeWillExpandLis {
	public enum TYPE {
		EXPAND, COLLAPSE
	}

	private final LinkedList<QRActionRegister<QRTreeExpansionEvent>> expand = new LinkedList<>();
	private final LinkedList<QRActionRegister<QRTreeExpansionEvent>> collapse = new LinkedList<>();

	/**
	 * 添加节点的展开与收起事件
	 *
	 * @param ar   操作
	 */
	public void add(QRTreeWillExpandListener.TYPE type, QRActionRegister<QRTreeExpansionEvent> ar) {
		if (Objects.requireNonNull(type) == TYPE.EXPAND) {
			expand.add(ar);
		} else {
			collapse.add(ar);
		}
	}

	@Deprecated
	@Override
	public void add(QRActionRegister<QRTreeWillExpandListener> ar) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void treeWillExpand(QRTreeExpansionEvent e) {
		QRComponentUtils.runActions(this.expand, e);
	}

	@Override
	public void treeWillCollapse(QRTreeExpansionEvent e) {
		QRComponentUtils.runActions(this.collapse, e);
	}
}