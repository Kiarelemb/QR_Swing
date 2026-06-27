package swing.qr.kiarelemb.task;

import java.awt.*;

/**
 * 后台任务执行选项。
 */
public class QRTaskOptions {
	private Window owner;
	private String title = "正在处理";
	private String description = "正在处理...";
	private boolean showProgressDialog = false;
	private boolean parentUnable = false;
	private boolean cancellable = true;
	private boolean autoCloseDialog = true;
	private boolean indeterminate = false;

	public Window owner() {
		return owner;
	}

	public QRTaskOptions owner(Window owner) {
		this.owner = owner;
		return this;
	}

	public String title() {
		return title;
	}

	public QRTaskOptions title(String title) {
		this.title = title == null || title.isBlank() ? "正在处理" : title;
		return this;
	}

	public String description() {
		return description;
	}

	public QRTaskOptions description(String description) {
		this.description = description == null ? "" : description;
		return this;
	}

	public boolean showProgressDialog() {
		return showProgressDialog;
	}

	public QRTaskOptions showProgressDialog(boolean showProgressDialog) {
		this.showProgressDialog = showProgressDialog;
		return this;
	}

	public boolean parentUnable() {
		return parentUnable;
	}

	public QRTaskOptions parentUnable(boolean parentUnable) {
		this.parentUnable = parentUnable;
		return this;
	}

	public boolean cancellable() {
		return cancellable;
	}

	public QRTaskOptions cancellable(boolean cancellable) {
		this.cancellable = cancellable;
		return this;
	}

	public boolean autoCloseDialog() {
		return autoCloseDialog;
	}

	public QRTaskOptions autoCloseDialog(boolean autoCloseDialog) {
		this.autoCloseDialog = autoCloseDialog;
		return this;
	}

	public boolean indeterminate() {
		return indeterminate;
	}

	public QRTaskOptions indeterminate(boolean indeterminate) {
		this.indeterminate = indeterminate;
		return this;
	}
}
