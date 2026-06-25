package swing.qr.kiarelemb.listener;

import method.qr.kiarelemb.utils.QRTimeCountUtil;
import swing.qr.kiarelemb.event.QRItemEvent;
import swing.qr.kiarelemb.inter.listener.QRItemLis;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-12-10 21:28
 **/
public class QRItemListener extends QRAction<QRItemEvent> implements QRItemLis {
    private final QRTimeCountUtil qcu = new QRTimeCountUtil((short) 100);

    @Override
    public final void itemChangedAction(QRItemEvent event) {
        // 用于去除重复触发的事件
        if (qcu.isPassedMmTime()) {
            qcu.startTimeUpdate();
            action(event);
        }
    }
}