package swing.qr.kiarelemb.inter.listener;

import swing.qr.kiarelemb.event.QRTreeExpansionEvent;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRTreeWillExpandLis
 * @description TODO
 * @create 2026/6/4 21:41
 */
public interface QRTreeWillExpandLis {
	void treeWillExpand(QRTreeExpansionEvent event);
	void treeWillCollapse(QRTreeExpansionEvent event);
}