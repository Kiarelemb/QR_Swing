package swing.qr.kiarelemb.basic;

import method.qr.kiarelemb.utils.QRSystemUtils;
import swing.qr.kiarelemb.assembly.QRTableCellRenderer;
import swing.qr.kiarelemb.assembly.QRTableHeaderRenderer;
import swing.qr.kiarelemb.assembly.QRToolTip;
import swing.qr.kiarelemb.combination.QRPopupMenu;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.listener.add.QRMouseListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseMotionListenerAdd;
import swing.qr.kiarelemb.listener.QRMouseListener;
import swing.qr.kiarelemb.listener.QRMouseMotionListener;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;

/**
 * QR Swing 的表格组件。
 *
 * <p>该类基于 {@link JTable}，统一了表头渲染、单元格居中渲染、主题刷新、
 * 鼠标事件注册、选择模式切换以及选中内容复制等常用表格能力。
 *
 * <p>默认支持多单元格选择，并绑定 {@code Ctrl + C} 将选中区域复制为 TSV 文本，
 * 可直接粘贴到表格软件或普通文本编辑器中。
 *
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRTable
 * @create 2026/6/6 18:55
 */
public class QRTable extends JTable implements QRComponentUpdate, QRMouseListenerAdd, QRMouseMotionListenerAdd {
    public enum QRTableSelectionMode {
        SINGLE_ROW,
        MULTIPLE_ROWS,
        SINGLE_CELL,
        MULTIPLE_CELLS,
        SINGLE_COLUMN,
        MULTIPLE_COLUMNS
    }

    protected QRScrollPane scrollPane;
    protected QRPopupMenu popupMenu;
    private final TableCellRenderer headerRenderer = new QRTableHeaderRenderer();
    private final TableCellRenderer cellRenderer = new QRTableCellRenderer();
    private QRMouseMotionListener mouseMotionListener;
    private QRMouseListener mouseListener;

    public QRTable() {
        super();
        personalize();
    }

    public QRTable(TableModel dm) {
        super(dm);
        personalize();
    }

    public QRTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
        personalize();
    }

    public QRTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
        personalize();
    }

    public QRTable(int numRows, int numColumns) {
        super(numRows, numColumns);
        personalize();
    }

    public QRTable(Vector<? extends Vector> rowData, Vector<?> columnNames) {
        super(rowData, columnNames);
        personalize();
    }

    public QRTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
        personalize();
    }

    /**
     * 初始化表格的默认视觉样式和基础行为。
     *
     * <p>该方法会设置行高、网格线、自动列宽、默认渲染器、默认选择模式、
     * 复制快捷键以及主题色。所有构造方法最终都会调用该方法。
     */
    protected void personalize() {
        setRowHeight(28);
        setShowGrid(true);
        setFillsViewportHeight(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        setIntercellSpacing(new Dimension(1, 1));
        setOpaque(true);
        JTableHeader header = getTableHeader();
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(headerRenderer);
        setDefaultCellRenderer();
        setQRTableSelectionMode(QRTableSelectionMode.MULTIPLE_CELLS);
        installCopyAction();
        componentFresh();
    }

    private void setDefaultCellRenderer() {
        setDefaultRenderer(Object.class, cellRenderer);
        setDefaultRenderer(String.class, cellRenderer);
        setDefaultRenderer(Number.class, cellRenderer);
        setDefaultRenderer(Integer.class, cellRenderer);
        setDefaultRenderer(Long.class, cellRenderer);
        setDefaultRenderer(Float.class, cellRenderer);
        setDefaultRenderer(Double.class, cellRenderer);
    }

    /**
     * 设置表格的选择模式。
     *
     * <p>该方法封装了 {@link JTable} 的行选择、列选择、单元格选择以及
     * {@link ListSelectionModel} 的选择区间模式，用于快速切换常见的表格选择行为。
     *
     * <p>支持的模式包括：
     * <ul>
     *     <li>{@link QRTableSelectionMode#SINGLE_ROW}：单行选择。</li>
     *     <li>{@link QRTableSelectionMode#MULTIPLE_ROWS}：多行选择，可选择多个不连续行。</li>
     *     <li>{@link QRTableSelectionMode#SINGLE_CELL}：单个单元格选择。</li>
     *     <li>{@link QRTableSelectionMode#MULTIPLE_CELLS}：多个单元格选择，可选择多个不连续区域。</li>
     *     <li>{@link QRTableSelectionMode#SINGLE_COLUMN}：单列选择。</li>
     *     <li>{@link QRTableSelectionMode#MULTIPLE_COLUMNS}：多列选择，可选择多个不连续列。</li>
     * </ul>
     *
     * @param mode 表格选择模式
     */
    public void setQRTableSelectionMode(QRTableSelectionMode mode) {
        switch (mode) {
            case SINGLE_ROW -> {
                setRowSelectionAllowed(true);
                setColumnSelectionAllowed(false);
                setCellSelectionEnabled(false);
                setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            }
            case MULTIPLE_ROWS -> {
                setRowSelectionAllowed(true);
                setColumnSelectionAllowed(false);
                setCellSelectionEnabled(false);
                setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            }
            case SINGLE_CELL -> {
                setCellSelectionEnabled(true);
                setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            }
            case MULTIPLE_CELLS -> {
                setCellSelectionEnabled(true);
                setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            }
            case SINGLE_COLUMN -> {
                setRowSelectionAllowed(false);
                setColumnSelectionAllowed(true);
                setCellSelectionEnabled(false);
                setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            }
            case MULTIPLE_COLUMNS -> {
                setRowSelectionAllowed(false);
                setColumnSelectionAllowed(true);
                setCellSelectionEnabled(false);
                setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            }
        }
    }

    /**
     * 将当前选中的表格内容复制到系统剪贴板。
     *
     * <p>复制内容使用 TSV 格式：列之间用制表符分隔，行之间使用系统换行符分隔。
     * 该格式可以直接粘贴到 Excel、WPS 表格或普通文本编辑器中。
     */
    public void copySelectionToClipboard() {
        String text = selectedText();
        if (!text.isEmpty()) {
            QRSystemUtils.putTextToClipboard(text);
        }
    }

    private void installCopyAction() {
        getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), "copySelection");
        getActionMap().put("copySelection", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                copySelectionToClipboard();
            }
        });
    }

    /**
     * 获取当前选中区域对应的 TSV 文本。
     *
     * <p>如果当前是整行选择，会复制所有列；如果当前是整列选择，会复制所有行；
     * 如果当前是单元格选择，则只复制选中的行列交叉区域。
     *
     * @return 当前选中内容的 TSV 文本；没有选中内容时返回空字符串
     */
    public String selectedText() {
        int[] rows = getSelectedRows();
        int[] columns = getSelectedColumns();
        if (rows.length == 0 && columns.length == 0) {
            return "";
        }
        if (rows.length == 0) {
            rows = allRows();
        }
        if (columns.length == 0) {
            columns = allColumns();
        }

        StringBuilder builder = new StringBuilder();
        for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
            if (rowIndex > 0) {
                builder.append(System.lineSeparator());
            }
            for (int columnIndex = 0; columnIndex < columns.length; columnIndex++) {
                if (columnIndex > 0) {
                    builder.append('\t');
                }
                Object value = getValueAt(rows[rowIndex], columns[columnIndex]);
                builder.append(value == null ? "" : value);
            }
        }
        return builder.toString();
    }

    private int[] allRows() {
        int[] rows = new int[getRowCount()];
        for (int i = 0; i < rows.length; i++) {
            rows[i] = i;
        }
        return rows;
    }

    private int[] allColumns() {
        int[] columns = new int[getColumnCount()];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = i;
        }
        return columns;
    }

    /**
     * 为表格创建并返回一个 {@link QRScrollPane}。
     *
     * <p>重复调用会返回同一个滚动面板实例。
     *
     * @return 承载当前表格的滚动面板
     */
    public QRScrollPane addScrollPane() {
        if (this.scrollPane == null) {
            this.scrollPane = new QRScrollPane();
            this.scrollPane.setViewportView(this);
        }
        return this.scrollPane;
    }

    /**
     * 为表格创建并绑定右键菜单。重复调用返回同一实例。
     *
     * @return 绑定当前表格的右键菜单
     */
    public QRPopupMenu addPopupMenu() {
        return addPopupMenu(null);
    }

    /**
     * 为表格创建并绑定右键菜单，并在显示前执行回调。
     *
     * <p>回调可用于动态更新菜单状态。只有首次创建菜单时传入的回调会被绑定。</p>
     *
     * @param beforeShow 菜单显示前的回调，可为 null
     * @return 绑定当前表格的右键菜单
     */
    public QRPopupMenu addPopupMenu(QRActionRegister<MouseEvent> beforeShow) {
        if (this.popupMenu == null) {
            this.popupMenu = QRPopupMenu.createAndBind(this, beforeShow);
        }
        return this.popupMenu;
    }

    @Override
    public final void addMouseMotionListener() {
        if (this.mouseMotionListener == null) {
            this.mouseMotionListener = new QRMouseMotionListener();
            this.mouseMotionListener.add(QRMouseMotionListener.TYPE.DRAG, this::mouseDrag);
            this.mouseMotionListener.add(QRMouseMotionListener.TYPE.MOVE, this::mouseMove);
            addMouseMotionListener(this.mouseMotionListener);
        }
    }

    @Override
    public final void addMouseMotionAction(QRMouseMotionListener.TYPE type, QRActionRegister<MouseEvent> ar) {
        if (this.mouseMotionListener == null) {
            addMouseMotionListener();
        }
        if (this.mouseMotionListener != null) {
            this.mouseMotionListener.add(type, ar);
        }
    }

    @Override
    public final void addMouseListener() {
        if (this.mouseListener == null) {
            this.mouseListener = new QRMouseListener();
            this.mouseListener.add(QRMouseListener.TYPE.CLICK, this::mouseClick);
            this.mouseListener.add(QRMouseListener.TYPE.PRESS, this::mousePress);
            this.mouseListener.add(QRMouseListener.TYPE.RELEASE, this::mouseRelease);
            this.mouseListener.add(QRMouseListener.TYPE.ENTER, this::mouseEnter);
            this.mouseListener.add(QRMouseListener.TYPE.EXIT, this::mouseExit);
            addMouseListener(this.mouseListener);
        }
    }

    @Override
    public final void addMouseAction(QRMouseListener.TYPE type, QRActionRegister<MouseEvent> ar) {
        if (this.mouseListener == null) {
            addMouseListener();
        }
        if (this.mouseListener != null) {
            this.mouseListener.add(type, ar);
        }
    }

    protected void mouseDrag(MouseEvent e) {
    }

    protected void mouseMove(MouseEvent e) {
    }

    protected void mouseClick(MouseEvent e) {
    }

    protected void mousePress(MouseEvent e) {
    }

    protected void mouseRelease(MouseEvent e) {
    }

    protected void mouseEnter(MouseEvent e) {
    }

    protected void mouseExit(MouseEvent e) {
    }

    public QRMouseMotionListener mouseMotionListener() {
        return mouseMotionListener;
    }

    public QRMouseListener mouseListener() {
        return mouseListener;
    }

    @Override
    public JToolTip createToolTip() {
        QRToolTip tip = new QRToolTip();
        tip.setComponent(tip);
        return tip;
    }

    @Override
    public void componentFresh() {
        setFont(QRColorsAndFonts.STANDARD_FONT_TEXT);
        setForeground(QRColorsAndFonts.TEXT_COLOR_FORE);
        setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
        setOpaque(true);
        setSelectionForeground(QRColorsAndFonts.TEXT_COLOR_FORE);
        setSelectionBackground(QRColorsAndFonts.PRESS_COLOR);
        setGridColor(QRColorsAndFonts.LINE_COLOR);

        JTableHeader header = getTableHeader();
        if (header != null) {
            header.setFont(QRColorsAndFonts.STANDARD_FONT_TEXT);
            header.setForeground(QRColorsAndFonts.TEXT_COLOR_FORE);
            header.setBackground(QRColorsAndFonts.TEXT_COLOR_BACK);
            header.setOpaque(true);
            header.setDefaultRenderer(headerRenderer);
            header.repaint();
        }
        setDefaultCellRenderer();
        if (this.scrollPane != null) {
            this.scrollPane.componentFresh();
        }

        repaint();
    }
}
