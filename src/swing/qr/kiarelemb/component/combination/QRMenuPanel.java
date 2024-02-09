package swing.qr.kiarelemb.component.combination;

import method.qr.kiarelemb.utils.QRSystemUtils;
import swing.qr.kiarelemb.component.basic.QRButton;
import swing.qr.kiarelemb.component.basic.QRPanel;
import swing.qr.kiarelemb.component.listener.QRMouseListener;
import swing.qr.kiarelemb.inter.QRMenuButtonProcess;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-04 17:11
 **/
public class QRMenuPanel extends QRPanel {
	protected final LinkedList<QRButton> bottons;
	private final ArrayList<Boolean> enables;
	QRPanel menuButtons;
	private boolean pressed = false;
	private QRButton preClickedItem;

	/**
	 * 实现一个菜单条，可以在其中加入菜单按钮
	 */
	public QRMenuPanel() {
		super(false);
		this.menuButtons = new QRPanel();
		this.menuButtons.setLayout(new GridLayout(1, 0, 2, 0));
//		menuButtons.setLayout(new FlowLayout(FlowLayout., 0, 0));
		setLayout(new BorderLayout());
		add(this.menuButtons, BorderLayout.WEST);
		//起到调节菜单竖直高度的作用
//		if (!QRSwing.windowTitleMenu) {
//			QRLabel rightLabel = new QRLabel(QRStringUtils.A_WHITE_SPACE);
//			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, QRColorsAndFonts.LINE_COLOR));
//			rightLabel.setFont(QRFontUtils.getFontInSize(22));
//			rightLabel.setForeground(QRColorsAndFonts.FRAME_COLOR_BACK);
//			add(rightLabel, BorderLayout.EAST);
//		}
		setFocusable(false);
		this.bottons = new LinkedList<>();
		this.enables = new ArrayList<>();

	}

	/**
	 * 因为 Mac 系统和 Windows, Linux 用的菜单按钮不一样，所以用这方法可以去掉判断
	 *
	 * @param name 按钮名称
	 * @return
	 */
	public QRButton add(String name) {
		QRButton button;
		if (QRSystemUtils.IS_OSX) {
			button = new QRMenuButtonOriginal(name, this);
		} else {
			button = new QRMenuButton(name, this);
		}
		button.addMouseAction(QRMouseListener.TYPE.PRESS, e -> {
			mousePressAction(button);
		});
		button.addMouseAction(QRMouseListener.TYPE.ENTER, e -> {
			mouseEnterAction(button);
		});
		this.menuButtons.add(button);
		this.bottons.add(button);
		return button;
	}

	private void mouseEnterAction(QRButton button) {
		if (QRMenuPanel.this.pressed && button.isEnabled()) {
			((QRMenuButtonProcess) button).showPopupMenu();
			if (QRMenuPanel.this.preClickedItem != null && QRMenuPanel.this.preClickedItem != button) {
				((QRMenuButtonProcess) button).closePopupMenu();
				QRMenuPanel.this.preClickedItem = button;
			}
		}
	}

	private void mousePressAction(QRButton button) {
		QRMenuPanel.this.pressed = true;
		QRMenuPanel.this.preClickedItem = button;
	}

	public void setPressed(boolean b) {
		if (this.pressed != b) {
			this.pressed = b;
		}
	}

	public void disableAll() {
		for (QRButton item : this.bottons) {
			this.enables.add(item.isEnabled());
			item.setEnabled(false);
			((QRMenuButtonProcess) this.preClickedItem).disableAll();
		}
	}

	public void enablesAll() {
		int index = 0;
		for (QRButton item : this.bottons) {
			item.setEnabled(this.enables.get(index++));
			((QRMenuButtonProcess) this.preClickedItem).enablesAll();
		}
	}
}
