package swing.qr.kiarelemb.window.utils;

import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.basic.QRRoundButton;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.window.basic.QREmptyDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * 用于显示任务进度的小型对话框。
 *
 * <p>窗口包含进度描述、进度条、可选的百分比文本和取消按钮。
 * 外部任务可以通过 {@link #setProgress(int)} 和
 * {@link #setProgressDescription(String)} 更新显示内容。
 *
 * <p>取消按钮默认关闭窗口。需要中断外部任务时，可以重写
 * {@link #cancelAction(ActionEvent)}，或调用 {@link #addCancelAction(QRActionRegister)}
 * 注册额外的取消处理逻辑。
 *
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRProgressDialog
 * @create 2026/6/11
 */
public class QRProgressDialog extends QREmptyDialog {
	private static final int DEFAULT_WIDTH = 340;
	private static final int DEFAULT_HEIGHT = 150;
	private final QRLabel descriptionLabel = new QRLabel("准备中...");
	private final QRLabel progressLabel = new QRLabel("0%");
	private final JProgressBar progressBar = new JProgressBar(0, 100);
	private final QRRoundButton cancelButton = new QRRoundButton("取消");
	private boolean progressLabelVisible = true;

	public QRProgressDialog(Window owner) {
		this(owner, true);
	}

	public QRProgressDialog(Window owner, boolean parentUnable) {
		super(owner, parentUnable);
		initView();
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setLocationRelativeTo(owner);
	}

	private void initView() {
		contentPane.setLayout(new BorderLayout(0, 12));
		contentPane.setBorder(new EmptyBorder(18, 20, 16, 20));

		descriptionLabel.setTextLeft();

		progressLabel.setTextRight();
		progressLabel.setPreferredSize(new Dimension(54, 24));

		progressBar.setValue(0);
		progressBar.setStringPainted(false);
		progressBar.setPreferredSize(new Dimension(240, 16));

		QRPanel progressPanel = new QRPanel(new BorderLayout(10, 0));
		progressPanel.add(progressBar, BorderLayout.CENTER);
		progressPanel.add(progressLabel, BorderLayout.EAST);

		QRPanel buttonPanel = new QRPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		cancelButton.setPreferredSize(new Dimension(78, 30));
		cancelButton.addClickAction(this::cancelAction);
		buttonPanel.add(cancelButton);

		contentPane.add(descriptionLabel, BorderLayout.NORTH);
		contentPane.add(progressPanel, BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		componentFresh();
	}

	public void setProgress(int progress) {
		runOnEdt(() -> {
			int value = limitProgress(progress);
			progressBar.setValue(value);
			progressLabel.setText(value + "%");
		});
	}

	public int progress() {
		return progressBar.getValue();
	}

	public void setProgressDescription(String description) {
		runOnEdt(() -> descriptionLabel.setText(description == null ? "" : description));
	}

	public String progressDescription() {
		return descriptionLabel.getText();
	}

	public void setProgressLabelVisible(boolean visible) {
		runOnEdt(() -> {
			this.progressLabelVisible = visible;
			progressLabel.setVisible(visible);
			contentPane.revalidate();
			contentPane.repaint();
		});
	}

	public boolean progressLabelVisible() {
		return progressLabelVisible;
	}

	public void addCancelAction(QRActionRegister<ActionEvent> action) {
		cancelButton.addClickAction(action);
	}

	protected void cancelAction(ActionEvent e) {
		dispose();
	}

	@Override
	public void componentFresh() {
		super.componentFresh();
		descriptionLabel.componentFresh();
		progressLabel.componentFresh();
		cancelButton.componentFresh();
		progressBar.setForeground(QRColorsAndFonts.PRESS_COLOR);
		progressBar.setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
	}

	private int limitProgress(int progress) {
		return Math.max(0, Math.min(100, progress));
	}

	private void runOnEdt(Runnable runnable) {
		if (SwingUtilities.isEventDispatchThread()) {
			runnable.run();
		} else {
			SwingUtilities.invokeLater(runnable);
		}
	}
}
