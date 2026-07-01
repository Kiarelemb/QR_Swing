package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.assembly.QRToolTip;
import swing.qr.kiarelemb.combination.QRMenuButton;
import swing.qr.kiarelemb.combination.QRMenuButtonOriginal;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.listener.add.QRActionListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseMotionListenerAdd;
import swing.qr.kiarelemb.listener.QRActionListener;
import swing.qr.kiarelemb.listener.QRMouseListener;
import swing.qr.kiarelemb.listener.QRMouseMotionListener;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * QR Swing 的基础按钮。
 *
 * <p>该类基于 {@link JButton}，统一了主题字体、透明绘制、悬停/按下背景色、
 * 自定义 Tooltip、点击事件封装以及鼠标事件注册。普通使用时优先调用
 * {@link #addClickAction(QRActionRegister)} 注册点击动作，而不是直接添加 Swing 原生
 * {@link ActionListener}。</p>
 *
 * <p>按钮始终以透明组件方式绘制，{@link #setOpaque(boolean)} 会被固定为 {@code false}。
 * 如果窗口开启背景图片，按钮的悬停/按下背景会按当前窗口背景模式调整透明度。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRButton saveButton = new QRButton("保存", "Ctrl+S");
 * saveButton.addClickAction(e -> save());
 * QRSwing.registerGlobalAction("ctrl s", saveButton.actionRegister(), true);
 * </code></pre>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 按钮
 * @create 2022-11-04 16:13
 **/
public class QRButton extends JButton implements QRComponentUpdate, QRActionListenerAdd, QRMouseMotionListenerAdd,
		QRMouseListenerAdd {
	protected final QRActionRegister<KeyStroke> actionRegister = e -> this.clickInvokeLater();
	private QRMouseMotionListener mouseMotionListener;
	private QRMouseListener mouseListener;
	private QRActionListener clickListener;

	public QRButton() {
		this(null);
	}

	public QRButton(String text) {
		setFocusPainted(false);
		setBorderPainted(false);
		setBorder(null);
		setContentAreaFilled(false);
		setOpaque(false);
		addActionListener();
		setText(text);
		componentFresh();
	}

	public QRButton(String text, String toolTipText) {
		this(text);
		setToolTipText(toolTipText);
	}

	//region 各种添加

	/**
	 * 给按钮添加单击事件，在实例化时已自动添加
	 */
	@Override
	public void addActionListener() {
		if (this.clickListener == null) {
			this.clickListener = new QRActionListener();
			this.clickListener.add(this::actionEvent);
			addActionListener(this.clickListener);
		}
	}

	/**
	 * 添加单击事件
	 * 已自动添加 {@link #addActionListener()}
	 *
	 * <p>多个动作会按注册顺序执行。通过 {@link #click()}、{@link #clickInvokeLater()}、
	 * 鼠标点击或全局快捷键触发时，都会进入同一组点击动作。</p>
	 *
	 * @param ar 操作
	 */
	@Override
	public final void addClickAction(QRActionRegister<ActionEvent> ar) {
		if (this.clickListener == null) {
			addActionListener();
		}
		if (this.clickListener != null) {
			this.clickListener.add(ar);
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
	//endregion

	//region 各种重写

	/**
	 * 已自动添加监听器，可直接重写
	 */
	protected void actionEvent(ActionEvent o) {
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

	public void setPreferredSize(int width, int height) {
		Dimension size = new Dimension(width, height);
		super.setPreferredSize(size);
	}

	//endregion

	//region 取得监听器

	public QRMouseMotionListener mouseMotionListener() {
		return mouseMotionListener;
	}

	public QRMouseListener mouseListener() {
		return mouseListener;
	}

	public QRActionListener clickListener() {
		return clickListener;
	}

	//endregion

	/**
	 * 菜单按钮专用扩展点。
	 *
	 * <p>普通 {@code QRButton} 不支持添加菜单项；只有 {@link QRMenuButton} 和
	 * {@link QRMenuButtonOriginal} 子类可以使用该方法。</p>
	 *
	 * @param qmi 菜单项
	 */
	public void add(QRMenuItem qmi) {
		if (!(this instanceof QRMenuButton) && !(this instanceof QRMenuButtonOriginal)) {
			throw new IllegalStateException("该方法只为菜单按钮而设立！");
		}
	}

	/**
	 * 本方法绕过鼠标点击的模拟，直接运行 {@link #clickListener} 中的 {@link QRActionListener#actionPerformed(ActionEvent)}
	 * 方法。这就意味着，只有本类中的 {@link #actionEvent(ActionEvent)} 和调用了 {@link #addClickAction(QRActionRegister)} 中的事件将被触发
	 * <p>需要注意的是，若运行的事件中大量包含界面 UI 的绘制，那本方法可能比 {@link #click()} 更合适</p>
	 * <p>常用于全局快捷键回调中触发按钮动作。</p>
	 */
	public void clickInvokeLater() {
		SwingUtilities.invokeLater(this::click);
	}

	/**
	 * 本方法绕过鼠标点击的模拟，直接运行 {@link #clickListener} 中的 {@link QRActionListener#actionPerformed(ActionEvent)}
	 * 方法。这就意味着，只有本类中的 {@link #actionEvent(ActionEvent)} 和调用了 {@link #addClickAction(QRActionRegister)} 中的事件将被触发
	 *
	 * <p>该方法会在当前线程立即执行监听器。如果当前线程不是 EDT，且监听器会更新界面，应改用
	 * {@link #clickInvokeLater()}。</p>
	 */
	public void click() {
		clickListener.actionPerformed(null);
	}

	/**
	 * 推荐使用本类中的 {@link #addClickAction(QRActionRegister)} 方法
	 */
	@Deprecated
	@Override
	public void addActionListener(ActionListener l) {
		super.addActionListener(l);
	}

	/**
	 * 已被本类中的 {@link #click()} 方法取代
	 */
	@Deprecated()
	@Override
	public void doClick() {
		super.doClick();
	}

	/**
	 * 返回可注册到全局快捷键系统的按钮动作。
	 *
	 * <p>该动作会调用 {@link #clickInvokeLater()}，因此适合直接传给
	 * {@link QRSwing#registerGlobalAction(String, QRActionRegister, boolean)}。</p>
	 *
	 * @return 快捷键动作
	 */
	public QRActionRegister<KeyStroke> actionRegister() {
		return actionRegister;
	}

	@Override
	public void setOpaque(boolean isOpaque) {
		super.setOpaque(false);
	}

	@Override
	public JToolTip createToolTip() {
		QRToolTip tip = new QRToolTip();
		tip.setComponent(tip);
		return tip;
	}

	@Override
	public void componentFresh() {
		setForeground(isEnabled() ? QRColorsAndFonts.MENU_COLOR : QRColorsAndFonts.DISABLED_COLOR_FORE);
		setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
		setFont(QRColorsAndFonts.MENU_ITEM_DEFAULT_FONT);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		setForeground(enabled ? QRColorsAndFonts.MENU_COLOR : QRColorsAndFonts.DISABLED_COLOR_FORE);
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		paintButtonBackground(g2);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		paintButtonContent(g);
	}

	protected void paintButtonBackground(Graphics2D g2) {
		var model = getModel();
		if ((model.isRollover() || model.isPressed()) && isEnabled()) {
			g2.setColor(model.isPressed() ? QRColorsAndFonts.PRESS_COLOR : QRColorsAndFonts.ENTER_COLOR);
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.addRenderingHints(rh);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, QRSwing.windowImageSet ? (model.isPressed() ? 1f : (model.isRollover() ? 0.7f : 0.5f)) : 1f));
			g2.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	protected void paintButtonContent(Graphics g) {
		super.paintComponent(g);
	}
}
