package swing.qr.kiarelemb.combination;

import method.qr.kiarelemb.utils.QRFontUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.basic.QRScrollPane;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.utils.QRComponentUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 一个可收缩伸展、宽度固定的纵向控件，在窗体的左侧
 * @create 2023-01-15 20:40
 **/
public class QRContractiblePanel extends QRPanel {
    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;
    private final ArrayList<QRColumnPanel> columns = new ArrayList<>() {
        @Override
        public int indexOf(Object o) {
            if (o instanceof QRColumnContentPanel) {
                for (int i = 0, thisSize = this.size(); i < thisSize; i++) {
                    QRColumnPanel c = this.get(i);
                    if (c.contentPanel == o) {
                        return i;
                    }
                }
                return -1;
            } else if (o instanceof String) {
                for (int i = 0, thisSize = this.size(); i < thisSize; i++) {
                    QRColumnPanel c = this.get(i);
                    if (c.name.equals(o)) {
                        return i;
                    }
                }
                return -1;
            }
            return super.indexOf(o);

        }
    };

    /**
     * 推荐的宽度是 300
     */
    private final Dimension DEFAULT_DIMENSION;

    private static final int GAP = 5;
    /**
     * 用于保存各栏的状态，其中 {@code 0} 为关闭状态， {@code 1} 为打开状态
     */
    private final Map<Integer, Integer> columnMap = new TreeMap<>(Integer::compareTo);
    private final QRPanel mainPanel;
    private final QRScrollPane scrollPane;
    private final int position;

    public QRContractiblePanel() {
        this(300);
    }

    public QRContractiblePanel(int defaultWidth) {
        this(defaultWidth, LEFT);
    }

    public QRContractiblePanel(int defaultWidth, int position) {
        super(false);
        DEFAULT_DIMENSION = new Dimension(defaultWidth, 300);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(DEFAULT_DIMENSION.width + 12, DEFAULT_DIMENSION.height));
        this.position = position;
        if (position == RIGHT) {
            setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, QRColorsAndFonts.LINE_COLOR));
        } else if (position == CENTER) {
            setBorder(BorderFactory.createMatteBorder(0, 2, 0, 2, QRColorsAndFonts.LINE_COLOR));
        } else {
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, QRColorsAndFonts.LINE_COLOR));
        }
        this.mainPanel = new QRPanel(false);
        this.mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT, GAP, GAP));
        this.mainPanel.setBorder(BorderFactory.createEmptyBorder(GAP, 0, GAP, GAP));
        this.mainPanel.setPreferredSize(new Dimension(DEFAULT_DIMENSION.width - 10, DEFAULT_DIMENSION.height));
        scrollPane = this.mainPanel.addScrollPane();
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * @param columnName 该栏的名字
     * @return 该栏的内容面板。布局为 {@code null}。默认的返回类是 {@link QRPanel} 的子类 {@link QRColumnContentPanel}，其默认高度为 {@code 100 }，可以在内容面板中放任何控件
     */
    public QRColumnContentPanel addColumn(String columnName) {
        QRColumnPanel column = new QRColumnPanel(columnName, this.columns.size());
        return addColumn(column);
    }

    /**
     * @param columnName         该栏的名字
     * @param contentPanelHeight 设置高度
     * @return 该栏的内容面板。布局为 {@code null}。默认的返回类是 {@link QRPanel} 的子类 {@link QRColumnContentPanel}，可以在内容面板中放任何控件
     */
    public QRColumnContentPanel addColumn(String columnName, int contentPanelHeight) {
        QRColumnPanel column = new QRColumnPanel(columnName, this.columns.size(), contentPanelHeight);
        return addColumn(column);
    }

    /**
     * @param index 索引
     * @return 取得 {@link QRColumnContentPanel}
     */
    public QRColumnContentPanel getColumn(int index) {
        if (index < 0 || index >= columns.size()) {
            return null;
        }
        return columns.get(index).contentPanel;
    }

    public QRColumnContentPanel getColumn(String columnName) {
        int index = getColumnIndex(columnName);
        if (index == -1) {
            return null;
        }
        return columns.get(index).contentPanel;
    }


    public int getColumnIndex(QRColumnContentPanel column) {
        return columns.indexOf(column);
    }

    public int getColumnIndex(String columnName) {
        return columns.indexOf(columnName);
    }

    private QRColumnContentPanel addColumn(QRColumnPanel column) {
        columnMap.put(column.index, 0);
        this.mainPanel.add(column);
        this.columns.add(column);
        this.mainPanel.setPreferredSize(new Dimension(DEFAULT_DIMENSION.width - 10, this.columns.size() * QRColumnPanel.COLUMN_HEIGHT + (this.columns.size() + 1) * GAP));
        return column.contentPanel;
    }

    public void expandColumn(QRColumnPanel column) {
        int index = column.index;
        Set<Map.Entry<Integer, Integer>> set = columnMap.entrySet();
        for (Map.Entry<Integer, Integer> entry : set) {
            Integer col = entry.getKey();
            if (col == column.index) {
                break;
            }
            Integer state = entry.getValue();
            if (state == 1) {
                index++;
            }
        }
        this.mainPanel.add(column.contentPanel, index + 1);
        update(column, true);
        columnMap.put(column.index, 1);
    }

    public void collapseColumn(QRColumnPanel column) {
//		if (ArrayUtils.indexOf(this.mainPanel.getComponents(), column.contentPanel) != -1) {
        this.mainPanel.remove(column.contentPanel);
        update(column, false);
        columnMap.put(column.index, 0);
//		}
    }

    private void update(QRColumnPanel column, boolean insert) {
        int value = column.contentPanel.getPreferredSize().height + GAP * 2;
        int height = insert ? value : -value;
        Dimension size = this.mainPanel.getPreferredSize();
        this.mainPanel.setPreferredSize(new Dimension(size.width, size.height + height));
        this.scrollPane.revalidate();
        this.scrollPane.repaint();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, QRSwing.windowImageSet ? 0.5f : 1f));
        super.paintBorder(g);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    @Override
    public void componentFresh() {
        super.componentFresh();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(GAP, 0, GAP, GAP));
        if (position == RIGHT) {
            setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, QRColorsAndFonts.LINE_COLOR));
        } else if (position == CENTER) {
            setBorder(BorderFactory.createMatteBorder(0, 2, 0, 2, QRColorsAndFonts.LINE_COLOR));
        } else {
            setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, QRColorsAndFonts.LINE_COLOR));
        }
    }

    public class QRColumnPanel extends QRPanel {
        public final QRColumnContentPanel contentPanel;
        private final String name;
        /**
         * 用于判断是否已展开，默认为折叠
         */
        private boolean fold = true;
        private final int index;
        private static final int COLUMN_HEIGHT = 40;
        private final int[] xRange = new int[2];
        private static final int[] Y_RANGE = {8, 32};
        private boolean collapsable = true;
        private final ArrayList<QRActionRegister> actionList = new ArrayList<>();

        private QRColumnPanel(String name, int index) {
            this(name, index, new QRColumnContentPanel());
        }

        private QRColumnPanel(String name, int index, int contentPanelHeight) {
            this(name, index, new QRColumnContentPanel(contentPanelHeight));
        }

        /**
         * 传入的 {@link QRPanel} 请设置好 {@link QRPanel#setPreferredSize(Dimension)}
         *
         * @param name         面板名称
         * @param contentPanel 内容面板
         */
        private QRColumnPanel(String name, int index, QRColumnContentPanel contentPanel) {
            super(false);
            contentPanel.setColumn(this);
            contentPanel.setPreferredSize(new Dimension(DEFAULT_DIMENSION.width - GAP * 2, contentPanel.getPreferredSize().height));
            this.name = name;
            this.index = index;
            this.contentPanel = contentPanel;
            setPreferredSize(new Dimension(DEFAULT_DIMENSION.width - GAP * 2 - 2, COLUMN_HEIGHT));
            setBorder(BorderFactory.createLineBorder(QRColorsAndFonts.LINE_COLOR, 1, true));
            Rectangle r = QRFontUtils.getStringBounds(name, QRColorsAndFonts.STANDARD_FONT_TEXT).getBounds();
            this.xRange[0] = 15;
            this.xRange[1] = 30 + r.width;
            addMouseListener();
            addMouseMotionListener();

        }

        /**
         * 若想在窗体显示前就打开，则请一定要添加完所有的 {@link QRContractiblePanel#addColumn(String)} 后再设置，否则会有 bug
         *
         * @param fold 是否折叠
         */
        public void setFold(boolean fold) {
            if (!collapsable) {
                fold = false;
            }
            if (this.fold != fold) {
                this.fold = fold;
                if (this.fold) {
                    collapseColumn(this);
                } else {
                    expandColumn(this);
                }
                QRComponentUtils.runActions(actionList, this.fold);
            }
        }

        public boolean fold() {
            return fold;
        }

        public String name() {
            return name;
        }

        /**
         * 当将此栏打开或关闭时可进行的操作
         *
         * @param ar 操作，其 {@link QRActionRegister#action(Object)} 的参数为操作后的 {@link QRColumnPanel#fold}
         */
        public final void addFoldAction(QRActionRegister ar) {
            actionList.add(ar);
        }

        public void setCollapsable(boolean collapsable) {
            this.collapsable = collapsable;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            super.paintComponent(g);
            g2.setColor(QRColorsAndFonts.CARET_COLOR);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.fillRect(15, 8, 5, 24);
            g2.setFont(QRColorsAndFonts.STANDARD_FONT_TEXT);
            g2.setColor(QRColorsAndFonts.DEFAULT_COLOR_LABEL);
            g2.drawString(this.name, 30, 28);
        }

        @Override
        protected void mouseClick(MouseEvent e) {
            if (mouseInRange(e.getPoint())) {
                setFold(!this.fold);
            }
        }

        @Override
        protected void mouseMove(MouseEvent e) {
            mouseCursorProcess(e.getPoint());
        }

        private void mouseCursorProcess(Point p) {
            if (mouseInRange(p)) {
                setCursorHand();
            } else {
                setCursorDefault();
            }
        }

        private boolean mouseInRange(Point p) {
            int x = p.x;
            int y = p.y;
            return x > this.xRange[0] && x < this.xRange[1] && y > Y_RANGE[0] && y < Y_RANGE[1];
        }

        @Override
        public void componentFresh() {
            super.componentFresh();
            setBorder(BorderFactory.createLineBorder(QRColorsAndFonts.LINE_COLOR, 1, true));
            contentPanel.componentFresh();
        }
    }

    public class QRColumnContentPanel extends QRPanel {
        private QRColumnPanel column;

        private QRColumnContentPanel() {
            this(100);
        }

        private QRColumnContentPanel(int height) {
            super(false, null);
            setPreferredSize(new Dimension(DEFAULT_DIMENSION.width - GAP * 2, height));
        }

        public QRColumnPanel column() {
            return column;
        }

        private void setColumn(QRColumnPanel column) {
            this.column = column;
        }

    }
}