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
		setLayout(new BorderLayout());
		add(this.menuButtons, BorderLayout.WEST);
		setFocusable(false);
		this.bottons = new LinkedList<>();
		this.enables = new ArrayList<>();

	}

	/**
	 * 因为 Mac 系统和 Windows, Linux 用的菜单按钮不一样，所以用这方法可以去掉判断
	 *
	 * @param name 按钮名称
	 * @return 菜单按钮
	 */
	public QRButton add(String name) {
		QRButton button = QRSystemUtils.IS_OSX ? new QRMenuButtonOriginal(name, this) : new QRMenuButton(name, this);
		button.addMouseListener();
		button.addMouseAction(QRMouseListener.TYPE.PRESS, e -> mousePressAction(button));
		button.addMouseAction(QRMouseListener.TYPE.ENTER, e -> mouseEnterAction(button));
		menuButtons.add(button);
		bottons.add(button);
		return button;
	}

	private void mouseEnterAction(QRButton button) {
		if (pressed && button.isEnabled()) {
			((QRMenuButtonProcess) button).showPopupMenu();
			if (preClickedItem != button) {
				((QRMenuButtonProcess) preClickedItem).closePopupMenu();
				preClickedItem = button;
			}
		}
	}

	private void mousePressAction(QRButton button) {
		pressed = true;
		preClickedItem = button;
	}

	public void setPressed(boolean b) {
		pressed = b;
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