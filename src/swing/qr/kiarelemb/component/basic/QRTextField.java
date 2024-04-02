package swing.qr.kiarelemb.component.basic;

import method.qr.kiarelemb.utils.QRStringUtils;
import swing.qr.kiarelemb.component.assembly.QRToolTip;
import swing.qr.kiarelemb.component.assembly.QRUndoManager;
import swing.qr.kiarelemb.component.listener.*;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.component.QRTextBasicActionSetting;
import swing.qr.kiarelemb.inter.listener.add.*;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.window.basic.QRFrame;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * @author Kiarelemb QR
 * @program: EnglishAnalyzer
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
	/**
	 * {@code 0} 为未设置，{@code 1} 为左，{@code 2} 为右
	 */
	private int clearButtonState = 0;

	public QRTextField() {
		setMargin(new Insets(10, 10, 10, 10));
		addKeyListener();
		addFocusListener();
		componentFresh();
	}

	public QRTextField(String text) {
		this();
		setText(text);
	}

	public QRTextField(String text, boolean numbersOnly) {
		this(text);
		if (numbersOnly) {
			numbersOnly();
		}
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
	 * 添加焦点事件，已自动添加
	 */
	@Override
	public final void addFocusListener() {
		if (this.focusListener == null) {
			this.focusListener = new QRFocusListener();
			this.focusListener.add(QRFocusListener.TYPE.GAIN, e -> this.focusGained((FocusEvent) e));
			this.focusListener.add(QRFocusListener.TYPE.LOST, e -> this.focusLost((FocusEvent) e));
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
	 * 添加文本内容更新事件
	 */
	@Override
	public final void addDocumentListener() {
		if (this.documentListener == null) {
			this.documentListener = new QRDocumentListener();
			this.documentListener.add(QRDocumentListener.TYPE.INSERT, e -> this.insertUpdate((DocumentEvent) e));
			this.documentListener.add(QRDocumentListener.TYPE.REMOVE, e -> this.removeUpdate((DocumentEvent) e));
			this.documentListener.add(QRDocumentListener.TYPE.CHANGED, e -> this.changedUpdate((DocumentEvent) e));
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
	 * 添加按键的事件，已自动添加
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
	 * 使文本框能够撤销重做
	 */
	public void addUndoManager() {
		this.undoManager = new QRUndoManager(this);
	}

	public void addClearButton(boolean right) {
		setLayout(new BorderLayout());
		clearButtonState = right ? 2 : 1;
		QRButton clearButton = new QRButton(QRFrame.CLOSE_MARK) {
			{
				addMouseListener();
			}

			@Override
			protected void mouseEnter(MouseEvent e) {
				setCursor(Cursor.getDefaultCursor());
			}

			@Override
			public void componentFresh() {
				super.componentFresh();
				setFont(QRColorsAndFonts.PROCESS_BUTTON_FONT.deriveFont(11f).deriveFont(Font.BOLD));
			}

			@Override
			protected void actionEvent(ActionEvent o) {
				QRTextField.this.clear();
			}
		};
		//按钮宽30
		add(clearButton, right ? BorderLayout.EAST : BorderLayout.WEST);
		setMargin(new Insets(10, right ? 10 : 40, 10, right ? 40 : 10));
	}

	//endregion

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