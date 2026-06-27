package swing.qr.kiarelemb.task;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * QR Swing 后台任务 worker。
 *
 * <p>任务主体在后台线程执行，监听器回调在 EDT 执行。
 *
 * @param <T> 任务结果类型
 */
public class QRTaskWorker<T> extends SwingWorker<T, QRTaskProgress> {
	private final QRTask<T> task;
	private final List<QRTaskListener<T>> listeners = new ArrayList<>();
	private QRTaskStatus status = QRTaskStatus.PENDING;

	public QRTaskWorker(QRTask<T> task) {
		if (task == null) {
			throw new IllegalArgumentException("task can not be null");
		}
		this.task = task;
	}

	public void addListener(QRTaskListener<T> listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}

	public QRTaskStatus status() {
		return status;
	}

	@Override
	protected T doInBackground() throws Exception {
		status = QRTaskStatus.RUNNING;
		SwingUtilities.invokeLater(() -> listeners.forEach(QRTaskListener::started));
		return task.run(new Context());
	}

	@Override
	protected void process(List<QRTaskProgress> chunks) {
		if (chunks == null || chunks.isEmpty()) {
			return;
		}
		QRTaskProgress progress = chunks.get(chunks.size() - 1);
		listeners.forEach(listener -> listener.progress(progress));
	}

	@Override
	protected void done() {
		QRTaskResult<T> result;
		try {
			T value = get();
			status = QRTaskStatus.SUCCEEDED;
			result = QRTaskResult.succeeded(value);
			listeners.forEach(listener -> listener.succeeded(value));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			status = QRTaskStatus.CANCELLED;
			result = QRTaskResult.cancelled();
			listeners.forEach(QRTaskListener::cancelled);
		} catch (CancellationException e) {
			status = QRTaskStatus.CANCELLED;
			result = QRTaskResult.cancelled();
			listeners.forEach(QRTaskListener::cancelled);
		} catch (ExecutionException e) {
			Throwable throwable = e.getCause() == null ? e : e.getCause();
			status = QRTaskStatus.FAILED;
			result = QRTaskResult.failed(throwable);
			listeners.forEach(listener -> listener.failed(throwable));
		}
		QRTaskResult<T> finalResult = result;
		listeners.forEach(listener -> listener.finished(finalResult));
	}

	private class Context implements QRTaskContext {
		@Override
		public void progress(int current, int total) {
			publish(QRTaskProgress.progress(current, total));
		}

		@Override
		public void progress(int percent) {
			publish(QRTaskProgress.percent(percent));
		}

		@Override
		public void message(String message) {
			publish(QRTaskProgress.message(message));
		}

		@Override
		public boolean isCancelled() {
			return QRTaskWorker.this.isCancelled();
		}
	}
}
