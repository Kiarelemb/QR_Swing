package swing.qr.kiarelemb.basic;

import method.qr.kiarelemb.utils.QRStringUtils;
import swing.qr.kiarelemb.assembly.QRCaret;
import swing.qr.kiarelemb.assembly.QRToolTip;
import swing.qr.kiarelemb.assembly.QRUndoManager;
import swing.qr.kiarelemb.combination.QRPopupMenu;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.QRTextBasicActionSetting;
import swing.qr.kiarelemb.inter.listener.add.*;
import swing.qr.kiarelemb.listener.*;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.plaf.basic.BasicTextFieldUI;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * QR Swing 的单行文本输入框。
 *
 * <p>该类基于 {@link JTextField}，统一了主题字体和颜色、自定义光标、焦点边框、
 * 键盘/鼠标/文档/光标事件注册、撤销重做以及常用输入限制。构造时会自动安装键盘和焦点监听器；
 * 文档、鼠标和光标监听器按需通过 {@code addXXXListener()} 或 {@code addXXXAction(...)} 添加。</p>
 *
 * <p>输入限制由 {@link TYPE} 或 {@link #addForbiddenChar(char...)} 等方法配置。限制发生在
 * {@link KeyEvent#KEY_TYPED} 阶段，只拦截用户键入字符；如果通过 {@link #setText(String)} 设置文本，
 * 调用方仍需要自行保证内容合法。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRTextField scoreField = new QRTextField(QRTextField.TYPE.NUMBERS_AND_DECIMAL);
 * scoreField.addUndoManager();
 * scoreField.addDocumentListenerActionAll(e -> preview(scoreField.getText()));
 *
 * QRTextField fileName = new QRTextField(QRTextField.TYPE.FILE_NAME);
 * fileName.addForbiddenChar('#', '%');
 * </code></pre>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2022-11-30 14:36
 **/
public class QRTextField extends JTextField implements QRComponentUpdate, QRTextBasicActionSetting, QRCaretListenerAdd, QRFocusListenerAdd,
		QRDocumentListenerAdd, QRKeyListenerAdd, QRMouseListenerAdd, QRMouseMotionListenerAdd {
	/**
	 * 内置输入类型。
	 *
	 * <p>这些类型只配置键入字符过滤，不做完整语义校验。例如 {@link #NUMBERS_AND_DECIMAL}
	 * 允许输入多个小数点；如需更严格规则，可重写 {@link #meetCondition()} 或监听文本变化。</p>
	 */
	public enum TYPE {
		/**
		 * 默认
		 */
		DEFAULT,
		/**
		 * 文件路径
		 */
		FILE_PATH,
		/**
		 * 文件名
		 */
		FILE_NAME,
		/**
		 * 数字
		 */
		NUMBERS,
		/**
		 * 数字和小数
		 */
		NUMBERS_AND_DECIMAL
	}

	private final StringBuilder forbiddenInputChar = new StringBuilder();
	private final StringBuilder onlyAllowedInputChar = new StringBuilder();
	protected Color enterColor = QRColorsAndFonts.BLUE_LIGHT;
	protected Color rightColor = QRColorsAndFonts.LIGHT_GREEN;
	protected Color errorColor = QRColorsAndFonts.RED_NORMAL;

	/**
	 * 使用前请先调用 {@link #addUndoManager()}
	 */
	public QRUndoManager undoManager;
	protected QRPopupMenu popupMenu;
	private QRDocumentListener documentListener;
	private QRCaretListener caretListener;
	private QRFocusListener focusListener;
	private QRKeyListener keyListener;
	private QRMouseListener mouseListener;
	private QRMouseMotionListener mouseMotionListener;
	private boolean initialized;

	public QRTextField() {
		this(null, TYPE.DEFAULT);
	}

	public QRTextField(TYPE type) {
		this(null, type);
	}

	public QRTextField(String text) {
		this(text, TYPE.DEFAULT);
	}

	public QRTextField(String text, TYPE type) {
		setText(text);
		setCaret(new QRCaret());
		addKeyListener();
		addFocusListener();
		setIgnoreRepaint(false);
		setType(type);
		this.initialized = true;
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
	 * 使文本框能够撤销重做。
	 *
	 * <p>调用后 {@link #undoManager} 才会被初始化，之后可通过其 Action 绑定菜单项或快捷键。</p>
	 */
	public void addUndoManager() {
		this.undoManager = new QRUndoManager(this);
	}

	/**
	 * 为文本框创建并绑定右键菜单。重复调用返回同一实例。
	 *
	 * @return 绑定当前文本框的右键菜单
	 */
	public QRPopupMenu addPopupMenu() {
		return addPopupMenu(null);
	}

	/**
	 * 为文本框创建并绑定右键菜单，并在显示前执行回调。
	 *
	 * <p>回调可用于动态更新菜单状态。只有首次创建菜单时传入的回调会被绑定。</p>
	 *
	 * @param beforeShow 菜单显示前的回调，可为 null
	 * @return 绑定当前文本框的右键菜单
	 */
	public QRPopupMenu addPopupMenu(QRActionRegister<MouseEvent> beforeShow) {
		if (this.popupMenu == null) {
			this.popupMenu = QRPopupMenu.createAndBind(this, beforeShow);
		}
		return this.popupMenu;
	}
	//endregion

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
	 * 设置文本框的内置输入类型。
	 *
	 * <p>可在构造后再次调用。多次调用会在现有过滤规则上继续追加字符规则，
	 * 不会清空此前通过 {@link #addForbiddenChar(char...)} 或其他类型方法添加的限制。</p>
	 *
	 * @param type 输入类型，不能为 {@code null}
	 */
	public void setType(TYPE type) {
		switch (type) {
			case NUMBERS -> numbersOnly();
			case NUMBERS_AND_DECIMAL -> numberAndDecimal();
			case FILE_PATH -> filePathField();
			case FILE_NAME -> fileNameField();
		}
	}

	/**
	 * 添加文件路径非法字符过滤。
	 *
	 * <p>会禁止 {@code * ? " < > |}，但允许目录分隔符 {@code /} 和 {@code \}。</p>
	 */
	public void filePathField() {
		String illegalMarks = "*?\"<>|";
		this.forbiddenInputChar.append(illegalMarks);
	}

	/**
	 * 添加文件名非法字符过滤。
	 *
	 * <p>会禁止 {@code \ / : * ? " < > |}，适合保存文件名、模板名等不应包含路径分隔符的输入。</p>
	 */
	public void fileNameField() {
		String illegalMarks = "\\/:*?\"<>|";
		this.forbiddenInputChar.append(illegalMarks);
	}

	/**
	 * 设置为只能输入数字字符 {@code 0-9}。
	 *
	 * <p>该限制只拦截键入字符，不会阻止通过粘贴或 {@link #setText(String)} 写入非数字内容。</p>
	 */
	public void numbersOnly() {
		String numbers = "1234567890";
		this.onlyAllowedInputChar.append(numbers);
	}

	/**
	 * 允许输入数字和小数点。
	 *
	 * <p>该方法不保证最终文本是合法小数，例如不会限制只能出现一个小数点。</p>
	 */
	public void numberAndDecimal() {
		String numbers = "1234567890.";
		this.onlyAllowedInputChar.append(numbers);
	}

	/**
	 * 追加禁止键入的字符。
	 *
	 * @param chars 禁止输入的字符列表
	 */
	public void addForbiddenChar(char... chars) {
		for (char c : chars) {
			this.forbiddenInputChar.append(c);
		}
	}

	/**
	 * 追加禁止键入的字符串中的每个字符。
	 *
	 * @param string 字符集合，null 会导致 {@link StringBuilder#append(String)} 追加字符串 "null"
	 */
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
		if (Character.isISOControl(e.getKeyChar())) {
			return false;
		}
		final String value = String.valueOf(e.getKeyChar());
		if (!this.onlyAllowedInputChar.isEmpty()) {
			return this.onlyAllowedInputChar.indexOf(value) == -1;
		}
		return this.forbiddenInputChar.indexOf(value) != -1;
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
	public void updateUI() {
		setUI(new BasicTextFieldUI());
		if (this.initialized) {
			componentFresh();
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
