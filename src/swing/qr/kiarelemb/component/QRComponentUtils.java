package swing.qr.kiarelemb.component;

import method.qr.kiarelemb.utils.QRFontUtils;
import swing.qr.kiarelemb.component.combination.QRTabbedContentPanel;
import swing.qr.kiarelemb.component.combination.QRTabbedPane;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRBackgroundUpdate;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.geom.Rectangle2D;
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
	 * 向 {@code parent} 中添加 {@code comToAdd}，并设置 {@code comToAdd} 的 {@link JComponent#setBounds(int, int, int, int)}
	 *
	 * @param parent   添加的父容器
	 * @param comToAdd 子控件
	 * @param x        子控件位置 x
	 * @param y        子控件位置 y
	 * @param width    子控件位置宽度
	 * @param height   子控件位置高度
	 */
	public static void setBoundsAndAddToComponent(JComponent parent, JComponent comToAdd, int x, int y, int width,
	                                              int height) {
		comToAdd.setBounds(x, y, width, height);
		parent.add(comToAdd);
	}

	/**
	 * 向 {@code parent} 中添加 {@code comToAdd}，并设置 {@code comToAdd} 的 {@link JComponent#setLocation(int, int)}
	 *
	 * @param parent   添加的父容器
	 * @param comToAdd 子控件
	 * @param x        子控件位置 x
	 * @param y        子控件位置 y
	 */
	public static void setBoundsAndAddToComponent(JComponent parent, JComponent comToAdd, int x, int y) {
		comToAdd.setLocation(x, y);
		parent.add(comToAdd);
	}


	/**
	 * 横纵居中绘制文字
	 *
	 * @param com   控件
	 * @param g     工具
	 * @param text  内容
	 * @param font  字体
	 * @param color 前景色
	 */
	public static void componentStringDraw(JComponent com, Graphics g, String text, Font font, Color color) {
		float y = com.getHeight() / 1.7f;
		componentStringDraw(com, g, text, font, color, y);
	}

	/**
	 * 居中绘制文字
	 *
	 * @param com   控件
	 * @param g     工具
	 * @param text  内容
	 * @param font  字体
	 * @param color 前景色
	 * @param y     纵位置
	 */
	public static void componentStringDraw(JComponent com, Graphics g, String text, Font font, Color color, float y) {
		Graphics2D g2 = (Graphics2D) g;
		Rectangle2D r = QRFontUtils.getStringBounds(text, font);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				com.getClientProperty(RenderingHints.KEY_TEXT_ANTIALIASING));
		g2.setFont(font);
		g2.setColor(color);
		float x = (float) (com.getWidth() / 2 - r.getWidth() / 2);
		g2.drawString(text, x, y);
	}

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

	public static SimpleAttributeSet getSimpleAttributeSet(String fontFamily, int fontSize, int fontStyle,
	                                                       Color colorFore, Color colorBack) {
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
		if (list != null && !list.isEmpty()) {
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