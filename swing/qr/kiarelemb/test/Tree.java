package swing.qr.kiarelemb.test;

import method.qr.kiarelemb.utils.QRSystemUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-01-28 21:50
 **/
public class Tree extends JFrame {
	public Tree() {

		QRSystemUtils.setSystemLookAndFeel();
		//先实例化的是父节点，实例化完父节点之后，再实例化子节点
		DefaultMutableTreeNode Goodfriend = new DefaultMutableTreeNode("我的好友");
		Goodfriend.add(new DefaultMutableTreeNode("好友1"));
		Goodfriend.add(new DefaultMutableTreeNode("好友2"));
		Goodfriend.add(new DefaultMutableTreeNode("好友3"));

		DefaultMutableTreeNode Webfriend = new DefaultMutableTreeNode("我的网友");
		Webfriend.add(new DefaultMutableTreeNode("网友1"));
		Webfriend.add(new DefaultMutableTreeNode("网友2"));
		Webfriend.add(new DefaultMutableTreeNode("网友3"));
		DefaultMutableTreeNode Doub = new DefaultMutableTreeNode("逗逼");
		//用Doub这个对象add，那么逗逼就是最高级了。
		Doub.add(Goodfriend);
		Doub.add(Webfriend);
		JTree jTree = new JTree(Doub);
		this.add(jTree);
		this.setTitle("JTree控件演示");
		this.setSize(200, 300);
		this.setVisible(true);
		this.setLocationRelativeTo(null);//居中
	}

	public static void main(String[] args) {
		new Tree();
	}
}
