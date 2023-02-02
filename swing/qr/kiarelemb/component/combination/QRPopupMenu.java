package swing.qr.kiarelemb.component.combination;

import method.qr.kiarelemb.utils.QRFontUtils;
import method.qr.kiarelemb.utils.QRSystemUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.component.basic.QRButton;
import swing.qr.kiarelemb.component.basic.QRMenuItem;
import swing.qr.kiarelemb.component.listener.QRActionListener;
import swing.qr.kiarelemb.component.listener.QRFocusListener;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.listener.add.QRFocusListenerAdd;
import swing.qr.kiarelemb.window.basic.QREmptyDialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-04 17:25
 **/
public class QRPopupMenu extends QREmptyDialog implements QRFocusListenerAdd {
	protected final int vgap = 4;
	protected int itemNums;
	protected int itemMaxLen;
	protected int itemMaxTipLen;
	private final QRActionListener actionListener;
	private QRFocusListener focusListener;

	public QRPopupMenu(Window parent) {
		super(parent, false);
		this.contentPane.setLayout(new GridLayout(0, 1, 3, this.vgap));
		setFreelyMotionFailed();
		setFocusable(true);
		this.actionListener = new QRActionListener();
		this.actionListener.add(e -> QRPopupMenu.this.buttonSelectAction((ActionEvent) e));
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
	 * 子类重写其中被选重时的操作
	 *
	 * @param event
	 */
	protected void buttonSelectAction(ActionEvent event) {

		//TODO 自写一个事件吧
	}

	public void addSeparator() {
	}

	/**
	 * 用来当作菜单栏
	 *
	 * @param menuItem
	 */
	public void add(QRMenuItem menuItem) {
		this.contentPane.add(menuItem);
		menuItem.addActionListener(this.actionListener);
		this.itemNums++;
		int textInWidth = QRFontUtils.getTextInWidth(menuItem, menuItem.getText());
		if (menuItem.quickTip() != null) {
			this.itemMaxTipLen = Math.max(this.itemMaxTipLen, QRFontUtils.getTextInWidth(menuItem, menuItem.quickTip()));
		}
		this.itemMaxLen = Math.max(this.itemMaxLen, textInWidth);
	}

	public void add(QRButton button) {
		this.contentPane.add(button);
		button.addActionListener(this.actionListener);
		this.itemNums++;
		int textInWidth = QRFontUtils.getTextInWidth(button, button.getText());
		this.itemMaxLen = Math.max(this.itemMaxLen, textInWidth);
	}

	public void show(Component invoker, int x, int y) {
		if (invoker != null) {
			Point invokerOrigin = invoker.getLocationOnScreen();
			x += invokerOrigin.x;
			y += invokerOrigin.y;
		}
		setLocation(x, y);
		setSize(this.itemMaxLen + this.itemMaxTipLen + 30, this.itemNums * (22 + this.vgap));
		if (QRSwing.windowRound) {
			QRSystemUtils.setWindowRound(this, QRSwing.windowTransparency);
		}
//		contentPane.setBorderPaint(QRSwing.windowRound);
//		setAlwaysOnTop(QRSwing.windowAlwaysOnTop);
		setVisible(true);
	}
}
