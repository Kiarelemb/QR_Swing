package swing.qr.kiarelemb.component.combination;

import method.qr.kiarelemb.utils.QRFontUtils;
import swing.qr.kiarelemb.component.basic.QRList;
import swing.qr.kiarelemb.component.basic.QRPanel;
import swing.qr.kiarelemb.component.event.QRTabSelectEvent;
import swing.qr.kiarelemb.component.listener.QRMouseListener;
import swing.qr.kiarelemb.component.listener.QRTabSelectChangedListener;
import swing.qr.kiarelemb.inter.QRActionRegister;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRListTabbedPane
 * @create 2024/5/4 下午1:08
 */
public class QRListTabbedPane extends QRPanel {
    protected final QRList list;
    protected final String listPositionFromBorderLayout;
    protected final ArrayList<QRTabbedContentPanel> panels = new ArrayList<>();
    private int maxLength = 0;
    private final QRTabSelectChangedListener tabSelectChangedListener = new QRTabSelectChangedListener();
    private final AtomicReference<QRTabbedContentPanel> current = new AtomicReference<>();
    private int selectedIndex = -1;

    /**
     * 创建一个QRListTabbedPane对象。
     * 使用默认的 {@link QRList} 对象和 {@link BorderLayout#WEST} 作为构造函数的参数。
     */
    public QRListTabbedPane() {
        this(new QRList(), BorderLayout.WEST);
    }

    public QRListTabbedPane(String[] listArray) {
        this(new QRList(listArray), BorderLayout.WEST);
    }

    public QRListTabbedPane(QRList alist) {
        this(alist, BorderLayout.WEST);
    }

    public QRListTabbedPane(QRList alist, String listPositionFromBorderLayout) {
        super(false);
        this.list = alist;
        this.listPositionFromBorderLayout = listPositionFromBorderLayout;
        this.setLayout(new BorderLayout(10, 10));
        this.add(this.list.addScrollPane(), listPositionFromBorderLayout);
        this.list.addMouseListener();

        this.list.addMouseListener(QRMouseListener.TYPE.CLICK, e -> {
            selectedIndex = list.getSelectedIndex();
            if (selectedIndex == -1) {
                return;
            }
            setSelectedTab(selectedIndex);
        });

        List<String> contents = this.list.contents();
        int height = 0;
        if (!contents.isEmpty()) {
            for (String content : contents) {
                int width = QRFontUtils.getTextInWidth(this.list, content);
                if (width > maxLength) {
                    maxLength = width;
                }
            }
            Rectangle2D bounds = QRFontUtils.getStringBounds(contents.get(0), this.list.getFont());
            height = (int) (bounds.getHeight() * contents.size());
            setSelectedTab(0);
        }
        this.list.setPreferredSize(new Dimension(maxLength + 20, height));
        this.tabSelectChangedListener.add(e -> tabSelectChangedAction((QRTabSelectEvent) e));
        this.setPreferredSize(0, height);
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    public QRListTabbedPane(QRList alist, QRTabbedContentPanel[] panels) {
        this(alist, panels, BorderLayout.WEST);
    }

    public QRListTabbedPane(QRList alist, QRTabbedContentPanel[] panels, String listPositionFromBorderLayout) {
        this(alist, listPositionFromBorderLayout);
        this.panels.addAll(Arrays.asList(panels));
    }

    /**
     * 通过 {@link QRList} 中的标题向当前 {@link QRListTabbedPane} 中添加一个 {@link QRPanel}。
     *
     * @param title 向 {@link QRList} 中添加的标题
     * @return 添加到 {@link QRListTabbedPane} 中的 {@link QRPanel}
     */
    public QRTabbedContentPanel addPanel(String title) {
        QRTabbedContentPanel panel = new QRTabbedContentPanel();
        addPanel(title, panel);
        return panel;
    }

    /**
     * 向当前 {@link QRListTabbedPane} 中添加一个 {@link QRPanel}，同时将 {@code title} 添加到 {@link QRList} 中。
     *
     * @param title 向 {@link QRList} 中添加的标题
     * @param panel 添加到 {@link QRListTabbedPane} 中的 {@link QRPanel}
     */
    public void addPanel(String title, QRTabbedContentPanel panel) {
        int index = list.addItem(title);
        int width = QRFontUtils.getTextInWidth(list, title);
        if (width > maxLength) {
            maxLength = width;
            list.setPreferredSize(new Dimension(maxLength + 10, 0));
        }
        addPanel(index, panel);
    }

    /**
     * 在指定索引位置添加面板。
     *
     * @param index 面板的索引位置
     * @param panel 要添加的面板
     * @throws IllegalArgumentException 若索引位置超过 {@link QRList} 内容的范围，则抛出此异常
     */
    public void addPanel(int index, QRTabbedContentPanel panel) {
        boundCheck(index);
        panels.add(index, panel);
    }

    /**
     * 传入 {@link QRActionRegister#action(Object)} 的是 {@link QRTabSelectEvent}
     */
    public void addTabSelectChangedAction(QRActionRegister ar) {
        this.tabSelectChangedListener.add(ar);
    }

    /**
     * 设置选中的标签页
     *
     * @param selectedIndex 选中标签页的索引
     * @throws IndexOutOfBoundsException 如果传入的索引超出范围，则抛出此异常
     */
    public void setSelectedTab(int selectedIndex) {
        boundCheck(selectedIndex);
        QRTabbedContentPanel tabbedContentPanel = panels.get(selectedIndex);
        int beforeIndex = -1;
        QRTabbedContentPanel before = current.get();
        if (before != null) {
            beforeIndex = panels.indexOf(before);
            this.remove(before);
        }
        current.set(tabbedContentPanel);
        // 更新 list 的选中索引
        if (list.getSelectedIndex() != selectedIndex) list.setSelectedIndex(selectedIndex);
        // 更新选重索引
        this.selectedIndex = selectedIndex;
        // 更新 tabbedContentPanel 的内容
        this.add(tabbedContentPanel, BorderLayout.CENTER);
        QRTabSelectEvent tabSelectEvent = new QRTabSelectEvent(beforeIndex, selectedIndex, tabbedContentPanel);
        tabbedContentPanel.thisTabSelectChangeAction(tabSelectEvent);
        this.tabSelectChangedListener.tabSelectChangeAction(tabSelectEvent);
    }

    /**
     * 可直接重写
     *
     * @param event 参数是 {@link QRTabSelectEvent}
     */
    protected void tabSelectChangedAction(QRTabSelectEvent event) {

    }

    public QRList getList() {
        return list;
    }

    public int selectedIndex() {
        return selectedIndex;
    }

    private void boundCheck(int index) {
        if (index < 0 || index > list.getListSize()) {
            throw new IndexOutOfBoundsException(index);
        }
    }
}