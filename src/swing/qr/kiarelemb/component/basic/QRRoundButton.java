package swing.qr.kiarelemb.component.basic;

import swing.qr.kiarelemb.component.assembly.QRToolTip;
import swing.qr.kiarelemb.component.listener.QRActionListener;
import swing.qr.kiarelemb.component.listener.QRMouseListener;
import swing.qr.kiarelemb.component.listener.QRMouseMotionListener;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.listener.add.QRActionListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseMotionListenerAdd;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-21 19:13
 **/
public class QRRoundButton extends JButton implements QRComponentUpdate, QRActionListenerAdd, QRMouseListenerAdd, QRMouseMotionListenerAdd {
	private final int ARC = 15;
	private volatile boolean isEntered = false;
	private volatile boolean isPressed = false;
	private QRMouseMotionListener mouseMotionListener;
	private QRMouseListener mouseListener;
	private QRActionListener clickListener;

	public QRRoundButton() {
		setContentAreaFilled(false);
		setFocusPainted(false);
		componentFresh();
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				QRRoundButton.this.isPressed = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				QRRoundButton.this.isPressed = false;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				QRRoundButton.this.isEntered = true;
			}

			@Override
			public void mouseExited(MouseEvent e) {
				QRRoundButton.this.isEntered = false;
			}
		};
		addMouseListener(mouseAdapter);
		addActionListener();
	}

	public QRRoundButton(String text) {
		this();
		setText(text);
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
	 * 在实例化时直接重写
	 */
	protected void actionEvent(ActionEvent o) {
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
	//endregion

	//region 各种重写

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

	/**
	 * 本方法绕过鼠标点击的模拟，直接运行 {@link #clickListener} 中的 {@link QRActionListener#actionPerformed(ActionEvent)}
	 * 方法。这就意味着，只有本类中的 {@link #actionEvent(ActionEvent)} 和调用了 {@link #addClickAction(QRActionRegister)} 中的事件将被触发
	 */
	public void click() {
		clickListener.actionPerformed(null);
	}

	@Deprecated(since = "推荐使用本类中的 addClickAction(QRActionRegister ar) 方法")
	@Override
	public void addActionListener(ActionListener l) {
		super.addActionListener(l);
	}

	@Deprecated(since = "已被本类中的 click() 方法取代")
	@Override
	public void doClick() {
		super.doClick();
	}


	@Override
	public JToolTip createToolTip() {
		QRToolTip tip = new QRToolTip();
		tip.setComponent(tip);
		return tip;
	}

	@Override
	public void componentFresh() {
		setFont(QRColorsAndFonts.MENU_ITEM_DEFAULT_FONT);
		setForeground(QRColorsAndFonts.MENU_COLOR);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
//        if (isEnabled()) {
		g2.setColor(this.isPressed ? QRColorsAndFonts.PRESS_COLOR : (this.isEntered ? QRColorsAndFonts.ENTER_COLOR : QRColorsAndFonts.LINE_COLOR));
//        }
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, this.ARC, this.ARC);
		super.paintComponent(g2);
	}

	@Override
	protected void paintBorder(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(QRColorsAndFonts.BORDER_COLOR);
		g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, this.ARC, this.ARC);
		super.paintBorder(g2);
	}


	@Override
	public void setEnabled(boolean b) {
		setForeground(b ? QRColorsAndFonts.MENU_COLOR : QRColorsAndFonts.FRAME_COLOR_BACK);
		super.setEnabled(b);
	}
}