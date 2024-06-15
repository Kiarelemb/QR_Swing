package swing.qr.kiarelemb.component.combination;


import method.qr.kiarelemb.utils.QRFontUtils;
import swing.qr.kiarelemb.component.QRComponentUtils;
import swing.qr.kiarelemb.component.basic.QRLabel;
import swing.qr.kiarelemb.component.basic.QRPanel;
import swing.qr.kiarelemb.component.event.QRTabSelectEvent;
import swing.qr.kiarelemb.component.event.QRTabbedPaneCloseEvent;
import swing.qr.kiarelemb.component.listener.QRTabCloseListener;
import swing.qr.kiarelemb.component.listener.QRTabSelectChangedListener;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-27 14:27
 **/
public class QRTabbedPane extends QRPanel {

    private final ArrayList<QRTabPanel> arrTabs;
    /**
     * 用于存放tabPane的Panel
     */
    private final QRPanel tabs;
    private final String tabPositionFromBorderLayout;
    private int selectedIndex = -1;
    private boolean loadCloseButton = false;
    private QRTabPanel selectedTab;
    private QRTabCloseListener closeButtonActionListener;
    private ActionListener actionListener;

    private final QRTabSelectChangedListener tabSelectChangedListener = new QRTabSelectChangedListener();

    private Font tabFont = QRColorsAndFonts.MENU_ITEM_DEFAULT_FONT;

    /**
     * 创建一个默认标签显示在上方的标签面板
     */
    public QRTabbedPane() {
        this(BorderLayout.NORTH);
    }

    /**
     * 创建一个标签显示可自定义的标签面板
     *
     * @param tabPositionFromBorderLayout 标签与中心内容的位置，值从 {@link BorderLayout#NORTH}, {@link BorderLayout#SOUTH},
     *                                    {@link BorderLayout#WEST} 和 {@link BorderLayout#EAST} 中选择
     */
    public QRTabbedPane(String tabPositionFromBorderLayout) {
        this(tabPositionFromBorderLayout, FlowLayout.LEFT);
    }

    /**
     * 创建一个标签显示可自定义的标签面板
     *
     * @param tabPositionFromBorderLayout 标签与中心内容的位置，值从 {@link BorderLayout#NORTH}, {@link BorderLayout#SOUTH},
     *                                    {@link BorderLayout#WEST} 和 {@link BorderLayout#EAST} 中选择
     * @param tabsPositionFromFlowLayout  标签在标签列中的对齐，值从 {@link FlowLayout#LEFT}, {@link FlowLayout#CENTER}  和 {@link FlowLayout#RIGHT}  中选择
     */
    public QRTabbedPane(String tabPositionFromBorderLayout, int tabsPositionFromFlowLayout) {
        super(false);
        this.arrTabs = new ArrayList<>();
        this.tabPositionFromBorderLayout = tabPositionFromBorderLayout;
        this.tabs = new QRPanel() {
            @Override
            protected void mouseMove(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
            }
        };
        this.tabs.addMouseMotionListener();
        int tabsPosition = Math.min(Math.max(tabsPositionFromFlowLayout, 0), 2);
        switch (tabPositionFromBorderLayout) {
            case BorderLayout.SOUTH -> {
                this.tabs.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, QRColorsAndFonts.LINE_COLOR));
                this.tabs.setLayout(new FlowLayout(tabsPosition, 0, 0));
            }
            case BorderLayout.EAST -> {
                this.tabs.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, QRColorsAndFonts.LINE_COLOR));
                this.tabs.setLayout(new GridLayout(0, 1, 0, 0));
            }
            case BorderLayout.WEST -> {
                this.tabs.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, QRColorsAndFonts.LINE_COLOR));
                this.tabs.setLayout(new GridLayout(0, 1, 0, 0));
            }
            default -> {
                tabPositionFromBorderLayout = BorderLayout.NORTH;
                this.tabs.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, QRColorsAndFonts.LINE_COLOR));
                this.tabs.setLayout(new FlowLayout(tabsPosition, 0, 0));
            }
        }
        setLayout(new BorderLayout());
        add(this.tabs, tabPositionFromBorderLayout);
        this.tabSelectChangedListener.add(e -> tabSelectChangedAction((QRTabSelectEvent) e));
    }


    /**
     * 传入 {@link QRActionRegister#action(Object)} 的是 {@link QRTabSelectEvent}
     */
    public void addTabSelectChangedAction(QRActionRegister ar) {
        this.tabSelectChangedListener.add(ar);
    }

    protected void tabSelectChangedAction(QRTabSelectEvent event) {

    }

    public void addTabCloseButton() {
        this.loadCloseButton = true;
        this.closeButtonActionListener = new QRTabCloseListener();
        this.closeButtonActionListener.add(e -> closeButtonAction((QRTabbedPaneCloseEvent) e));
        this.actionListener = e -> QRTabbedPane.this.closeButtonActionListener.tabCloseButtonAction(new QRTabbedPaneCloseEvent(QRTabbedPane.this));
    }

    /**
     * 设置当前欲显示的面板索引
     *
     * @param index 从 {@code 0} 开始计数
     */
    public void setSelectedTab(int index) {
        setContentPaneIndex(index);
    }

    private void setContentPaneIndex(int index) {
        if (this.selectedIndex != index) {
            QRTabPanel tab = this.arrTabs.get(index);
            if (tab != null) {
                this.selectedTab = tab;
                setContentPane(this.selectedTab);
            }
        }
    }

    /**
     * 在添加 Tab 前设置
     *
     * @param tabTitleFont 标签统一的字体设置
     */
    public void setTabTitleFont(Font tabTitleFont) {
        this.tabFont = tabTitleFont;
    }

    public int getContentPaneIndex() {
        return getSelectedTabIndex();
    }

    public int getSelectedTabIndex() {
        return this.selectedIndex;
    }

    public QRTabPanel getSelectedTab() {
        return this.selectedTab;
    }

    public int getTabSize() {
        return this.arrTabs.size();
    }

    public int addTab(String title, QRTabbedContentPanel content) {
        return addTab(title, null, content);
    }

    /**
     * 添加一个标签，并设置它的图标
     *
     * @param title   标签名
     * @param image   图片
     * @param content 内容面板
     * @return 添加的标签的索引
     */
    public int addTab(String title, ImageIcon image, QRTabbedContentPanel content) {
        int index = this.arrTabs.size();
        QRTabPanel tabPane = new QRTabPanel(title, content, index, image, this.loadCloseButton);
        tabPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                QRTabbedPane.this.setContentPane(tabPane);
                QRComponentUtils.windowFresh(QRTabbedPane.this);
            }
        });
        if (this.loadCloseButton) {
            tabPane.setCloseButtonAction(this.actionListener);
        }
        this.tabs.add(tabPane, index);
        this.arrTabs.add(index, tabPane);
        return index;
    }

    /**
     * 关闭按钮的事件
     */
    protected void closeButtonAction(QRTabbedPaneCloseEvent e) {

    }

    private void setContentPane(QRTabPanel tabPanel) {
        Component[] components = getComponents();
        if (components != null) {
            for (Component c : components) {
                if (c instanceof QRTabbedContentPanel) {
                    remove(c);
                }
            }
        }
        int foreSelectedIndex = this.selectedIndex;
        if (this.selectedIndex != -1) {
            this.arrTabs.get(this.selectedIndex).setClicked(false);
        }
        tabPanel.setClicked(true);
        this.selectedIndex = tabPanel.getIndex();
        add(tabPanel.getContentPane(), BorderLayout.CENTER);
        QRTabSelectEvent selectEvent = new QRTabSelectEvent(foreSelectedIndex, this.selectedIndex, tabPanel.content);
        tabPanel.content.thisTabSelectChangeAction(selectEvent);
        this.tabSelectChangedListener.tabSelectChangeAction(selectEvent);
        this.revalidate();
        repaint();
    }

    public QRTabbedContentPanel getContentPanel(int index) {
        if (this.arrTabs.size() <= index || index < 0) {
            throw new IndexOutOfBoundsException(index);
        }
        return this.arrTabs.get(index).getContentPane();
    }

    /**
     * @author Kiarelemb QR
     * @program: QR_Swing
     * @description:
     * @create 2022-11-27 14:30
     **/
    class QRTabPanel extends QRPanel {
        private final QRTabbedContentPanel content;
        private final String title;
        private QRLabel iconLabel;
        private int index;
        private QRTabCloseButton closeButton;

        QRTabPanel(String title, QRTabbedContentPanel content, int index, boolean loadCloseButton) {
            super(false);
            this.content = content;
            this.index = index;
            setFocusable(false);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, QRColorsAndFonts.FRAME_COLOR_BACK));
            this.title = title;
            Rectangle bounds = QRFontUtils.getStringBounds(title, QRColorsAndFonts.DEFAULT_FONT_MENU).getBounds();
            setPreferredSize(bounds.width + 10, bounds.height + 20);
            if (loadCloseButton) {
                this.closeButton = new QRTabCloseButton();
                add(this.closeButton, BorderLayout.EAST);
            }
        }

        QRTabPanel(String title, QRTabbedContentPanel content, int index, ImageIcon image, boolean loadCloseButton) {
            this(title, content, index, loadCloseButton);
            setIconImage(image);
        }

        public void setIconImage(ImageIcon image) {
            if (image != null && this.iconLabel == null) {
                this.iconLabel = new QRLabel();
                this.iconLabel.setIcon(image);
                add(this.iconLabel, BorderLayout.WEST);
            }
        }

        public void setCloseButtonAction(ActionListener action) {
            if (this.closeButton != null) {
                this.closeButton.addActionListener(action);
            }
        }

        @Override
        protected void paintBorder(Graphics g) {
            super.paintBorder(g);
            QRComponentUtils.componentStringDraw(this, g, this.title, QRColorsAndFonts.DEFAULT_FONT_MENU, QRColorsAndFonts.TEXT_COLOR_FORE);
        }

        public int getIndex() {
            return this.index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public void setClicked(boolean clicked) {
            if (clicked) {
                setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, QRColorsAndFonts.CARET_COLOR));
                switch (tabPositionFromBorderLayout) {
                    case BorderLayout.SOUTH ->
                            setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, QRColorsAndFonts.CARET_COLOR));
                    case BorderLayout.EAST ->
                            setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, QRColorsAndFonts.CARET_COLOR));
                    case BorderLayout.WEST ->
                            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 3, QRColorsAndFonts.CARET_COLOR));
                    default -> setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, QRColorsAndFonts.CARET_COLOR));
                }
            } else {
                switch (tabPositionFromBorderLayout) {
                    case BorderLayout.SOUTH ->
                            setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, QRColorsAndFonts.FRAME_COLOR_BACK));
                    case BorderLayout.EAST ->
                            setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, QRColorsAndFonts.FRAME_COLOR_BACK));
                    case BorderLayout.WEST ->
                            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, QRColorsAndFonts.FRAME_COLOR_BACK));
                    default ->
                            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, QRColorsAndFonts.FRAME_COLOR_BACK));
                }
            }


        }

        public QRTabbedContentPanel getContentPane() {
            return this.content;
        }

        /**
         * 关闭按钮
         */
        static class QRTabCloseButton extends JButton {
            private QRTabCloseButton() {
                final int size = 12;
                setPreferredSize(new Dimension(size, size));
                setToolTipText("关闭");
                setUI(new BasicButtonUI());
                setContentAreaFilled(false);
                setFocusable(false);
                setBorderPainted(false);
                setRolloverEnabled(true);
            }

            @Override
            public void updateUI() {
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();

                if (getModel().isPressed()) {
                    g2.translate(1, 1);
                }
                g2.setStroke(new BasicStroke(2));
                if (getModel().isRollover()) {
                    g2.setColor(Color.RED);
                } else {
                    g2.setColor(QRColorsAndFonts.TEXT_COLOR_FORE);
                }
                int delta = 3;
                int add = (getHeight() - 14) / 2;
                g2.drawLine(delta, add + delta, 14 - delta - 1, add + 14 - delta - 1);
                g2.drawLine(14 - delta - 1, add + delta, delta, add + 14 - delta - 1);
                g2.dispose();
            }
        }
    }
}