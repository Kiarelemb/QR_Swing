package swing.qr.kiarelemb.combination;

import method.qr.kiarelemb.utils.QRFontUtils;
import method.qr.kiarelemb.utils.QRSystemUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.basic.QRMenuItem;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.window.basic.QREmptyDialog;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-04 17:25
 **/
public class QRPopupMenu extends QREmptyDialog  {
    protected final int vgap = 4;
    protected int itemNums;
    protected int itemMaxLen;
    protected int itemMaxTipLen;
    private final QRActionRegister<ActionEvent> actionRegister;

    public QRPopupMenu(Window parent) {
        super(parent, false);
        this.contentPane.setLayout(new GridLayout(0, 1, 3, this.vgap));
        addFocusListener();
        setFreelyMotionFailed();
        setFocusable(true);
        this.actionRegister = QRPopupMenu.this::buttonSelectAction;
    }

    /**
     * 子类重写其中被选重时的操作
     */
    protected void buttonSelectAction(ActionEvent event) {
    }

    public void addSeparator() {

    }

    /**
     * 用来当作菜单栏
     *
     * @param menuItem 添加的菜单按钮
     */
    public void add(QRMenuItem menuItem) {
        this.contentPane.add(menuItem);
        menuItem.addClickAction(this.actionRegister);
        this.itemNums++;
        int textInWidth = QRFontUtils.getTextInWidth(menuItem, menuItem.getText());
        if (menuItem.quickTip() != null) {
            this.itemMaxTipLen = Math.max(this.itemMaxTipLen, QRFontUtils.getTextInWidth(menuItem, menuItem.quickTip()));
        }
        this.itemMaxLen = Math.max(this.itemMaxLen, textInWidth);
    }

    public void add(QRButton button) {
        this.contentPane.add(button);
        button.addClickAction(this.actionRegister);
        this.itemNums++;
        int textInWidth = QRFontUtils.getTextInWidth(button, button.getText());
        this.itemMaxLen = Math.max(this.itemMaxLen, textInWidth);
    }

    public void show(Component invoker, int x, int y) {
        if (invoker != null) {
            Point invokerOrigin = invoker.getLocationOnScreen();
            x += invokerOrigin.x;
            y += invokerOrigin.y;
        }
        setLocation(x, y);
        setSize(this.itemMaxLen + this.itemMaxTipLen + 30, this.itemNums * (22 + this.vgap));
        if (QRSwing.windowRound) {
            QRSystemUtils.setWindowRound(this, QRSwing.windowTransparency);
        }
        super.setVisible(true);
    }
}