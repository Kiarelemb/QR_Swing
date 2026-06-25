package swing.qr.kiarelemb.combination;

import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.inter.QRActionRegister;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-26 22:39
 **/
public class QRPopupItem extends QRButton {
    private final int index;

    public QRPopupItem(int index, String text) {
        super(text);
        this.index = index;
        addMouseListener();
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setHorizontalAlignment(SwingConstants.LEFT);
    }

    public QRPopupItem(int index, String text, QRActionRegister<ActionEvent> ar) {
        this(index, text);
        addClickAction(ar);
    }

    public int index() {
        return index;
    }
}