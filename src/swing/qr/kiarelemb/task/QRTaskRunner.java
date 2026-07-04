package swing.qr.kiarelemb.task;

import swing.qr.kiarelemb.utils.QRComponentUtils;
import swing.qr.kiarelemb.window.utils.QRProgressDialog;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

/**
 * 后台任务启动入口。任务主体在后台线程执行，任务监听器和 options 回调都在 EDT 执行。
 */
public final class QRTaskRunner {
	private QRTaskRunner() {
	}

	/**
	 * 使用默认选项启动后台任务。
	 *
	 * <p>{@link QRTask#run(QRTaskContext)} 在后台线程执行，返回的 worker 可继续添加监听器。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskWorker&lt;String&gt; worker = QRTaskRunner.run(context -> {
	 *     context.message("正在处理...");
	 *     return "完成";
	 * });
	 * worker.addListener(new QRTaskListener&lt;&gt;() {
	 *     &#64;Override
	 *     public void succeeded(String result) {
	 *         label.setText(result);
	 *     }
	 * });
	 * </code></pre>
	 *
	 * @param task 后台任务
	 * @param <T>  任务结果类型
	 * @return 已启动的任务 worker
	 */
	public static <T> QRTaskWorker<T> run(QRTask<T> task) {
		return run(new QRTaskOptions(), task);
	}

	/**
	 * 使用指定选项启动后台任务。
	 *
	 * <p>{@link QRTask#run(QRTaskContext)} 在后台线程执行；{@link QRTaskOptions} 中的
	 * 生命周期回调、worker 监听器和进度对话框绑定回调都在 EDT 执行。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskRunner.run(new QRTaskOptions()
	 *         .owner(this)
	 *         .title("导出")
	 *         .description("正在导出...")
	 *         .showProgressDialog(true)
	 *         .onSuccess(path -> label.setText("完成：" + path))
	 *         .onError(error -> label.setText("失败：" + error.getMessage())),
	 *         context -> {
	 *             context.progress(50);
	 *             return exportFile();
	 *         });
	 * </code></pre>
	 *
	 * @param options 执行选项，null 时使用默认选项
	 * @param task    后台任务
	 * @param <T>     任务结果类型
	 * @return 已启动的任务 worker
	 */
	public static <T> QRTaskWorker<T> run(QRTaskOptions options, QRTask<T> task) {
		QRTaskOptions actualOptions = options == null ? new QRTaskOptions() : options;
		QRTaskWorker<T> worker = new QRTaskWorker<>(task);
		QRProgressDialog dialog = null;
		if (actualOptions.showProgressDialog()) {
			dialog = createProgressDialog(actualOptions, worker);
		}
		bindOptionCallbacks(actualOptions, worker);
		worker.execute();
		showProgressDialog(worker, dialog);
		return worker;
	}

	/**
	 * 使用进度对话框启动后台任务。标题和初始描述相同。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskRunner.runWithProgress(this, "正在导出", context -> {
	 *     for (int i = 1; i &lt;= 100; i++) {
	 *         context.checkCancelled();
	 *         context.progress(i);
	 *     }
	 *     return "完成";
	 * }, result -> label.setText(result),
	 *         error -> label.setText("失败：" + error.getMessage()));
	 * </code></pre>
	 *
	 * @param owner     所属窗口
	 * @param title     对话框标题和初始描述
	 * @param task      后台任务
	 * @param onSuccess 成功回调，在 EDT 执行
	 * @param onError   失败回调，在 EDT 执行
	 * @param <T>       任务结果类型
	 * @return 已启动的任务 worker
	 */
	public static <T> QRTaskWorker<T> runWithProgress(Window owner,
	                                                  String title,
	                                                  QRTask<T> task,
	                                                  Consumer<T> onSuccess,
	                                                  Consumer<Throwable> onError) {
		return runWithProgress(owner, title, title, task, onSuccess, onError);
	}

	/**
	 * 使用进度对话框启动后台任务。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskRunner.runWithProgress(this, "导出文件", "正在准备数据...", context -> {
	 *     context.message("正在写入文件...");
	 *     context.progress(80);
	 *     return exportFile();
	 * }, file -> label.setText(file.getAbsolutePath()),
	 *         error -> label.setText("导出失败"));
	 * </code></pre>
	 *
	 * @param owner       所属窗口
	 * @param title       对话框标题
	 * @param description 初始阶段描述
	 * @param task        后台任务
	 * @param onSuccess   成功回调，在 EDT 执行
	 * @param onError     失败回调，在 EDT 执行
	 * @param <T>         任务结果类型
	 * @return 已启动的任务 worker
	 */
	public static <T> QRTaskWorker<T> runWithProgress(Window owner,
	                                                  String title,
	                                                  String description,
	                                                  QRTask<T> task,
	                                                  Consumer<T> onSuccess,
	                                                  Consumer<Throwable> onError) {
		QRTaskOptions options = new QRTaskOptions()
				.owner(owner)
				.title(title)
				.description(description)
				.showProgressDialog(true)
				.onSuccess(onSuccess)
				.onError(onError);
		return run(options, task);
	}

	private static <T> void bindOptionCallbacks(QRTaskOptions options, QRTaskWorker<T> worker) {
		worker.addListener(new QRTaskListener<>() {
			@Override
			public void started() {
				if (options.onStarted() != null) {
					options.onStarted().run();
				}
			}

			@Override
			public void progress(QRTaskProgress progress) {
				if (options.onProgress() != null) {
					options.onProgress().accept(progress);
				}
			}

			@Override
			public void succeeded(T result) {
				if (options.onSuccess() != null) {
					options.onSuccess().accept(result);
				}
			}

			@Override
			public void failed(Throwable throwable) {
				if (options.onError() != null) {
					options.onError().accept(throwable);
				}
			}

			@Override
			public void cancelled() {
				if (options.onCancelled() != null) {
					options.onCancelled().run();
				}
			}

			@Override
			public void finished(QRTaskResult<T> result) {
				if (options.onFinished() != null) {
					options.onFinished().accept(result);
				}
			}
		});
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
		dialog.setProgressDescription(options.description())
				.setProgress(0)
				.setIndeterminate(options.indeterminate())
				.setCancelEnabled(options.cancellable())
				.bind(worker, options.autoCloseDialog());
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
