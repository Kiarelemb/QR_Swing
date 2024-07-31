package swing.qr.kiarelemb.listener;

import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.utils.QRComponentUtils;

import java.io.Serializable;
import java.util.LinkedList;

public class QRAction<T> implements Serializable {
    private final LinkedList<QRActionRegister<T>> list = new LinkedList<>();

    public void add(QRActionRegister<T> ar) {
        list.add(ar);
    }

    public boolean remove(QRActionRegister<T> ar) {
        return list.remove(ar);
    }

    protected void action(T e) {
        QRComponentUtils.runActions(list, e);
    }
}