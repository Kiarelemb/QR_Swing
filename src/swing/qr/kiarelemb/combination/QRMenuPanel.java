package swing.qr.kiarelemb.combination;

import method.qr.kiarelemb.utils.QRSystemUtils;
import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.inter.QRMenuButtonProcess;
import swing.qr.kiarelemb.listener.QRMouseListener;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

/**
 * QR Swing 标题栏菜单面板。
 *
 * <p>该组件用于承载多个菜单按钮。调用 {@link #add(String)} 时会根据当前系统自动创建合适的
 * 菜单按钮实现：macOS 使用 {@link QRMenuButtonOriginal}，其他系统使用 {@link QRMenuButton}。
 * 因此调用方通常不需要关心平台差异。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRMenuPanel menu = new QRMenuPanel();
 * QRButton file = menu.add("文件");
 * file.add(new QRMenuItem("打开"));
 * file.add(new QRMenuItem("保存"));
 * menu.setAutoExpend(true);
 * </code></pre>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
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
        super(false, new BorderLayout());
        this.buttonsPanel = new QRPanel(false, new GridLayout(1, 0, 2, 0));
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
        QRButton button = QRSystemUtils.IS_OSX ? new QRMenuButtonOriginal(name, this)
                : new QRMenuButton(name, this);
        button.addMouseListener();
        button.addMouseAction(QRMouseListener.TYPE.PRESS, e -> mousePressAction(button));
        button.addMouseAction(QRMouseListener.TYPE.ENTER, e -> mouseEnterAction(button));
        buttonsPanel.add(button);
        buttons.add(button);
        return button;
    }

    /**
     * 按名称查找菜单按钮。
     *
     * @param name 按钮文本
     * @return 匹配的按钮，未找到时为 null
     */
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

    /**
     * 暂时禁用所有菜单按钮和当前展开菜单中的菜单项。
     *
     * <p>会记录禁用前状态，稍后可调用 {@link #enablesAll()} 恢复。</p>
     */
    public void disableAll() {
        for (QRButton item : this.buttons) {
            this.enables.add(item.isEnabled());
            item.setEnabled(false);
            ((QRMenuButtonProcess) this.preClickedItem).disableAll();
        }
    }

    /**
     * 恢复 {@link #disableAll()} 前记录的启用状态。
     */
    public void enablesAll() {
        int index = 0;
        for (QRButton item : this.buttons) {
            item.setEnabled(this.enables.get(index++));
            ((QRMenuButtonProcess) this.preClickedItem).enablesAll();
        }
    }

    /**
     * 设置鼠标经过菜单按钮时是否自动展开菜单。
     *
     * @param autoExpend true 表示鼠标进入按钮就展开菜单
     */
    public void setAutoExpend(boolean autoExpend) {
        this.autoExpend = autoExpend;
    }
}
