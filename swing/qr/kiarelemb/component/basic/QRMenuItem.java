package swing.qr.kiarelemb.component.basic;

import method.qr.kiarelemb.utils.QRFontUtils;
import method.qr.kiarelemb.utils.QRStringUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.component.assembly.QRToolTip;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.*;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 菜单栏按钮
 * @create 2022-11-04 16:21
 **/
public class QRMenuItem extends QRButton implements QRComponentUpdate, QRActionRegister {
	private final QRLabel tip;
	private String quickTip;

	public QRMenuItem(String text) {
		this(text, null);
	}

	/**
	 * @param text 菜单按钮显示的内容
	 * @param key  方法 {@link QRSwing#registerGlobalKeyEvents(Window)} 被调用了才生效
	 */
	public QRMenuItem(String text, String key) {
		super(text);
		setLayout(new BorderLayout());
		tip = new QRLabel();
		tip.setFont(QRColorsAndFonts.MENU_ITEM_DEFAULT_FONT);
		tip.setHorizontalAlignment(SwingConstants.RIGHT);

		KeyStroke keyStroke = QRStringUtils.getKeyStroke(key);
		setKeyStroke(keyStroke);

		add(tip, BorderLayout.EAST);
		setHorizontalAlignment(SwingConstants.LEFT);
		setPreferredSize(new Dimension(QRFontUtils.getTextInWidth(this, text + quickTip) + 20, 25));
		componentFresh();
	}

	/**
	 * 若更新快捷键也是调用该方法
	 *
	 * @param keyStroke 快捷键
	 */
	public final void setKeyStroke(KeyStroke keyStroke) {
		if (keyStroke != null) {
			quickTip = QRStringUtils.getKeyStrokeString(keyStroke);
			tip.setText(quickTip);
			setPreferredSize(new Dimension(QRFontUtils.getTextInWidth(this, getText() + quickTip) + 20, 25));
			QRSwing.registerGlobalActionRemove(keyStroke, this, true);
			QRSwing.registerGlobalAction(keyStroke, this, true);
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
		super.componentFresh();
		setFont(QRColorsAndFonts.MENU_ITEM_DEFAULT_FONT);
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		if (tip != null) {
			tip.componentFresh();
		}
		repaint();
	}

	@Override
	public final void action(Object event) {
		actionEvent(null);
	}

	public String quickTip() {
		return quickTip;
	}
}
