package swing.qr.kiarelemb.component.combination;

import method.qr.kiarelemb.utils.QRSystemUtils;
import swing.qr.kiarelemb.component.basic.QRButton;
import swing.qr.kiarelemb.component.basic.QRPanel;
import swing.qr.kiarelemb.component.listener.QRMouseListener;
import swing.qr.kiarelemb.inter.QRMenuButtonProcess;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-04 17:11
 **/
public class QRMenuPanel extends QRPanel {
    protected final LinkedList<QRButton> buttons;
    private final ArrayList<Boolean> enables;
    private final QRPanel buttonsPanel;
    private boolean pressed = false;
    private boolean autoExpend = false;
    private QRButton preClickedItem;

    /**
     * 实现一个菜单条，可以在其中加入菜单按钮
     */
    public QRMenuPanel() {
        super();
        this.buttonsPanel = new QRPanel();
        this.buttonsPanel.setLayout(new GridLayout(1, 0, 2, 0));
        setLayout(new BorderLayout());
        add(this.buttonsPanel, BorderLayout.WEST);
        addFocusListener();
        this.buttons = new LinkedList<>();
        this.enables = new ArrayList<>();
    }


    /**
     * 因为 Mac 系统和 Windows, Linux 用的菜单按钮不一样，所以用这方法可以去掉判断
     *
     * @param name 按钮名称
     * @return 菜单按钮
     */
    public QRButton add(String name) {
        QRButton button = QRSystemUtils.IS_OSX ? new QRMenuButtonOriginal(name, this) : new QRMenuButton(name, this);
        button.addMouseListener();
        button.addMouseAction(QRMouseListener.TYPE.PRESS, e -> mousePressAction(button));
        button.addMouseAction(QRMouseListener.TYPE.ENTER, e -> mouseEnterAction(button));
        buttonsPanel.add(button);
        buttons.add(button);
        return button;
    }

    public QRButton get(String name) {
        for (QRButton button : buttons) {
            if (Objects.equals(button.getText(), name)) {
                return button;
            }
        }
        return null;
    }

    private void mouseEnterAction(QRButton button) {
        if (pressed && button.isEnabled() || autoExpend) {
            ((QRMenuButtonProcess) button).showPopupMenu();
            if (preClickedItem != button) {
                if (preClickedItem != null) {
                    ((QRMenuButtonProcess) preClickedItem).closePopupMenu();
                }
                preClickedItem = button;
            }
        }
    }

    @Override
    protected void focusLost(FocusEvent e) {
        setPressed(false);
    }

    private void mousePressAction(QRButton button) {
        pressed = true;
        preClickedItem = button;
    }

    protected void setPressed(boolean b) {
        pressed = b;
        if (!b) {
            preClickedItem = null;
        }
    }

    public void disableAll() {
        for (QRButton item : this.buttons) {
            this.enables.add(item.isEnabled());
            item.setEnabled(false);
            ((QRMenuButtonProcess) this.preClickedItem).disableAll();
        }
    }

    public void enablesAll() {
        int index = 0;
        for (QRButton item : this.buttons) {
            item.setEnabled(this.enables.get(index++));
            ((QRMenuButtonProcess) this.preClickedItem).enablesAll();
        }
    }

    /**
     * 设置菜单按钮是否自动展开菜单栏
     */
    public void setAutoExpend(boolean autoExpend) {
        this.autoExpend = autoExpend;
    }
}