package swing.qr.kiarelemb.window.utils;

import method.qr.kiarelemb.utils.QRSystemUtils;
import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.basic.QRList;
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.basic.QRRoundButton;
import swing.qr.kiarelemb.basic.QRScrollPane;
import swing.qr.kiarelemb.basic.QRTextField;
import swing.qr.kiarelemb.basic.QRTree;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.listener.QRDocumentListener;
import swing.qr.kiarelemb.listener.QRKeyListener;
import swing.qr.kiarelemb.listener.QRMouseListener;
import swing.qr.kiarelemb.task.QRTaskListener;
import swing.qr.kiarelemb.task.QRTaskWorker;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.window.basic.QRDialog;
import swing.qr.kiarelemb.window.enhance.QROpinionDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.*;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 文件/文件夹选择对话框，支持「打开文件」「选择文件夹」「保存文件」三种模式。
 * @apiNote 本类使用方法：
 * <pre><code>
 *      // ── 打开文件 ──
 *      QRFileSelectDialog open = new QRFileSelectDialog(parent, SelectMode.FILE_ONLY, "文本文件", "txt");
 *      open.setVisible(true);
 *      if (open.selectedSucceeded()) {
 *          File file = open.selectedFile();
 *      }
 *
 *      // ── 选择文件夹 ──
 *      QRFileSelectDialog dir = new QRFileSelectDialog(parent, SelectMode.DIRECTORY_ONLY);
 *      dir.setVisible(true);
 *      if (dir.selectedSucceeded()) {
 *          File folder = dir.selectedFile();
 *      }
 *
 *      // ── 保存文件 ──
 *      QRFileSelectDialog save = new QRFileSelectDialog(parent, SelectMode.SAVE_FILE, "图片", "png", "jpg");
 *      save.setVisible(true);
 *      if (save.selectedSucceeded()) {
 *          File file = save.selectedFile();  // 返回用户输入的完整路径（文件可不存在）
 *      }
 * </code></pre>
 * @create 2022-11-22 15:26
 **/
public class QRFileSelectDialog extends QRDialog {
	public enum SelectMode {
		FILE_ONLY, DIRECTORY_ONLY, FILE_AND_DIRECTORY, SAVE_FILE
	}

	private enum SortType {
		NAME, TIME, SIZE
	}

	private final FileSystemView fileSystemView = FileSystemView.getFileSystemView();
	private final DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode("本地磁盘");
	private final DefaultTreeModel treeModel = new DefaultTreeModel(treeRoot);
	private final DefaultListModel<FileItem> fileListModel = new DefaultListModel<>();
	private final QRTree directoryTree = new QRTree(treeRoot);
	private final QRList<FileItem> fileList = new QRList<>(fileListModel);
	/**
	 * 路径框，是对话框顶部的文本框
	 */
	private final QRTextField pathField = new QRTextField();
	/**
	 * 当前选择，是对话框底部的文本框
	 */
	private final QRTextField selectedPathField = new QRTextField();
	private final QRLabel statusLabel = new QRLabel();
	private final QRRoundButton sureButton = new QRRoundButton("确定");
	private final QRRoundButton cancelButton = new QRRoundButton("取消");
	private final QRButton sortNameButton = new QRButton();
	private final QRButton sortTimeButton = new QRButton();
	private final QRButton sortSizeButton = new QRButton();

	private SelectMode selectMode;
	private SortType sortType = SortType.NAME;
	private boolean sortAscending = true;
	private String fileType;
	private final Set<String> extensions = new LinkedHashSet<>();
	private File currentDirectory;
	private File selectedFile;
	private QRTaskWorker<FileListSnapshot> fileListWorker;
	private boolean approved = false;
	private boolean treeSelectionChanging = false;

	public QRFileSelectDialog(Window parent) {
		this(parent, SelectMode.FILE_ONLY);
	}

	public QRFileSelectDialog(Window parent, SelectMode selectMode) {
		this(parent, selectMode, "文件");
	}

	public QRFileSelectDialog(Window parent, SelectMode selectMode, String fileType, String... extension) {
		super(parent);
		this.selectMode = selectMode == null ? SelectMode.FILE_ONLY : selectMode;
		this.fileType = fileType == null || fileType.isBlank() ? "文件" : fileType;
		setExtensions(extension);
		initWindow();
		initTree();
		initList();
		initActions();
		setCurrentDirectory(defaultDirectory());
	}

	public QRFileSelectDialog(Window parent, SelectMode selectMode, File defaultDirectory, String fileType, String... extension) {
		this(parent, selectMode, fileType, extension);
		setCurrentDirectory(initialDirectory(defaultDirectory));
	}

	private void initWindow() {
		setTitle("选择" + switch (selectMode) {
			case FILE_ONLY -> "文件";
			case DIRECTORY_ONLY -> "文件夹";
			case FILE_AND_DIRECTORY -> "文件/文件夹";
			case SAVE_FILE -> "保存文件";
		});
		setTitlePlace(QRDialog.CENTER);
		setSize(820, 560);
		setResizable(true);
		setParentWindowNotFollowMove();

		mainPanel.setLayout(new BorderLayout(8, 8));
		mainPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

		QRPanel topPanel = new QRPanel(new BorderLayout(8, 6));
		QRPanel pathPanel = new QRPanel(new BorderLayout(8, 0));
		QRButton parentButton = new QRButton("上一级");
		QRButton refreshButton = new QRButton("刷新");
		pathField.setToolTipText("输入路径后按 Enter");
		pathPanel.add(new QRLabel("路径"), BorderLayout.WEST);
		pathPanel.add(pathField, BorderLayout.CENTER);
		pathPanel.add(refreshButton, BorderLayout.EAST);

		QRPanel sortPanel = new QRPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		sortNameButton.setPreferredSize(86, 28);
		sortTimeButton.setPreferredSize(86, 28);
		sortSizeButton.setPreferredSize(86, 28);
		parentButton.setPreferredSize(86, 28);
		sortPanel.add(parentButton);
		sortPanel.add(sortNameButton);
		sortPanel.add(sortTimeButton);
		sortPanel.add(sortSizeButton);
		topPanel.add(pathPanel, BorderLayout.NORTH);
		topPanel.add(sortPanel, BorderLayout.CENTER);

		QRPanel centerPanel = new QRPanel(true, new BorderLayout(8, 0));
		QRScrollPane treeScrollPane = new QRScrollPane();
		treeScrollPane.setViewportView(directoryTree);
		treeScrollPane.setPreferredSize(new Dimension(245, 360));

//		listScrollPane.setBorderPaint(true);
//		listScrollPane.getViewport().setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
		centerPanel.add(treeScrollPane, BorderLayout.WEST);
		centerPanel.add(fileList.addScrollPane(), BorderLayout.CENTER);

		QRPanel bottomPanel = new QRPanel(new BorderLayout(8, 8));
		boolean isSaveFile = selectMode == SelectMode.SAVE_FILE;
		selectedPathField.setEditable(isSaveFile);
		selectedPathField.setToolTipText(isSaveFile ? "请输入文件名" : "当前选择");
		statusLabel.setText(fileTypeText());
		QRPanel selectedPanel = new QRPanel(new BorderLayout(8, 0));
		QRLabel selectLabel = new QRLabel(isSaveFile ? "文件名" : "选择");
		selectedPanel.add(selectLabel, BorderLayout.WEST);
		selectedPanel.add(selectedPathField, BorderLayout.CENTER);
		if (isSaveFile) selectedPathField.addUndoManager();

		QRPanel buttonPanel = new QRPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		sureButton.setPreferredSize(78, 32);
		cancelButton.setPreferredSize(78, 32);
		buttonPanel.add(statusLabel);
		buttonPanel.add(cancelButton);
		buttonPanel.add(sureButton);
		bottomPanel.add(selectedPanel, BorderLayout.CENTER);
		bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		parentButton.addClickAction(e -> gotoParentDirectory());
		refreshButton.addClickAction(e -> refreshCurrentDirectory());
		sortNameButton.addClickAction(e -> setSortType(SortType.NAME));
		sortTimeButton.addClickAction(e -> setSortType(SortType.TIME));
		sortSizeButton.addClickAction(e -> setSortType(SortType.SIZE));
		updateSortButtonText();

		QRSystemUtils.setWindowTrans(this, 0.99f);
	}

	private void initTree() {
		directoryTree.setModel(treeModel);
		directoryTree.setRootVisible(false);
		directoryTree.setShowsRootHandles(true);
		directoryTree.setCellRenderer(new FileTreeCellRenderer());
		directoryTree.addTreeWillExpandListener(new TreeWillExpandListener() {
			@Override
			public void treeWillExpand(TreeExpansionEvent event) {
				Object node = event.getPath().getLastPathComponent();
				if (node instanceof FileTreeNode fileTreeNode) {
					loadDirectoryNode(fileTreeNode);
				}
			}

			@Override
			public void treeWillCollapse(TreeExpansionEvent event) {
			}
		});
		directoryTree.addTreeSelectionListener(e -> {
			if (treeSelectionChanging) {
				return;
			}
			Object node = directoryTree.getLastSelectedPathComponent();
			if (node instanceof FileTreeNode fileTreeNode && fileTreeNode.file != null) {
				setCurrentDirectory(fileTreeNode.file, false);
			}
		});

		treeRoot.removeAllChildren();
		for (File root : File.listRoots()) {
			if (root == null) {
				continue;
			}
			FileTreeNode node = new FileTreeNode(root);
			addPlaceHolderIfNeeded(node);
			treeRoot.add(node);
		}
		treeModel.reload();
	}

	private void initList() {
		fileList.setCellRenderer(new FileItemCellRenderer());
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fileList.setFixedCellHeight(28);
		fileList.setOpaque(true);
		fileList.setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
		fileList.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				updateSelectedFromList(false);
			}
		});
		fileList.addMouseListener(QRMouseListener.TYPE.CLICK, e -> {
			if (e.getClickCount() < 2) {
				return;
			}
			FileItem item = fileList.getSelectedValue();
			if (item == null) {
				return;
			}
			if (item.parent) {
				gotoParentDirectory();
			} else if (item.file.isDirectory()) {
				setCurrentDirectory(item.file);
				// 确保导航后当前目录被正确设为已选择状态
				if (selectMode == SelectMode.DIRECTORY_ONLY || selectMode == SelectMode.SAVE_FILE) {
					updateSelectedFile(currentDirectory);
				}
			} else if (selectMode == SelectMode.SAVE_FILE) {
				updateSelectedFile(item.file);
				approveSelection();
			} else if (canSelect(item.file)) {
				updateSelectedFromList(true);
			}
		});
		fileList.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					openOrApproveSelectedListItem();
				}
			}
		});
	}

	private void initActions() {
		pathField.addKeyListenerAction(QRKeyListener.TYPE.PRESS, e -> {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				applyPathField();
			}
		});
		QRActionRegister<DocumentEvent> action = e -> {
			if (selectMode == SelectMode.SAVE_FILE) {
				sureButton.setEnabled(canSelectSaveTarget(resolveSaveTarget(selectedPathField.getText())));
			} else {
				sureButton.setEnabled(canSelect(new File(selectedPathField.getText())));
			}
		};
		selectedPathField.addDocumentListenerAction(QRDocumentListener.TYPE.INSERT, action);
		selectedPathField.addDocumentListenerAction(QRDocumentListener.TYPE.REMOVE, action);

		sureButton.addClickAction(e -> approveSelection());
		cancelButton.addClickAction(e -> dispose());

	}

	private File defaultDirectory() {
		File home = fileSystemView.getDefaultDirectory();
		if (home != null && home.exists() && home.isDirectory()) {
			return home;
		}
		File[] roots = File.listRoots();
		return roots.length == 0 ? new File(System.getProperty("user.dir")) : roots[0];
	}

	private File initialDirectory(File defaultDirectory) {
		File normalized = normalizeDirectory(defaultDirectory);
		if (normalized != null && normalized.exists() && normalized.isDirectory()) {
			return normalized;
		}
		return defaultDirectory();
	}

	private void setCurrentDirectory(File directory) {
		setCurrentDirectory(directory, true);
	}

	private void setCurrentDirectory(File directory, boolean selectTreeNode) {
		if (directory == null) {
			return;
		}
		File normalized = normalizeDirectory(directory);
		if (normalized == null || !normalized.exists() || !normalized.isDirectory()) {
			QROpinionDialog.messageErrShow(parent, "路径不存在或不是文件夹");
			return;
		}
		currentDirectory = normalized;
		pathField.setText(currentDirectory.getAbsolutePath());
		fillFileList(currentDirectory);
		if (selectTreeNode) {
			selectCurrentDirectoryInTree();
		}
		updateSelectedFile(selectMode == SelectMode.FILE_ONLY ? null : currentDirectory);
	}

	private File normalizeDirectory(File file) {
		try {
			if (file.isFile()) {
				return file.getParentFile();
			}
			return file.getCanonicalFile();
		} catch (Exception e) {
			return file;
		}
	}

	private void fillFileList(File directory) {
		if (fileListWorker != null && !fileListWorker.isDone()) {
			fileListWorker.cancel(true);
		}
		fileListModel.clear();
		statusLabel.setText("加载中...  " + fileTypeText());
		SelectMode mode = selectMode;
		SortType sort = sortType;
		boolean ascending = sortAscending;
		Set<String> extensionSnapshot = new LinkedHashSet<>(extensions);
		String fileTypeSnapshot = fileTypeText();
		QRTaskWorker<FileListSnapshot> worker = new QRTaskWorker<>(context -> {
			ArrayList<FileItem> items = new ArrayList<>();
			File parentFile = directory.getParentFile();
			if (parentFile != null) {
				items.add(FileItem.parent(parentFile));
			}
			File[] files = safeListFiles(directory);
			Arrays.sort(files, fileComparator(sort, ascending));
			for (File file : files) {
				context.checkCancelled();
				if ((mode == SelectMode.DIRECTORY_ONLY) && file.isFile()) {
					continue;
				}
				if (file.isDirectory() || acceptExtension(file, extensionSnapshot)) {
					items.add(new FileItem(file, false));
				}
			}
			return new FileListSnapshot(directory, items, fileTypeSnapshot);
		});
		fileListWorker = worker;
		worker.addListener(new QRTaskListener<>() {
			@Override
			public void succeeded(FileListSnapshot result) {
				if (fileListWorker != worker || !sameFile(currentDirectory, result.directory())) {
					return;
				}
				fileListModel.clear();
				result.items().forEach(fileListModel::addElement);
				statusLabel.setText(fileListModel.getSize() + " 项  " + result.fileTypeText());
			}

			@Override
			public void cancelled() {
				if (fileListWorker == worker && sameFile(currentDirectory, directory)) {
					statusLabel.setText(fileTypeText());
				}
			}

			@Override
			public void failed(Throwable throwable) {
				if (fileListWorker == worker && sameFile(currentDirectory, directory)) {
					statusLabel.setText("读取失败  " + fileTypeText());
				}
			}
		});
		worker.execute();
	}

	private Comparator<File> fileComparator() {
		return fileComparator(sortType, sortAscending);
	}

	private Comparator<File> fileComparator(SortType sortType, boolean sortAscending) {
		Comparator<File> comparator = Comparator.comparing(File::isFile);
		Comparator<File> valueComparator = switch (sortType) {
			case NAME -> Comparator.comparing(file -> displayName(file).toLowerCase(Locale.ROOT));
			case TIME -> Comparator.comparingLong(File::lastModified);
			case SIZE -> Comparator.comparingLong(file -> file.isDirectory() ? 0L : file.length());
		};
		if (!sortAscending) {
			valueComparator = valueComparator.reversed();
		}
		return comparator.thenComparing(valueComparator).thenComparing(file -> displayName(file).toLowerCase(Locale.ROOT));
	}

	private void setSortType(SortType sortType) {
		if (this.sortType == sortType) {
			sortAscending = !sortAscending;
		} else {
			this.sortType = sortType;
			sortAscending = true;
		}
		updateSortButtonText();
		if (currentDirectory != null) {
			fillFileList(currentDirectory);
		}
	}

	private void updateSortButtonText() {
		String direction = sortAscending ? "↑" : "↓";
		sortNameButton.setText((sortType == SortType.NAME ? direction : " ") + " 文件名");
		sortTimeButton.setText((sortType == SortType.TIME ? direction : " ") + " 时间");
		sortSizeButton.setText((sortType == SortType.SIZE ? direction : " ") + " 大小");
	}

	private File[] safeListFiles(File directory) {
		try {
			File[] files = directory.listFiles(file -> file != null && !file.isHidden());
			return files == null ? new File[0] : files;
		} catch (Exception e) {
			return new File[0];
		}
	}

	private void openOrApproveSelectedListItem() {
		FileItem item = fileList.getSelectedValue();
		if (item == null) {
			approveSelection();
			return;
		}
		if (item.parent) {
			gotoParentDirectory();
		} else if (item.file.isDirectory() && (selectMode == SelectMode.FILE_ONLY || selectMode == SelectMode.SAVE_FILE)) {
			setCurrentDirectory(item.file);
		} else {
			approveSelection();
		}
	}

	private void updateSelectedFromList(boolean approve) {
		FileItem item = fileList.getSelectedValue();
		if (item == null) {
			updateSelectedFile(selectMode == SelectMode.FILE_ONLY ? null : currentDirectory);
			return;
		}
		if (selectMode == SelectMode.SAVE_FILE) {
			updateSelectedFile(item.file);
			if (approve && !item.parent && item.file.isFile()) {
				approveSelection();
			}
			return;
		}
		if (item.parent) {
			updateSelectedFile(item.file);
		} else if (canSelect(item.file)) {
			updateSelectedFile(item.file);
			if (approve) {
				approveSelection();
			}
		} else {
			updateSelectedFile(null);
		}
	}

	private void updateSelectedFile(File file) {
		selectedFile = file;
		if (file == null) {
			selectedPathField.setText("");
		} else if (selectMode == SelectMode.SAVE_FILE) {
			selectedPathField.setText(file.isFile() ? file.getName() : "");
		} else if (file.isFile()) {
			selectedPathField.setText(file.getName());
		} else {
			selectedPathField.setText(file.getAbsolutePath());
		}
		sureButton.setEnabled(canSelect(file));
	}

	private void approveSelection() {
		File target = selectedFile;

		// 保存模式：从文本框中构建路径
		if (selectMode == SelectMode.SAVE_FILE) {
			approveSaveFile();
			return;
		}

		if (target == null) {
			target = resolvePath(pathField.getText());
		}
		if (target == null || !target.exists()) {
			QROpinionDialog.messageErrShow(parent, "请选择一个存在的路径");
			return;
		}
		if (!canSelect(target)) {
			QROpinionDialog.messageErrShow(parent, "当前选择不符合选择类型或文件格式要求");
			return;
		}
		selectedFile = target;
		approved = true;
		dispose();
	}

	/**
	 * 保存文件模式下的确认逻辑：从文本框读取路径，组合当前目录后返回。
	 */
	private void approveSaveFile() {
		File target = resolveSaveTarget(selectedPathField.getText());
		if (target == null) {
			QROpinionDialog.messageErrShow(parent, "请输入文件名");
			return;
		}
		if (target.getParentFile() == null || !target.getParentFile().exists()) {
			QROpinionDialog.messageErrShow(parent, "目标文件夹不存在");
			return;
		}
		if (target.isDirectory()) {
			QROpinionDialog.messageErrShow(parent, "请输入文件名，不能选择文件夹作为保存目标");
			return;
		}
		if (!acceptExtension(target)) {
			QROpinionDialog.messageErrShow(parent, "文件扩展名不符合要求：" + fileTypeText());
			return;
		}
		if (target.exists() &&
		    QROpinionDialog.messageInfoShow(parent, "文件已存在，是否覆盖？") != QROpinionDialog.OK) {
			return;
		}
		selectedFile = target;
		approved = true;
		dispose();
	}

	private File resolveSaveTarget(String text) {
		if (text == null || text.isBlank()) {
			return null;
		}
		File target = new File(text.trim());
		if (target.isAbsolute()) {
			return appendDefaultSaveExtension(target);
		}
		File base = selectedFile != null ? selectedFile : currentDirectory;
		if (base != null && base.isFile()) {
			base = base.getParentFile();
		}
		if (base == null || !base.isDirectory()) {
			base = currentDirectory;
		}
		return appendDefaultSaveExtension(base == null ? target : new File(base, text.trim()));
	}

	private boolean canSelectSaveTarget(File target) {
		return target != null &&
		       target.getParentFile() != null &&
		       target.getParentFile().exists() &&
		       !target.isDirectory() &&
		       !target.getName().isBlank() &&
		       acceptExtension(target);
	}

	private File appendDefaultSaveExtension(File target) {
		if (target == null || extensions.isEmpty() || hasExtension(target)) {
			return target;
		}
		return new File(target.getParentFile(), target.getName() + "." + extensions.iterator().next());
	}

	private boolean hasExtension(File file) {
		if (file == null) {
			return false;
		}
		String name = file.getName();
		int index = name.lastIndexOf('.');
		return index > 0 && index < name.length() - 1;
	}

	private void applyPathField() {
		File file = resolvePath(pathField.getText());
		if (file == null || !file.exists()) {
			QROpinionDialog.messageErrShow(parent, "路径不存在");
			return;
		}
		if (file.isDirectory()) {
			setCurrentDirectory(file);
			if (selectMode != SelectMode.FILE_ONLY) {
				updateSelectedFile(file);
			}
		} else {
			setCurrentDirectory(file.getParentFile());
			updateSelectedFile(file);
		}
	}

	private File resolvePath(String path) {
		if (path == null || path.isBlank()) {
			return null;
		}
		return new File(path.trim());
	}

	private void gotoParentDirectory() {
		if (currentDirectory == null) {
			return;
		}
		File parentFile = currentDirectory.getParentFile();
		if (parentFile != null) {
			setCurrentDirectory(parentFile);
		}
	}

	private void refreshCurrentDirectory() {
		if (currentDirectory == null) {
			return;
		}
		fillFileList(currentDirectory);
		refreshTreeNode(currentDirectory);
	}

	private void refreshTreeNode(File directory) {
		FileTreeNode node = findTreeNode(directory);
		if (node != null) {
			node.loaded = false;
			node.removeAllChildren();
			addPlaceHolderIfNeeded(node);
			loadDirectoryNode(node);
			treeModel.reload(node);
		}
	}

	private boolean canSelect(File file) {
		if (file == null || file.getParentFile() == null || (!file.exists() && selectMode != SelectMode.SAVE_FILE)) {
			return false;
		}
		return switch (selectMode) {
			case FILE_ONLY -> file.isFile() && acceptExtension(file);
			case DIRECTORY_ONLY -> file.isDirectory();
			case FILE_AND_DIRECTORY -> file.isDirectory() || (file.isFile() && acceptExtension(file));
			case SAVE_FILE -> canSelectSaveTarget(resolveSaveTarget(selectedPathField.getText()));
		};
	}

	private boolean acceptExtension(File file) {
		return acceptExtension(file, extensions);
	}

	private boolean acceptExtension(File file, Set<String> extensions) {
		if (file == null || file.isDirectory() || extensions.isEmpty()) {
			return true;
		}
		String name = file.getName();
		int index = name.lastIndexOf('.');
		if (index < 0 || index == name.length() - 1) {
			return false;
		}
		return extensions.contains(name.substring(index + 1).toLowerCase(Locale.ROOT));
	}

	private String fileTypeText() {
		if (extensions.isEmpty() || selectMode == SelectMode.DIRECTORY_ONLY) {
			return fileType;
		}
		return fileType + " (" + String.join(", ", extensions.stream().map(s -> "." + s).toList()) + ")";
	}

	private void loadDirectoryNode(FileTreeNode node) {
		if (node.loaded || node.file == null) {
			return;
		}
		node.loaded = true;
		node.removeAllChildren();
		File directory = node.file;
		QRTaskWorker<ArrayList<File>> worker = new QRTaskWorker<>(context -> {
			File[] files = safeListFiles(directory);
			ArrayList<File> directories = new ArrayList<>();
			for (File file : files) {
				context.checkCancelled();
				if (file.isDirectory()) {
					directories.add(file);
				}
			}
			directories.sort(Comparator.comparing(file -> displayName(file).toLowerCase(Locale.ROOT)));
			return directories;
		});
		worker.addListener(new QRTaskListener<>() {
			@Override
			public void succeeded(ArrayList<File> directories) {
				node.removeAllChildren();
				for (File directory : directories) {
					FileTreeNode child = new FileTreeNode(directory);
					addPlaceHolderIfNeeded(child);
					node.add(child);
				}
				treeModel.nodeStructureChanged(node);
			}

			@Override
			public void failed(Throwable throwable) {
				node.removeAllChildren();
				treeModel.nodeStructureChanged(node);
			}
		});
		worker.execute();
	}

	private void addPlaceHolderIfNeeded(FileTreeNode node) {
		if (node.file != null && node.file.isDirectory()) {
			node.add(FileTreeNode.placeHolder());
		}
	}

	private boolean hasChildDirectory(File directory) {
		File[] files = safeListFiles(directory);
		for (File file : files) {
			if (file.isDirectory()) {
				return true;
			}
		}
		return false;
	}

	private void selectCurrentDirectoryInTree() {
		FileTreeNode node = findTreeNode(currentDirectory);
		if (node == null) {
			node = buildPathToCurrentDirectory();
		}
		if (node == null) {
			return;
		}
		TreePath treePath = new TreePath(node.getPath());
		treeSelectionChanging = true;
		directoryTree.setSelectionPath(treePath);
		directoryTree.scrollPathToVisible(treePath);
		treeSelectionChanging = false;
	}

	private FileTreeNode buildPathToCurrentDirectory() {
		File rootFile = rootOf(currentDirectory);
		FileTreeNode node = findTreeNode(rootFile);
		if (node == null) {
			return null;
		}

		ArrayList<File> directories = new ArrayList<>();
		File file = currentDirectory;
		while (file != null && !sameFile(file, rootFile)) {
			directories.add(0, file);
			file = file.getParentFile();
		}

		for (File directory : directories) {
			loadDirectoryNode(node);
			FileTreeNode child = findDirectChild(node, directory);
			if (child == null) {
				return node;
			}
			node = child;
		}
		return node;
	}

	private File rootOf(File file) {
		File root = file;
		while (root != null && root.getParentFile() != null) {
			root = root.getParentFile();
		}
		return root == null ? file : root;
	}

	private FileTreeNode findTreeNode(File file) {
		if (file == null) {
			return null;
		}
		for (int i = 0; i < treeRoot.getChildCount(); i++) {
			FileTreeNode node = (FileTreeNode) treeRoot.getChildAt(i);
			FileTreeNode found = findTreeNode(node, file);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	private FileTreeNode findTreeNode(FileTreeNode node, File file) {
		if (sameFile(node.file, file)) {
			return node;
		}
		for (int i = 0; i < node.getChildCount(); i++) {
			Object child = node.getChildAt(i);
			if (child instanceof FileTreeNode childNode && !childNode.placeHolder) {
				FileTreeNode found = findTreeNode(childNode, file);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}

	private FileTreeNode findDirectChild(FileTreeNode node, File file) {
		for (int i = 0; i < node.getChildCount(); i++) {
			Object child = node.getChildAt(i);
			if (child instanceof FileTreeNode childNode && sameFile(childNode.file, file)) {
				return childNode;
			}
		}
		return null;
	}

	private boolean sameFile(File a, File b) {
		if (a == null || b == null) {
			return false;
		}
		try {
			return a.getCanonicalFile().equals(b.getCanonicalFile());
		} catch (Exception e) {
			return a.equals(b);
		}
	}

	private String displayName(File file) {
		String displayName = fileSystemView.getSystemDisplayName(file);
		return displayName == null || displayName.isBlank() ? file.getName() : displayName;
	}

	public boolean showDialog() {
		setVisible(true);
		return selectedSucceeded();
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			approved = false;
		}
		super.setVisible(b);
	}

	public boolean selectedSucceeded() {
		if (selectMode == SelectMode.SAVE_FILE) {
			return approved && canSelectSaveTarget(selectedFile);
		}
		return approved && canSelect(selectedFile);
	}

	public File selectedFile() {
		return selectedFile;
	}

	public String selectedFilePath() {
		return selectedFile == null ? null : selectedFile.getAbsolutePath();
	}

	public void setSelectedFile(File selectedFile) {
		if (selectedFile == null) {
			updateSelectedFile(null);
			return;
		}
		if (selectedFile.isDirectory()) {
			setCurrentDirectory(selectedFile);
		} else {
			setCurrentDirectory(selectedFile.getParentFile());
			updateSelectedFile(selectedFile);
		}
	}

	public void setSelectedFilePath(String selectedFilePath) {
		File file = resolvePath(selectedFilePath);
		if (file != null) {
			setSelectedFile(file);
		}
	}

	public void setSelectMode(SelectMode selectMode) {
		this.selectMode = selectMode == null ? SelectMode.FILE_ONLY : selectMode;
		setTitle("选择" + switch (this.selectMode) {
			case FILE_ONLY -> "文件";
			case DIRECTORY_ONLY -> "文件夹";
			case FILE_AND_DIRECTORY -> "文件/文件夹";
			case SAVE_FILE -> "保存文件";
		});
		if (currentDirectory != null) {
			fillFileList(currentDirectory);
		}
		updateSelectedFile(this.selectMode == SelectMode.FILE_ONLY ? null : currentDirectory);
	}

	public void setExtensions(String... extension) {
		extensions.clear();
		if (extension == null) {
			return;
		}
		for (String ext : extension) {
			if (ext == null || ext.isBlank()) {
				continue;
			}
			String value = ext.startsWith(".") ? ext.substring(1) : ext;
			extensions.add(value.toLowerCase(Locale.ROOT));
		}
		if (currentDirectory != null) {
			fillFileList(currentDirectory);
		} else {
			statusLabel.setText(fileTypeText());
		}
	}

	public void setFileType(String fileType) {
		this.fileType = fileType == null || fileType.isBlank() ? "文件" : fileType;
		if (currentDirectory != null) {
			fillFileList(currentDirectory);
		} else {
			statusLabel.setText(fileTypeText());
		}
	}

	@Override
	public void componentFresh() {
		super.componentFresh();
		fileList.setFont(QRColorsAndFonts.STANDARD_FONT_TEXT);
		fileList.setForeground(QRColorsAndFonts.TEXT_COLOR_FORE);
		fileList.setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
		fileList.repaint();
	}

	private class FileTreeCellRenderer extends QRTree.QRTreeCellRenderer {
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
		                                              boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			if (value instanceof FileTreeNode node && node.file != null) {
				setIcon(fileSystemView.getSystemIcon(node.file));
				setText(displayName(node.file));
			}
			return this;
		}
	}

	private class FileItemCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
		                                              boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, false);
			setBorder(new EmptyBorder(0, 8, 0, 8));
			setOpaque(true);
			setFont(QRColorsAndFonts.STANDARD_FONT_TEXT);
			setForeground(QRColorsAndFonts.TEXT_COLOR_FORE);
			setBackground(isSelected ? QRColorsAndFonts.PRESS_COLOR : QRColorsAndFonts.FRAME_COLOR_BACK);
			if (value instanceof FileItem item) {
				setText(item.parent ? ".." : displayName(item.file));
				setIcon(item.parent ? UIManager.getIcon("FileChooser.upFolderIcon") : fileSystemView.getSystemIcon(item.file));
//                setToolTipText(item.file.getAbsolutePath());
			}
			return this;
		}
	}

	private static class FileTreeNode extends DefaultMutableTreeNode {
		private final File file;
		private final boolean placeHolder;
		private boolean loaded = false;

		private FileTreeNode(File file) {
			this.file = file;
			this.placeHolder = false;
		}

		private FileTreeNode() {
			this.file = null;
			this.placeHolder = true;
		}

		private static FileTreeNode placeHolder() {
			return new FileTreeNode();
		}
	}

	private record FileItem(File file, boolean parent) {

		private static FileItem parent(File file) {
			return new FileItem(file, true);
		}
	}

	private record FileListSnapshot(File directory, ArrayList<FileItem> items, String fileTypeText) {
	}
}
