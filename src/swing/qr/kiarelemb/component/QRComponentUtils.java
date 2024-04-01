package swing.qr.kiarelemb.component;

import swing.qr.kiarelemb.component.combination.QRTabbedContentPanel;
import swing.qr.kiarelemb.component.combination.QRTabbedPane;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRBackgroundUpdate;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-23 13:22
 **/
public class QRComponentUtils {
	/**
	 * 为添加背景图片时所专设的静态方法
	 *
	 * @param com 需要循环的控件，其里面可能有设置了 {@link swing.qr.kiarelemb.component.assembly.QRBackgroundBorder} 的面板
	 */
	public static void loopComsForBackgroundSetting(Component com) {
		if (com instanceof JComponent) {
			if (com instanceof QRBackgroundUpdate) {
				((QRBackgroundUpdate) com).addCaretListenerForBackgroundUpdate();
			} else if (com instanceof QRTabbedPane pane) {
				int size = pane.getTabSize();
				for (int i = 0; i < size; i++) {
					QRTabbedContentPanel p = pane.getContentPanel(i);
					loopComsForBackgroundSetting(p);
				}
			} else {
				int count = ((JComponent) com).getComponentCount();
				if (count > 0) {
					Component[] coms = ((JComponent) com).getComponents();
					for (Component c : coms) {
						loopComsForBackgroundSetting(c);
					}
				}
			}
		}
	}

	public static SimpleAttributeSet getSimpleAttributeSet(Font f, Color colorFore, Color colorBack) {
		return getSimpleAttributeSet(f.getFamily(), f.getSize(), f.getStyle(), colorFore, colorBack);
	}

	public static SimpleAttributeSet getSimpleAttributeSet(String fontFamily, int fontSize, int fontStyle, Color colorFore, Color colorBack) {
		SimpleAttributeSet sas = new SimpleAttributeSet();
		StyleConstants.setFontFamily(sas, fontFamily);
		StyleConstants.setFontSize(sas, fontSize);
		switch (fontStyle) {
			case Font.ITALIC -> StyleConstants.setItalic(sas, true);
			case Font.BOLD -> StyleConstants.setBold(sas, true);
		}
		StyleConstants.setForeground(sas, colorFore);
		StyleConstants.setBackground(sas, colorBack);
		return sas;
	}

	/**
	 * 将 {@link QRActionRegister} 列表使用 {@code null} 参数运行，会检查其内容是否为空
	 *
	 * @param list 任务列表
	 */
	public static void runActions(List<QRActionRegister> list) {
		runActions(list, null);
	}

	/**
	 * 将 {@link QRActionRegister} 列表使用 {@code obj} 参数运行，会检查其内容是否为空
	 *
	 * @param list 任务列表
	 */
	public static void runActions(List<QRActionRegister> list, Object obj) {
		if (!list.isEmpty()) {
			ArrayList<QRActionRegister> temp = new ArrayList<>(list);
			temp.forEach(e -> {
				//确保每个都能完成而不影响之后的事件
				try {
					e.action(obj);
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			});
		}
	}


	/**
	 * 如果设置背景图片，调用此方法。窗体刷新会延迟 30 毫秒
	 *
	 * @param com 窗体内的一控件
	 */
	public static void windowFresh(JComponent com) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				windowFreshRightNow(com);
			}
		}, 30L);
	}

	/**
	 * 如果设置背景图片，调用此方法
	 *
	 * @param com 窗体内的一控件
	 */
	public static void windowFreshRightNow(JComponent com) {
		Window w = SwingUtilities.getWindowAncestor(com);
		if (w != null) {
			w.repaint();
		}
	}

}