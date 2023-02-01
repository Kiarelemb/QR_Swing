package swing.qr.kiarelemb.component.basic;

import swing.qr.kiarelemb.component.assembly.QRToolTip;
import swing.qr.kiarelemb.component.listener.QRActionListener;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.listener.add.QRActionListenerAdd;
import swing.qr.kiarelemb.resource.QRSwingInfo;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.plaf.basic.BasicCheckBoxUI;
import java.awt.event.ActionEvent;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-21 18:44
 **/
public class QRCheckBox extends JCheckBox implements QRComponentUpdate, QRActionListenerAdd {

	private QRActionListener clickListener;

	public QRCheckBox() {
		super();
		addActionListener();
		componentFresh();

		ImageIcon selectedIcon = new ImageIcon(QRSwingInfo.loadUrl("check_box_selected.png"));
		ImageIcon notSelectedIcon = new ImageIcon(QRSwingInfo.loadUrl("check_box_not_selected.png"));
		ImageIcon pressedIcon = new ImageIcon(QRSwingInfo.loadUrl("check_box_pressed.png"));
		ImageIcon disabledIcon = new ImageIcon(QRSwingInfo.loadUrl("check_box_disable.png"));
		ImageIcon disabledSelectedIcon = new ImageIcon(QRSwingInfo.loadUrl("check_box_disable_selected.png"));
		ImageIcon overIcon = new ImageIcon(QRSwingInfo.loadUrl("check_box_over.png"));
		ImageIcon overSelectedIcon = new ImageIcon(QRSwingInfo.loadUrl("check_box_over_selected.png"));
		setIcon(notSelectedIcon);
		setSelectedIcon(selectedIcon);
		setDisabledIcon(disabledIcon);
		setDisabledSelectedIcon(disabledSelectedIcon);
		setPressedIcon(pressedIcon);
		setRolloverIcon(overIcon);
		setRolloverSelectedIcon(overSelectedIcon);
	}

	public QRCheckBox(String text) {
		this();
		setText(text);
	}

	public QRCheckBox(String text, boolean selected) {
		this(text);
		setSelected(selected);
	}

	public QRCheckBox(String text, boolean selected, boolean enabled) {
		this(text, selected);
		setEnabled(enabled);
	}


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
	 * 已自动添加监听器，可直接重写
	 */
	protected void actionEvent(ActionEvent o) {
	}

	@Override
	public JToolTip createToolTip() {
		QRToolTip tip = new QRToolTip();
		tip.setComponent(tip);
		return tip;
	}

	@Override
	public void componentFresh() {
		setFont(QRColorsAndFonts.DEFAULT_FONT_MENU);
		setForeground(QRColorsAndFonts.MENU_COLOR);
		setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
	}
}
