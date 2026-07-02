package swing.qr.kiarelemb.combination;

import method.qr.kiarelemb.utils.QRFontUtils;
import method.qr.kiarelemb.utils.QRSystemUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.basic.QRMenuItem;
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.window.basic.QREmptyDialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-04 17:25
 **/
public class QRPopupMenu extends QREmptyDialog  {
    protected final int vgap = 5;
    private static final int ITEM_HEIGHT = 25;
    private static final int MENU_PADDING = 6;
    protected int itemNums;
    protected int itemMaxLen;
    protected int itemMaxTipLen;
    private final QRPanel mainPanel;
    private final List<Integer> separatorIndexes;
    private final QRActionRegister<ActionEvent> actionRegister;

    public QRPopupMenu(Window parent) {
        super(parent, false);
        this.contentPane.setLayout(new BorderLayout());
        this.mainPanel = new PopupMainPanel();
        this.separatorIndexes = new ArrayList<>();
        this.contentPane.add(this.mainPanel, BorderLayout.CENTER);
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

    /**
     * 将当前菜单绑定到指定组件，组件触发系统右键菜单事件时显示该菜单。
     *
     * <p>同时监听 {@code mousePressed} 和 {@code mouseReleased}，用于兼容不同平台
     * 对 {@link MouseEvent#isPopupTrigger()} 的触发时机差异。</p>
     *
     * @param component 触发右键菜单的组件
     * @return 当前 {@link QRPopupMenu} 实例
     */
    public QRPopupMenu bind(Component component) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopupMenuIfNeeded(component, e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopupMenuIfNeeded(component, e);
            }
        });
        return this;
    }

    private void showPopupMenuIfNeeded(Component component, MouseEvent e) {
        if (e.isPopupTrigger()) {
            show(component, e.getX(), e.getY());
        }
    }

    public void addSeparator() {
        if (this.itemNums <= 0) {
            return;
        }
        int index = this.itemNums;
        if (!this.separatorIndexes.contains(index)) {
            this.separatorIndexes.add(index);
        }
        this.mainPanel.repaint();
    }

    /**
     * 用来当作菜单栏
     *
     * @param menuItem 添加的菜单按钮
     */
    public void add(QRMenuItem menuItem) {
        this.mainPanel.add(menuItem);
        menuItem.addClickAction(this.actionRegister);
        this.itemNums++;
        int textInWidth = QRFontUtils.getTextInWidth(menuItem, menuItem.getText());
        if (menuItem.quickTip() != null) {
            this.itemMaxTipLen = Math.max(this.itemMaxTipLen, QRFontUtils.getTextInWidth(menuItem, menuItem.quickTip()));
        }
        this.itemMaxLen = Math.max(this.itemMaxLen, textInWidth);
    }

    public void add(QRButton button) {
        this.mainPanel.add(button);
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
        int gapHeight = Math.max(0, this.itemNums - 1) * this.vgap;
        setSize(this.itemMaxLen + this.itemMaxTipLen + 30,
                this.itemNums * ITEM_HEIGHT + gapHeight + MENU_PADDING);
        if (QRSwing.windowRound) {
            QRSystemUtils.setWindowRound(this, QRSwing.windowTransparency);
        }
        super.setVisible(true);
    }

    private void paintSeparators(Graphics g) {
        if (this.separatorIndexes.isEmpty()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(QRColorsAndFonts.BORDER_COLOR);
        for (Integer index : this.separatorIndexes) {
            if (index <= 0 || index >= this.mainPanel.getComponentCount()) {
                continue;
            }
            Component prev = this.mainPanel.getComponent(index - 1);
            Component next = this.mainPanel.getComponent(index);
            int y = (prev.getY() + prev.getHeight() + next.getY()) / 2;
            g2.drawLine(0, y, this.mainPanel.getWidth() - 1, y);
        }
        g2.dispose();
    }

    private class PopupMainPanel extends QRPanel {
        private PopupMainPanel() {
            super(false, new GridLayout(0, 1, 3, QRPopupMenu.this.vgap));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            QRPopupMenu.this.paintSeparators(g);
        }
    }
}
