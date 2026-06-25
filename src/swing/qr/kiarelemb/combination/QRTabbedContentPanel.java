package swing.qr.kiarelemb.combination;

import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.event.QRTabSelectEvent;

import java.awt.*;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 在 {@link QRTabbedPane} 的每个标签中，都有一个{@link QRTabbedContentPanel}
 * @create 2022-11-27 15:07
 **/
public class QRTabbedContentPanel extends QRPanel {
    public QRTabbedContentPanel() {
        super(false);
    }

    public QRTabbedContentPanel(LayoutManager layout) {
        super(false, layout);
    }

    /**
     * 当前标签选择后，将自动触发该事件。
     * <p>注意，该 {@link #thisTabSelectChangeAction(QRTabSelectEvent)} 事件的触发先于 {@link QRTabbedPane#tabSelectChangedAction(QRTabSelectEvent)}</p>
     */
    protected void thisTabSelectChangeAction(QRTabSelectEvent event) {
    }
}