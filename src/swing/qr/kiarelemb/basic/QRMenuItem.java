package swing.qr.kiarelemb.basic;

import method.qr.kiarelemb.utils.QRFontUtils;
import method.qr.kiarelemb.utils.QRStringUtils;
import swing.qr.kiarelemb.QRGlobalAction;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.utils.QRComponentUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 菜单栏按钮
 * @create 2022-11-04 16:21
 **/
public class QRMenuItem extends QRButton {
	private final QRLabel tip;
	private final List<KeyStroke> keyStrokes;
	private final List<QRGlobalAction> globalActions;
	private String quickTip;
	private boolean windowFocus = true;
	private boolean globalActionsLoaded;

	public QRMenuItem(String text) {
		this(text, null);
	}

	/**
	 * @param text 菜单按钮显示的内容
	 * @param key  方法 {@link QRSwing#registerGlobalKeyEvents()} 被调用了才生效
	 */
	public QRMenuItem(String text, Object key) {
		this(text, key, true);
	}

	public QRMenuItem(String text, Object key, boolean windowFocus) {
		super(text);
		setLayout(new BorderLayout());
		tip = new QRLabel();
		keyStrokes = new ArrayList<>();
		globalActions = new ArrayList<>();
		tip.setFont(QRColorsAndFonts.MENU_ITEM_DEFAULT_FONT);
		tip.setHorizontalAlignment(SwingConstants.RIGHT);

		//设置快捷键
		setKeyStroke(key, windowFocus);

		add(tip, BorderLayout.EAST);
		setHorizontalAlignment(SwingConstants.LEFT);
		setPreferredSize(new Dimension(QRFontUtils.getTextInWidth(this, text + quickTip) + 20, 25));
		componentFresh();
	}

	/**
	 * 若更新快捷键也是调用该方法
	 *
	 * @param key 快捷键
	 */
	public final void setKeyStroke(Object key, boolean windowFocus) {
		boolean reload = this.globalActionsLoaded;
		closeGlobalActions();
		this.keyStrokes.clear();
		this.windowFocus = windowFocus;
		this.quickTip = null;
		this.tip.setText(null);
		if (key != null) {
			this.keyStrokes.addAll(QRComponentUtils.parseKeyStrokes(key));
			if (!this.keyStrokes.isEmpty()) {
				quickTip = QRStringUtils.getKeyStrokeString(this.keyStrokes.get(0));
				tip.setText(quickTip);
				setPreferredSize(new Dimension(QRFontUtils.getTextInWidth(this, getText() + quickTip) + 20, 25));
			}
		}
		if (reload) {
			loadGlobalActions();
		}
	}

	/**
	 * 注册当前菜单项的全局快捷键。
	 *
	 * <p>该方法由菜单显示时调用；重复调用不会重复注册。</p>
	 */
	public void loadGlobalActions() {
		if (this.globalActionsLoaded || this.keyStrokes.isEmpty()) {
			return;
		}
		Window window = SwingUtilities.getWindowAncestor(this);
		if (this.windowFocus && window == null) {
			return;
		}
		for (KeyStroke keyStroke : this.keyStrokes) {
			QRGlobalAction globalAction = new QRGlobalAction(this.actionRegister).key(keyStroke);
			if (this.windowFocus) {
				globalAction.window(window);
			} else {
				globalAction.focus(false);
			}
			globalAction.load();
			this.globalActions.add(globalAction);
		}
		this.globalActionsLoaded = true;
	}

	/**
	 * 注销当前菜单项的全局快捷键。
	 *
	 * <p>该方法由菜单隐藏或释放时调用；重复调用不会抛异常。</p>
	 */
	public void closeGlobalActions() {
		for (QRGlobalAction globalAction : this.globalActions) {
			globalAction.close();
		}
		this.globalActions.clear();
		this.globalActionsLoaded = false;
	}

	@Override
	public void componentFresh() {
		super.componentFresh();
		setFont(QRColorsAndFonts.MENU_ITEM_DEFAULT_FONT);
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		if (tip != null) {
			tip.setEnabled(isEnabled());
			tip.componentFresh();
		}
		repaint();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (tip != null) {
			tip.setEnabled(enabled);
			tip.componentFresh();
		}
		repaint();
	}

	public String quickTip() {
		return quickTip;
	}
}