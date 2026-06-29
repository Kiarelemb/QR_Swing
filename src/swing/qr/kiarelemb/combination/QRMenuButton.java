package swing.qr.kiarelemb.combination;

import method.qr.kiarelemb.utils.QRFontUtils;
import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.basic.QRMenuItem;
import swing.qr.kiarelemb.inter.QRMenuButtonProcess;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 非 macOS 平台使用的菜单栏按钮。
 *
 * <p>该按钮内部使用 {@link QRPopupMenu} 承载 {@link QRMenuItem}。
 * 一般由 {@link QRMenuPanel#add(String)} 自动创建，调用方拿到返回的 {@link QRButton}
 * 后直接调用 {@link #add(QRMenuItem)} 添加菜单项。</p>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 适用于Windows系统的菜单栏按钮
 * @create 2022-11-04 17:18
 **/
public class QRMenuButton extends QRButton implements QRMenuButtonProcess {
    private final LinkedList<QRMenuItem> buttons;
    private final ArrayList<Boolean> enables;
    private final QRPopupMenu jpm;
    private final QRMenuPanel menuPanel;

    public QRMenuButton(String text, QRMenuPanel menuPanel) {
        super(text);
        this.menuPanel = menuPanel;

        this.jpm = new QRPopupMenu(SwingUtilities.getWindowAncestor(menuPanel)) {
            @Override
            public void focusGain(FocusEvent e) {
                QRMenuButton.this.setBackground(QRColorsAndFonts.PRESS_COLOR);
            }

            @Override
            public void focusLose(FocusEvent e) {
                super.focusLose(e);
                QRMenuButton.this.setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
            }
        };
        setPreferredSize(new Dimension(QRFontUtils.getTextInWidth(this, text) + 20, 32));
        this.buttons = new LinkedList<>();
        this.enables = new ArrayList<>();
        addMouseListener();
    }

    @Override
    protected final void mousePress(MouseEvent e) {
        press(e);
    }

    private void press(MouseEvent e) {
        if (isEnabled()) {
            switch (e.getButton()) {
                case MouseEvent.BUTTON1 -> showPopupMenu();
                case MouseEvent.BUTTON3 -> e.consume();
            }
        }
    }

    /**
     * 显示下拉菜单。
     */
    @Override
    public void showPopupMenu() {
        this.jpm.show(this.menuPanel, getX(), getY() + getHeight());
    }

    @Override
    public void componentFresh() {
        super.componentFresh();
        setFont(QRColorsAndFonts.MENU_ITEM_DEFAULT_FONT);
        setForeground(QRColorsAndFonts.MENU_COLOR);
        if (this.jpm != null) {
            this.jpm.componentFresh();
        }
    }

    /**
     * 添加菜单项。
     *
     * <p>菜单项点击后会自动关闭弹出菜单，并重置所属菜单面板的按压状态。</p>
     *
     * @param qmi 菜单项
     */
    @Override
    public void add(QRMenuItem qmi) {
        this.jpm.add(qmi);
        this.buttons.add(qmi);
        qmi.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                qmi.componentFresh();
                closePopupMenu();
                menuPanel.setPressed(false);
            }
        });
    }

    /**
     * 禁用当前菜单中的所有菜单项，并记录原启用状态。
     */
    @Override
    public void disableAll() {
        for (QRMenuItem item : this.buttons) {
            this.enables.add(item.isEnabled());
            item.setEnabled(false);
        }
    }

    /**
     * 恢复 {@link #disableAll()} 前记录的菜单项启用状态。
     */
    @Override
    public void enablesAll() {
        int index = 0;
        for (QRMenuItem item : this.buttons) {
            item.setEnabled(this.enables.get(index++));
        }
    }

    /**
     * 添加菜单分隔线。
     */
    public void addSeparator() {
        this.jpm.addSeparator();
    }

    /**
     * 关闭弹出菜单。
     */
    @Override
    public void closePopupMenu() {
        this.jpm.setVisible(false);
    }
}
