package swing.qr.kiarelemb.combination;

import method.qr.kiarelemb.utils.QRFontUtils;
import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.basic.QRMenuItem;
import swing.qr.kiarelemb.inter.QRMenuButtonProcess;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 适用于Windows系统的菜单栏按钮
 * @create 2022-11-04 17:18
 **/
public class QRMenuButton extends QRButton implements QRMenuButtonProcess {
	private final LinkedList<QRMenuItem> buttons;
	private final ArrayList<Boolean> enables;
	private final QRPopupMenu jpm;
	private final QRMenuPanel menuPanel;

	public QRMenuButton(String text, QRMenuPanel menuPanel) {
		super(text);
		setFont(QRColorsAndFonts.MENU_ITEM_DEFAULT_FONT);
		setForeground(QRColorsAndFonts.MENU_COLOR);
		this.menuPanel = menuPanel;

		this.jpm = new QRPopupMenu(SwingUtilities.getWindowAncestor(menuPanel)) {
			@Override
			public void focusGain(FocusEvent e) {
				QRMenuButton.this.setBackColor(QRColorsAndFonts.PRESS_COLOR);
				QRMenuButton.this.setBackground(QRColorsAndFonts.PRESS_COLOR);
			}

			@Override
			public void focusLose(FocusEvent e) {
				if (QRMenuButton.this.jpm.isVisible()) {
					QRMenuButton.this.jpm.setVisible(false);
				}
				QRMenuButton.this.setBackColor(QRColorsAndFonts.FRAME_COLOR_BACK);
				QRMenuButton.this.setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
			}
		};
		this.jpm.addFocusListener();
		setPreferredSize(new Dimension(QRFontUtils.getTextInWidth(this, text) + 20, 32));
		this.buttons = new LinkedList<>();
		this.enables = new ArrayList<>();
		addMouseListener();
	}

	@Override
	protected final void mousePress(MouseEvent e) {
		press(e);
	}

	private void press(MouseEvent e) {
		if (isEnabled()) {
			switch (e.getButton()) {
				case MouseEvent.BUTTON1 -> showPopupMenu();
				case MouseEvent.BUTTON3 -> e.consume();
			}
		}
	}

	@Override
	public void showPopupMenu() {
		this.jpm.show(this.menuPanel, getX(), getY() + getHeight());
	}

	@Override
	public void componentFresh() {
		super.componentFresh();
		if (this.jpm != null) {
			this.jpm.componentFresh();
		}
	}

	@Override
	public void add(QRMenuItem qmi) {
		this.jpm.add(qmi);
		this.buttons.add(qmi);
		qmi.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				qmi.componentFresh();
				closePopupMenu();
				menuPanel.setPressed(false);
			}
		});
	}

	@Override
	public void disableAll() {
		for (QRMenuItem item : this.buttons) {
			this.enables.add(item.isEnabled());
			item.setEnabled(false);
		}
	}

	@Override
	public void enablesAll() {
		int index = 0;
		for (QRMenuItem item : this.buttons) {
			item.setEnabled(this.enables.get(index++));
		}
	}

	public void addSeparator() {
		this.jpm.addSeparator();
	}

	@Override
	public void closePopupMenu() {
		this.jpm.setVisible(false);
	}
}