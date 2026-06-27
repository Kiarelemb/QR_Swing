package swing.qr.kiarelemb.task;

import swing.qr.kiarelemb.utils.QRComponentUtils;
import swing.qr.kiarelemb.window.utils.QRProgressDialog;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

/**
 * 后台任务启动入口。
 */
public final class QRTaskRunner {
	private QRTaskRunner() {
	}

	public static <T> QRTaskWorker<T> run(QRTask<T> task) {
		return run(new QRTaskOptions(), task);
	}

	public static <T> QRTaskWorker<T> run(QRTaskOptions options, QRTask<T> task) {
		QRTaskOptions actualOptions = options == null ? new QRTaskOptions() : options;
		QRTaskWorker<T> worker = new QRTaskWorker<>(task);
		QRProgressDialog dialog = null;
		if (actualOptions.showProgressDialog()) {
			dialog = createProgressDialog(actualOptions, worker);
		}
		worker.execute();
		showProgressDialog(worker, dialog);
		return worker;
	}

	public static <T> QRTaskWorker<T> runWithProgress(Window owner,
	                                                   String title,
	                                                   QRTask<T> task,
	                                                   Consumer<T> onSuccess,
	                                                   Consumer<Throwable> onError) {
		QRTaskOptions options = new QRTaskOptions()
				.owner(owner)
				.title(title)
				.description(title)
				.showProgressDialog(true);
		QRTaskWorker<T> worker = new QRTaskWorker<>(task);
		worker.addListener(new QRTaskListener<>() {
			@Override
			public void succeeded(T result) {
				if (onSuccess != null) {
					onSuccess.accept(result);
				}
			}

			@Override
			public void failed(Throwable throwable) {
				if (onError != null) {
					onError.accept(throwable);
				}
			}
		});
		QRProgressDialog dialog = createProgressDialog(options, worker);
		worker.execute();
		showProgressDialog(worker, dialog);
		return worker;
	}

	private static QRProgressDialog createProgressDialog(QRTaskOptions options, QRTaskWorker<?> worker) {
		final QRProgressDialog[] dialog = new QRProgressDialog[1];
		Runnable action = () -> dialog[0] = buildProgressDialog(options, worker);
		if (SwingUtilities.isEventDispatchThread()) {
			action.run();
			return dialog[0];
		}
		try {
			SwingUtilities.invokeAndWait(action);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getCause());
		}
		return dialog[0];
	}

	private static QRProgressDialog buildProgressDialog(QRTaskOptions options, QRTaskWorker<?> worker) {
		QRProgressDialog dialog = new QRProgressDialog(options.owner(), options.parentUnable());
		dialog.setTitle(options.title());
		dialog.setProgressDescription(options.description());
		dialog.setProgress(0);
		dialog.setIndeterminate(options.indeterminate());
		dialog.setCancelEnabled(options.cancellable());
		dialog.bind(worker, options.autoCloseDialog());
		return dialog;
	}

	private static void showProgressDialog(QRTaskWorker<?> worker, QRProgressDialog dialog) {
		if (dialog != null) {
			QRComponentUtils.runOnEdt(() -> {
				if (!worker.isDone()) {
					dialog.setVisible(true);
				}
			});
		}
	}
}
