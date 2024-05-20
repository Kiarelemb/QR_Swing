package swing.qr.kiarelemb.component.basic;

import method.qr.kiarelemb.utils.QRFontUtils;
import swing.qr.kiarelemb.component.assembly.QRToolTip;
import swing.qr.kiarelemb.component.listener.QRActionListener;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.listener.add.QRActionListenerAdd;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-01-09 16:52
 **/
public class QRRadioButton extends JRadioButton implements QRComponentUpdate, QRActionListenerAdd {
	private QRActionListener clickListener;

	public QRRadioButton(String text) {
		super(text);
		addActionListener();
		componentFresh();
	}

	public QRRadioButton(String text, boolean selected) {
		this(text);
		setSelected(selected);
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
	public void setBounds(int x, int y, int width, int height) {
		int w = QRFontUtils.getTextInWidth(this, getText()) + 35;
		super.setBounds(x, y, w, height);
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
		setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
	}
}