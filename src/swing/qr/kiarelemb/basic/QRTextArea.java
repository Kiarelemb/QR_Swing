package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.assembly.QRCaret;
import swing.qr.kiarelemb.assembly.QRToolTip;
import swing.qr.kiarelemb.assembly.QRUndoManager;
import swing.qr.kiarelemb.combination.QRPopupMenu;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.listener.add.*;
import swing.qr.kiarelemb.listener.*;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * QR Swing 的多行纯文本输入组件。
 *
 * <p>该类基于 {@link JTextArea}，统一了主题字体和颜色、自定义光标、撤销重做、
 * 光标/文档/键盘/鼠标/焦点事件封装，以及 {@link QRScrollPane} 接入能力。
 * 如果需要富文本样式、行列定位、选区结束事件或文件拖入，请使用 {@link QRTextPane}；
 * 如果只需要普通多行输入，优先使用本类。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRTextArea area = new QRTextArea(true);
 * area.addUndoManager();
 * area.addDocumentListenerActionAll(event -> updatePreview(area.getText()));
 * panel.add(area.addScrollPane());
 * </code></pre>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2023-01-11 15:11
 **/
public class QRTextArea extends JTextArea implements QRComponentUpdate, QRCaretListenerAdd, QRFocusListenerAdd, QRDocumentListenerAdd, QRKeyListenerAdd, QRMouseListenerAdd, QRMouseMotionListenerAdd {
    public Font textFont = QRColorsAndFonts.STANDARD_FONT_TEXT;
    /**
     * 使用前请先调用 {@link #addUndoManager()}
     */
    public QRUndoManager undoManager;
    public final QRCaret caret;
    protected boolean caretBlock = false;
    protected QRScrollPane scrollPane;
    protected QRPopupMenu popupMenu;
    private QRCaretListener caretListener;
    private QRDocumentListener documentListener;
    private QRKeyListener keyListener;
    private QRMouseMotionListener mouseMotionListener;
    private QRMouseListener mouseListener;
    private QRFocusListener focusListener;

    public QRTextArea() {
        this.caret = new QRCaret();
        setCaret(this.caret);
        setMargin(new Insets(QRTextPane.INSECT, QRTextPane.INSECT, QRTextPane.INSECT, QRTextPane.INSECT));
        componentFresh();
    }

    public QRTextArea(boolean lineWrap) {
        this();
        setLineWrap(lineWrap);
    }

    //region 各种添加

    /**
     * 添加鼠标位置更新事件
     */
    @Override
    public final void addCaretListener() {
        if (this.caretListener == null) {
            this.caretListener = new QRCaretListener();
            addCaretListener(this.caretListener);
        }
    }

    /**
     * 添加光标事件
     * 已自动添加 {@link #addCaretListener()}
     *
     * @param ar 操作
     */
    @Override
    public final void addCaretListenerAction(QRActionRegister<CaretEvent> ar) {
        if (this.caretListener == null) {
            addCaretListener();
        }
        if (this.caretListener != null) {
            this.caretListener.add(ar);
        }
    }

    /**
     * 添加文本内容更新事件
     */
    @Override
    public final void addDocumentListener() {
        if (this.documentListener == null) {
            this.documentListener = new QRDocumentListener();
            this.documentListener.add(QRDocumentListener.TYPE.INSERT, QRTextArea.this::insertUpdate);
            this.documentListener.add(QRDocumentListener.TYPE.REMOVE, QRTextArea.this::removeUpdate);
            this.documentListener.add(QRDocumentListener.TYPE.CHANGED, QRTextArea.this::changedUpdate);
            getDocument().addDocumentListener(this.documentListener);
        }
    }

    /**
     * 添加文本事件
     * 已自动添加 {@link #addDocumentListener()}
     *
     * @param type 类型
     * @param ar   操作
     */
    @Override
    public final void addDocumentListenerAction(QRDocumentListener.TYPE type, QRActionRegister<DocumentEvent> ar) {
        if (this.documentListener == null) {
            addDocumentListener();
        }
        if (this.documentListener != null) {
            this.documentListener.add(type, ar);
        }
    }

    /**
     * 给INSERT、REMOVE、CHANGED 一键添加文本事件
     * 已自动添加 {@link #addDocumentListener()}
     *
     * @param ar 操作
     */
    @Override
    public final void addDocumentListenerActionAll(QRActionRegister<DocumentEvent> ar) {
        if (this.documentListener == null) {
            addDocumentListener();
        }
        if (this.documentListener != null) {
            this.documentListener.add(QRDocumentListener.TYPE.INSERT, ar);
            this.documentListener.add(QRDocumentListener.TYPE.REMOVE, ar);
            this.documentListener.add(QRDocumentListener.TYPE.CHANGED, ar);
        }
    }

    /**
     * 添加按键的事件
     */
    @Override
    public final void addKeyListener() {
        if (this.keyListener == null) {
            this.keyListener = new QRKeyListener();
            this.keyListener.add(QRKeyListener.TYPE.TYPE, this::keyType);
            this.keyListener.add(QRKeyListener.TYPE.PRESS, this::keyPress);
            this.keyListener.add(QRKeyListener.TYPE.RELEASE, this::keyRelease);
            addKeyListener(this.keyListener);

        }
    }

    /**
     * 添加按键事件
     * 已自动添加 {@link #addKeyListener()}
     *
     * @param type 类型
     * @param ar   操作
     */
    @Override
    public final void addKeyListenerAction(QRKeyListener.TYPE type, QRActionRegister<KeyEvent> ar) {
        addKeyListenerAction(type, ar, (Object[]) null);
    }

    /**
     * 添加按键过滤事件。
     * <p>已自动添加 {@link #addKeyListener()}，按键参数规则见 {@link QRKeyListener#add(QRKeyListener.TYPE, QRActionRegister, Object...)}。</p>
     *
     * @param type 类型
     * @param ar   操作
     * @param keys 按键过滤条件
     */
    @Override
    public final void addKeyListenerAction(QRKeyListener.TYPE type, QRActionRegister<KeyEvent> ar, Object... keys) {
        if (this.keyListener == null) {
            addKeyListener();
        }
        if (this.keyListener != null) {
            this.keyListener.add(type, ar, keys);
        }
    }

    /**
     * 添加鼠标移动事件
     */
    @Override
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
    @Override
    public final void addMouseMotionAction(QRMouseMotionListener.TYPE type, QRActionRegister<MouseEvent> ar) {
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

    /**
     * 添加鼠标事件
     * 已自动添加 {@link #addMouseListener()}
     *
     * @param type 类型
     * @param ar   操作
     */
    @Override
    public final void addMouseAction(QRMouseListener.TYPE type, QRActionRegister<MouseEvent> ar) {
        if (this.mouseListener == null) {
            addMouseListener();
        }
        if (this.mouseListener != null) {
            this.mouseListener.add(type, ar);
        }
    }

    /**
     * 添加焦点事件
     */
    @Override
    public final void addFocusListener() {
        if (this.focusListener == null) {
            this.focusListener = new QRFocusListener();
            this.focusListener.add(QRFocusListener.TYPE.GAIN, this::focusGain);
            this.focusListener.add(QRFocusListener.TYPE.LOST, this::focusLose);
            addFocusListener(this.focusListener);

        }
    }

    /**
     * 添加焦点事件
     * 已自动添加 {@link #addFocusListener()}
     *
     * @param type 类型
     * @param ar   操作
     */
    @Override
    public final void addFocusAction(QRFocusListener.TYPE type, QRActionRegister<FocusEvent> ar) {
        if (this.focusListener == null) {
            addFocusListener();
        }
        if (this.focusListener != null) {
            this.focusListener.add(type, ar);
        }
    }

    /**
     * 添加滚动条
     *
     * <p>重复调用会返回同一个 {@link QRScrollPane} 实例，并默认开启每次滚动 3 行的平滑滚动。</p>
     *
     * @return 承载当前文本域的滚动面板
     */
    public QRScrollPane addScrollPane() {
        if (this.scrollPane == null) {
            this.scrollPane = new QRScrollPane();
            this.scrollPane.setViewportView(this);
            this.scrollPane.setScrollSmoothly(3);
        }
        return this.scrollPane;
    }

    /**
     * 为文本域创建并绑定右键菜单。重复调用返回同一实例。
     *
     * @return 绑定当前文本域的右键菜单
     */
    public QRPopupMenu addPopupMenu() {
        return addPopupMenu(null);
    }

    /**
     * 为文本域创建并绑定右键菜单，并在显示前执行回调。
     *
     * <p>回调可用于动态更新菜单状态。只有首次创建菜单时传入的回调会被绑定。</p>
     *
     * @param beforeShow 菜单显示前的回调，可为 null
     * @return 绑定当前文本域的右键菜单
     */
    public QRPopupMenu addPopupMenu(QRActionRegister<MouseEvent> beforeShow) {
        if (this.popupMenu == null) {
            this.popupMenu = QRPopupMenu.createAndBind(this, beforeShow);
        }
        return this.popupMenu;
    }

    /**
     * 使文本框能够撤销重做。
     *
     * <p>调用后会创建 {@link #undoManager}，并自动为当前文档绑定 Ctrl+Z/Ctrl+Y。</p>
     */
    public void addUndoManager() {
        this.undoManager = new QRUndoManager(this);
    }

    //endregion

    //region 推荐重写的方法

    /**
     * 重写前请先调用 {@link #addCaretListener()}
     */
    protected void caretUpdate(CaretEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addDocumentListener()}
     */
    protected void insertUpdate(DocumentEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addDocumentListener()}
     */
    protected void removeUpdate(DocumentEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addDocumentListener()}
     */
    protected void changedUpdate(DocumentEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addKeyListener()}
     */
    protected void keyPress(KeyEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addKeyListener()}
     */
    protected void keyType(KeyEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addKeyListener()}
     */
    protected void keyRelease(KeyEvent e) {
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

    /**
     * 重写前请先调用 {@link #addFocusListener()}
     */
    protected void focusGain(FocusEvent e) {

    }

    /**
     * 重写前请先调用 {@link #addFocusListener()}
     */
    protected void focusLose(FocusEvent e) {

    }

    /**
     * 清除全部文本内容。
     */
    public void clear() {
        setText("");
    }

    protected void cutAction() {
        super.cut();
    }

    protected void copyAction() {
        super.copy();
    }

    protected void pasteAction() {
        super.paste();
    }
    //endregion

    //region 上级方法
    @Override
    public final void cut() {
        cutAction();
    }

    @Override
    public final void copy() {
        copyAction();
    }

    @Override
    public final void paste() {
        pasteAction();
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        textFont = font;
        if (this.caret != null) {
            this.caret.setFont(font).update();
        }
    }

    @Override
    public JToolTip createToolTip() {
        QRToolTip tip = new QRToolTip();
        tip.setComponent(tip);
        return tip;
    }

    @Override
    public void componentFresh() {
        setFont(textFont);
        caret.setCaretColor(QRColorsAndFonts.CARET_COLOR);
        setForeground(QRColorsAndFonts.TEXT_COLOR_FORE);
        setBackground(QRColorsAndFonts.TEXT_COLOR_BACK);
    }

    //endregion

    //region 取得监听器

    public QRCaretListener caretListener() {
        return caretListener;
    }

    public QRDocumentListener documentListener() {
        return documentListener;
    }

    public QRKeyListener keyListener() {
        return keyListener;
    }

    public QRMouseMotionListener mouseMotionListener() {
        return mouseMotionListener;
    }

    public QRMouseListener mouseListener() {
        return mouseListener;
    }

    public QRFocusListener focusListener() {
        return focusListener;
    }

    //endregion

    /**
     * 暂时阻止子类光标更新逻辑。
     *
     * <p>批量修改文本或程序化移动光标时可设置该标记，避免自定义 {@link #caretUpdate(CaretEvent)}
     * 中的逻辑反复执行。修改结束后应调用 {@link #setCaretUnblock()}。</p>
     */
    public final void setCaretBlock() {
        this.caretBlock = true;
    }

    /**
     * 恢复光标更新逻辑。
     */
    public final void setCaretUnblock() {
        this.caretBlock = false;
    }
}
