package swing.qr.kiarelemb.test;

import method.qr.kiarelemb.utils.QRRandomUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.component.basic.QRButton;
import swing.qr.kiarelemb.component.basic.QRLabel;
import swing.qr.kiarelemb.component.basic.QRMenuItem;
import swing.qr.kiarelemb.component.basic.QRTextPane;
import swing.qr.kiarelemb.component.combination.QRStatePanel;
import swing.qr.kiarelemb.theme.QRSwingThemeDesigner;
import swing.qr.kiarelemb.window.basic.QRFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-04 17:41
 **/
public class MenuTest extends QRFrame {

	public MenuTest(String title) {
		super(title);
		//设置窗体标题居中
		setTitlePlace(SwingConstants.CENTER);
//		setTitleCenter();
		//设置单击关闭按钮后窗体淡化退出并结束程序
		setCloseButtonSystemExit();
		this.mainPanel.setLayout(new BorderLayout());
		setTitlePanel();
		for (int i = 0; i < 4; i++) {
			QRButton button = titleMenuPanel.add("Menu " + i);
			for (int j = 0, size = QRRandomUtils.getRandomInt(2, 9); j < size; j++) {
				button.add(new QRMenuItem(String.format("Menu %s of Button %s", i, j)));
			}
		}
		QRMenuItem btn = new QRMenuItem("主题设置") {
			@Override
			protected void actionEvent(ActionEvent o) {
				QRSwingThemeDesigner designer = new QRSwingThemeDesigner(MenuTest.this);
				designer.setVisible(true);
			}
		};
		titleMenuPanel.add("settings").add(btn);
		btn.setSize(80, 30);

		this.mainPanel.add(new QRTextPane().addScrollPane());
		QRStatePanel statePanel = new QRStatePanel();
		statePanel.leftAdd(new QRLabel("Test"));
		this.mainPanel.add(statePanel, BorderLayout.SOUTH);

	}

	@Override
	public void windowOpened(WindowEvent e) {

	}

	@Override
	public void windowClosed(WindowEvent e) {
		QRSwing.globalPropSave();
	}

	public static void main(String[] args) {
		QRSwing.start("setting.properties");
		QRSwing.setWindowTitleMenu(true);

		QRSwing.setWindowRound(false);
		MenuTest window = new MenuTest("测试窗体");
		window.setBackgroundImage("C:\\Users\\Kiare\\Desktop\\壁纸.png");
		window.setBackgroundBorderAlpha(0.9f);
		QRSwing.registerGlobalKeyEvents(window);
		QRSwing.registerGlobalAction("ctrl s", (System.out::println), false);
		//设置窗体可见
		window.setVisible(true);
	}
}