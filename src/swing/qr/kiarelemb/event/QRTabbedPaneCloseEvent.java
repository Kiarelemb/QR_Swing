package swing.qr.kiarelemb.event;

import swing.qr.kiarelemb.combination.QRTabbedPane;

import java.util.EventObject;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-27 14:40
 **/
public class QRTabbedPaneCloseEvent extends EventObject {

    private final QRTabbedPane tabbedPane;

    public QRTabbedPaneCloseEvent(QRTabbedPane tabbedPane) {
        super(tabbedPane);
        this.tabbedPane = tabbedPane;
    }

    public QRTabbedPane tabbedPane() {
        return tabbedPane;
    }
}