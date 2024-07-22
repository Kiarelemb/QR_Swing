package swing.qr.kiarelemb.inter;

import swing.qr.kiarelemb.data.QRInternalScrollBarData;

public interface QRInternalScrollbarUpdate {
    QRInternalScrollBarData getScrollBarData();

    /**
     * 各滚动条数据更新
     */
    void scrollBarValueUpdate();
}