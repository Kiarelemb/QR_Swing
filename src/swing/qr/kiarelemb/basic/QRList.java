package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.assembly.QRToolTip;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.listener.QRMouseListener;
import swing.qr.kiarelemb.listener.QRMouseMotionListener;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
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
public class QRList<T> extends JList<T> implements QRComponentUpdate {
    protected QRScrollPane scrollPane;
    protected final LinkedList<T> contents = new LinkedList<>();
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
    public QRList(T[] contents) {
        setContents(contents);
        personalize();
    }

    /**
     * 默认只能选择一个
     *
     * @param contents 列表内容
     */
    public QRList(List<T> contents) {
        setContents(contents);
        personalize();
    }

    public QRList(ListModel<T> dataModel) {
        super(dataModel);
        personalize();
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
        return getModel().getSize();
    }

    /**
     * @return 默认 {@link List} 的实例是 {@link LinkedList}
     */
    public List<T> contents() {
        return this.contents;
    }

    public void removeItem(int index) {
        this.contents.remove(index);
        contentUpdate();
    }

    public void removeItem(T item) {
        this.contents.remove(item);
        contentUpdate();
    }

    public int addItem(T item) {
        int size = this.contents.size();
        return addItem(size, item) ? size : -1;
    }

    public boolean addFirst(T item) {
        return addItem(0, item);
    }

    public boolean addItem(int index, T item) {
        if (this.noRepeat && this.contents.contains(item)) {
            return false;
        }
        this.contents.add(index, item);
        contentUpdate();
        return true;
    }

    public void setContents(T[] contents) {
        setContents(Arrays.asList(contents));
    }

    public void setContents(List<T> contents) {
        this.contents.clear();
        if (this.noRepeat) {
            for (T content : contents) {
                if (!this.contents.contains(content)) {
                    this.contents.add(content);
                }
            }
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
        setModel(new AbstractListModel<>() {
            @Override
            public int getSize() {
                return contents.size();
            }

            @Override
            public T getElementAt(int i) {
                return contents.get(i);
            }
        });
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        Container parent = getParent();
        return parent instanceof JViewport && parent.getHeight() > getPreferredSize().height;
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
     * 已自动添加 {@link #addMouseMotionListener()}
     *
     * @param type 类型
     * @param ar   操作
     */
    public final void addMouseMotionListener(QRMouseMotionListener.TYPE type, QRActionRegister<MouseEvent> ar) {
        if (this.mouseMotionListener == null) {
            addMouseMotionListener();
        }
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
     * 已自动添加 {@link #addMouseListener()}
     *
     * @param type 类型
     * @param ar   操作
     */
    public final void addMouseListener(QRMouseListener.TYPE type, QRActionRegister<MouseEvent> ar) {
        if (this.mouseListener == null) {
            addMouseListener();
        }
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
        if (this.scrollPane == null) {
            this.scrollPane = new QRScrollPane();
            this.scrollPane.setViewportView(this);
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

    //region 取得监听器

    public QRMouseMotionListener mouseMotionListener() {
        return mouseMotionListener;
    }

    public QRMouseListener mouseListener() {
        return mouseListener;
    }

    //endregion

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