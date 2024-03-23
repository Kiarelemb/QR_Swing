package swing.qr.kiarelemb.test;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.component.assembly.QRMutableTreeNode;
import swing.qr.kiarelemb.component.basic.*;
import swing.qr.kiarelemb.component.combination.QRContractiblePanel;
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
public class Test extends QRFrame {
	QRTree jTree;
	QRMutableTreeNode node;

	public Test(String title) {
		super(title);
		//设置窗体标题居中
		setTitleCenter();
		//设置单击关闭按钮后窗体淡化退出并结束程序
		setCloseButtonSystemExit();
		this.mainPanel.setLayout(new BorderLayout());

		QRSplitPane splitPane = new QRSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(new QRTextPane().addScrollPane());
		splitPane.setBottomComponent(new QRTextPane().addScrollPane());
		splitPane.setResizeWeight(0.618);
		this.mainPanel.add(splitPane, BorderLayout.CENTER);

		QRContractiblePanel contractiblePanel = new QRContractiblePanel();

		QRContractiblePanel.QRColumnContentPanel first = contractiblePanel.addColumn("First", 300);
		first.setLayout(new BorderLayout());
		first.add(new QRTextPane().addScrollPane(), BorderLayout.CENTER);

		for (int i = 1; i <= 4; i++) {
			contractiblePanel.addColumn("Test " + i, 300);
		}
		this.mainPanel.add(contractiblePanel, BorderLayout.WEST);

		QRContractiblePanel.QRColumnContentPanel second = contractiblePanel.getColumn(1);
		QRRoundButton btn = new QRRoundButton("主题设置"){
			@Override
			protected void actionEvent(ActionEvent o) {
				QRSwingThemeDesigner designer = new QRSwingThemeDesigner(Test.this);
				designer.setVisible(true);
			}
		};
		btn.setSize(80,30);
		second.add(btn);

		QRStatePanel statePanel = new QRStatePanel();
		statePanel.leftAdd(new QRLabel("Test"));
		this.mainPanel.add(statePanel, BorderLayout.SOUTH);

		first.column().setCollapsable(false);
		first.column().setFold(false);
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
		Test window = new Test("这是一个测试窗体");
		window.setBackgroundImage("C:\\Users\\Kiare\\Desktop\\壁纸.png");
		window.setBackgroundBorderAlpha(0.9f);
		QRSwing.registerGlobalKeyEvents(window);
		QRSwing.registerGlobalAction("ctrl s", (System.out::println), false);
		//设置窗体可见
		window.setVisible(true);
	}

}