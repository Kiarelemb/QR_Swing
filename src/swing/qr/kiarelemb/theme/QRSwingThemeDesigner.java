package swing.qr.kiarelemb.theme;

import method.qr.kiarelemb.utils.*;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.adapter.QRButtonMouseListener;
import swing.qr.kiarelemb.utils.QRComponentUtils;
import swing.qr.kiarelemb.assembly.QRScrollBarUI;
import swing.qr.kiarelemb.basic.*;
import swing.qr.kiarelemb.combination.QRBorderContentPanel;
import swing.qr.kiarelemb.combination.QRMenuButton;
import swing.qr.kiarelemb.combination.QRMenuPanel;
import swing.qr.kiarelemb.event.QRItemEvent;
import swing.qr.kiarelemb.utils.QRRGBColorPane;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.window.basic.QRDialog;
import swing.qr.kiarelemb.window.basic.QRFrame;
import swing.qr.kiarelemb.window.enhance.QROpinionDialog;
import swing.qr.kiarelemb.window.enhance.QRSmallTipShow;
import swing.qr.kiarelemb.window.utils.QRResizableTextShowDialog;
import swing.qr.kiarelemb.window.utils.QRValueInputDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 颜色和字体库
 * @create 2024-03-13 11:47
 */
public class QRSwingThemeDesigner extends QRDialog {
    private final QRMenuPanel menuPanel;
    private final QRTextPane previewTextPane;
    private final QRBorderContentPanel previewFramePanel;
    private final QRRGBColorPane[] panes;
    private final QRComboBox topicCB;
    private final QRPanel statisticsPanel;
    private boolean currentThemeChanged = false;

    /**
     * 主题设计器，大小和位置已设置。<p>使用方法：</p>
     * <pre><code>
     * QRSwingThemeDesigner designer = new QRSwingThemeDesigner(MainWindow.INSTANCE);
     * designer.setVisible(true);
     * </code></pre>
     *
     * @param parent
     */
    public QRSwingThemeDesigner(QRFrame parent) {
        super(parent);
        setTitle("主题设计器");
        setTitlePlace(QRDialog.CENTER);
        setParentWindowNotFollowMove();
        final int width = 1145;
        final int height = 680;
        setSize(width, height);
        setLocationRelativeTo(parent);

        topicCB = new QRComboBox(getThemes());
        QRRoundButton topicInput = new QRRoundButton("导入主题");
        QRColorsAndFonts.loadTheme();
        final String topic = QRColorsAndFonts.topicEnglishToChinese(QRSwing.theme);
        topicCB.setText(topic);

        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, new QRLabel("基于:"), 21, 29, 47, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, topicCB, 71, 29, 150, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, topicInput, 230, 29, 100, 30);

        topicInput.addClickAction(e -> {
            final File file = QRFileUtils.fileSelect(QRSwingThemeDesigner.this, "主题文件", "xq");
            if (file != null) {
                if (!QRColorsAndFonts.isThemeFile(file)) {
                    QROpinionDialog.messageErrShow(QRSwingThemeDesigner.this, "您选中的文件不是主题文件！");
                    return;
                }
                try {
                    QRFileUtils.fileCopy(file, QRSwing.THEME_DIRECTORY);
                    QRSmallTipShow.display(QRSwingThemeDesigner.this, "导入成功！");
                    topicCB.setModel(new DefaultComboBoxModel<>(getThemes()));
                    topicCB.setText(QRColorsAndFonts.getThemeFileName(file));
                } catch (IOException ex) {
                    QROpinionDialog.messageErrShow(QRSwingThemeDesigner.this, "导入失败！");
                    ex.printStackTrace();
                }
            }
        });

        //region label
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, new QRLabel("跟打框文字前景色:"), 735, 29, 147, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, new QRLabel("跟打框面板背景色:"), 735, 69, 147, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, new QRLabel("窗体背景色:"), 735, 229, 147, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, new QRLabel("窗体边框色:"), 735, 269, 147, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, new QRLabel("打对字前景色:"), 735, 149, 147, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, new QRLabel("打对字背景色:"), 735, 189, 147, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, new QRLabel("高级随机字背景色:"), 735, 109, 147, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, new QRLabel("按钮进入填充色:"), 735, 309, 147, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, new QRLabel("按钮按下填充色:"), 735, 349, 147, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, new QRLabel("滚动条填充色:"), 735, 389, 147, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, new QRLabel("菜单文字前景色:"), 735, 429, 147, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, new QRLabel("菜单分隔线颜色:"), 735, 469, 147, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, new QRLabel("文本区光标颜色:"), 735, 509, 147, 30);
        //endregion

        //region show label
        QRLabel showFrameColorBackLabel = new QRLabel();
        QRLabel showTextColorForeLabel = new QRLabel();
        QRLabel showTextColorBackLabel = new QRLabel();
        QRLabel showFrameColorBorderLabel = new QRLabel();
        QRLabel showCorrectColorForeLabel = new QRLabel();
        QRLabel showCorrectColorBackLabel = new QRLabel();
        QRLabel showSeniorCorrectColorForeLabel = new QRLabel();
        QRLabel showButtonEnterColorLabel = new QRLabel();
        QRLabel showButtonPressColorLabel = new QRLabel();
        QRLabel showScrollColorLabel = new QRLabel();
        QRLabel showMenuTextColorForeLabel = new QRLabel();
        QRLabel showMenuLineColorLabel = new QRLabel();
        QRLabel showCaretColorLabel = new QRLabel();

        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, showFrameColorBackLabel, 685, 229, 30, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, showTextColorForeLabel, 685, 29, 30, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, showTextColorBackLabel, 685, 69, 30, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, showFrameColorBorderLabel, 685, 269, 30, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, showCorrectColorForeLabel, 685, 149, 30, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, showCorrectColorBackLabel, 685, 189, 30, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, showSeniorCorrectColorForeLabel, 685, 109, 30, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, showButtonEnterColorLabel, 685, 309, 30, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, showButtonPressColorLabel, 685, 349, 30, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, showScrollColorLabel, 685, 389, 30, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, showMenuTextColorForeLabel, 685, 429, 30, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, showMenuLineColorLabel, 685, 469, 30, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, showCaretColorLabel, 685, 509, 30, 30);

        //endregion

        //region  color value
        QRActionRegister colorChangedAction = e -> update();
        QRRGBColorPane textColorForeColorValue = new QRRGBColorPane(QRColorsAndFonts.TEXT_COLOR_FORE, showTextColorForeLabel, colorChangedAction);
        QRRGBColorPane textColorBackColorValue = new QRRGBColorPane(QRColorsAndFonts.TEXT_COLOR_BACK, showTextColorBackLabel, colorChangedAction);
        QRRGBColorPane correctColorForeColorValue = new QRRGBColorPane(QRColorsAndFonts.CORRECT_COLOR_FORE, showCorrectColorForeLabel, colorChangedAction);
        QRRGBColorPane correctColorBackColorValue = new QRRGBColorPane(QRColorsAndFonts.CORRECT_COLOR_BACK, showCorrectColorBackLabel, colorChangedAction);
        QRRGBColorPane seniorCorrectColorForeColorValue = new QRRGBColorPane(QRColorsAndFonts.SENIOR_RANDOM_COLOR_BACK, showSeniorCorrectColorForeLabel, colorChangedAction);
        QRRGBColorPane frameColorBackColorValue = new QRRGBColorPane(QRColorsAndFonts.FRAME_COLOR_BACK, showFrameColorBackLabel, colorChangedAction);
        QRRGBColorPane frameColorBorderColorValue = new QRRGBColorPane(QRColorsAndFonts.BORDER_COLOR, showFrameColorBorderLabel, colorChangedAction);
        QRRGBColorPane ButtonEnterColorColorValue = new QRRGBColorPane(QRColorsAndFonts.ENTER_COLOR, showButtonEnterColorLabel, colorChangedAction);
        QRRGBColorPane ButtonPressColorColorValue = new QRRGBColorPane(QRColorsAndFonts.PRESS_COLOR, showButtonPressColorLabel, colorChangedAction);
        QRRGBColorPane ScrollColorColorValue = new QRRGBColorPane(QRColorsAndFonts.SCROLL_COLOR, showScrollColorLabel, colorChangedAction);
        QRRGBColorPane menuLineColorColorValue = new QRRGBColorPane(QRColorsAndFonts.LINE_COLOR, showMenuLineColorLabel, colorChangedAction);
        QRRGBColorPane menuTextColorForeColorValue = new QRRGBColorPane(QRColorsAndFonts.MENU_COLOR, showMenuTextColorForeLabel, colorChangedAction);
        QRRGBColorPane caretColorColorValue = new QRRGBColorPane(QRColorsAndFonts.CARET_COLOR, showCaretColorLabel, colorChangedAction);

        panes = new QRRGBColorPane[]{textColorForeColorValue, textColorBackColorValue, correctColorForeColorValue, correctColorBackColorValue,
                seniorCorrectColorForeColorValue, frameColorBackColorValue, frameColorBorderColorValue,
                ButtonEnterColorColorValue, ButtonPressColorColorValue, menuLineColorColorValue, ScrollColorColorValue, menuTextColorForeColorValue,
                caretColorColorValue};

        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, textColorForeColorValue, 884, 29, 200, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, textColorBackColorValue, 884, 69, 200, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, seniorCorrectColorForeColorValue, 884, 109, 200, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, correctColorForeColorValue, 884, 149, 200, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, correctColorBackColorValue, 884, 189, 200, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, frameColorBackColorValue, 884, 229, 200, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, frameColorBorderColorValue, 884, 269, 200, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, ButtonEnterColorColorValue, 884, 309, 200, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, ButtonPressColorColorValue, 884, 349, 200, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, ScrollColorColorValue, 884, 389, 200, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, menuTextColorForeColorValue, 884, 429, 200, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, menuLineColorColorValue, 884, 469, 200, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, caretColorColorValue, 884, 509, 200, 30);
        //endregion

        previewFramePanel = new QRBorderContentPanel() {
            private final LinkedList<QRPanel> panels = new LinkedList<>();

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                final int arc = 18;
                final Color color = frameColorBorderColorValue.getColor();
//                System.out.println("border - " + color);
                g2.setColor(color);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            }

            @Override
            public void add(Component comp, Object constraints) {
                if (comp instanceof QRPanel) {
                    panels.add((QRPanel) comp);
                }
                super.add(comp, constraints);
            }

            @Override
            public void componentFresh() {
                if (panels != null) {
                    final Color color = frameColorBackColorValue.getColor();
                    for (QRPanel panel : panels) {
                        panelLoop(panel, color);
                    }
                    setBackground(color);
                }
                repaint();
            }

            private void panelLoop(QRPanel q, Color color) {
                final Component[] components = q.getComponents();
                for (Component component : components) {
                    if (component instanceof QRPanel) {
                        panelLoop((QRPanel) component, color);
                    }
                }
                q.setBackground(color);
            }
        };
        previewFramePanel.setBounds(21, 84, 628, 455);
        previewFramePanel.setLayout(new BorderLayout());
        mainPanel.add(previewFramePanel);

        QRPanel previewTopPanel = new QRPanel();
        previewTopPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, QRColorsAndFonts.LINE_COLOR));
        previewFramePanel.add(previewTopPanel, BorderLayout.NORTH);
        previewTopPanel.setLayout(new BorderLayout());

        QRButton closeButton = new QRButton("  ╳  ");
        closeButton.setFont(QRColorsAndFonts.PROCESS_BUTTON_FONT.deriveFont(11f).deriveFont(Font.BOLD));
        closeButton.disableListener();
        QRButtonMouseListener closeButtonMouseListener = new QRButtonMouseListener(closeButton);
        closeButton.addMouseListener(closeButtonMouseListener);
        previewTopPanel.add(closeButton, BorderLayout.EAST);

        QRLabel titleLabel = new QRLabel(parent.getTitle());
        titleLabel.setHorizontalAlignment(parent.getTitlePlace());
        previewTopPanel.add(titleLabel, BorderLayout.CENTER);

        QRLabel iconLabel = QRLabel.getIconLabel();
        previewTopPanel.add(iconLabel, BorderLayout.WEST);

        QRPanel previewPanel = new QRPanel();
        previewPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        previewFramePanel.add(previewPanel, BorderLayout.CENTER);
        previewPanel.setLayout(new BorderLayout());

        QRScrollPane previewContentScrollPane = new QRScrollPane();

        //region TextPane的实例化与重写
        previewTextPane = new QRTextPane() {
            @Override
            public void componentFresh() {
                setText("");
                caret.setCaretColor(caretColorColorValue.getColor());
                final JScrollBar verticalScrollBar = previewContentScrollPane.getVerticalScrollBar();
                verticalScrollBar.setUI(new QRScrollBarUI(false) {
                    @Override
                    public Dimension getPreferredSize(JComponent c) {
                        c.setPreferredSize(new Dimension(12, 0));
                        return super.getPreferredSize(c);
                    }

                    @Override
                    protected void configureScrollBarColors() {
                        thumbColor = ScrollColorColorValue.getColor();
                        thumbDarkShadowColor = thumbColor;
                        trackColor = textColorBackColorValue.getColor();
                        setThumbBounds(0, 0, 3, 10);
                    }
                });
                String text = "在工作或学习中遇到不开心的时候，不妨静下来好好想想，自己到底是对是错。生活中不是你对别人好，别人就该对你好，你要明白这个道理，每个人都有自己的原则，有人功利，有人善良，你不可能要求别人什么。有时间的话，不妨到处走走，在雄伟的高山之间，" +
                        "放声大喊，一吐心中的阴郁，在浪漫的大海之间，看潮起潮落，感悟人生的起伏跌宕，在落日余晖中感受天地的宁静，洗涤心中的贪念。";
                final Color textColorBack = textColorBackColorValue.getColor();
                previewContentScrollPane.setBackground(frameColorBackColorValue.getColor());
                previewContentScrollPane.setBorder(BorderFactory.createLineBorder(frameColorBackColorValue.getColor(), 5));
                print(text, textColorForeColorValue.getColor(), textColorBack);
                setBackground(textColorBack);
                setCaretPosition(0);
            }

            @Override
            protected void keyPress(KeyEvent e) {
                e.consume();
            }

            @Override
            protected void keyType(KeyEvent e) {
                e.consume();
            }
        };
        MutableAttributeSet paragraph = new SimpleAttributeSet();
        StyleConstants.setLineSpacing(paragraph, 0.6f);
        previewTextPane.addKeyListener();
        previewTextPane.setParagraphAttributes(paragraph, false);
        previewContentScrollPane.setViewportView(previewTextPane);
        //endregion

        //region menu

        //region 数据面板
        QRPanel previewContentPanel = new QRPanel();
        previewContentPanel.setLayout(new BorderLayout());
//        previewContentPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
        QRButton btnSpeed = new QRButton("速度");
        QRButton btnKeystroke = new QRButton("击键");
        QRButton btnCodeLength = new QRButton("码长");
        QRButton btnStandardCodeLength = new QRButton("标顶");
        QRButton btnTime = new QRButton("时间");
        statisticsPanel = new QRPanel() {
            @Override
            public void componentFresh() {
                final Component[] components = getComponents();
                for (Component component : components) {
                    if (component instanceof QRButton button) {
                        button.setEnterColor(ButtonEnterColorColorValue.getColor());
                        button.setPressColor(ButtonPressColorColorValue.getColor());
                        button.setBackColor(frameColorBackColorValue.getColor());
                        button.setForeground(menuTextColorForeColorValue.getColor());
                        button.setBackground(frameColorBackColorValue.getColor());
                    }
                }
            }
        };
        statisticsPanel.setLayout(new GridLayout(1, 0, 5, 8));
        statisticsPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        statisticsPanel.add(btnSpeed);
        statisticsPanel.add(btnKeystroke);
        statisticsPanel.add(btnCodeLength);
        statisticsPanel.add(btnStandardCodeLength);
        statisticsPanel.add(btnTime);
        previewContentPanel.add(statisticsPanel, BorderLayout.NORTH);
        previewContentPanel.add(previewContentScrollPane, BorderLayout.CENTER);
        //endregion

        //region menuPanel实例化也重写
        menuPanel = new QRMenuPanel() {
            @Override
            public void componentFresh() {
                //把这个也一并改了
                titleLabel.setForeground(menuTextColorForeColorValue.getColor());
                //这个也改了
                previewTopPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, menuLineColorColorValue.getColor()));
                Color frameColorBackColorValueColor = frameColorBackColorValue.getColor();
                closeButton.setForeground(menuTextColorForeColorValue.getColor());
                closeButtonMouseListener.setBackColor(frameColorBackColorValueColor);
                closeButton.setBackground(frameColorBackColorValueColor);
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, menuLineColorColorValue.getColor()));
                for (AbstractButton button : buttons) {
                    ((QRMenuButton) button).setEnterColor(ButtonEnterColorColorValue.getColor());
                    ((QRMenuButton) button).setPressColor(ButtonPressColorColorValue.getColor());
                    ((QRMenuButton) button).setBackColor(frameColorBackColorValueColor);
                    button.setForeground(menuTextColorForeColorValue.getColor());
                    button.setBackground(frameColorBackColorValueColor);
                }
            }
        };
        //endregion

        menuPanel.add("菜单");
        menuPanel.add("比赛");
        menuPanel.add("窗口");
        menuPanel.add("模式");
        menuPanel.add("工具");
        menuPanel.add("我的");
        //endregion

        previewPanel.add(menuPanel, BorderLayout.NORTH);
        previewPanel.add(previewContentPanel, BorderLayout.CENTER);

        //region function components
        QRRoundButton designIntroductionButton = new QRRoundButton("使用说明");
        QRRoundButton changeAndSaveButton = new QRRoundButton("修改并保存");
        QRRoundButton newOneAndSaveButton = new QRRoundButton("新建并保存");
        QRRoundButton abandonAndExitButton = new QRRoundButton("放弃并退出");
        QRSlider transparencySlider = new QRSlider();
        QRRoundButton randomColors = new QRRoundButton("随机");

        designIntroductionButton.addClickAction(e -> {
            String content = """
                    1. 单击右侧的颜色方块或修改色值文本框都可以改变对应的颜色；

                    2. 内置的默认主题无法修改，但可以在此基础上新建一个，在编辑好主题后，单击“新建并保存”即可；

                    3. 导入的主题在小启软件根目录的theme文件夹中，仅当设计器中的主题是选择的导入的主题时，才可以点击“修改并保存”；

                    4. 色值文本框选择一个，并ctrl c后复制的是整个颜色值到剪贴板，可以在其它任意一个文本框内直接粘贴；

                    5. 色值文本框支持两种形式的粘贴，一种是以#开头的6位16进制色值，一种是以任意长度任意非数字隔开的3个色值，例如：“#BBBBBB”、“77 77 77”、“77,77, 77”、“77c77b77”；

                    6. 主题的选择请在“设置” -> “跟打设置” -> “字体颜色设置”处修改。""";
            QRResizableTextShowDialog textShowDialog = new QRResizableTextShowDialog(this, width / 5 * 3, height / 5 * 3, "设计器使用说明", content, true);
            QRSystemUtils.setWindowNotTrans(textShowDialog);
            textShowDialog.setVisible(true);
        });

        changeAndSaveButton.addClickAction(e -> {
            final String topicName = topicCB.getText();
            saveToFile(topicName);
        });

        newOneAndSaveButton.addClickAction(e -> {
            final QRValueInputDialog fileName = new QRValueInputDialog(QRSwingThemeDesigner.this, "不包含扩展名的文件名（其文件名不能为dark、light、pink、brown、gray）", "请输入文件名（不包含扩展名）") {
                @Override
                public boolean meetCondition() {
                    final String text = textField.getText();
                    final String notAllowed = "*?<>|\\/:\"";
                    final char[] chars = text.toCharArray();
                    for (char c : chars) {
                        if (notAllowed.contains(String.valueOf(c))) {
                            QROpinionDialog.messageErrShow(this, "文件名中不能有*、?、小于号、大于号、|、\"、:、/、\\字符！");
                            return false;
                        }
                    }
                    return super.meetCondition();
                }
            };
            fileName.setVisible(true);
            saveToFile(fileName.getAnswer());
        });

        topicCB.addItemChangeListener(e -> {
            QRItemEvent event = (QRItemEvent) e;
            final String text = event.after();
            topicChangedAction(text);
            //不相等就说明选择的是内置的主题
            changeAndSaveButton.setEnabled(!"自定义".equals(text));
        });

        transparencySlider.addChangeListener(e -> {
            final int value = transparencySlider.getValue();
            QRSystemUtils.setWindowTrans(QRSwingThemeDesigner.this, 0.9999f - value / 100f);
        });
        abandonAndExitButton.addClickAction(e -> dispose());

        transparencySlider.setMaximum(50);
        transparencySlider.setValue(0);
        randomColors.addClickAction(e -> {
            setCursorWait();
            for (QRRGBColorPane pane : panes) {
                pane.setColor(QRRandomUtils.getRandomColor());
            }
            update();
            setCursorDefault();
        });

        changeAndSaveButton.setEnabled(QRSwing.theme.equals(topic));
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, designIntroductionButton, 21, 578, 98, 35);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, changeAndSaveButton, 284, 578, 160, 35);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, newOneAndSaveButton, 484, 578, 160, 35);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, abandonAndExitButton, 684, 578, 160, 35);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, transparencySlider, 900, 578, 90, 35);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, randomColors, 1000, 578, 80, 35);
        //endregion

        addWindowListener();
        QRSystemUtils.setWindowNotTrans(this);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        topicChangedAction(QRSwing.theme);
    }

    private void topicChangedAction(String result) {
        final Color[] colors = QRColorsAndFonts.getThemeColors(result);
        if (colors != null) {
            for (int i = 0; i < panes.length; i++) {
                panes[i].setColor(colors[i]);
            }
            update();
        }
    }

    public void update() {
        menuPanel.componentFresh();
        previewTextPane.componentFresh();
        previewFramePanel.componentFresh();
        statisticsPanel.componentFresh();
    }

    private void saveToFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }
        String filePath = QRSwing.THEME_DIRECTORY + fileName + ".th.xq";
        if (!QRFileUtils.fileCreate(filePath)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < panes.length; i++) {
            String rgbValue = QRRGBColorPane.getColor(panes[i].getColor());
            String lineText = QRColorsAndFonts.COLOR_ATTRIBUTES[i] + "=" + rgbValue;
            sb.append(QRCodePack.encrypt(lineText, "theme")).append("\n");
        }
        QRFileUtils.fileWriterWithUTF8(filePath, sb.toString());
        QROpinionDialog.messageTellShow(this, "保存成功！");
        topicCB.setModel(new DefaultComboBoxModel<>(getThemes()));
        topicCB.setText(fileName);
        if (fileName.equals(QRColorsAndFonts.topicEnglishToChinese(QRSwing.theme))) {
            currentThemeChanged = true;
        }
    }

    private String[] getThemes() {
        QRColorsAndFonts.loadTheme();
        ArrayList<String> themes = new ArrayList<>(Arrays.asList(QRColorsAndFonts.THEME_CLASS));
        themes.add("自定义");
        return themes.toArray(QRStringUtils.ARR_EMPTY);
    }

    public boolean currentThemeChanged() {
        return currentThemeChanged;
    }
}