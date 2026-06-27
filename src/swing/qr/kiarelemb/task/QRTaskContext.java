package swing.qr.kiarelemb.task;

import java.util.concurrent.CancellationException;

/**
 * 后台任务上下文。
 */
public interface QRTaskContext {
	void progress(int current, int total);

	void progress(int percent);

	void message(String message);

	boolean isCancelled();

	default void checkCancelled() {
		if (isCancelled()) {
			throw new CancellationException("Task cancelled");
		}
	}
}
