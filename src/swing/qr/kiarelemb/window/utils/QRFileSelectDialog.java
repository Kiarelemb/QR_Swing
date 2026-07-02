package swing.qr.kiarelemb.window.utils;

import method.qr.kiarelemb.utils.QRSystemUtils;
import swing.qr.kiarelemb.basic.*;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.listener.QRDocumentListener;
import swing.qr.kiarelemb.listener.QRKeyListener;
import swing.qr.kiarelemb.listener.QRMouseListener;
import swing.qr.kiarelemb.listener.QRWindowListener;
import swing.qr.kiarelemb.task.QRTaskListener;
import swing.qr.kiarelemb.task.QRTaskWorker;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.utils.QRComponentUtils;
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
import java.util.concurrent.ConcurrentHashMap;

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
	/**
	 * 文件选择模式。
	 *
	 * <p>{@link #FILE_ONLY} 只能选择已存在文件；{@link #DIRECTORY_ONLY} 只能选择文件夹；
	 * {@link #FILE_AND_DIRECTORY} 文件或文件夹均可；{@link #SAVE_FILE} 用于生成保存路径，
	 * 返回的文件可以尚不存在。</p>
	 */
	public enum SelectMode {
		FILE_ONLY, DIRECTORY_ONLY, FILE_AND_DIRECTORY, SAVE_FILE
	}

	private enum SortType {
		NAME, TIME, SIZE
	}

	private enum DirectoryReadStatus {
		SUCCESS, NOT_EXISTS, NOT_DIRECTORY, ACCESS_DENIED, FAILED
	}

	private final FileSystemView fileSystemView = FileSystemView.getFileSystemView();
	private final Map<String, String> displayNameCache = new ConcurrentHashMap<>();
	private final Map<String, Icon> systemIconCache = new ConcurrentHashMap<>();
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
	private final Icon parentDirectoryIcon = UIManager.getIcon("FileChooser.upFolderIcon");

	private SelectMode selectMode;
	private SortType sortType = SortType.NAME;
	private boolean sortAscending = true;
	private String fileType;
	private final Set<String> extensions = new LinkedHashSet<>();
	private final Set<String> directoryVisibleFileExtensions = new LinkedHashSet<>();
	private File currentDirectory;
	private File selectedFile;
	private QRTaskWorker<ArrayList<FileTreeNodeData>> rootLoadWorker;
	private QRTaskWorker<FileListSnapshot> fileListWorker;
	private boolean approved = false;
	private boolean treeSelectionChanging = false;
	private int treePathSelectionVersion = 0;

	public QRFileSelectDialog(Window parent) {
		this(parent, SelectMode.FILE_ONLY);
	}

	/**
	 * 创建文件选择对话框。
	 *
	 * @param parent     父窗体，可为 null
	 * @param selectMode 选择模式，null 时按 {@link SelectMode#FILE_ONLY}
	 */
	public QRFileSelectDialog(Window parent, SelectMode selectMode) {
		this(parent, selectMode, "文件");
	}

	/**
	 * 创建带文件类型过滤的文件选择对话框。
	 *
	 * <p>{@code extension} 可带或不带英文句点，例如 {@code "png"} 和 {@code ".png"} 等价。
	 * 在 {@link SelectMode#SAVE_FILE} 模式下，如果用户输入的文件名没有扩展名，会自动补第一个扩展名。</p>
	 *
	 * @param parent     父窗体，可为 null
	 * @param selectMode 选择模式，null 时按 {@link SelectMode#FILE_ONLY}
	 * @param fileType   状态栏显示的文件类型名称，空白时显示“文件”
	 * @param extension  可选择的扩展名列表；为空时不过滤文件类型
	 */
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

	/**
	 * 创建带默认目录和文件类型过滤的文件选择对话框。
	 *
	 * @param parent           父窗体，可为 null
	 * @param selectMode       选择模式，null 时按 {@link SelectMode#FILE_ONLY}
	 * @param defaultDirectory 初始目录；为文件时使用其父目录，无效时使用系统默认目录
	 * @param fileType         状态栏显示的文件类型名称，空白时显示“文件”
	 * @param extension        可选择的扩展名列表；为空时不过滤文件类型
	 */
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

		QRSplitPane splitPane = new QRSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerSize(3);
//		QRPanel centerPanel = new QRPanel(true, new BorderLayout(8, 0));
		QRScrollPane treeScrollPane = new QRScrollPane();
		treeScrollPane.setViewportView(directoryTree);
		treeScrollPane.setPreferredSize(new Dimension(200, 360));

//		directoryTree.setOpaque(true);
//		treeScrollPane.getViewport().setOpaque(true);
//		treeScrollPane.getViewport().setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
//		listScrollPane.setBorderPaint(true);
//		listScrollPane.getViewport().setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);

		splitPane.setTopComponent(treeScrollPane);
		splitPane.setBottomComponent(fileList.addScrollPane());
//		centerPanel.add(treeScrollPane, BorderLayout.WEST);
//		centerPanel.add(fileList.addScrollPane(), BorderLayout.CENTER);

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
//		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(splitPane, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		parentButton.addClickAction(e -> gotoParentDirectory());
		refreshButton.addClickAction(e -> refreshCurrentDirectory());
		sortNameButton.addClickAction(e -> setSortType(SortType.NAME));
		sortTimeButton.addClickAction(e -> setSortType(SortType.TIME));
		sortSizeButton.addClickAction(e -> setSortType(SortType.SIZE));
		updateSortButtonText();

		QRSystemUtils.setWindowTrans(this, 0.98f);
		QRComponentUtils.componentLoopToSetOpaque(this.contentPane, true);

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

		loadRootNodes();
		treeModel.reload();
	}

	private void loadRootNodes() {
		treeRoot.removeAllChildren();
		treeRoot.add(FileTreeNode.placeHolder());
		if (rootLoadWorker != null && !rootLoadWorker.isDone()) {
			rootLoadWorker.cancel(true);
		}
		QRTaskWorker<ArrayList<FileTreeNodeData>> worker = new QRTaskWorker<>(context -> {
			ArrayList<FileTreeNodeData> rootData = new ArrayList<>();
			File[] roots = File.listRoots();
			if (roots != null) {
				for (File root : roots) {
					context.checkCancelled();
					if (root != null) {
						rootData.add(createTreeNodeData(root));
					}
				}
			}
			return rootData;
		});
		rootLoadWorker = worker;
		worker.addListener(new QRTaskListener<>() {
			@Override
			public void succeeded(ArrayList<FileTreeNodeData> roots) {
				if (rootLoadWorker != worker) {
					return;
				}
				treeRoot.removeAllChildren();
				for (FileTreeNodeData data : roots) {
					FileTreeNode node = new FileTreeNode(data);
					addPlaceHolderIfNeeded(node);
					treeRoot.add(node);
				}
				treeModel.reload();
				if (currentDirectory != null) {
					selectCurrentDirectoryInTree();
				}
			}

			@Override
			public void failed(Throwable throwable) {
				if (rootLoadWorker != worker) {
					return;
				}
				treeRoot.removeAllChildren();
				treeModel.reload();
				statusLabel.setText("读取磁盘根目录失败  " + fileTypeText());
			}
		});
		worker.execute();
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

		selectedPathField.addDocumentListenerActionAll(e -> {
			if (selectMode == SelectMode.SAVE_FILE) {
				sureButton.setEnabled(canSelectSaveTarget(resolveSaveTarget(selectedPathField.getText())));
			} else {
				sureButton.setEnabled(canSelect(new File(selectedPathField.getText())));
			}
		});

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
		Set<String> directoryVisibleFileExtensionSnapshot = new LinkedHashSet<>(directoryVisibleFileExtensions);
		String fileTypeSnapshot = fileTypeText();
		QRTaskWorker<FileListSnapshot> worker = new QRTaskWorker<>(context -> {
			ArrayList<FileItem> items = new ArrayList<>();
			File parentFile = directory.getParentFile();
			if (parentFile != null) {
				items.add(FileItem.parent(parentFile, parentDirectoryIcon));
			}
			DirectoryReadResult readResult = listVisibleFiles(directory);
			if (readResult.success()) {
				File[] files = readResult.files();
				Arrays.sort(files, fileComparator(sort, ascending));
				for (File file : files) {
					context.checkCancelled();
					if (mode == SelectMode.DIRECTORY_ONLY) {
						if (file.isDirectory() ||
						    (!directoryVisibleFileExtensionSnapshot.isEmpty() &&
						     acceptExtension(file, directoryVisibleFileExtensionSnapshot))) {
							items.add(createFileItem(file));
						}
						continue;
					}
					if (file.isDirectory() || acceptExtension(file, extensionSnapshot)) {
						items.add(createFileItem(file));
					}
				}
			}
			return new FileListSnapshot(directory, items, fileTypeSnapshot, readResult.status(), readResult.error());
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
				if (result.status() == DirectoryReadStatus.SUCCESS) {
					statusLabel.setText(fileListModel.getSize() + " 项  " + result.fileTypeText());
				} else {
					statusLabel.setText(readStatusText(result.status(), result.error()) + "  " + result.fileTypeText());
				}
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
		return listVisibleFiles(directory).files();
	}

	private DirectoryReadResult listVisibleFiles(File directory) {
		if (directory == null || !directory.exists()) {
			return new DirectoryReadResult(new File[0], DirectoryReadStatus.NOT_EXISTS, null);
		}
		if (!directory.isDirectory()) {
			return new DirectoryReadResult(new File[0], DirectoryReadStatus.NOT_DIRECTORY, null);
		}
		if (!directory.canRead()) {
			return new DirectoryReadResult(new File[0], DirectoryReadStatus.ACCESS_DENIED, null);
		}
		try {
			File[] files = directory.listFiles(file -> file != null && !file.isHidden());
			if (files == null) {
				return new DirectoryReadResult(new File[0], DirectoryReadStatus.FAILED, null);
			}
			return new DirectoryReadResult(files, DirectoryReadStatus.SUCCESS, null);
		} catch (Exception e) {
			return new DirectoryReadResult(new File[0], DirectoryReadStatus.FAILED, e);
		}
	}

	private String readStatusText(DirectoryReadStatus status, Throwable error) {
		return switch (status) {
			case SUCCESS -> "读取完成";
			case NOT_EXISTS -> "目录不存在";
			case NOT_DIRECTORY -> "不是文件夹";
			case ACCESS_DENIED -> "无权限";
			case FAILED -> error == null || error.getMessage() == null || error.getMessage().isBlank()
					? "读取失败"
					: "读取失败：" + error.getMessage();
		};
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
		if (item.parent) {
			return;
		}
		if (selectMode == SelectMode.SAVE_FILE) {
			updateSelectedFile(item.file);
			if (approve && item.file.isFile()) {
				approveSelection();
			}
			return;
		}
		if (selectMode == SelectMode.DIRECTORY_ONLY && !item.parent && item.file.isFile()) {
			SwingUtilities.invokeLater(fileList::clearSelection);
			updateSelectedFile(currentDirectory);
			return;
		}
		if (canSelect(item.file)) {
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
			selectedPathField.setText(displayName(file));
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
			cancelTreeLoad(node);
			node.loaded = false;
			node.loading = false;
			node.loadWorker = null;
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

	private String normalizeExtension(String extension) {
		if (extension == null || extension.isBlank()) {
			return null;
		}
		String value = extension.startsWith(".") ? extension.substring(1) : extension;
		return value.isBlank() ? null : value.toLowerCase(Locale.ROOT);
	}

	private String fileTypeText() {
		if (extensions.isEmpty() || selectMode == SelectMode.DIRECTORY_ONLY) {
			return fileType;
		}
		return fileType + " (" + String.join(", ", extensions.stream().map(s -> "." + s).toList()) + ")";
	}

	private QRTaskWorker<ArrayList<FileTreeNodeData>> loadDirectoryNode(FileTreeNode node) {
		if (node.loaded || node.loading || node.file == null) {
			return node.loadWorker;
		}
		node.loading = true;
		node.loadErrorMessage = null;
		node.removeAllChildren();
		addPlaceHolderIfNeeded(node);
		treeModel.nodeStructureChanged(node);
		File directory = node.file;
		QRTaskWorker<ArrayList<FileTreeNodeData>> worker = new QRTaskWorker<>(context -> {
			DirectoryReadResult readResult = listVisibleFiles(directory);
			if (!readResult.success()) {
				throw new DirectoryReadException(readResult.status(), readResult.error());
			}
			File[] files = readResult.files();
			ArrayList<FileTreeNodeData> directories = new ArrayList<>();
			for (File file : files) {
				context.checkCancelled();
				if (file.isDirectory()) {
					directories.add(createTreeNodeData(file));
				}
			}
			directories.sort(Comparator.comparing(data -> data.displayName().toLowerCase(Locale.ROOT)));
			return directories;
		});
		node.loadWorker = worker;
		worker.addListener(new QRTaskListener<>() {
			@Override
			public void succeeded(ArrayList<FileTreeNodeData> directories) {
				if (node.loadWorker != worker) {
					return;
				}
				node.loadErrorMessage = null;
				node.removeAllChildren();
				for (FileTreeNodeData directory : directories) {
					FileTreeNode child = new FileTreeNode(directory);
					addPlaceHolderIfNeeded(child);
					node.add(child);
				}
				node.loaded = true;
				node.loading = false;
				node.loadWorker = null;
				treeModel.nodeStructureChanged(node);
			}

			@Override
			public void cancelled() {
				if (node.loadWorker != worker) {
					return;
				}
				node.loadErrorMessage = "已取消";
				resetUnloadedTreeNode(node);
			}

			@Override
			public void failed(Throwable throwable) {
				if (node.loadWorker != worker) {
					return;
				}
				node.loadErrorMessage = directoryReadFailureText(throwable);
				resetUnloadedTreeNode(node);
			}
		});
		worker.execute();
		return worker;
	}

	private void cancelTreeLoad(FileTreeNode node) {
		if (node.loadWorker != null && !node.loadWorker.isDone()) {
			node.loadWorker.cancel(true);
		}
		for (int i = 0; i < node.getChildCount(); i++) {
			Object child = node.getChildAt(i);
			if (child instanceof FileTreeNode childNode) {
				cancelTreeLoad(childNode);
			}
		}
	}

	private void resetUnloadedTreeNode(FileTreeNode node) {
		node.loaded = false;
		node.loading = false;
		node.loadWorker = null;
		node.removeAllChildren();
		addPlaceHolderIfNeeded(node);
		treeModel.nodeStructureChanged(node);
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
			selectCurrentDirectoryInTreeAsync();
			return;
		}
		selectTreeNode(node);
	}

	private void selectTreeNode(FileTreeNode node) {
		TreePath treePath = new TreePath(node.getPath());
		treeSelectionChanging = true;
		directoryTree.setSelectionPath(treePath);
		directoryTree.scrollPathToVisible(treePath);
		treeSelectionChanging = false;
	}

	private void selectCurrentDirectoryInTreeAsync() {
		File target = currentDirectory;
		if (target == null) {
			return;
		}
		int version = ++treePathSelectionVersion;
		File rootFile = rootOf(target);
		FileTreeNode node = findTreeNode(rootFile);
		if (node == null) {
			return;
		}

		ArrayList<File> directories = new ArrayList<>();
		File file = target;
		while (file != null && !sameFile(file, rootFile)) {
			directories.add(0, file);
			file = file.getParentFile();
		}

		expandPathStep(version, target, node, directories, 0);
	}

	private void expandPathStep(int version, File target, FileTreeNode node, ArrayList<File> directories, int index) {
		if (!isCurrentTreePathRequest(version, target)) {
			return;
		}
		if (index >= directories.size()) {
			selectTreeNode(node);
			return;
		}
		File nextDirectory = directories.get(index);
		QRTaskWorker<ArrayList<FileTreeNodeData>> worker = loadDirectoryNode(node);
		if (node.loaded) {
			expandLoadedPathNode(version, target, node, directories, index);
			return;
		}
		if (worker == null) {
			selectTreeNode(node);
			return;
		}
		worker.addListener(new QRTaskListener<>() {
			@Override
			public void succeeded(ArrayList<FileTreeNodeData> result) {
				if (!isCurrentTreePathRequest(version, target)) {
					return;
				}
				expandLoadedPathNode(version, target, node, directories, index);
			}

			@Override
			public void failed(Throwable throwable) {
				if (isCurrentTreePathRequest(version, target)) {
					selectTreeNode(node);
				}
			}

			@Override
			public void cancelled() {
				if (isCurrentTreePathRequest(version, target)) {
					selectTreeNode(node);
				}
			}
		});
	}

	private void expandLoadedPathNode(int version, File target, FileTreeNode node, ArrayList<File> directories, int index) {
		if (!isCurrentTreePathRequest(version, target)) {
			return;
		}
		TreePath path = new TreePath(node.getPath());
		directoryTree.expandPath(path);
		FileTreeNode child = findDirectChild(node, directories.get(index));
		if (child == null) {
			selectTreeNode(node);
			return;
		}
		expandPathStep(version, target, child, directories, index + 1);
	}

	private boolean isCurrentTreePathRequest(int version, File target) {
		return version == treePathSelectionVersion && sameFile(currentDirectory, target);
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
		if (file == null) {
			return "";
		}
		return displayNameCache.computeIfAbsent(fileCacheKey(file), key -> {
			try {
				String displayName = fileSystemView.getSystemDisplayName(file);
				return displayName == null || displayName.isBlank() ? fallbackFileName(file) : displayName;
			} catch (Exception e) {
				return fallbackFileName(file);
			}
		});
	}

	private Icon systemIcon(File file) {
		if (file == null) {
			return null;
		}
		return systemIconCache.computeIfAbsent(fileCacheKey(file), key -> {
			try {
				return fileSystemView.getSystemIcon(file);
			} catch (Exception e) {
				return file.isDirectory() ? UIManager.getIcon("FileView.directoryIcon") : UIManager.getIcon("FileView.fileIcon");
			}
		});
	}

	private String fallbackFileName(File file) {
		String name = file.getName();
		return name == null || name.isBlank() ? file.getAbsolutePath() : name;
	}

	private String directoryReadFailureText(Throwable throwable) {
		if (throwable instanceof DirectoryReadException e) {
			return readStatusText(e.status, e.error);
		}
		return throwable == null || throwable.getMessage() == null || throwable.getMessage().isBlank()
				? "读取失败"
				: "读取失败：" + throwable.getMessage();
	}

	private String fileCacheKey(File file) {
		return file.getAbsolutePath();
	}

	private FileItem createFileItem(File file) {
		return new FileItem(file, false, displayName(file), systemIcon(file));
	}

	private FileTreeNodeData createTreeNodeData(File file) {
		return new FileTreeNodeData(file, displayName(file), systemIcon(file));
	}

	/**
	 * 显示对话框并返回本次是否成功选择。
	 *
	 * <p>该方法等价于先调用 {@link #setVisible(boolean)}，再调用 {@link #selectedSucceeded()}。</p>
	 *
	 * @return 用户点击确定且当前选择符合模式要求时返回 true
	 */
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

	@Override
	public void dispose() {
		if (fileListWorker != null && !fileListWorker.isDone()) {
			fileListWorker.cancel(true);
		}
		if (rootLoadWorker != null && !rootLoadWorker.isDone()) {
			rootLoadWorker.cancel(true);
		}
		for (int i = 0; i < treeRoot.getChildCount(); i++) {
			Object child = treeRoot.getChildAt(i);
			if (child instanceof FileTreeNode childNode) {
				cancelTreeLoad(childNode);
			}
		}
		super.dispose();
	}

	/**
	 * 返回本次选择是否成功。
	 *
	 * <p>只有用户点击“确定”并且当前选择符合 {@link SelectMode} 时才返回 true。
	 * 取消、关闭窗口或选择了不符合模式的路径都会返回 false。</p>
	 *
	 * @return 是否成功选择
	 */
	public boolean selectedSucceeded() {
		if (selectMode == SelectMode.SAVE_FILE) {
			return approved && canSelectSaveTarget(selectedFile);
		}
		return approved && canSelect(selectedFile);
	}

	/**
	 * 返回用户选择的文件或目录。
	 *
	 * <p>调用方应先判断 {@link #selectedSucceeded()}。在 {@link SelectMode#SAVE_FILE}
	 * 模式下，该路径表示保存目标，文件可以尚不存在。</p>
	 *
	 * @return 选择结果，未选择时为 null
	 */
	public File selectedFile() {
		return selectedFile;
	}

	/**
	 * 返回用户选择路径的绝对路径字符串。
	 *
	 * @return 选择结果路径，未选择时为 null
	 */
	public String selectedFilePath() {
		return selectedFile == null ? null : selectedFile.getAbsolutePath();
	}

	/**
	 * 预设当前选择。
	 *
	 * <p>传入目录时会切换当前目录；传入文件时会切换到文件所在目录，并在底部选择框中显示该文件。</p>
	 *
	 * @param selectedFile 要预设的文件或目录，可为 null
	 */
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

	/**
	 * 通过路径字符串预设当前选择。
	 *
	 * @param selectedFilePath 文件或目录路径；无效路径会被忽略
	 */
	public void setSelectedFilePath(String selectedFilePath) {
		File file = resolvePath(selectedFilePath);
		if (file != null) {
			setSelectedFile(file);
		}
	}

	/**
	 * 动态切换选择模式。
	 *
	 * <p>切换后会更新窗口标题、刷新当前目录列表，并按新模式重置当前选择。</p>
	 *
	 * @param selectMode 选择模式，null 时按 {@link SelectMode#FILE_ONLY}
	 */
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

	/**
	 * 设置可选择的文件扩展名。
	 *
	 * <p>扩展名可带或不带英文句点，内部统一转换为小写且带点格式。
	 * 传入空数组或 null 表示清空过滤条件，显示全部文件。</p>
	 *
	 * @param extension 扩展名列表，如 {@code "pdf"}、{@code ".png"}
	 */
	public void setExtensions(String... extension) {
		extensions.clear();
		if (extension == null) {
			return;
		}
		for (String ext : extension) {
			String value = normalizeExtension(ext);
			if (value != null) {
				extensions.add(value);
			}
		}
		if (currentDirectory != null) {
			fillFileList(currentDirectory);
		} else {
			statusLabel.setText(fileTypeText());
		}
	}

	/**
	 * 在 {@link SelectMode#DIRECTORY_ONLY} 选择文件夹模式下，额外显示指定扩展名的文件。
	 * <p>这些文件仅用于提示当前文件夹下存在对应类型文件，不会成为可选结果；确认选择时仍只能返回文件夹。
	 *
	 * @param extension 要显示的文件扩展名，可带或不带英文句点，如 {@code "txt"} 或 {@code ".txt"}
	 */
	public void addDirectoryVisibleFileExtensions(String... extension) {
		if (extension == null) {
			return;
		}
		for (String ext : extension) {
			String value = normalizeExtension(ext);
			if (value != null) {
				directoryVisibleFileExtensions.add(value);
			}
		}
		if (currentDirectory != null) {
			fillFileList(currentDirectory);
		}
	}

	/**
	 * 设置状态栏展示的文件类型名称。
	 *
	 * @param fileType 文件类型名称，空白时显示“文件”
	 */
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
			if (value instanceof FileTreeNode node) {
				setIcon(node.icon);
				setText(node.displayName);
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
				setText(item.displayName);
				setIcon(item.icon);
//                setToolTipText(item.file.getAbsolutePath());
			}
			return this;
		}
	}

	private static class FileTreeNode extends DefaultMutableTreeNode {
		private final File file;
		private final boolean placeHolder;
		private final String displayName;
		private final Icon icon;
		private boolean loaded = false;
		private boolean loading = false;
		private String loadErrorMessage;
		private QRTaskWorker<ArrayList<FileTreeNodeData>> loadWorker;

		private FileTreeNode(FileTreeNodeData data) {
			this.file = data.file();
			this.placeHolder = false;
			this.displayName = data.displayName();
			this.icon = data.icon();
		}

		private FileTreeNode() {
			this.file = null;
			this.placeHolder = true;
			this.displayName = "加载中...";
			this.icon = null;
		}

		private static FileTreeNode placeHolder() {
			return new FileTreeNode();
		}
	}

	private record FileItem(File file, boolean parent, String displayName, Icon icon) {

		private static FileItem parent(File file, Icon icon) {
			return new FileItem(file, true, "..", icon);
		}
	}

	private record FileListSnapshot(File directory, ArrayList<FileItem> items, String fileTypeText,
	                                DirectoryReadStatus status, Throwable error) {
	}

	private record FileTreeNodeData(File file, String displayName, Icon icon) {
	}

	private record DirectoryReadResult(File[] files, DirectoryReadStatus status, Throwable error) {
		private boolean success() {
			return status == DirectoryReadStatus.SUCCESS;
		}
	}

	private static class DirectoryReadException extends RuntimeException {
		private final DirectoryReadStatus status;
		private final Throwable error;

		private DirectoryReadException(DirectoryReadStatus status, Throwable error) {
			super(error);
			this.status = status;
			this.error = error;
		}
	}
}