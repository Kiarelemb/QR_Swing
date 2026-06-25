package swing.qr.kiarelemb.test;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.assembly.QRMutableTreeNode;
import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.basic.QRTextPane;
import swing.qr.kiarelemb.basic.QRTree;
import swing.qr.kiarelemb.combination.QRStatePanel;
import swing.qr.kiarelemb.combination.QRTreeTabbedPane;
import swing.qr.kiarelemb.window.basic.QRFrame;
import swing.qr.kiarelemb.window.utils.QRFileSelectDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className TreeTabbedPaneTest
 * @description TODO
 * @create 2024/5/4 下午2:44
 */
public class TreeTabbedPaneTest extends QRFrame {
	public TreeTabbedPaneTest(String title) {
		super(title);
		//设置窗体标题居中
		setTitlePlace(SwingConstants.CENTER);
//		setTitleCenter();
		//设置单击关闭按钮后窗体淡化退出并结束程序
		setCloseButtonSystemExit();
		this.mainPanel.setLayout(new BorderLayout());
		setTitlePanel();
		QRMutableTreeNode root = new QRMutableTreeNode("设置");
		root.setCollapsable(false);
		QRTree tree = new QRTree(root);
		tree.setRowHeight(35);
		tree.setRootVisible(false);
		tree.setPreferredSize(new Dimension(150, 100));
		QRMutableTreeNode normalNode = root.addChild("常规");
		QRMutableTreeNode appearance = normalNode.addChild("外观");
		QRMutableTreeNode mainWindow = normalNode.addChild("窗体");
		QRMutableTreeNode gradeSend = normalNode.addChild("成绩单");
		QRMutableTreeNode typeNode = root.addChild("跟打");
		QRMutableTreeNode tip = typeNode.addChild("词提");
		QRMutableTreeNode innerInput = typeNode.addChild("内置输入");

		QRMutableTreeNode sendNode = root.addChild("发文");

		QRMutableTreeNode keyNode = root.addChild("快捷键");

		QRTreeTabbedPane pane = new QRTreeTabbedPane(tree);

		this.mainPanel.add(new QRTextPane().addScrollPane());
		QRStatePanel statePanel = new QRStatePanel();
		statePanel.leftAdd(new QRLabel("Test"));
		this.mainPanel.add(statePanel, BorderLayout.SOUTH);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		QRFileSelectDialog dialog = new QRFileSelectDialog(
				this, QRFileSelectDialog.SelectMode.SAVE_FILE, "文本文件", "txt"
		);
		dialog.setVisible(true);
		if (dialog.selectedSucceeded()) {
			File file = dialog.selectedFile();  // 返回 /home/user/Documents/myfile.txt
			System.out.println(file.getAbsolutePath());
		}
	}

	public static void main(String[] args) {
		QRSwing.start();
		QRSwing.registerGlobalKeyEvents();
		TreeTabbedPaneTest test = new TreeTabbedPaneTest("Test");
		QRSwing.registerGlobalEventWindow(test);
		test.setVisible(true);
	}
}