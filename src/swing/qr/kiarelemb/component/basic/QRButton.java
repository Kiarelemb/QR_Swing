package swing.qr.kiarelemb.component.basic;

import swing.qr.kiarelemb.adapter.QRButtonMouseListener;
import swing.qr.kiarelemb.component.assembly.QRToolTip;
import swing.qr.kiarelemb.component.combination.QRMenuButton;
import swing.qr.kiarelemb.component.combination.QRMenuButtonOriginal;
import swing.qr.kiarelemb.component.listener.QRActionListener;
import swing.qr.kiarelemb.component.listener.QRMouseListener;
import swing.qr.kiarelemb.component.listener.QRMouseMotionListener;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.listener.add.QRActionListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseMotionListenerAdd;
import swing.qr.kiarelemb.resource.QRSwingInfo;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.window.enhance.QROpinionDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 按扭
 * @create 2022-11-04 16:13
 **/
public class QRButton extends JButton implements QRComponentUpdate, QRActionListenerAdd, QRMouseMotionListenerAdd,
		QRMouseListenerAdd {
	protected QRButtonMouseListener bml = new QRButtonMouseListener(this, QRColorsAndFonts.ENTER_COLOR,
			QRColorsAndFonts.PRESS_COLOR);
	private QRMouseMotionListener mouseMotionListener;
	private QRMouseListener mouseListener;
	private QRActionListener clickListener;

	public static class QRIntroduceButton extends QRButton {
		private final Window parent;
		private final String toolTipText;

		public QRIntroduceButton(Window parent, String toolTipText) {
			super();
			this.parent = parent;
			this.toolTipText = toolTipText;
			setToolTipText(toolTipText);
			setIcon(new ImageIcon(QRSwingInfo.loadUrl("introduction.png")));
			setSize(12, 12);
		}

		@Override
		protected void actionEvent(ActionEvent o) {
			QROpinionDialog.messageTellShow(this.parent, this.toolTipText);
		}
	}

	public QRButton() {
		addMouseListener(this.bml);
		setFocusPainted(false);
		setBorderPainted(false);
		setBorder(null);
		addActionListener();
		componentFresh();
	}

	public QRButton(String text) {
		addMouseListener(this.bml);
		setFocusPainted(false);
		setBorderPainted(false);
		setBorder(null);
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
			this.clickListener.add(e -> actionEvent((ActionEvent) e));
			addActionListener(this.clickListener);
		}
	}

	/**
	 * 添加单击事件
	 *
	 * @param ar 操作
	 */
	@Override
	public final void addClickAction(QRActionRegister ar) {
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
	 * 添加鼠标事件，请先调用 {@link #addMouseListener()}
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

	@Override
	public synchronized void addMouseListener(MouseListener l) {
		super.addMouseListener(l);
		if (l instanceof QRButtonMouseListener) {
			if (!l.equals(this.bml)) {
				this.bml = (QRButtonMouseListener) l;
			}
		}
	}
	//endregion

	//region 按钮颜色设置
	public void setEnterColor(Color enterColor) {
		this.bml.setEnterColor(enterColor);
	}

	public void setPressColor(Color pressColor) {
		this.bml.setPressColor(pressColor);
	}

	public void setBackColor(Color defaultBackColor) {
		this.bml.setBackColor(defaultBackColor);
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

	//endregion

	public void disableListener() {
		this.removeMouseListener(this.bml);
	}

	public void add(QRMenuItem qmi) {
		if (!(this instanceof QRMenuButton) && !(this instanceof QRMenuButtonOriginal)) {
			throw new IllegalStateException("该方法只为菜单按钮而设立！");
		}
	}

	/**
	 * 本方法绕过鼠标点击的模拟，直接运行 {@link #clickListener} 中的 {@link QRActionListener#actionPerformed(ActionEvent)}
	 * 方法。这就意味着，只有本类中的 {@link #actionEvent(ActionEvent)} 和调用了 {@link #addClickAction(QRActionRegister)} 中的事件将被触发
	 * <p>需要注意的是，若运行的事件中大量包含界面 UI 的绘制，那本方法可能比 {@link #click()} 更合适</p>
	 */
	public void clickInvokeLater() {
		SwingUtilities.invokeLater(this::click);
	}

	/**
	 * 本方法绕过鼠标点击的模拟，直接运行 {@link #clickListener} 中的 {@link QRActionListener#actionPerformed(ActionEvent)}
	 * 方法。这就意味着，只有本类中的 {@link #actionEvent(ActionEvent)} 和调用了 {@link #addClickAction(QRActionRegister)} 中的事件将被触发
	 */
	public void click() {
		clickListener.actionPerformed(null);
	}

	/**
	 * 推荐使用本类中的 {@link #addClickAction(QRActionRegister)} 方法
	 */
	@Deprecated()
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

	@Override
	public void setEnabled(boolean b) {
		super.setEnabled(b);
		if (b) {
			addMouseListener(this.bml);
		} else {
			disableListener();
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
		setForeground(QRColorsAndFonts.MENU_COLOR);
		setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
		setFont(QRColorsAndFonts.MENU_ITEM_DEFAULT_FONT);
		if (bml.isNotCloseButton) {
			this.bml.setEnterColor(QRColorsAndFonts.ENTER_COLOR);
			this.bml.setPressColor(QRColorsAndFonts.PRESS_COLOR);
		} else {
			this.bml.setEnterColor(Color.RED);
			this.bml.setPressColor(QRColorsAndFonts.DEFAULT_COLOR_LABEL);
		}
		this.bml.setBackColor(QRColorsAndFonts.FRAME_COLOR_BACK);
	}
}