package swing.qr.kiarelemb.task;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;
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
	private final List<QRTaskListener<T>> listeners = new CopyOnWriteArrayList<>();
	private final Object resultLock = new Object();
	private volatile QRTaskStatus status = QRTaskStatus.PENDING;
	private QRTaskResult<T> result;

	public QRTaskWorker(QRTask<T> task) {
		if (task == null) {
			throw new IllegalArgumentException("task can not be null");
		}
		this.task = task;
	}

	/**
	 * 添加任务监听器。
	 *
	 * <p>任务运行期间添加的监听器会从下一次事件开始接收回调。任务完成后添加的监听器，
	 * 会在 EDT 回放终态回调：成功、失败或取消，以及 {@link QRTaskListener#finished(QRTaskResult)}。
	 * 不回放 {@link QRTaskListener#started()} 或历史进度。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskWorker&lt;String&gt; worker = QRTaskRunner.run(context -> "完成");
	 * worker.addListener(new QRTaskListener&lt;&gt;() {
	 *     &#64;Override
	 *     public void succeeded(String result) {
	 *         label.setText(result);
	 *     }
	 * });
	 * </code></pre>
	 *
	 * @param listener 任务监听器
	 */
	public void addListener(QRTaskListener<T> listener) {
		if (listener == null) {
			return;
		}
		QRTaskResult<T> completedResult;
		synchronized (resultLock) {
			completedResult = result;
			if (completedResult == null) {
				listeners.add(listener);
				return;
			}
		}
		replayCompleted(listener, completedResult);
	}

	/**
	 * 返回当前任务状态。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * if (worker.status() == QRTaskStatus.RUNNING) {
	 *     worker.cancel(true);
	 * }
	 * </code></pre>
	 *
	 * @return 当前任务状态
	 */
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
		QRTaskResult<T> completedResult;
		try {
			T value = get();
			status = QRTaskStatus.SUCCEEDED;
			completedResult = QRTaskResult.succeeded(value);
			setResult(completedResult);
			listeners.forEach(listener -> listener.succeeded(value));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			status = QRTaskStatus.CANCELLED;
			completedResult = QRTaskResult.cancelled();
			setResult(completedResult);
			listeners.forEach(QRTaskListener::cancelled);
		} catch (CancellationException e) {
			status = QRTaskStatus.CANCELLED;
			completedResult = QRTaskResult.cancelled();
			setResult(completedResult);
			listeners.forEach(QRTaskListener::cancelled);
		} catch (ExecutionException e) {
			Throwable throwable = e.getCause() == null ? e : e.getCause();
			status = QRTaskStatus.FAILED;
			completedResult = QRTaskResult.failed(throwable);
			setResult(completedResult);
			listeners.forEach(listener -> listener.failed(throwable));
		}
		QRTaskResult<T> finalResult = completedResult;
		listeners.forEach(listener -> listener.finished(finalResult));
	}

	private void setResult(QRTaskResult<T> result) {
		synchronized (resultLock) {
			this.result = result;
		}
	}

	private void replayCompleted(QRTaskListener<T> listener, QRTaskResult<T> completedResult) {
		Runnable callback = () -> {
			switch (completedResult.status()) {
				case SUCCEEDED -> listener.succeeded(completedResult.value());
				case FAILED -> listener.failed(completedResult.throwable());
				case CANCELLED -> listener.cancelled();
				default -> {
				}
			}
			listener.finished(completedResult);
		};
		if (SwingUtilities.isEventDispatchThread()) {
			callback.run();
		} else {
			SwingUtilities.invokeLater(callback);
		}
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
