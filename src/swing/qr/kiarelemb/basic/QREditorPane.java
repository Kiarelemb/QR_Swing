package swing.qr.kiarelemb.basic;

import method.qr.kiarelemb.utils.QRStringUtils;
import swing.qr.kiarelemb.assembly.QRCaret;
import swing.qr.kiarelemb.assembly.QRHighlighter;
import swing.qr.kiarelemb.assembly.QRToolTip;
import swing.qr.kiarelemb.assembly.QRUndoManager;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.listener.add.*;
import swing.qr.kiarelemb.listener.*;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.utils.QRComponentUtils;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * QR Swing 的轻量富文本编辑组件。
 *
 * <p>该类基于 {@link JEditorPane}，能力介于 {@link QRTextArea} 和 {@link QRTextPane} 之间：
 * 支持通过 {@code print/println} 按字体、前景色、背景色插入文本，支持撤销重做、
 * 事件封装、滚动面板、主题刷新和自定义光标。相比 {@link QRTextPane}，本类不提供完整的
 * 行列索引、选区结束、文件拖入等高级能力。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QREditorPane logPane = new QREditorPane();
 * logPane.addUndoManager();
 * logPane.println("启动完成", QRColorsAndFonts.LIGHT_GREEN);
 * logPane.println("读取失败", QRColorsAndFonts.RED_NORMAL);
 * panel.add(logPane.addScrollPane());
 * </code></pre>
 *
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QREditorPane
 * @create 2024/7/12 下午10:51
 */
public class QREditorPane extends JEditorPane implements QRComponentUpdate, QRCaretListenerAdd, QRFocusListenerAdd, QRDocumentListenerAdd, QRKeyListenerAdd, QRMouseListenerAdd, QRMouseMotionListenerAdd {
    public Font textFont = QRColorsAndFonts.STANDARD_FONT_TEXT;
    /**
     * 使用前请先调用 {@link #addUndoManager()}
     */
    public QRUndoManager undoManager;
    public final QRCaret caret;
    protected boolean caretBlock = false;
    protected boolean lineWrap = true;
    protected QRScrollPane scrollPane;
    private QRCaretListener caretListener;
    private QRDocumentListener documentListener;
    private QRKeyListener keyListener;
    private QRMouseMotionListener mouseMotionListener;
    private QRMouseListener mouseListener;
    private QRFocusListener focusListener;

    public QREditorPane() {
        this.caret = new QRCaret();
        setCaret(this.caret);
        setMargin(new Insets(QRTextPane.INSECT, QRTextPane.INSECT, QRTextPane.INSECT, QRTextPane.INSECT));
        componentFresh();
    }

    //region 打印的方法

    /**
     * 在光标的当前位置插入该文字
     *
     * @param str 要插入的文字
     */
    public final void print(String str) {
        print(str, QRColorsAndFonts.TEXT_COLOR_FORE, QRColorsAndFonts.TEXT_COLOR_BACK, getDocument().getLength());
    }

    /**
     * 在末尾插入指定前景色文本。
     *
     * @param str       要插入的文本
     * @param colorFore 前景色
     */
    public final void print(String str, Color colorFore) {
        print(str, textFont, colorFore, QRColorsAndFonts.TEXT_COLOR_BACK, getDocument().getLength());
    }

    public final void print(String str, Color colorFore, int index) {
        print(str, textFont, colorFore, QRColorsAndFonts.TEXT_COLOR_BACK, index);
    }

    public final void print(String str, Color colorFore, Color colorBack) {
        print(str, textFont, colorFore, colorBack, getDocument().getLength());
    }

    /**
     * 在指定位置打印指定内容
     *
     * @param str   指定内容
     * @param index 指定位置
     */
    public final void print(String str, int index) {
        print(str, QRColorsAndFonts.TEXT_COLOR_FORE, QRColorsAndFonts.TEXT_COLOR_BACK, index);
    }

    public final void print(String str, Font f, int index) {
        print(str, f, QRColorsAndFonts.TEXT_COLOR_FORE, QRColorsAndFonts.TEXT_COLOR_BACK, index);
    }

    public final void print(String str, Color colorFore, Color colorBack, int index) {
        print(str, textFont, colorFore, colorBack, index);
    }

    public void print(String str, Font f, Color colorFore, int index) {
        print(str, f, colorFore, QRColorsAndFonts.TEXT_COLOR_BACK, index);
    }

    public void print(String str, Font f, Color colorFore, Color colorBack, int index) {
        SimpleAttributeSet attributeSet = QRComponentUtils.getSimpleAttributeSet(f, colorFore, colorBack);
        print(str, attributeSet, index);
    }

    /**
     * 在末尾插入带完整样式的文本。
     *
     * @param str 要插入的文本
     * @param sas 字体、颜色等样式属性
     */
    public final void print(String str, SimpleAttributeSet sas) {
        print(str, sas, getDocument().getLength());
    }

    /**
     * 在指定位置插入带完整样式的文本。
     *
     * <p>插入期间会临时设置 {@link #caretBlock}，避免子类光标更新逻辑在批量写入时反复触发。</p>
     *
     * @param str   要插入的文本
     * @param sas   字体、颜色等样式属性
     * @param index 插入位置
     */
    public void print(String str, SimpleAttributeSet sas, int index) {
        try {
            setCaretBlock();
            getDocument().insertString(index, str, sas);
        } catch (BadLocationException e) {
            e.printStackTrace();
        } finally {
            setCaretUnblock();
        }
    }

    /**
     * 在末尾插入文本并追加换行。
     *
     * @param str 要插入的文本
     */
    public final void println(String str) {
        print(str + QRStringUtils.AN_ENTER);
    }

    /**
     * 在指定位置打印指定内容
     *
     * @param str   指定内容
     * @param index 指定位置
     */
    public final void println(String str, int index) {
        println(str, QRColorsAndFonts.TEXT_COLOR_FORE, QRColorsAndFonts.TEXT_COLOR_BACK, index);
    }

    public final void println(String str, Font f, int index) {
        println(str, f, QRColorsAndFonts.TEXT_COLOR_FORE, QRColorsAndFonts.TEXT_COLOR_BACK, index);
    }

    public final void println(String str, Color colorFore) {
        println(str, colorFore, QRColorsAndFonts.TEXT_COLOR_BACK);
    }

    public final void println(String str, Color colorFore, Color colorBack) {
        println(str, textFont, colorFore, colorBack, getDocument().getLength());
    }

    public final void println(String str, Color colorFore, int index) {
        println(str, textFont, colorFore, QRColorsAndFonts.TEXT_COLOR_BACK, index);
    }

    public final void println(String str, Color colorFore, Color colorBack, int index) {
        println(str, textFont, colorFore, colorBack, index);
    }

    public final void println(String str, Font f, Color colorFore, int index) {
        print(str + QRStringUtils.AN_ENTER, f, colorFore, QRColorsAndFonts.TEXT_COLOR_BACK, index);
    }


    public final void println(String str, Font f, Color colorFore, Color colorBack, int index) {
        print(str + QRStringUtils.AN_ENTER, f, colorFore, colorBack, index);
    }

    public final void println(String str, SimpleAttributeSet sas, int index) {
        print(str + QRStringUtils.AN_ENTER, sas, index);
    }

    //endregion

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
            this.documentListener.add(QRDocumentListener.TYPE.INSERT, QREditorPane.this::insertUpdate);
            this.documentListener.add(QRDocumentListener.TYPE.REMOVE, QREditorPane.this::removeUpdate);
            this.documentListener.add(QRDocumentListener.TYPE.CHANGED, QREditorPane.this::changedUpdate);
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
     * 添加滚动条。
     *
     * <p>重复调用会返回同一个 {@link QRScrollPane} 实例，并默认开启每次滚动 3 行的平滑滚动。</p>
     *
     * @return 承载当前编辑器的滚动面板
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
     * 使编辑器能够撤销重做。
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

    //region 各种设置

    /**
     * 设置为不可编辑但保留文本光标外观。
     *
     * <p>适合只读日志、预览文本等场景：用户不能修改内容，但仍能看到编辑光标并进行选择/复制。</p>
     */
    public void setEditableFalseButCursorEdit() {
        setEditable(false);
        setCursorEdit();
        QREditorPane.this.caret.setVisible(true);
    }

    /**
     * 鼠标进入组件时自动请求焦点。
     */
    public void setEnterAutoFocus() {
        addMouseListener();
        addMouseAction(QRMouseListener.TYPE.ENTER, event -> requestFocus());
    }

    /**
     * 设置文本是否允许选择高亮。
     *
     * <p>关闭时会移除 highlighter，因此用户无法看到选择高亮。</p>
     *
     * @param selectable true 表示允许选择
     */
    public void setSelectable(boolean selectable) {
        if (selectable) {
            var highlighter = getHighlighter();
            if (highlighter == null) {
                highlighter = new QRHighlighter();
                setHighlighter(highlighter);
            }
        } else {
            setHighlighter(null);
        }
    }

    /**
     * 设置文本的换行方式。
     *
     * @param lineWrap true表示启用行包装，false表示禁用行包装。
     */
    public void setLineWrap(boolean lineWrap) {
        this.lineWrap = lineWrap;
    }

    //endregion

    //region 鼠标处理

    /**
     * 鼠标处理，Wait
     */
    public final void setCursorWait() {
        setCursor(QRTextPane.WAIT);
    }

    /**
     * 鼠标处理，edit
     */
    public final void setCursorEdit() {
        setCursor(QRTextPane.EDIT);
    }

    /**
     * 暂时阻止子类光标更新逻辑。
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

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return lineWrap;
    }

}
