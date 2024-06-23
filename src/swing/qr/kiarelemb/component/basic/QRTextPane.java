package swing.qr.kiarelemb.component.basic;

import method.qr.kiarelemb.utils.QRArrayUtils;
import method.qr.kiarelemb.utils.QRStringUtils;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import swing.qr.kiarelemb.component.QRComponentUtils;
import swing.qr.kiarelemb.component.assembly.QRBackgroundBorder;
import swing.qr.kiarelemb.component.assembly.QRCaret;
import swing.qr.kiarelemb.component.assembly.QRUndoManager;
import swing.qr.kiarelemb.component.data.QRMousePointIndexData;
import swing.qr.kiarelemb.component.drag.QRFileTransferHandler;
import swing.qr.kiarelemb.component.event.QRTextSelectionEndEvent;
import swing.qr.kiarelemb.component.listener.*;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRBackgroundUpdate;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.listener.add.*;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.LinkedList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 所有控件中拓展最为丰富和强大的类。不出意外，我们推荐将 {@link QRTextArea} 替换为 {@link QRTextPane}。
 * @create 2022-11-21 20:52D
 **/
public class QRTextPane extends JTextPane implements QRComponentUpdate, QRCaretListenerAdd, QRFocusListenerAdd,
        QRDocumentListenerAdd, QRKeyListenerAdd, QRMouseListenerAdd, QRMouseMotionListenerAdd,
        QRTextSelectionEndListenerAdd, QRBackgroundUpdate {

    public static final Cursor WAIT = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    public static final Cursor EDIT = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
    public static final int INSECT = 10;
    public QRCaret caret;
    /**
     * 使用前请先调用 {@link #addUndoManager()}
     */
    public QRUndoManager undoManager;
    /**
     * 使用前请先调用 {@link #addCaretListener()}
     */
    public QRCaretUpdateData caretUpdateData;
    public Font textFont = QRColorsAndFonts.STANDARD_FONT_TEXT;
    protected QRScrollPane scrollPane;
    protected char blankMark = ' ';
    protected char lineMark = '\n';
    protected boolean caretBlock = false;

    //region 高级操作变量，用于鼠标取文
    /**
     * 通过 光标位置 {@link Integer} 及 光标坐标信息 {@link Rectangle} 双向 map 来实现数据相互映射
     */
    protected BidiMap<Integer, Rectangle> indexesAndRectangles;
    /**
     * 这是每一行的每个字符的 {@link Rectangle}
     */
    protected Rectangle[][] rectanglesOfEachLines;
    /**
     * 各行最后一位字符的位置
     */
    protected int[] lastWordIndexes;
    /**
     * 在调用 {@link QRTextPane#indexesUpdate()} 方法时自动更新
     */
    protected String indexUpdatedText;
    protected int lineWords;
    protected int linePerHeight;
    //endregion

    //region 私有变量
    private QRCaretListener caretListener;
    private QRDocumentListener documentListener;
    private QRKeyListener keyListener;
    private QRMouseMotionListener mouseMotionListener;
    private QRMouseListener mouseListener;
    private QRFocusListener focusListener;
    private QRTextSelectionEndListener selectionEndListener;
    //endregion


    /**
     * 构造方法
     */
    public QRTextPane() {
        setMargin(new Insets(INSECT, INSECT, INSECT, INSECT));
//		setPreferredSize(new Dimension(800, 400));
        this.caret = new QRCaret();
        setCaret(this.caret);
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
            this.caretUpdateData = new QRCaretUpdateData();
            this.caretListener.add(event -> {
                if (!this.caretBlock) {
                    QRTextPane.this.caretUpdate((CaretEvent) event);
                }
            });
            addCaretListener(this.caretListener);
        }
    }

    /**
     * 添加光标事件
     *
     * @param ar 操作
     */
    @Override
    public final void addCaretListenerAction(QRActionRegister ar) {
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
            this.documentListener.add(QRDocumentListener.TYPE.INSERT,
                    e -> QRTextPane.this.insertUpdate((DocumentEvent) e));
            this.documentListener.add(QRDocumentListener.TYPE.REMOVE,
                    e -> QRTextPane.this.removeUpdate((DocumentEvent) e));
            this.documentListener.add(QRDocumentListener.TYPE.CHANGED,
                    e -> QRTextPane.this.changedUpdate((DocumentEvent) e));
            getDocument().addDocumentListener(this.documentListener);
        }
    }

    /**
     * 添加文本事件
     *
     * @param type 类型
     * @param ar   操作
     */
    @Override
    public final void addDocumentListenerAction(QRDocumentListener.TYPE type, QRActionRegister ar) {
        if (this.documentListener != null) {
            this.documentListener.add(type, ar);
        }
    }

    /**
     * 添加按键的事件
     */
    @Override
    public final void addKeyListener() {
        if (this.keyListener == null) {
            this.keyListener = new QRKeyListener();
            this.keyListener.add(QRKeyListener.TYPE.TYPE, e -> keyType((KeyEvent) e));
            this.keyListener.add(QRKeyListener.TYPE.PRESS, e -> keyPress((KeyEvent) e));
            this.keyListener.add(QRKeyListener.TYPE.RELEASE, e -> keyRelease((KeyEvent) e));
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
    public final void addKeyListenerAction(QRKeyListener.TYPE type, QRActionRegister ar) {
        if (this.keyListener != null) {
            this.keyListener.add(type, ar);
        }
    }

    /**
     * 添加鼠标移动事件
     */
    @Override
    public final void addMouseMotionListener() {
        if (this.mouseMotionListener == null) {
            this.mouseMotionListener = new QRMouseMotionListener();
            this.mouseMotionListener.add(QRMouseMotionListener.TYPE.DRAG, e -> mouseDrag((MouseEvent) e));
            this.mouseMotionListener.add(QRMouseMotionListener.TYPE.MOVE, e -> mouseMove((MouseEvent) e));
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
    public final void addMouseMotionAction(QRMouseMotionListener.TYPE type, QRActionRegister ar) {
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
            this.mouseListener.add(QRMouseListener.TYPE.CLICK, e -> mouseClick((MouseEvent) e));
            this.mouseListener.add(QRMouseListener.TYPE.PRESS, e -> mousePress((MouseEvent) e));
            this.mouseListener.add(QRMouseListener.TYPE.RELEASE, e -> mouseRelease((MouseEvent) e));
            this.mouseListener.add(QRMouseListener.TYPE.ENTER, e -> mouseEnter((MouseEvent) e));
            this.mouseListener.add(QRMouseListener.TYPE.EXIT, e -> mouseExit((MouseEvent) e));
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
    public final void addMouseAction(QRMouseListener.TYPE type, QRActionRegister ar) {
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
            this.focusListener.add(QRFocusListener.TYPE.GAIN, e -> focusGain((FocusEvent) e));
            this.focusListener.add(QRFocusListener.TYPE.LOST, e -> focusLose((FocusEvent) e));
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
    public final void addFocusAction(QRFocusListener.TYPE type, QRActionRegister ar) {
        if (this.focusListener != null) {
            this.focusListener.add(type, ar);
        }
    }

    /**
     * 自动选择单词。如果用于纯英文的面板，则很有效
     */
    public final void addWordAutoSelectListener() {
        if (this.selectionEndListener == null) {
            this.selectionEndListener = new QRTextSelectionEndListener();
            this.selectionEndListener.add(e -> QRTextPane.this.selectionEnd((QRTextSelectionEndEvent) e));
            MouseAdapter adapter = new MouseAdapter() {
                private int pressPosition;
                private boolean isCutWord = false;
                private QRCaretNearData caretNearData;
                private boolean selected = false;

                @Override
                public void mousePressed(MouseEvent e) {
                    this.pressPosition = getCaretPosition();
                    this.isCutWord = isCutWord(this.pressPosition);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    int position = getCaretPosition();
                    if (position != this.pressPosition) {
                        boolean left = position < this.pressPosition;
                        //开始选中时，自动全选鼠标下的单词
                        if (this.isCutWord) {
                            String foreWord = caretForeWord(this.pressPosition);
                            String nextWord = caretNextWord(this.pressPosition);
                            if (left) {
                                setSelectionEnd(this.pressPosition + nextWord.length());
                            } else {
                                setSelectionStart(this.pressPosition - foreWord.length());
                            }
                        } else {
                            if (left) {
                                setSelectionEnd(this.pressPosition);
                            }
                        }
                        this.caretNearData = caretNearData(position, getText());
                        boolean thisCutWord = this.caretNearData.isCutWord();
                        if (thisCutWord) {
                            String cutWord = this.caretNearData.cutWord();
                            double cl = cutWord.length() / 2d;
                            String foreWord = this.caretNearData.foreWord();
                            int fl = foreWord.length();
                            String nextWord = this.caretNearData.nextWord();
                            int nl = nextWord.length();
                            if (left) {
                                if (fl < cl) {
                                    setSelectionStart(position - fl);
                                } else {
                                    setSelectionStart(position + nl);
                                }
                            } else {
                                if (nl < cl) {
                                    setSelectionEnd(position + nl);
                                } else {
                                    setSelectionEnd(position - fl);
                                }
                            }
                        } else {
                            if (left) {
                                setSelectionStart(position);
                            } else {
                                setSelectionEnd(position);
                            }
                        }
                        this.selected = true;
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (this.selected) {
                        QRTextPane.this.selectionEndListener.selectionEnd(new QRTextSelectionEndEvent(QRTextPane.this
                                , this.caretNearData, getSelectionStart(), getSelectionEnd(), getSelectedText()));
                    }
                    this.pressPosition = -1;
                    this.isCutWord = false;
                    this.selected = false;
                    this.caretNearData = null;
                }
            };
            addMouseListener(adapter);
            addMouseMotionListener(adapter);
        }
    }

    @Override
    public final void addSelectionEndListener() {
        if (this.selectionEndListener == null) {
            this.selectionEndListener = new QRTextSelectionEndListener();
            this.selectionEndListener.add(e -> QRTextPane.this.selectionEnd((QRTextSelectionEndEvent) e));
            MouseAdapter adapter = new MouseAdapter() {
                private int pressPosition;
                private QRCaretNearData caretNearData;
                private boolean selected = false;

                @Override
                public void mousePressed(MouseEvent e) {
                    this.pressPosition = getCaretPosition();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    int position = getCaretPosition();
                    if (position != this.pressPosition) {
                        this.caretNearData = caretNearData(position, getText());
                        this.selected = true;
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (this.selected) {
                        QRTextPane.this.selectionEndListener.selectionEnd(new QRTextSelectionEndEvent(QRTextPane.this
                                , this.caretNearData, getSelectionStart(), getSelectionEnd(), getSelectedText()));
                    }
                    this.pressPosition = -1;
                    this.selected = false;
                    this.caretNearData = null;
                }
            };
            addMouseListener(adapter);
            addMouseMotionListener(adapter);
        }
    }

    /**
     * 添加选择结束事件
     *
     * @param ar 操作
     */
    @Override
    public final void addSelectionEndAction(QRActionRegister ar) {
        if (this.selectionEndListener != null) {
            this.selectionEndListener.add(ar);
        }
    }

    /**
     * 添加文件拖入事件
     */
    public final void addFileDragAction() {
        setTransferHandler(new QRFileTransferHandler() {
            @Override
            protected void fileImportAction(File[] files) {
                fileDragInAction(files);
            }
        });
    }

    /**
     * 添加滚动条，默认单次滚动五行
     *
     * @return 滚动条本身，实例是 {@link QRScrollPane}
     */
    public QRScrollPane addScrollPane() {
        return addScrollPane(5);
    }

    /**
     * 添加滚动条
     *
     * @param line 单次滚动行数
     * @return 滚动条本身，实例是 {@link QRScrollPane}
     */
    public QRScrollPane addScrollPane(int line) {
        if (this.scrollPane == null) {
            this.scrollPane = new QRScrollPane();
            this.scrollPane.setViewportView(this);
            this.scrollPane.setScrollSmoothly(line);
        }
        return this.scrollPane;
    }

    /**
     * 使文本框能够撤销重做
     */
    public void addUndoManager() {
        this.undoManager = new QRUndoManager(this);
    }
    //endregion

    //region 光标位置信息获取

    /**
     * 更新光标所在的行与列，及所有行
     * <p>重写前请先调用 {@link #addCaretListener()}
     */
    public void freshCaretLineRow(final int position) {
        final String text = getText();
        int line = 1;
        int index = 0;
        int foreIndex = index;
        boolean set = false;
        while (true) {
            index = text.indexOf(this.lineMark, index) + 1;
            if (index > position && !set) {
                this.caretUpdateData.setLine(line);
                this.caretUpdateData.setRow(position - foreIndex);
                this.caretUpdateData.setLineText(text.substring(foreIndex, index - 1));
                set = true;
            }
            if (index == 0) {
                this.caretUpdateData.setTotalLines(line);
                if (!set) {
                    this.caretUpdateData.setLine(line);
                    this.caretUpdateData.setRow(position - foreIndex);
                    this.caretUpdateData.setLineText(text.substring(foreIndex));
                }
                break;
            }
            line++;
            foreIndex = index;
        }
        this.caretUpdateData.setPosition(position);
        this.caretUpdateData.setTextLength(text.length());
    }

    /**
     * 根据文本获取及光标位置获取其行和列
     *
     * @param position 光标位置
     * @param text     文本内容
     * @return 设置了行和列的数据类
     */
    public QRCaretUpdateData getLineAndRow(int position, String text) {
        int line = 1;
        int index = 0;
        int foreIndex = index;
        int caretLine;
        int caretRow;
        while (true) {
            index = text.indexOf(this.lineMark, index) + 1;
            if (index > position || index == 0) {
                caretLine = line;
                caretRow = position - foreIndex;
                break;
            }
            line++;
            foreIndex = index;
        }
        return new QRCaretUpdateData(caretLine, caretRow);
    }

    /**
     * 获取每行的开始位置
     *
     * @param line 行号
     * @return 位置
     */
    public int getLineStartOffset(int line) {
        if (line == 1) {
            return 0;
        }
        String text = getText();
        int index = QRStringUtils.getIndexWhenFindTimes(text, "\n", line - 1);
        return index + 1;
    }

    /**
     * 获取文本框的总行数
     *
     * @return 总行数
     */
    public int allLines() {
        String text = getText();
        int line = 1;
        int index = 0;
        while ((index = text.indexOf(QRStringUtils.AN_ENTER_CHAR, index) + 1) != 0) {
            line++;
        }
        return line;
    }

    /**
     * 获取文本框的总行数
     * <p>请及时在调用本方法前执行 {@link #freshCaretLineRow(int)} 方法
     *
     * @return 返回文本框的总行数
     */
    public final int totalLines() {
        return this.caretUpdateData.totalLines;
    }

    /**
     * 在调用了 {@link #currentLineAndRow(int)} 方法之后，{@link #lineWords} 就会更新
     *
     * @return 这一行的字数，或每行的字数
     */
    public final int lineWords() {
        return lineWords;
    }

    /**
     * 在调用了 {@link #indexesUpdate()} 方法之后，该变量的数据会更新
     *
     * @return 每行的行高
     */
    public int linePerHeight() {
        return linePerHeight;
    }

    /**
     * 获取光标所在的行
     * <p>请及时在调用本方法前执行 {@link #freshCaretLineRow(int)} 方法
     *
     * @return 光标所在的行
     */
    public final int caretLine() {
        return this.caretUpdateData.line;
    }

    /**
     * 获取光标所在的列，该列的起始位置为0
     * <p>请及时在调用本方法前执行 {@link #freshCaretLineRow(int)} 方法
     *
     * @return 光标所在的列
     */
    public final int caretRow() {
        return this.caretUpdateData.row;
    }

    /**
     * 返回光标当前所在行的文字长度
     * <p>请及时在调用本方法前执行 {@link #freshCaretLineRow(int)} 方法
     *
     * @return 当前行的文字长度
     */
    public final int caretLineLength() {
        return this.caretUpdateData.lineLength;
    }

    /**
     * @return 返回文本的长度
     */
    public int textLength() {
        return getText().length();
    }

    /**
     * @param caretPosition 位置
     * @return 判断传入的位置是否切断了单词，其执行标准是其前后是否有空格或换行符
     */
    public final boolean isCutWord(int caretPosition) {
        String text = getText();
        int length = text.length();
        if (caretPosition == 0 || caretPosition >= length) {
            return false;
        }
        return text.substring(caretPosition - 1, caretPosition + 1).trim().length() == 2;
    }

    /**
     * 获取光标在指定位置时所在的坐标信息
     *
     * @param index 指定位置
     * @return 坐标信息
     */
    public final Rectangle2D positionRectangle(int index) {
        Rectangle2D r;
        try {
            r = modelToView2D(index);
            return r;
        } catch (Exception e) {
            return null;
        }
    }
    //endregion

    //region 光标位置文本获取

    /**
     * 获取当前光标所在位置的前一个字符，如果是输入时获取，则会返回输入前的前一个字符
     *
     * @return 光标所在位置的前一个字符
     */
    public final String caretForeChar() {
        int position;
        if (this.caretListener != null) {
            if (this.caretUpdateData.row == 1) {
                return "";
            }
            position = this.caretUpdateData.row - 1;
            return this.caretUpdateData.lineText.substring(position - 1, position);
        } else {
            position = getCaretPosition();
            return caretForeChar(position);
        }
    }

    /**
     * 获取光标所在位置的前一个字符，如果是输入时获取，则会返回输入前的前一个字符
     *
     * @param position 光标位置
     * @return 光标所在位置的前一个字符
     */
    public final String caretForeChar(int position) {
        return caretForeChar(position, getText());
    }

    /**
     * 获取光标所在位置的前一个字符，如果是输入时获取，则会返回输入前的前一个字符
     *
     * @param position 光标位置
     * @return 光标所在位置的前一个字符
     */
    public String caretForeChar(int position, String text) {
        if (text == null || position == 0) {
            return "";
        }
        int length = text.length();
        if (length < position) {
            return "";
        }
        if (length == position) {
            return text.substring(length - 1);
        }
        return text.substring(position - 1, position);

    }

    /**
     * 获取当前光标前一个单词，已去除前后空白
     *
     * @return 返回光标前一个单词，已去除前后空白
     */
    public final String caretForeWord() {
        int position;
        if (this.caretListener != null) {
            if (this.caretUpdateData.row == 1) {
                return "";
            }
            position = this.caretUpdateData.row - 1;
            return caretForeWord(position, this.caretUpdateData.lineText);
        } else {
            position = getCaretPosition();
            return caretForeWord(position);
        }
    }

    /**
     * 获取光标前一个单词，已去除前后空白
     *
     * @param position 光标位置
     * @return 返回光标前一个单词，已去除前后空白
     */
    public final String caretForeWord(int position) {
        return caretForeWord(position, getText());
    }

    /**
     * 获取光标前一个单词，已去除前后空白
     *
     * @param position 光标位置
     * @return 返回光标前一个单词，已去除前后空白
     */
    public String caretForeWord(int position, String text) {
        if (text == null || text.length() < position) {
            return "";
        }
        if (text.charAt(position - 1) == this.blankMark) {
            position--;
        }
        String fore = text.substring(0, position).trim();
        int foreBlank = fore.lastIndexOf(this.blankMark) + 1;
        int foreLine = fore.lastIndexOf(this.lineMark) + 1;
        return fore.substring(Math.max(foreBlank, foreLine)).trim();
    }

    /**
     * 取得当前光标位置之前的行内容，已去除前后空白
     *
     * @return 光标之前的行内容
     */
    public final String caretForeText() {
        int position;
        if (this.caretListener != null) {
            if (this.caretUpdateData.row == 1) {
                return "";
            }
            position = this.caretUpdateData.row - 1;
            return this.caretUpdateData.lineText.substring(0, position).trim();
        } else {
            position = getCaretPosition();
            return caretForeText(position);
        }
    }

    /**
     * 取得光标位置之前的行内容，已去除前后空白
     *
     * @param position 光标位置
     * @return 光标之前的行内容
     */
    public final String caretForeText(int position) {
        return caretForeText(position, getText());
    }

    /**
     * 取得光标位置之前的行内容，已去除前后空白
     *
     * @param position 光标位置
     * @param text     文本
     * @return 光标之前的行内容
     */
    public String caretForeText(int position, String text) {
        if (text == null || text.length() < position) {
            return "";
        }
        int foreLineIndex = text.lastIndexOf(this.lineMark, position) + 1;
        if (foreLineIndex == 0 || foreLineIndex >= position) {
            return text.substring(0, position).trim();
        }
        return text.substring(foreLineIndex, position).trim();
    }

    /**
     * 取得光标位置的行内容
     *
     * @return 光标位置的行内容
     */
    public final String caretLineText() {
        int position;
        if (this.caretListener != null) {
            return this.caretUpdateData.lineText;
        } else {
            position = getCaretPosition();
            return caretLineText(position);
        }
    }

    /**
     * 取得光标位置的行内容
     *
     * @param position 光标位置
     * @return 光标位置的行内容
     */
    public final String caretLineText(int position) {
        return caretLineText(position, getText());
    }

    /**
     * 取得光标位置的行内容
     *
     * @param position 光标位置
     * @param text     文本
     * @return 光标位置的行内容
     */
    public String caretLineText(int position, String text) {
        if (text == null || text.length() < position) {
            return "";
        }
        int index = text.indexOf(this.lineMark);
        if (index == -1) {
            return text;
        }
        int foreIndex = text.lastIndexOf(this.lineMark, position);
        if (foreIndex == position) {
            foreIndex = text.lastIndexOf(this.lineMark, position - 1);
        }
        if (foreIndex == -1) {
            return text.substring(0, index);
        }
        int endIndex = text.indexOf(this.lineMark, position);
        if (endIndex == -1) {
            return text.substring(foreIndex + 1);
        }
        return text.substring(foreIndex + 1, endIndex);
    }

    /**
     * 获取光标位置的后一个字符，如果是输入时获取，则会返回输入前的后个字符
     *
     * @return 光标所在位置的后一个字符
     */
    public final String caretNextChar() {
        int position;
        if (this.caretListener != null) {
            position = this.caretUpdateData.row - 1;
            if (position == this.caretUpdateData.lineLength) {
                return "";
            }
            return this.caretUpdateData.lineText.substring(position, position + 1);
        } else {
            position = getCaretPosition();
            return caretNextChar(position);
        }
    }

    /**
     * 获取光标位置的后一个字符，如果是输入时获取，则会返回输入前的后个字符
     *
     * @param position 光标位置
     * @return 光标所在位置的后一个字符
     */
    public final String caretNextChar(int position) {
        return caretNextChar(position, getText());
    }

    /**
     * 获取光标位置的后一个字符，如果是输入时获取，则会返回输入前的后个字符
     *
     * @param position 光标位置
     * @param text     文本
     * @return 光标所在位置的后一个字符
     */
    public String caretNextChar(int position, String text) {
        if (text == null || text.length() < position) {
            return "";
        }
        int length = text.length();
        if (length == position) {
            return "";
        }
        return text.substring(position, position + 1);
    }

    /**
     * @return 返回光标后一个单词，已去除前后空白
     */
    public final String caretNextWord() {
        int position;
        if (this.caretListener != null) {
            position = this.caretUpdateData.row - 1;
            if (position == this.caretUpdateData.lineLength) {
                return "";
            }
            return caretNextWord(position, this.caretUpdateData.lineText);
        } else {
            position = getCaretPosition();
            return caretNextWord(position);
        }
    }

    /**
     * @param position 光标位置
     * @return 返回光标后一个单词，已去除前后空白
     */
    public final String caretNextWord(int position) {
        return caretNextWord(position, getText());
    }

    /**
     * @param position 光标位置
     * @param text     文本
     * @return 返回光标后一个单词，已去除前后空白
     */
    public String caretNextWord(int position, String text) {
        if (text == null || text.length() < position) {
            return "";
        }
        if (text.charAt(position) == this.blankMark) {
            position++;
        }
        String back = text.substring(position).trim();
        int nextBlank = back.indexOf(this.blankMark) + 1;
        int nextLine = back.indexOf(this.lineMark) + 1;
        if (nextBlank == 0 && nextLine == 0) {
            return back;
        }
        nextBlank--;
        if (nextBlank == -1 || nextLine == 0) {
            return back.substring(0, Math.max(nextBlank, nextLine)).trim();
        }
        return back.substring(0, Math.min(nextBlank, nextLine)).trim();
    }

    /**
     * @return 取得当前光标位置之后的行内容，已去除前后空白
     */
    public final String caretNextText() {
        int position;
        if (this.caretListener != null) {
            position = this.caretUpdateData.row - 1;
            if (position == this.caretUpdateData.lineLength) {
                return "";
            }
            return this.caretUpdateData.lineText.substring(position);
        } else {
            position = getCaretPosition();
            return caretNextText(position);
        }
    }

    /**
     * @param position 光标位置
     * @return 取得光标位置之后的行内容，已去除前后空白
     */
    public final String caretNextText(int position) {
        return caretNextText(position, getText());
    }

    /**
     * @param position 光标位置
     * @param text     文本
     * @return 取得光标位置之后的行内容，已去除前后空白
     */
    public String caretNextText(int position, String text) {

        if (text == null || text.length() < position) {
            return "";
        }
        String back = text.substring(position).trim();
        int nextLineIndex = back.indexOf(this.lineMark) + 1;
        if (nextLineIndex == 0) {
            return back;
        }
        return back.substring(0, nextLineIndex).trim();
    }

    /**
     * 调用前提是能确定 {@link #isCutWord(int)} 返回为 {@code true}
     *
     * @return 被切断的单词
     */
    public final String caretCutWord() {
        int position;
        if (this.caretListener != null) {
            position = this.caretUpdateData.row - 1;
            return caretCutWord(position, this.caretUpdateData.lineText);
        } else {
            position = getCaretPosition();
            return caretCutWord(position);
        }
    }

    /**
     * 调用前提是能确定 {@link #isCutWord(int)} 返回为 {@code true}
     *
     * @param position 光标位置
     * @return 被切断的单词
     */
    public final String caretCutWord(int position) {
        if (position == 0) {
            return caretNextWord(0);
        }
        String text = getText();
        return caretCutWord(position, text);
    }

    /**
     * 调用前提是能确定 {@link #isCutWord(int)} 返回为 {@code true}
     *
     * @param position 光标位置
     * @return 被切断的单词
     */
    public String caretCutWord(int position, String text) {
        if (text == null || text.length() < position) {
            return "";
        }
        int length = text.length();
        if (position == length) {
            return caretForeWord(position, text);
        }
        int foreBlank = text.lastIndexOf(this.blankMark, position) + 1;
        int foreLine = text.lastIndexOf(this.lineMark, position) + 1;

        int nextBlank = text.indexOf(this.blankMark, position) + 1;
        int nextLine = text.indexOf(this.lineMark, position) + 1;

        if (nextBlank == 0 && nextLine == 0) {
            return text.substring(Math.max(foreBlank, foreLine)).trim();
        }
        if (nextBlank == 0 || nextLine == 0) {
            return text.substring(Math.max(foreBlank, foreLine), Math.max(nextBlank, nextLine)).trim();
        }
        return text.substring(Math.max(foreBlank, foreLine), Math.min(nextBlank, nextLine)).trim();
    }

    /**
     * 一次性从文本面板全文中获取光标附近的数据内容
     *
     * @param position 光标位置
     * @param text     光标所在的文本内容
     * @return 光标附近的数据内容
     */
    public QRCaretNearData caretNearData(int position, String text) {
        return new QRCaretNearData(position, text);
    }
    //endregion

    //region 文本样式更新

    /**
     * 改变字符的样式
     *
     * @param offset 开始位置
     * @param length 从开始位置的长度
     * @param attrs  新样式
     */
    public void changeTextsStyle(int offset, int length, AttributeSet attrs) {
        changeTextsStyle(offset, length, attrs, false);
    }

    /**
     * 改变字符的样式
     *
     * @param offset 开始位置
     * @param length 从开始位置的长度
     * @param attrs  新样式
     */
    public void changeTextsStyle(int offset, int length, AttributeSet attrs, boolean replace) {
        ((DefaultStyledDocument) getDocument()).setCharacterAttributes(offset, length, attrs, replace);
    }

    /**
     * 从指定位置插入内容
     *
     * @param offset 插入的位置
     * @param str    内容
     * @param a      样式
     */
    public void insertString(int offset, String str, AttributeSet a) {
        try {
            getDocument().insertString(offset, str, a);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void removeText(int offset, int length) {
        try {
            if (length <= 0) {
                return;
            }
            getDocument().remove(offset, length);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从指定设置开始，根据新文本长度，替换旧文本样式
     *
     * @param offset 开始位置
     * @param text   要更新的文本
     * @param attrs  样式
     */
    public void replaceText(int offset, String text, AttributeSet attrs) {
        replaceText(offset, text.length(), text, attrs);
    }

    /**
     * 从指定设置开始，指定替换的长度，用新内容替换旧文本样式
     *
     * @param offset 开始位置
     * @param length 旧文本长度
     * @param text   新文本
     * @param attrs  样式
     */
    public void replaceText(int offset, int length, String text, AttributeSet attrs) {
        try {
            ((DefaultStyledDocument) getDocument()).replace(offset, length, text, attrs);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置选择的范围
     *
     * @param start 开始位置
     * @param end   结束位置
     */
    public void selectText(int start, int end) {
        setSelectionStart(start);
        setSelectionEnd(end);
    }

    /**
     * 当添加了背景图片时，就需要不断更新文本面板，以确保它透明
     *
     * <p>确保窗体中有面板设置了 {@link QRBackgroundBorder}
     */
    @Override
    public final void addCaretListenerForBackgroundUpdate() {
        QRActionRegister action = e -> {
            if (!this.caretBlock) {
                try {
                    SwingUtilities.getWindowAncestor(QRTextPane.this).repaint();
                } catch (Exception ignore) {
                }
            }
        };
        if (this.caretListener != null) {
            addCaretListenerAction(action);
        } else {
            addCaretListener(action::action);
        }
        if (this.mouseListener != null) {
            addMouseAction(QRMouseListener.TYPE.CLICK, action);
        } else {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    action.action(e);
                }
            });
        }
    }

    //endregion

    //region 各种设置

    /**
     * 设置行距
     *
     * <p>若想让设置遍及所有行，则需重写 {@link #setParagraphAttributes(AttributeSet, boolean)} 方法
     * <p>{@code public void setParagraphAttributes(AttributeSet attr, boolean replace)} {
     * <p>{@code StyledDocument doc = getStyledDocument();}
     * <p>{@code doc.setParagraphAttributes(0, textLength(), attr, replace);}
     * <p>}
     *
     * @param lineSpacing 行距值
     */
    public void setLineSpacing(float lineSpacing) {
        MutableAttributeSet paragraph = new SimpleAttributeSet();
        StyleConstants.setLineSpacing(paragraph, lineSpacing);
        setParagraphAttributes(paragraph, false);
    }

    /**
     * 如果你不想让面板可编辑，但又想让光标处于编辑状态，那就调用这个方法吧
     */
    public void setEditableFalseButCursorEdit() {
        setEditable(false);
        setCursorEdit();
        QRTextPane.this.caret.setVisible(true);
    }

    public void setEnterAutoFocus() {
        addMouseListener();
        addMouseAction(QRMouseListener.TYPE.ENTER, event -> requestFocus());
    }

    public void setSelectable(boolean selectable) {
        if (selectable) {
            Highlighter highlighter = getHighlighter();
            if (highlighter == null) {
                highlighter = new BasicTextUI.BasicHighlighter();
                setHighlighter(highlighter);
            }
        } else {
            setHighlighter(null);
        }
    }
    //endregion

    //region 鼠标处理

    /**
     * 鼠标处理，Wait
     */
    public final void setCursorWait() {
        setCursor(WAIT);
    }

    /**
     * 鼠标处理，edit
     */
    public final void setCursorEdit() {
        setCursor(EDIT);
    }

    public final void setCaretBlock() {
        this.caretBlock = true;
    }

    public final void setCaretUnblock() {
        this.caretBlock = false;
    }

    //region 高级操作
    public void indexesUpdate() {
        LinkedList<Integer> list = new LinkedList<>();
        this.indexUpdatedText = getText();
        final int length = this.indexUpdatedText.length();
        this.indexesAndRectangles = new DualHashBidiMap<>();
        int index;
        int foreX = -1;
        int foreY = 0;
        int height = -1;
        Rectangle zeroPosi = null;
        LinkedList<Rectangle> lr = new LinkedList<>();
        LinkedList<Rectangle[]> lrs = new LinkedList<>();
        final Rectangle[] rec = new Rectangle[0];
        for (int j = 0; j < length; j++) {
            Rectangle2D r = positionRectangle(j);
            if (r != null) {
                Rectangle showPosition = r.getBounds();
                this.indexesAndRectangles.put(j, showPosition);
                index = showPosition.x;
                if (j == 0) {
                    foreY = showPosition.y;
                    zeroPosi = showPosition;
                    height = showPosition.height;
                } else if (showPosition.y > foreY && height == -1) {
                    height = showPosition.y - foreY;
                    lrs.add(lr.toArray(rec));
                    lr.clear();
                }
                if (foreX > index) {
                    if (lr.size() > 0) {
                        lrs.add(lr.toArray(rec));
                        lr.clear();
                    }
                    list.add(j);
                }
                foreX = index;
                lr.add(showPosition);
            }
        }
        if (lr.size() > 0) {
            lrs.add(lr.toArray(rec));
        }
        this.rectanglesOfEachLines = new Rectangle[lrs.size()][];
        for (int i = 0, lrsSize = lrs.size(); i < lrsSize; i++) {
            this.rectanglesOfEachLines[i] = lrs.get(i);
        }
        this.lastWordIndexes = QRArrayUtils.listToArr(list);
        this.linePerHeight = height == -1 ? (zeroPosi != null ? zeroPosi.height : textFont.getSize()) : height;
    }


    /**
     * 对数组中的值，使用二分查找。
     *
     * @param index 光标当前位置
     * @return 如果找到了，则说明当前光标在文本框的末尾，且这个位置是{@link  #lastWordIndexes}的{@code mid}的取值位置
     * <P>如果没有找到，那么{@link  #lastWordIndexes}的{@code mid}的取值位置是上一行末尾的位置</P>
     * <p>特别地，如果光标在第一行，则返回{@code -1}</p>
     * <p>返回的这个{@code mid}值{@code +2}，就是当前光标从在的行</p>
     */
    public final int getIndexSearchMid(int index) {
        int start = -1;
        int end = this.lastWordIndexes.length;
        int mid = end / 2;
        while (end != mid && start != mid) {
            if (this.lastWordIndexes[mid] > index) {
                end = mid;
                mid = start + (mid - start) / 2;
            } else if (this.lastWordIndexes[mid] < index) {
                start = mid;
                mid += (end - mid) / 2;
            } else {
                break;
            }
        }
        return mid;
    }

    public int[] currentLineAndRow(int index) {
        if (this.lastWordIndexes == null || this.lastWordIndexes.length == 0) {
            return new int[]{1, index};
        }
        int mid = getIndexSearchMid(index);
        int line;
        int row;
        line = mid + 2;
        if (mid == -1) {
            row = index;
            this.lineWords = this.lastWordIndexes[0];
        } else {
            //这个值是当前光标所在行的上一行末尾的位置
            final int midIndexValue = this.lastWordIndexes[mid];
            row = index - midIndexValue;
            this.lineWords = mid != this.lastWordIndexes.length - 1 ? this.lastWordIndexes[mid + 1] - midIndexValue :
                    textLength() - midIndexValue;
        }
        return new int[]{line, row};
    }

    public final int innerIndex(int value, Rectangle[] xes, int startIndex, int endIndex) {
        int lastX = xes[endIndex - 1].x;
        int lastXs = xes[endIndex - 2].x;
        if (lastX <= value && value - lastX <= lastX - lastXs) {
            return endIndex - 1;
        }
        for (int i = startIndex; i < endIndex - 1; i++) {
            final Rectangle thisRec = xes[i];
            final Rectangle nextRec = xes[i + 1];
            if (value >= thisRec.x && value < nextRec.x) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 通过鼠标在文本面板的位置，获取鼠标所对应的 Rectangle
     * <p>
     *
     * @param p 鼠标位置
     */
    public final QRMousePointIndexData getMousePointIndexData(final Point p) {
        if (this.indexesAndRectangles == null || this.rectanglesOfEachLines == null || this.indexesAndRectangles.size() == 0) {
            return null;
        }
        final int x = p.x;
        int lineIndex = p.y / this.linePerHeight;
        if (lineIndex >= this.rectanglesOfEachLines.length) {
            return null;
        }
        final Rectangle[] lineRectangles = this.rectanglesOfEachLines[lineIndex];
        final int lineRectanglesLength = lineRectangles.length;
        int oneThird = lineRectanglesLength / 3;
        int twoThird = lineRectanglesLength * 2 / 3;
        int startIndex;
        int endIndex;
        if (x < lineRectangles[oneThird].x) {
            startIndex = 0;
            endIndex = oneThird + 1;
        } else if (x > lineRectangles[twoThird].x) {
            startIndex = twoThird;
            endIndex = lineRectanglesLength;
        } else {
            startIndex = oneThird;
            endIndex = twoThird + 1;
        }
        final int rowIndex = innerIndex(x, lineRectangles, startIndex, endIndex);
        if (rowIndex == -1) {
            return null;
        }
        return getMousePointIndexDataFinalProcess(p, lineRectangles, lineIndex, rowIndex);
    }

    /**
     * 重写本方法可以更新鼠标位置所在的数据
     *
     * @param mousePoint     鼠标位置
     * @param lineRectangles 鼠标位置所在的该行的 {@link Rectangle} 数组
     * @param lineIndex      鼠标位置所在行，从 {@code 0} 开始
     * @param rowIndex       鼠标位置所在列，从 {@code 0} 开始
     * @return 鼠标位置信息类，可传多参
     */
    protected QRMousePointIndexData getMousePointIndexDataFinalProcess(final Point mousePoint,
                                                                       final Rectangle[] lineRectangles,
                                                                       final int lineIndex, final int rowIndex) {
        return new QRMousePointIndexData(mousePoint, lineRectangles[rowIndex],
                this.indexesAndRectangles.getKey(lineRectangles[rowIndex]), lineIndex + 1, rowIndex + 1);
    }
    //endregion

    //endregion

    //region 打印的方法

    /**
     * 在光标的当前位置插入该文字
     *
     * @param str 要插入的文字
     */
    public final void print(String str) {
        print(str, QRColorsAndFonts.TEXT_COLOR_FORE, QRColorsAndFonts.TEXT_COLOR_BACK, textLength());
    }

    public final void print(String str, Color colorFore) {
        print(str, textFont, colorFore, QRColorsAndFonts.TEXT_COLOR_BACK, textLength());
    }

    public final void print(String str, Color colorFore, int index) {
        print(str, textFont, colorFore, QRColorsAndFonts.TEXT_COLOR_BACK, index);
    }

    public final void print(String str, Color colorFore, Color colorBack) {
        print(str, textFont, colorFore, colorBack, textLength());
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

    public final void print(String str, SimpleAttributeSet sas) {
        print(str, sas, textLength());
    }

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
        println(str, textFont, colorFore, colorBack, textLength());
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

    /**
     * 清除内容
     */
    public void clear() {
        setText(null);
    }

    //endregion

    //region 推荐重写的方法

    /**
     * 重写前请先调用 {@link #addCaretListener()}
     * <p>当 {@link #caretBlock} 为 {@code false} 时才会调用该方法
     */
    protected void caretUpdate(CaretEvent e) {
        //鼠标位置的改变需要更新其位置数据
        freshCaretLineRow(e.getDot());
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
     * 重写前请先调用 {@link #addWordAutoSelectListener()} 或 {@link #addSelectionEndListener()}
     */
    protected void selectionEnd(QRTextSelectionEndEvent e) {

    }

    /**
     * 重写前请先调用 {@link #addFileDragAction()}
     *
     * @param files 拖入的文件
     */
    protected void fileDragInAction(File[] files) {
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

    //region 文本操作

    /**
     * 由于原方法 {@link #getText()} 方法已经被重写了，如果要调用未重写的方法，则使用这个
     */
    public String superText() {
        return super.getText();
    }

    @Override
    public String getText() {
        String text = super.getText();
        return System.lineSeparator().length() > 1 ? text.replaceAll(System.lineSeparator(), QRStringUtils.AN_ENTER)
                : text;
    }

    @Override
    public String getText(int startIndex, int length) {
        if (startIndex < 0 || length <= 0) {
            return "";
        }
        try {
            return getDocument().getText(startIndex, length);
        } catch (Exception e) {
            return "";
        }
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

    /**
     * 自动设置了光标的样式所需要的字体
     *
     * @param font 字体
     */
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        textFont = font;
        if (this.caret != null) {
            this.caret.setFont(font).update();
        }
    }

    @Override
    public void componentFresh() {
        setFont(textFont);
        caret.setCaretColor(QRColorsAndFonts.CARET_COLOR);
        setForeground(QRColorsAndFonts.TEXT_COLOR_FORE);
        setBackground(QRColorsAndFonts.TEXT_COLOR_BACK);
    }
    //endregion

    /**
     * 启用了 {@link #addCaretListener()} 之后，光标位置更新所产生的变量
     */
    public final class QRCaretUpdateData {
        /**
         * 光标位置
         */
        private int position;
        /**
         * 文本总行
         */
        private int totalLines;
        /**
         * 光标当前行
         */
        private int line;
        /**
         * 光标当前列
         */
        private int row;
        /**
         * 光标当前行内容
         */
        private String lineText;
        /**
         * 光标当前行内容长度
         */
        private int lineLength;
        /**
         * 文本总长度
         */
        private int textLength;

        private QRCaretUpdateData() {
            if (QRTextPane.this.caretListener == null) {
                throw new IllegalStateException("请先调用 addCaretListener() 方法！");
            }
        }

        private QRCaretUpdateData(int line, int row) {
            this();
            this.line = line;
            this.row = row;
        }

        private void setTotalLines(int totalLines) {
            this.totalLines = totalLines;
        }

        private void setLine(int line) {
            this.line = line;
        }

        private void setRow(int row) {
            this.row = row;
        }

        private void setLineText(String lineText) {
            this.lineText = lineText;
            this.lineLength = lineText.length();
        }

        private void setTextLength(int textLength) {
            this.textLength = textLength;
        }

        /**
         * 光标位置
         */
        private void setPosition(int position) {
            this.position = position;
        }

        /**
         * 文本总行
         */
        public int totalLines() {
            return this.totalLines;
        }

        /**
         * 光标当前行，从 {@code 1} 开始
         */
        public int line() {
            return this.line;
        }

        /**
         * 光标当前列，从 {@code 0} 开始
         */
        public int row() {
            return this.row;
        }

        /**
         * 光标当前行内容
         */
        public String lineText() {
            return this.lineText;
        }

        /**
         * 光标当前行内容长度
         */
        public int lineLength() {
            return this.lineLength;
        }

        /**
         * 文本总长度
         */
        public int textLength() {
            return this.textLength;
        }

        /**
         * 光标位置
         */
        public int position() {
            return this.position;
        }
    }

    /**
     * @author Kiarelemb QR
     * @program: QR_Swing
     * @description:
     * @create 2022-11-24 15:40
     **/
    public final class QRCaretNearData {
        private final String foreText;
        private final String nextText;
        private final String foreWord;
        private final String nextWord;
        private final String foreCharStr;
        private final String nextCharStr;
        private final boolean isCutWord;
        private final String cutWord;
        private final int foreTextStartIndex;
        private final int foreWordStartIndex;
        private final int foreWordEndIndex;
        private final int nextWordStartIndex;
        private final int nextTextEndIndex;
        private final int nextWordEndIndex;
        private final String text;

        /**
         * 最多进行了四次的搜索
         *
         * @param position 光标位置
         * @param text     光标所在的文本
         */
        public QRCaretNearData(int position, String text) {
            this.text = text;
            int textLen;
            if (text == null || (textLen = text.length()) == 0 || textLen < position) {
                this.foreText = "";
                this.nextText = "";
                this.foreWord = "";
                this.nextWord = "";
                this.foreCharStr = "";
                this.nextCharStr = "";
                this.isCutWord = false;
                this.cutWord = "";
                this.foreTextStartIndex = -1;
                this.foreWordStartIndex = -1;
                this.nextTextEndIndex = -1;
                this.foreWordEndIndex = -1;
                this.nextWordStartIndex = -1;
                this.nextWordEndIndex = -1;
            } else {
                if (position == 0) {
                    this.foreText = "";
                    this.foreWord = "";
                    this.foreCharStr = "";
                    this.foreTextStartIndex = -1;
                    this.foreWordStartIndex = -1;
                    this.foreWordEndIndex = -1;
                } else {
                    int foreLineIndexTemp = text.lastIndexOf(QRTextPane.this.lineMark, position);
                    int foreLineIndex = foreLineIndexTemp == position ? foreLineIndexTemp : foreLineIndexTemp + 1;
                    this.foreTextStartIndex = foreLineIndex;
                    this.foreText = text.substring(foreLineIndex, position);
                    this.foreWord = caretForeWord(position, text);
                    this.foreWordStartIndex = text.lastIndexOf(this.foreWord, position);
                    this.foreWordEndIndex = this.foreWordStartIndex + this.foreWord.length();
                    if (this.foreText.length() > 0) {
                        this.foreCharStr = this.foreText.substring(this.foreText.length() - 1);
                    } else {
                        this.foreCharStr = "";
                    }
                }
                if (position == textLen) {
                    this.nextText = "";
                    this.nextWord = "";
                    this.nextCharStr = "";
                    this.nextTextEndIndex = -1;
                    this.nextWordStartIndex = -1;
                    this.nextWordEndIndex = -1;
                } else {
                    int nextLineIndex = text.indexOf(QRTextPane.this.lineMark, position);
                    if (nextLineIndex == -1) {
                        this.nextText = text.substring(position);
                        this.nextTextEndIndex = this.nextText.length();
                    } else {
                        this.nextText = text.substring(position, nextLineIndex);
                        this.nextTextEndIndex = nextLineIndex;
                    }
                    this.nextWord = caretNextWord(position, text);
                    this.nextWordStartIndex = text.indexOf(this.nextWord, position);
                    this.nextWordEndIndex = this.nextWordStartIndex + this.nextWord.length();
                    if (this.nextText.length() > 0) {
                        this.nextCharStr = this.nextText.substring(0, 1);
                    } else {
                        this.nextCharStr = "";
                    }
                }
                if (!this.foreCharStr.isBlank() && !this.nextCharStr.isBlank()) {
                    this.isCutWord = true;
                    this.cutWord = this.foreWord + this.nextWord;
                } else {
                    this.isCutWord = false;
                    this.cutWord = "";
                }
            }
        }

        public String foreText() {
            return this.foreText;
        }

        public String nextText() {
            return this.nextText;
        }

        public String foreWord() {
            return this.foreWord;
        }

        public String nextWord() {
            return this.nextWord;
        }

        public String nearWord() {
            if (this.nextWordEndIndex < this.foreWordStartIndex) {
                return this.text.substring(this.nextWordEndIndex + this.foreWordStartIndex);
            }
            if (this.foreWordStartIndex < 0) {
                return this.text.substring(0, this.nextWordEndIndex);
            }
            return this.text.substring(this.foreWordStartIndex, this.nextWordEndIndex).trim();
        }

        public String foreCharStr() {
            return this.foreCharStr;
        }

        public String nextCharStr() {
            return this.nextCharStr;
        }

        public boolean isCutWord() {
            return this.isCutWord;
        }

        public String cutWord() {
            return this.cutWord;
        }

        public int foreTextStartIndex() {
            return this.foreTextStartIndex;
        }

        public int nextTextEndIndex() {
            return this.nextTextEndIndex;
        }

        public int foreWordStartIndex() {
            return this.foreWordStartIndex;
        }

        public int nextWordEndIndex() {
            return this.nextWordEndIndex;
        }

        public int foreWordEndIndex() {
            return this.foreWordEndIndex;
        }

        public int nextWordStartIndex() {
            return this.nextWordStartIndex;
        }

        @Override
        public String toString() {
            return "CaretNearData {" + "\n\tforeText: \n\t\t" + this.foreText + "\n\tnextText: \n\t\t" + this.nextText + "\n\tforeWord: \n\t\t" + this.foreWord + "\n\tnextWord: \n\t\t" + this.nextWord + "\n\tforeCharStr: \n\t\t" + this.foreCharStr + "\n\tnextCharStr: \n\t\t" + this.nextCharStr + "\n\tforeTextStartIndex: \n\t\t" + this.foreTextStartIndex + "\n\tnextTextEndIndex: \n\t\t" + this.nextTextEndIndex + "\n\tforeWordStartIndex: \n\t\t" + this.foreWordStartIndex + "\n\tforeWordEndIndex: \n\t\t" + this.foreWordEndIndex + "\n\tnextWordStartIndex: \n\t\t" + this.nextWordStartIndex + "\n\tnextWordEndIndex: \n\t\t" + this.nextWordEndIndex + "\n\tisCutWord: \n\t\t" + this.isCutWord + "\n\tcutWord: \n\t\t" + this.cutWord + "\n}";
        }
    }
}