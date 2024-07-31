package swing.qr.kiarelemb.basic;

import method.qr.kiarelemb.utils.QRStringUtils;
import swing.qr.kiarelemb.assembly.QRCaret;
import swing.qr.kiarelemb.assembly.QRToolTip;
import swing.qr.kiarelemb.assembly.QRUndoManager;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.QRTextBasicActionSetting;
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
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-30 14:36
 **/
public class QRTextField extends JTextField implements QRComponentUpdate, QRTextBasicActionSetting, QRCaretListenerAdd, QRFocusListenerAdd,
        QRDocumentListenerAdd, QRKeyListenerAdd, QRMouseListenerAdd, QRMouseMotionListenerAdd {
    private final StringBuilder forbiddenInputChar = new StringBuilder();
    private final StringBuilder onlyAllowedInputChar = new StringBuilder();
    protected Color enterColor = QRColorsAndFonts.BLUE_LIGHT;
    protected Color rightColor = QRColorsAndFonts.LIGHT_GREEN;
    protected Color errorColor = QRColorsAndFonts.RED_NORMAL;

    /**
     * 使用前请先调用 {@link #addUndoManager()}
     */
    public QRUndoManager undoManager;
    private QRDocumentListener documentListener;
    private QRCaretListener caretListener;
    private QRFocusListener focusListener;
    private QRKeyListener keyListener;
    private QRMouseListener mouseListener;
    private QRMouseMotionListener mouseMotionListener;

    public QRTextField() {
        this(null);
    }

    public QRTextField(String text) {
        this(text, false);
    }

    public QRTextField(String text, boolean numbersOnly) {
        if (numbersOnly) {
            numbersOnly();
        }
        setText(text);
        setCaret(new QRCaret());
        addKeyListener();
        addFocusListener();
        setIgnoreRepaint(true);
        componentFresh();
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
     *
     * @param ar 操作
     */
    @Override
    public final void addCaretListenerAction(QRActionRegister<CaretEvent> ar) {
        if (this.caretListener != null) {
            this.caretListener.add(ar);
        }
    }


    /**
     * 添加焦点事件，已自动添加
     */
    @Override
    public final void addFocusListener() {
        if (this.focusListener == null) {
            this.focusListener = new QRFocusListener();
            this.focusListener.add(QRFocusListener.TYPE.GAIN, this::focusGained);
            this.focusListener.add(QRFocusListener.TYPE.LOST, this::focusLost);
            addFocusListener(this.focusListener);
        }
    }

    /**
     * 添加焦点事件
     *
     * @param type 类型
     * @param ar   操作
     */
    @Override
    public final void addFocusAction(QRFocusListener.TYPE type, QRActionRegister<FocusEvent> ar) {
        if (this.focusListener != null) {
            this.focusListener.add(type, ar);
        }
    }

    /**
     * 添加文本内容更新事件
     */
    @Override
    public final void addDocumentListener() {
        if (this.documentListener == null) {
            this.documentListener = new QRDocumentListener();
            this.documentListener.add(QRDocumentListener.TYPE.INSERT, this::insertUpdate);
            this.documentListener.add(QRDocumentListener.TYPE.REMOVE, this::removeUpdate);
            this.documentListener.add(QRDocumentListener.TYPE.CHANGED, this::changedUpdate);
            getDocument().addDocumentListener(this.documentListener);
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
     *
     * @param type 类型
     * @param ar   操作
     */
    @Override
    public final void addMouseMotionAction(QRMouseMotionListener.TYPE type, QRActionRegister<MouseEvent> ar) {
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
     *
     * @param type 类型
     * @param ar   操作
     */
    @Override
    public final void addMouseAction(QRMouseListener.TYPE type, QRActionRegister<MouseEvent> ar) {
        if (this.mouseListener != null) {
            this.mouseListener.add(type, ar);
        }
    }

    /**
     * 添加文本事件
     *
     * @param type 类型
     * @param ar   操作
     */
    @Override
    public final void addDocumentListenerAction(QRDocumentListener.TYPE type, QRActionRegister<DocumentEvent> ar) {
        if (this.documentListener != null) {
            this.documentListener.add(type, ar);
        }
    }

    /**
     * 添加按键的事件，已自动添加
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
     *
     * @param type 类型
     * @param ar   操作
     */
    @Override
    public final void addKeyListenerAction(QRKeyListener.TYPE type, QRActionRegister<KeyEvent> ar) {
        if (this.keyListener != null) {
            this.keyListener.add(type, ar);
        }
    }

    /**
     * 使文本框能够撤销重做
     */
    public void addUndoManager() {
        this.undoManager = new QRUndoManager(this);
    }
    //endregion

    public final boolean isOkay() {
        return meetCondition();
    }

    //region 边框设置

    /**
     * 获得焦点时的边框
     */
    protected void setEnterBorder() {
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, this.enterColor));
    }

    /**
     * 内容为空时的边框
     */
    protected void setEmptyBorder() {
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.YELLOW));
    }

    /**
     * 符合条件的边框
     */
    protected void setRightBorder() {
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, this.rightColor));
    }

    /**
     * 不符合条件的边框
     */
    protected void setErrorBorder() {
        setBorder(BorderFactory.createLineBorder(this.errorColor, 1));
    }
    //endregion 边框设置

    //region 输入限制

    /**
     * 可检查文件路径的非法字符
     */
    public void filePathOnly() {
        String illegalMarks = "*?\"<>|";
        this.onlyAllowedInputChar.append(illegalMarks);
    }

    /**
     * 可检查文件名的非法字符
     */
    public void fileNameOnly() {
        String illegalMarks = "\\/:*?\"<>|";
        this.onlyAllowedInputChar.append(illegalMarks);
    }

    /**
     * 设置为只能输入数字
     */
    public void numbersOnly() {
        String numbers = "1234567890";
        this.onlyAllowedInputChar.append(numbers);
    }

    public void addForbiddenChar(char... chars) {
        for (char c : chars) {
            this.forbiddenInputChar.append(c);
        }
    }

    public void addForbiddenChar(String string) {
        this.forbiddenInputChar.append(string);
    }

    /**
     * @return {@code true} 则禁止输入
     */
    private boolean notAllowInput(KeyEvent e) {
        if (this.forbiddenInputChar.isEmpty() && this.onlyAllowedInputChar.isEmpty()) {
            return false;
        }
        final String value = String.valueOf(e.getKeyChar());
        if (!this.onlyAllowedInputChar.isEmpty() && this.onlyAllowedInputChar.toString().contains(value)) {
            return false;
        }
        return this.forbiddenInputChar.toString().contains(value);
    }
    //endregion

    //region 各种重写

    /**
     * 已自动添加监听器，可直接重写，但不建议完全重写
     */
    protected void focusGained(FocusEvent e) {
        setEnterBorder();
    }

    /**
     * 已自动添加监听器，可直接重写，但不建议完全重写
     */
    protected void focusLost(FocusEvent e) {
        String text = getText();
        if (text.isEmpty()) {
            //内容为空的边框
            setEmptyBorder();
            return;
        }
        if (meetCondition()) {
            //符合条件的边框
            setRightBorder();
            return;
        }
        //不符合条件的边框
        setErrorBorder();
    }

    /**
     * 当文本框获得焦点，符合条件的方法
     * 子类重载
     *
     * @return 不重载，则默认返回 {@code true}
     */
    protected boolean meetCondition() {
        return true;
    }

    /**
     * 已自动添加监听器，可直接重写
     */
    protected void keyPress(KeyEvent e) {
    }

    /**
     * 已自动添加监听器，可直接重写
     */
    protected void keyType(KeyEvent e) {
        if (notAllowInput(e)) {
            e.consume();
        }
    }

    /**
     * 已自动添加监听器，可直接重写
     */
    protected void keyRelease(KeyEvent e) {
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

    //endregion

    //region 取得监听器

    public QRDocumentListener documentListener() {
        return documentListener;
    }

    public QRCaretListener caretListener() {
        return caretListener;
    }

    public QRFocusListener focusListener() {
        return focusListener;
    }

    public QRKeyListener keyListener() {
        return keyListener;
    }

    public QRMouseListener mouseListener() {
        return mouseListener;
    }

    public QRMouseMotionListener mouseMotionListener() {
        return mouseMotionListener;
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
        setFont(QRColorsAndFonts.STANDARD_FONT_TEXT);
        setForeground(QRColorsAndFonts.TEXT_COLOR_FORE);
        setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
        setCaretColor(QRColorsAndFonts.CARET_COLOR);
        focusLost(null);
    }

    //region 文本设置
    @Override
    public void setTextLeft() {
        setHorizontalAlignment(SwingConstants.LEFT);
    }

    @Override
    public void setTextCenter() {
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public void setTextRight() {
        setHorizontalAlignment(SwingConstants.RIGHT);
    }

    @Override
    public void clear() {
        setText(null);
    }

    @Override
    public void setText(String t) {
        super.setText(t);
        focusLost(null);
    }

    public void setText(int value) {
        setText(String.valueOf(value));
    }

    public int getValue() {
        String text = getText();
        if (QRStringUtils.isNumberStrict(text)) {
            return Integer.parseInt(text);
        }
        throw new NumberFormatException(text);
    }

    //endregion
}