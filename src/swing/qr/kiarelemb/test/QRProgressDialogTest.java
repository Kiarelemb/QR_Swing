package swing.qr.kiarelemb.test;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.task.QRTaskOptions;
import swing.qr.kiarelemb.task.QRTaskRunner;
import swing.qr.kiarelemb.window.basic.QRFrame;
import swing.qr.kiarelemb.window.utils.QRProgressDialog;

import javax.swing.*;
import java.awt.*;

/**
 * 测试 {@link QRProgressDialog} 自定义手绘细长进度条。
 *
 * <p>点击按钮弹出进度对话框，模拟耗时任务逐步推进，验证进度条和百分比文本的显示效果。
 *
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRProgressDialogTest
 * @create 2026/6/26
 */
public class QRProgressDialogTest extends QRFrame {

	public static void main(String[] args) {
		QRSwing.start("setting.properties");
		QRSwing.setWindowTitleMenu(true);
		QRSwing.setWindowRound(false);

		QRProgressDialogTest window = new QRProgressDialogTest();
		window.setVisible(true);
	}

	private QRProgressDialogTest() {
		setTitle("进度条测试");
		setTitlePlace(SwingConstants.CENTER);
		setCloseButtonSystemExit();
		setSize(400, 250);

		mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 60));

		QRButton btnSimple = new QRButton("简单进度");
		btnSimple.addClickAction(e -> startSimpleProgress());

		QRButton btnDescription = new QRButton("带描述进度");
		btnDescription.addClickAction(e -> startProgressWithDescription());

		QRButton btnHideLabel = new QRButton("隐藏百分比");
		btnHideLabel.addClickAction(e -> startProgressHideLabel());

		QRButton btnTask = new QRButton("后台任务");
		btnTask.addClickAction(e -> startWorkerProgress());

		QRButton btnIndeterminate = new QRButton("不确定进度");
		btnIndeterminate.addClickAction(e -> startIndeterminateProgress());

		mainPanel.add(btnSimple);
		mainPanel.add(btnDescription);
		mainPanel.add(btnHideLabel);
		mainPanel.add(btnTask);
		mainPanel.add(btnIndeterminate);
	}

	/**
	 * 简单进度测试：进度条从 0 匀速走到 100，然后自动关闭。
	 */
	private void startSimpleProgress() {
		QRProgressDialog dialog = new QRProgressDialog(this, false);
		dialog.setTitle("正在处理");
		dialog.setProgressDescription("正在导出文件...");
		dialog.setVisible(true);

		Timer timer = new Timer(40, null);
		timer.addActionListener(e -> {
			int p = dialog.progress() + 1;
			dialog.setProgress(p);
			if (p >= 100) {
				timer.stop();
				dialog.dispose();
			}
		});
		timer.start();
	}

	/**
	 * 带描述变化的进度测试：在不同阶段更新描述文字。
	 */
	private void startProgressWithDescription() {
		QRProgressDialog dialog = new QRProgressDialog(this, false);
		dialog.setTitle("正在编译");
		dialog.setProgressDescription("正在扫描文件...");
		dialog.setVisible(true);

		Timer timer = new Timer(50, null);
		timer.addActionListener(e -> {
			int p = dialog.progress() + 1;
			dialog.setProgress(p);

			if (p == 30) {
				dialog.setProgressDescription("正在编译代码...");
			} else if (p == 60) {
				dialog.setProgressDescription("正在链接库文件...");
			} else if (p == 85) {
				dialog.setProgressDescription("正在生成输出...");
			}

			if (p >= 100) {
				timer.stop();
				dialog.setProgressDescription("完成！");
				// 延迟关闭，让用户看到 100%
				Timer closeTimer = new Timer(600, e2 -> dialog.dispose());
				closeTimer.setRepeats(false);
				closeTimer.start();
			}
		});
		timer.start();
	}

	/**
	 * 隐藏百分比文本，只显示细长进度条。
	 */
	private void startProgressHideLabel() {
		QRProgressDialog dialog = new QRProgressDialog(this, false);
		dialog.setTitle("正在下载");
		dialog.setProgressDescription("正在下载更新包...")
				.setProgressLabelVisible(false)
				.setVisible(true);

		Timer timer = new Timer(35, null);
		timer.addActionListener(e -> {
			int p = dialog.progress() + 2;
			if (p > 100) p = 100;
			dialog.setProgress(p);
			if (p >= 100) {
				timer.stop();
				dialog.dispose();
			}
		});
		timer.start();
	}

	/**
	 * 真实后台任务测试：后台线程推进进度，EDT 回调关闭进度窗口。
	 */
	private void startWorkerProgress() {
		QRTaskRunner.runWithProgress(this, "后台任务", context -> {
					for (int i = 1; i <= 100; i++) {
						context.checkCancelled();
						Thread.sleep(35);
						if (i == 30) {
							context.message("正在读取数据...");
						} else if (i == 65) {
							context.message("正在生成结果...");
						}
						context.progress(i);
					}
					return "完成";
				}, result -> System.out.println("任务成功：" + result),
				throwable -> System.err.println("任务失败：" + throwable.getMessage()));
	}

	/**
	 * 不确定进度测试：任务未知总量时显示小球滚动动画。
	 */
	private void startIndeterminateProgress() {
		QRTaskRunner.run(new QRTaskOptions()
						.owner(this)
						.title("正在扫描")
						.description("正在等待外部任务...")
						.showProgressDialog(true)
						.indeterminate(true),
				context -> {
					for (int i = 0; i < 50000; i++) {
						context.checkCancelled();
						Thread.sleep(2);
						if (i == 400) {
							context.message("仍在处理...");
						}
					}
					return null;
				});
	}
}