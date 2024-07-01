package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.assembly.QRCaret;
import swing.qr.kiarelemb.assembly.QRToolTip;
import swing.qr.kiarelemb.assembly.QRUndoManager;
import swing.qr.kiarelemb.listener.*;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRBackgroundUpdate;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.listener.add.*;
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
 * @create 2023-01-11 15:11
 **/
public class QRTextArea extends JTextArea implements QRComponentUpdate, QRCaretListenerAdd, QRFocusListenerAdd, QRDocumentListenerAdd, QRKeyListenerAdd, QRMouseListenerAdd, QRMouseMotionListenerAdd, QRBackgroundUpdate {
	public Font textFont = QRColorsAndFonts.STANDARD_FONT_TEXT;
	/**
	 * 使用前请先调用 {@link #addUndoManager()}
	 */
	public QRUndoManager undoManager;
	public final QRCaret caret;
	protected boolean caretBlock = false;
	protected QRScrollPane scrollPane;
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
			this.documentListener.add(QRDocumentListener.TYPE.INSERT, e -> QRTextArea.this.insertUpdate((DocumentEvent) e));
			this.documentListener.add(QRDocumentListener.TYPE.REMOVE, e -> QRTextArea.this.removeUpdate((DocumentEvent) e));
			this.documentListener.add(QRDocumentListener.TYPE.CHANGED, e -> QRTextArea.this.changedUpdate((DocumentEvent) e));
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
	 * 添加滚动条
	 *
	 * @return 滚动条本身，实例是 {@link QRScrollPane}
	 */
	public QRScrollPane addScrollPane() {
		if (this.scrollPane == null) {
			this.scrollPane = new QRScrollPane();
			this.scrollPane.setViewportView(this);
			//单次滚动5行
			int line = 5;
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

	/**
	 * 当添加了背景图片时，就需要不断更新文本面板，以确保它透明
	 *
	 * <p>确保窗体中有面板设置了 {@link swing.qr.kiarelemb.assembly.QRBackgroundBorder}
	 */
	@Override
	public final void addCaretListenerForBackgroundUpdate() {
		QRActionRegister action = e -> {
			if (!this.caretBlock) {
				try {
					SwingUtilities.getWindowAncestor(QRTextArea.this).repaint();
				} catch (Exception ignore) {
				}
			}
		};
		if (this.caretListener != null) {
			addCaretListenerAction(action);
		} else {
			addCaretListener(action::action);
		}
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
	 * 清除内容
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
}