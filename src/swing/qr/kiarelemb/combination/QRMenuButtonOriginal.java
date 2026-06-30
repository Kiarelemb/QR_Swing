package swing.qr.kiarelemb.combination;

import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.basic.QRMenuItem;
import swing.qr.kiarelemb.inter.QRMenuButtonProcess;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 适用于非Windows系统的菜单按钮
 * @create 2022-11-04 17:15
 **/
public class QRMenuButtonOriginal extends QRButton implements QRMenuButtonProcess {
	private final LinkedList<QRMenuItem> buttons = new LinkedList<>();
	private final ArrayList<Boolean> enables = new ArrayList<>();
	JPopupMenu jpm;
	QRMenuPanel menuPanel;
	private boolean popupActive;

	public QRMenuButtonOriginal(String text, QRMenuPanel menuPanel) {
		super(text);
		setFont(QRColorsAndFonts.MENU_ITEM_DEFAULT_FONT);
		this.menuPanel = menuPanel;
		this.jpm = new JPopupMenu();
		this.jpm.setBorderPainted(true);
		this.jpm.setBorder(BorderFactory.createLineBorder(QRColorsAndFonts.ENTER_COLOR, 1));
		this.jpm.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				setPopupActive(true);
				menuPanel.popupMenuOpened(QRMenuButtonOriginal.this);
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				setPopupActive(false);
				menuPanel.popupMenuClosed(QRMenuButtonOriginal.this);
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				setPopupActive(false);
				menuPanel.popupMenuClosed(QRMenuButtonOriginal.this);
			}
		});
		addMouseListener();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				press(e);
			}
		});
	}

	@Override
	protected final void mousePress(MouseEvent e) {
		press(e);
	}

	private void press(MouseEvent e) {
		switch (e.getButton()) {
			//左键
			case MouseEvent.BUTTON1 -> showPopupMenu();
			//右键
			case MouseEvent.BUTTON3 -> e.consume();
		}
	}

	@Override
	public void showPopupMenu() {
		setPopupActive(true);
		this.jpm.show(this.menuPanel, getX(), getY() + getHeight());
	}

	@Override
	public void closePopupMenu() {
		this.jpm.setVisible(false);
		setPopupActive(false);
	}

	@Override
	public void componentFresh() {
		super.componentFresh();
		if (this.jpm != null) {
			this.jpm.setBorder(BorderFactory.createLineBorder(QRColorsAndFonts.ENTER_COLOR, 1));
		}
		if (this.buttons != null) {
			for (QRMenuItem item : this.buttons) {
				if (item != null) {
					item.componentFresh();
				}
			}
		}
		setFont(QRColorsAndFonts.MENU_ITEM_DEFAULT_FONT);
		setForeground(QRColorsAndFonts.MENU_COLOR);
	}

	@Override
	public void add(QRMenuItem qmi) {
		this.jpm.add(qmi);
		this.buttons.add(qmi);
		qmi.addClickAction(e -> {
			closePopupMenu();
			qmi.componentFresh();
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

	private void setPopupActive(boolean popupActive) {
		if (this.popupActive != popupActive) {
			this.popupActive = popupActive;
			repaint();
		}
	}

	@Override
	protected void paintButtonBackground(Graphics2D g2) {
		if (this.popupActive && isEnabled()) {
			g2.setColor(QRColorsAndFonts.PRESS_COLOR);
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.addRenderingHints(rh);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
			g2.fillRect(0, 0, getWidth(), getHeight());
			return;
		}
		super.paintButtonBackground(g2);
	}
}
