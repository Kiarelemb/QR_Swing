package swing.qr.kiarelemb.task;

/**
 * 后台任务生命周期监听器。
 *
 * <p>所有回调都在 EDT 执行。
 *
 * <p>使用例：
 * <pre><code>
 * QRTaskWorker&lt;String&gt; worker = QRTaskRunner.run(context -> "完成");
 * worker.addListener(new QRTaskListener&lt;&gt;() {
 *     &#64;Override
 *     public void succeeded(String result) {
 *         label.setText(result);
 *     }
 *
 *     &#64;Override
 *     public void failed(Throwable throwable) {
 *         label.setText("失败：" + throwable.getMessage());
 *     }
 * });
 * </code></pre>
 *
 * @param <T> 任务结果类型
 */
public interface QRTaskListener<T> {
	/**
	 * 任务开始回调，在 EDT 执行。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * public void started() {
	 *     button.setEnabled(false);
	 * }
	 * </code></pre>
	 */
	default void started() {
	}

	/**
	 * 进度回调，在 EDT 执行。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * public void progress(QRTaskProgress progress) {
	 *     if (progress.percent() != null) {
	 *         progressBar.setValue(progress.percent());
	 *     }
	 * }
	 * </code></pre>
	 *
	 * @param progress 进度事件
	 */
	default void progress(QRTaskProgress progress) {
	}

	/**
	 * 成功回调，在 EDT 执行。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * public void succeeded(String result) {
	 *     label.setText(result);
	 * }
	 * </code></pre>
	 *
	 * @param result 任务结果
	 */
	default void succeeded(T result) {
	}

	/**
	 * 失败回调，在 EDT 执行。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * public void failed(Throwable throwable) {
	 *     label.setText("失败：" + throwable.getMessage());
	 * }
	 * </code></pre>
	 *
	 * @param throwable 失败原因
	 */
	default void failed(Throwable throwable) {
	}

	/**
	 * 取消回调，在 EDT 执行。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * public void cancelled() {
	 *     label.setText("已取消");
	 * }
	 * </code></pre>
	 */
	default void cancelled() {
	}

	/**
	 * 结束回调，在 EDT 执行。成功、失败或取消后都会调用。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * public void finished(QRTaskResult&lt;String&gt; result) {
	 *     button.setEnabled(true);
	 * }
	 * </code></pre>
	 *
	 * @param result 任务终态结果
	 */
	default void finished(QRTaskResult<T> result) {
	}
}
