package swing.qr.kiarelemb.test;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.component.assembly.QRMutableTreeNode;
import swing.qr.kiarelemb.component.basic.QRButton;
import swing.qr.kiarelemb.component.basic.QRTree;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.window.basic.QRFrame;

import javax.swing.border.MatteBorder;
import java.awt.*;
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

		QRMutableTreeNode Doub = new QRMutableTreeNode("逗逼");
		jTree = new QRTree(Doub);

		QRMutableTreeNode Goodfriend = new QRMutableTreeNode("我的好友");
		Goodfriend.add(new QRMutableTreeNode("好友1"));
		Goodfriend.add(new QRMutableTreeNode("好友2"));
		Goodfriend.add(new QRMutableTreeNode("好友3"));

		QRMutableTreeNode Webfriend = new QRMutableTreeNode("我的网友");
		Webfriend.add(new QRMutableTreeNode("网友1"));
		Webfriend.add(new QRMutableTreeNode("网友2"));
		node = new QRMutableTreeNode("网友3");
		Webfriend.add(node);
		Doub.add(Goodfriend);
		Doub.add(Webfriend);
		jTree.addTreeNodeListener();
		jTree.expendAll();
		node.addClickAction(jTree, (e -> {
			System.out.println("我被单击了");
		}), false);
		this.mainPanel.add(jTree);
		this.setTitle("JTree控件演示");

		QRButton b = new QRButton("你好");
		b.setToolTipText("AAAAA");
		this.mainPanel.add(b, BorderLayout.NORTH);
//		QRSplitPane splitPane = new QRSplitPane(JSplitPane.VERTICAL_SPLIT);
//		QRTextPane textPane = new QRTextPane();
//		splitPane.setTopComponent(textPane.addScrollPane());
//		QRLabel label = new QRLabel("这是一个用于测试的简单标签");
//		label.setTextCenter();
//		splitPane.setBottomComponent(label);
//		splitPane.setResizeWeight(0.118);
//		this.mainPanel.add(splitPane, BorderLayout.CENTER);
//
//		QRContractiblePanel contractiblePanel = new QRContractiblePanel();
//
//		QRContractiblePanel.QRColumnContentPanel first = contractiblePanel.addColumn("First", 300);
//		first.setLayout(new BorderLayout());
//		first.add(new QRTextPane().addScrollPane(), BorderLayout.CENTER);
//
//		for (int i = 1; i <= 4; i++) {
//			contractiblePanel.addColumn("Test " + i, 300);
//		}
//		this.mainPanel.add(contractiblePanel, BorderLayout.WEST);
//
//		first.column().setCollapsable(false);
//		first.column().setFold(false);
	}

	@Override
	public void windowOpened(WindowEvent e) {

	}

	public static void main(String[] args) {
		QRSwing.start("setting.properties");
		Test window = new Test("这是一个测试窗体");
//		window.setBackgroundImage("C:\\Users\\Kiare\\Desktop\\壁纸.png");
//		window.setBackgroundBorderAlpha(0.9f);
		QRSwing.registerGlobalKeyEvents(window);
		QRSwing.registerGlobalAction("ctrl s", (new QRActionRegister() {
			@Override
			public void action(Object event) {
				System.out.println(event);
			}
		}), false);
		//设置窗体可见
		window.setVisible(true);
	}
}
