package swing.qr.kiarelemb.basic;

import method.qr.kiarelemb.utils.QRArrayUtils;
import method.qr.kiarelemb.utils.QRStringUtils;
import swing.qr.kiarelemb.assembly.QRToolTip;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.listener.QRMouseListener;
import swing.qr.kiarelemb.listener.QRMouseMotionListener;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-21 21:59
 **/
public class QRList extends JList<String> implements QRComponentUpdate {
    protected QRScrollPane scrollPane;
    protected final LinkedList<String> contents = new LinkedList<>();
    /**
     * 默认不可重
     */
    protected boolean noRepeat;
    private QRMouseMotionListener mouseMotionListener;
    private QRMouseListener mouseListener;

    /**
     * 默认只能选择一个
     */
    public QRList() {
        personalize();
    }

    /**
     * 默认只能选择一个
     *
     * @param contents 列表内容
     */
    public QRList(String[] contents) {
        setContents(contents);
        personalize();
    }

    /**
     * 默认只能选择一个
     *
     * @param contents 列表内容
     */
    public QRList(List<String> contents) {
        this(QRArrayUtils.stringListToArr(contents));
    }

    private void personalize() {
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        addListSelectionListener(this::listSelectedAction);
        componentFresh();
    }

    /**
     * 设置列表是否可以含有可重元素
     *
     * @param repeatable 为 {@code true} 则可重
     */
    public void setRepeatable(boolean repeatable) {
        this.noRepeat = !repeatable;
    }

    /**
     * 可直接重写
     */
    protected void listSelectedAction(ListSelectionEvent listSelectionEvent) {
    }

    public String getSelected() {
        return String.valueOf(getSelectedValue());
    }

    /**
     * 获取列表的大小
     *
     * @return 返回列表的大小
     */
    public int getListSize() {
        return this.contents.size();
    }

    /**
     * @return 默认 {@link List} 的实例是 {@link LinkedList}
     */
    public List<String> contents() {
        return this.contents;
    }

    public void removeItem(int index) {
        this.contents.remove(index);
        contentUpdate();
    }

    public void removeItem(String text) {
        this.contents.remove(text);
        contentUpdate();
    }

    public int addItem(String text) {
        int size = this.contents.size();
        return addItem(size, text) ? size : -1;
    }

    public boolean addFirst(String text) {
        return addItem(0, text);
    }

    public boolean addItem(int index, String text) {
        if (this.noRepeat && this.contents.contains(text)) {
            return false;
        }
        this.contents.add(index, text);
        contentUpdate();
        return true;
    }

    public void setContents(String[] contents) {
        setContents(Arrays.asList(contents));
    }

    public void setContents(List<String> contents) {
        this.contents.clear();
        if (this.noRepeat) {
            this.contents.addAll(QRStringUtils.getNoRepeatList(this.contents, contents));
        } else {
            this.contents.addAll(contents);
        }
        contentUpdate();
    }

    public void clear() {
        this.contents.clear();
        contentUpdate();
    }

    public final void contentUpdate() {
        final String[] contents = QRArrayUtils.stringListToArr(this.contents);
        setModel(new AbstractListModel<>() {
            @Override
            public int getSize() {
                return contents.length;
            }

            @Override
            public String getElementAt(int i) {
                return contents[i];
            }
        });
    }

    /**
     * 添加鼠标移动事件
     */
    public final void addMouseMotionListener() {
        if (this.mouseMotionListener == null) {
            this.mouseMotionListener = new QRMouseMotionListener();
            this.mouseMotionListener.add(QRMouseMotionListener.TYPE.DRAG, this::mouseDrag);
            this.mouseMotionListener.add(QRMouseMotionListener.TYPE.MOVE, this::mouseMove);
            addMouseMotionListener(this.mouseMotionListener);
        }
    }

    /**
     * 添加鼠标移动事件
     * 使用前请先调用 {@link #addMouseMotionListener()}
     *
     * @param type 类型
     * @param ar   操作
     */
    public final void addMouseMotionListener(QRMouseMotionListener.TYPE type, QRActionRegister<MouseEvent> ar) {
        if (this.mouseMotionListener != null) {
            this.mouseMotionListener.add(type, ar);
        }
    }

    /**
     * 添加鼠标事件
     */
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

    /**
     * 添加鼠标事件
     * 使用前请先调用 {@link #addMouseListener()}
     *
     * @param type 类型
     * @param ar   操作
     */
    public final void addMouseListener(QRMouseListener.TYPE type, QRActionRegister<MouseEvent> ar) {
        if (this.mouseListener != null) {
            this.mouseListener.add(type, ar);
        }
    }

    /**
     * 添加滚动条
     *
     * @return 滚动条本身
     */
    public JScrollPane addScrollPane() {
        int line = 20;
        return addScrollPane(line);
    }

    /**
     * 添加滚动条
     *
     * @param line 每次滚动条高度
     * @return 滚动条本身
     */
    public JScrollPane addScrollPane(int line) {
        if (this.scrollPane == null) {
            this.scrollPane = new QRScrollPane();
            this.scrollPane.setViewportView(this);
            this.scrollPane.setScrollSmoothly(line);
        }
        return this.scrollPane;
    }


    /**
     * 重写前请先调用 {@link #addMouseMotionListener()}
     */
    protected void mouseDrag(MouseEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addMouseMotionListener()}
     */
    protected void mouseMove(MouseEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addMouseListener()}
     */
    protected void mouseClick(MouseEvent e) {

    }

    /**
     * 重写前请先调用 {@link #addMouseListener()}
     */
    protected void mousePress(MouseEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addMouseListener()}
     */
    protected void mouseRelease(MouseEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addMouseListener()}
     */
    protected void mouseEnter(MouseEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addMouseListener()}
     */
    protected void mouseExit(MouseEvent e) {
    }

    @Override
    public JToolTip createToolTip() {
        QRToolTip tip = new QRToolTip();
        tip.setComponent(tip);
        return tip;
    }

    @Override
    public void componentFresh() {
        setFont(QRColorsAndFonts.DEFAULT_FONT_MENU);
        setForeground(QRColorsAndFonts.TEXT_COLOR_FORE);
        setBackground(QRColorsAndFonts.TEXT_COLOR_BACK);
        setSelectionBackground(QRColorsAndFonts.PRESS_COLOR);
    }
}