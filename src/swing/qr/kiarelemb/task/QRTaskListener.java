package swing.qr.kiarelemb.task;

/**
 * 后台任务生命周期监听器。
 *
 * <p>所有回调都在 EDT 执行。
 *
 * @param <T> 任务结果类型
 */
public interface QRTaskListener<T> {
	default void started() {
	}

	default void progress(QRTaskProgress progress) {
	}

	default void succeeded(T result) {
	}

	default void failed(Throwable throwable) {
	}

	default void cancelled() {
	}

	default void finished(QRTaskResult<T> result) {
	}
}
