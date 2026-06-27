package swing.qr.kiarelemb.window.utils;

import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.basic.QRProgressBar;
import swing.qr.kiarelemb.basic.QRRoundButton;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.task.QRTaskListener;
import swing.qr.kiarelemb.task.QRTaskProgress;
import swing.qr.kiarelemb.task.QRTaskResult;
import swing.qr.kiarelemb.task.QRTaskWorker;
import swing.qr.kiarelemb.utils.QRComponentUtils;
import swing.qr.kiarelemb.window.basic.QREmptyDialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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
	private static final int DEFAULT_HEIGHT = 100;
	private final Rectangle determinateProgressBarBounds = new Rectangle(15, 45, 230, 10);
	private Rectangle indeterminateProgressBarBounds = null;
	private final Rectangle determinateCancelButtonBounds = new Rectangle(265, 33, 60, 30);
	private Rectangle indeterminateCancelButtonBounds = new Rectangle(265, 60, 60, 30);
	private final QRLabel descriptionLabel = new QRLabel("准备中...");
	private final QRLabel progressLabel = new QRLabel("0%");
	private final QRProgressBar progressBar = new QRProgressBar();
	private final QRRoundButton cancelButton = new QRRoundButton("取消");
	private QRTaskWorker<?> worker;

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
		contentPane.setLayout(null);

		descriptionLabel.setTextLeft();
		progressLabel.setTextLeft();
		progressBar.setValue(0);

		cancelButton.addClickAction(this::cancelAction);

		QRComponentUtils.setBoundsAndAddToComponent(contentPane, progressLabel,15,10,200,30);
		QRComponentUtils.setBoundsAndAddToComponent(contentPane, progressBar,
				determinateProgressBarBounds.x, determinateProgressBarBounds.y,
				determinateProgressBarBounds.width, determinateProgressBarBounds.height);
		QRComponentUtils.setBoundsAndAddToComponent(contentPane, cancelButton,
				determinateCancelButtonBounds.x, determinateCancelButtonBounds.y,
				determinateCancelButtonBounds.width, determinateCancelButtonBounds.height);
		QRComponentUtils.setBoundsAndAddToComponent(contentPane, descriptionLabel,15,60,240,30);
		componentFresh();
	}

	public void setProgress(int progress) {
		QRComponentUtils.runOnEdt(() -> {
			int value = limitProgress(progress);
			progressBar.setValue(value);
			progressLabel.setText(value + "%");
		});
	}

	public int progress() {
		return progressBar.value();
	}

	public void setIndeterminate(boolean indeterminate) {
		QRComponentUtils.runOnEdt(() -> {
			applyProgressLayout(indeterminate);
			progressBar.setIndeterminate(indeterminate);
			if (indeterminate) {
				progressLabel.setText("");
			} else {
				progressLabel.setText(progressBar.value() + "%");
			}
		});
	}

	public boolean indeterminate() {
		return progressBar.indeterminate();
	}

	/**
	 * 返回对话框内部使用的进度条实例。
	 *
	 * <p>需要调整不确定进度动画参数时，可以通过该实例调用
	 * {@link QRProgressBar#setIndeterminateBallCount(int)}、
	 * {@link QRProgressBar#setIndeterminateBallDiameter(int)}、
	 * {@link QRProgressBar#setIndeterminateTimerDelay(int)}、
	 * {@link QRProgressBar#setIndeterminateBallDelaySeconds(double)}、
	 * {@link QRProgressBar#setIndeterminateCycleRestSeconds(double)}、
	 * {@link QRProgressBar#setIndeterminateEdgeToMiddleSpeedRatio(double)} 或
	 * {@link QRProgressBar#setIndeterminateTravelSeconds(double, double, double, double)}。
	 *
	 * @return 当前对话框使用的进度条
	 */
	public QRProgressBar progressBar() {
		return progressBar;
	}

	public void setDeterminateProgressBarBounds(Rectangle bounds) {
		if (bounds == null) {
			return;
		}
		QRComponentUtils.runOnEdt(() -> {
			determinateProgressBarBounds.setBounds(bounds);
			applyProgressLayout(progressBar.indeterminate());
		});
	}

	public void setIndeterminateProgressBarBounds(Rectangle bounds) {
		QRComponentUtils.runOnEdt(() -> {
			indeterminateProgressBarBounds = bounds == null ? null : new Rectangle(bounds);
			applyProgressLayout(progressBar.indeterminate());
		});
	}

	public void setDeterminateCancelButtonBounds(Rectangle bounds) {
		if (bounds == null) {
			return;
		}
		QRComponentUtils.runOnEdt(() -> {
			determinateCancelButtonBounds.setBounds(bounds);
			applyProgressLayout(progressBar.indeterminate());
		});
	}

	public void setIndeterminateCancelButtonBounds(Rectangle bounds) {
		QRComponentUtils.runOnEdt(() -> {
			indeterminateCancelButtonBounds = bounds == null ? null : new Rectangle(bounds);
			applyProgressLayout(progressBar.indeterminate());
		});
	}

	public void setProgressDescription(String description) {
		QRComponentUtils.runOnEdt(() -> descriptionLabel.setText(description == null ? "" : description));
	}

	public String progressDescription() {
		return descriptionLabel.getText();
	}

	public void setProgressLabelVisible(boolean visible) {
		QRComponentUtils.runOnEdt(() -> {
			progressLabel.setVisible(visible);
			contentPane.revalidate();
			contentPane.repaint();
		});
	}

	public boolean progressLabelVisible() {
		return progressLabel.isVisible();
	}

	public void setCancelEnabled(boolean enabled) {
		QRComponentUtils.runOnEdt(() -> cancelButton.setEnabled(enabled));
	}

	public void addCancelAction(QRActionRegister<ActionEvent> action) {
		cancelButton.addClickAction(action);
	}

	public void bind(QRTaskWorker<?> worker) {
		bind(worker, true);
	}

	public void bind(QRTaskWorker<?> worker, boolean autoClose) {
		this.worker = worker;
		if (worker == null) {
			return;
		}
		bindWorker(worker, autoClose);
	}

	private <T> void bindWorker(QRTaskWorker<T> worker, boolean autoClose) {
		worker.addListener(new QRTaskListener<>() {
			@Override
			public void progress(QRTaskProgress progress) {
				if (progress.message() != null) {
					setProgressDescription(progress.message());
				}
				if (progress.percent() != null) {
					setIndeterminate(false);
					setProgress(progress.percent());
				}
			}

			@Override
			public void cancelled() {
				setProgressDescription("已取消");
			}

			@Override
			public void failed(Throwable throwable) {
				setProgressDescription("处理失败");
			}

			@Override
			public void finished(QRTaskResult<T> result) {
				if (autoClose) {
					dispose();
				}
			}
		});
	}

	protected void cancelAction(ActionEvent e) {
		if (worker != null && !worker.isDone()) {
			worker.cancel(true);
			return;
		}
		dispose();
	}

	@Override
	public void componentFresh() {
		super.componentFresh();
		descriptionLabel.componentFresh();
		progressLabel.componentFresh();
		cancelButton.componentFresh();
		progressBar.repaint();
	}

	private int limitProgress(int progress) {
		return Math.max(0, Math.min(100, progress));
	}

	private void applyProgressLayout(boolean indeterminate) {
		Rectangle progressBounds = indeterminate ? indeterminateProgressBounds() : determinateProgressBarBounds;
		Rectangle buttonBounds = indeterminate && indeterminateCancelButtonBounds != null
				? indeterminateCancelButtonBounds
				: determinateCancelButtonBounds;
		progressBar.setBounds(progressBounds);
		cancelButton.setBounds(buttonBounds);
		contentPane.revalidate();
		contentPane.repaint();
	}

	private Rectangle indeterminateProgressBounds() {
		if (indeterminateProgressBarBounds != null) {
			return indeterminateProgressBarBounds;
		}
		int width = contentPane.getWidth();
		if (width <= 0) {
			width = getWidth();
		}
		if (width <= 0) {
			width = DEFAULT_WIDTH;
		}
		return new Rectangle(0, determinateProgressBarBounds.y-10, width, determinateProgressBarBounds.height);
	}
}
