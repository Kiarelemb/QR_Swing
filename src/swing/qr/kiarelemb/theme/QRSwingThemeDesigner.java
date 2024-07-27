package swing.qr.kiarelemb.theme;

import method.qr.kiarelemb.utils.*;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.assembly.QRScrollBarUI;
import swing.qr.kiarelemb.basic.*;
import swing.qr.kiarelemb.combination.QRBorderContentPanel;
import swing.qr.kiarelemb.combination.QRMenuPanel;
import swing.qr.kiarelemb.event.QRColorChangedEvent;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.utils.QRComponentUtils;
import swing.qr.kiarelemb.utils.QRRGBColorPane;
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
import java.awt.event.ActionEvent;
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
    QRMenuPanel menuPanel;
    private final QRComboBox topicCB;
    private boolean currentThemeChanged = false;

    QRBorderContentPanel previewFramePanel;
    QRPanel statisticsPanel;
    QRTextPane previewTextPane;
    QRRGBColorPane textColorForeColorValue;
    QRRGBColorPane textColorBackColorValue;
    QRRGBColorPane correctColorForeColorValue;
    QRRGBColorPane correctColorBackColorValue;
    QRRGBColorPane seniorCorrectColorForeColorValue;
    QRRGBColorPane frameColorBackColorValue;
    QRRGBColorPane frameColorBorderColorValue;
    QRRGBColorPane ButtonEnterColorColorValue;
    QRRGBColorPane ButtonPressColorColorValue;
    QRRGBColorPane ScrollColorColorValue;
    QRRGBColorPane menuLineColorColorValue;
    QRRGBColorPane menuTextColorForeColorValue;
    QRRGBColorPane caretColorColorValue;
    QRRGBColorPane[] panes;

    QRLabel showFrameColorBackLabel;
    QRLabel showTextColorForeLabel;
    QRLabel showTextColorBackLabel;
    QRLabel showFrameColorBorderLabel;
    QRLabel showCorrectColorForeLabel;
    QRLabel showCorrectColorBackLabel;
    QRLabel showSeniorCorrectColorForeLabel;
    QRLabel showButtonEnterColorLabel;
    QRLabel showButtonPressColorLabel;
    QRLabel showScrollColorLabel;
    QRLabel showMenuTextColorForeLabel;
    QRLabel showMenuLineColorLabel;
    QRLabel showCaretColorLabel;

    QRPanel previewTopPanel;
    QRButton closeButton;
    QRLabel titleLabel;

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
        setSize(1145, 680);
        setLocationRelativeTo(parent);

        topicCB = new QRComboBox(getThemes());
        QRColorsAndFonts.loadTheme();
        final String topic = QRColorsAndFonts.topicEnglishToChinese(QRSwing.theme);
        topicCB.setText(topic);

        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, new QRLabel("基于:"), 21, 29, 47, 30);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, topicCB, 71, 29, 150, 30);

        init_topicInputButton();

        //region label
        init_labels();
        //endregion

        //region show label
        init_colorLabel();

        //endregion

        //region  color value
        init_colorValue();
        //endregion

        init_contentPanel();

        init_previewTopPanel(parent);

        QRPanel previewPanel = new QRPanel();
        previewPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        previewFramePanel.add(previewPanel, BorderLayout.CENTER);
        previewPanel.setLayout(new BorderLayout());


        //region menu
        init_statisticsPanel();
        init_textPane();

        QRPanel previewContentPanel = new QRPanel(new BorderLayout());
        previewContentPanel.add(statisticsPanel, BorderLayout.NORTH);
        previewContentPanel.add(previewTextPane.addScrollPane(), BorderLayout.CENTER);

        init_menuPanel(titleLabel, previewTopPanel, closeButton);
        //endregion

        previewPanel.add(menuPanel, BorderLayout.NORTH);
        previewPanel.add(previewContentPanel, BorderLayout.CENTER);

        //region function components
        var changeAndSaveButton = new QRRoundButton("修改并保存") {
            @Override
            protected void actionEvent(ActionEvent o) {
                final String topicName = topicCB.getText();
                saveToFile(topicName);
            }
        };
        init_newOneAndSaveButton();
        init_abandonAndExitButton();
        init_designIntroductionButton();


        topicCB.addItemChangeListener(e -> {
            final String text = e.after();
            topicChangedAction(text);
            //不相等就说明选择的是内置的主题
            changeAndSaveButton.setEnabled(!"自定义".equals(text));
        });

        init_transparentSlider();
        init_randomColorsButton();

        changeAndSaveButton.setEnabled(QRSwing.theme.equals(topic));
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, previewFramePanel, 21, 84, 628, 455);
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, changeAndSaveButton, 284, 578, 160, 35);
        //endregion

        QRComponentUtils.componentLoopToSetOpaque(previewFramePanel, true);
        addWindowListener();
        QRSystemUtils.setWindowNotTrans(this);
    }

    private void init_topicInputButton() {
        QRRoundButton topicInput = new QRRoundButton("导入主题");
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, topicInput, 230, 29, 100, 30);
        topicInput.addClickAction(e -> {
            final File file = QRFileUtils.fileSelect(QRSwingThemeDesigner.this, "主题文件", "xq");
            if (file == null) {
                return;
            }
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
        });
    }

    private void init_abandonAndExitButton() {
        var abandonAndExitButton = new QRRoundButton("放弃并退出");
        abandonAndExitButton.addClickAction(e -> dispose());
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, abandonAndExitButton, 684, 578, 160, 35);
    }

    private void init_randomColorsButton() {
        var randomColorsButton = new QRRoundButton("随机");
        randomColorsButton.addClickAction(e -> {
            setCursorWait();
            var colorList = new ArrayList<Color>(panes.length);
            for (QRRGBColorPane pane : panes) {
                var randomColor = QRRandomUtils.getRandomColor();
                colorList.add(randomColor);
                pane.setColor(randomColor);
            }
            QRColorsAndFonts.loadColors(colorList.toArray(new Color[0]));
            update();
            setCursorDefault();
        });
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, randomColorsButton, 1000, 578, 80, 35);
    }

    private void init_transparentSlider() {
        if (QRSystemUtils.IS_WINDOWS) {
            var transparencySlider = new QRSlider();
            transparencySlider.addChangeListener(e -> {
                final int value = transparencySlider.getValue();
                QRSystemUtils.setWindowTrans(QRSwingThemeDesigner.this, 0.9999f - value / 100f);
            });
            transparencySlider.setMaximum(50);
            transparencySlider.setValue(0);
            QRComponentUtils.setBoundsAndAddToComponent(mainPanel, transparencySlider, 900, 578, 90, 35);
        }
    }

    private void init_previewTopPanel(QRFrame parent) {
        previewTopPanel = new QRPanel();
        previewTopPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, QRColorsAndFonts.LINE_COLOR));
        previewFramePanel.add(previewTopPanel, BorderLayout.NORTH);
        previewTopPanel.setLayout(new BorderLayout());

        closeButton = new QRButton("  ╳  ") {
            @Override
            public void componentFresh() {
                super.componentFresh();
                setFont(QRColorsAndFonts.PROCESS_BUTTON_FONT.deriveFont(11f).deriveFont(Font.BOLD));
            }
        };
        previewTopPanel.add(closeButton, BorderLayout.EAST);

        titleLabel = new QRLabel(parent.getTitle());
        titleLabel.setHorizontalAlignment(parent.getTitlePlace());
        previewTopPanel.add(titleLabel, BorderLayout.CENTER);

        var iconLabel = QRLabel.getIconLabel();
        previewTopPanel.add(iconLabel, BorderLayout.WEST);
    }

    private void init_menuPanel(QRLabel titleLabel, QRPanel previewTopPanel, QRButton closeButton) {
        menuPanel = new QRMenuPanel() {
            @Override
            public void componentFresh() {
                //把这个也一并改了
                titleLabel.setForeground(menuTextColorForeColorValue.getColor());
                //这个也改了
                previewTopPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, menuLineColorColorValue.getColor()));
                var frameColorBackColorValueColor = frameColorBackColorValue.getColor();
                closeButton.setForeground(menuTextColorForeColorValue.getColor());
                closeButton.setBackground(frameColorBackColorValueColor);
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, menuLineColorColorValue.getColor()));
                for (QRButton button : buttons) {
                    button.setEnterColor(ButtonEnterColorColorValue.getColor());
                    button.setPressColor(ButtonPressColorColorValue.getColor());
                    button.setBackColor(frameColorBackColorValueColor);
                    button.setForeground(menuTextColorForeColorValue.getColor());
                    button.setBackground(frameColorBackColorValueColor);
                }
            }
        };
        for (int i = 0; i < 6; i++) {
            menuPanel.add("菜单 " + (i + 1));
        }
    }

    private void init_statisticsPanel() {
        statisticsPanel = new QRPanel() {
            @Override
            public void componentFresh() {
                QRComponentUtils.componentLoop(this, QRButton.class, button -> {
                    button.setEnterColor(ButtonEnterColorColorValue.getColor());
                    button.setPressColor(ButtonPressColorColorValue.getColor());
                    button.setBackColor(frameColorBackColorValue.getColor());
                    button.setForeground(menuTextColorForeColorValue.getColor());
                    button.setBackground(frameColorBackColorValue.getColor());
                });
            }
        };
        statisticsPanel.setLayout(new GridLayout(1, 0, 5, 8));
        for (int i = 0; i < 5; i++) {
            statisticsPanel.add(new QRButton("按钮" + (i + 1)));
        }
        statisticsPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
    }

    private void init_newOneAndSaveButton() {
        QRRoundButton newOneAndSaveButton = new QRRoundButton("新建并保存");
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
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, newOneAndSaveButton, 484, 578, 160, 35);
    }

    private void init_designIntroductionButton() {
        QRRoundButton designIntroductionButton = new QRRoundButton("使用说明");
        designIntroductionButton.addClickAction(e -> {
            String content = """
                    1. 单击右侧的颜色方块或修改色值文本框都可以改变对应的颜色；

                    2. 内置的默认主题无法修改，但可以在此基础上新建一个，在编辑好主题后，单击“新建并保存”即可；

                    3. 导入的主题在小启软件根目录的theme文件夹中，仅当设计器中的主题是选择的导入的主题时，才可以点击“修改并保存”；

                    4. 色值文本框选择一个，并ctrl c后复制的是整个颜色值到剪贴板，可以在其它任意一个文本框内直接粘贴；

                    5. 色值文本框支持两种形式的粘贴，一种是以#开头的6位16进制色值，一种是以任意长度任意非数字隔开的3个色值，例如：“#BBBBBB”、“77 77 77”、“77,77, 77”、“77c77b77”。""";
            QRResizableTextShowDialog textShowDialog = new QRResizableTextShowDialog(QRSwingThemeDesigner.this, QRSwingThemeDesigner.this.getWidth() / 5 * 3, QRSwingThemeDesigner.this.getHeight() / 5 * 3, "设计器使用说明", content, true);
            QRSystemUtils.setWindowNotTrans(textShowDialog);
            textShowDialog.setVisible(true);
        });
        QRComponentUtils.setBoundsAndAddToComponent(mainPanel, designIntroductionButton, 21, 578, 98, 35);
    }

    private void init_textPane() {
        previewTextPane = new QRTextPane() {
            @Override
            public void componentFresh() {
                setText("");
                caret.setCaretColor(caretColorColorValue.getColor());
                final JScrollBar verticalScrollBar = addScrollPane().getVerticalScrollBar();
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
                String text = "在工作或学习中遇到不开心的时候，不妨静下来好好想想，自己到底是对是错。生活中不是你对别人好，别人就该对你好，你要明白这个道理，每个人都有自己的原则，有人功利，有人善良，你不可能要求别人什么。有时间的话，不妨到处走走，在雄伟的高山之间，" + "放声大喊，一吐心中的阴郁，在浪漫的大海之间，看潮起潮落，感悟人生的起伏跌宕，在落日余晖中感受天地的宁静，洗涤心中的贪念。";
                final Color textColorBack = textColorBackColorValue.getColor();
                addScrollPane().setBackground(frameColorBackColorValue.getColor());
                addScrollPane().setBorder(BorderFactory.createLineBorder(frameColorBackColorValue.getColor(), 5));
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
    }

    private void init_contentPanel() {
        previewFramePanel = new QRBorderContentPanel(new BorderLayout()) {
            private final LinkedList<QRPanel> panels = new LinkedList<>();

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                final int arc = 18;
                final Color color = frameColorBorderColorValue.getColor();
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
                }
                repaint();
            }

            private void panelLoop(QRPanel p, Color color) {
                QRComponentUtils.componentLoop(p, QRPanel.class, panel -> panelLoop((QRPanel) panel, color), com -> com.setBackground(color));
                p.setBackground(color);
            }
        };
    }

    private void init_colorLabel() {
        showFrameColorBackLabel = new QRLabel();
        showTextColorForeLabel = new QRLabel();
        showTextColorBackLabel = new QRLabel();
        showFrameColorBorderLabel = new QRLabel();
        showCorrectColorForeLabel = new QRLabel();
        showCorrectColorBackLabel = new QRLabel();
        showSeniorCorrectColorForeLabel = new QRLabel();
        showButtonEnterColorLabel = new QRLabel();
        showButtonPressColorLabel = new QRLabel();
        showScrollColorLabel = new QRLabel();
        showMenuTextColorForeLabel = new QRLabel();
        showMenuLineColorLabel = new QRLabel();
        showCaretColorLabel = new QRLabel();

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
    }

    private void init_colorValue() {
        QRActionRegister<QRColorChangedEvent> colorChangedAction = e -> update();

        textColorForeColorValue = new QRRGBColorPane(QRColorsAndFonts.TEXT_COLOR_FORE, showTextColorForeLabel, colorChangedAction);
        textColorBackColorValue = new QRRGBColorPane(QRColorsAndFonts.TEXT_COLOR_BACK, showTextColorBackLabel, colorChangedAction);
        correctColorForeColorValue = new QRRGBColorPane(QRColorsAndFonts.CORRECT_COLOR_FORE, showCorrectColorForeLabel, colorChangedAction);
        correctColorBackColorValue = new QRRGBColorPane(QRColorsAndFonts.CORRECT_COLOR_BACK, showCorrectColorBackLabel, colorChangedAction);
        seniorCorrectColorForeColorValue = new QRRGBColorPane(QRColorsAndFonts.SENIOR_RANDOM_COLOR_BACK, showSeniorCorrectColorForeLabel, colorChangedAction);
        frameColorBackColorValue = new QRRGBColorPane(QRColorsAndFonts.FRAME_COLOR_BACK, showFrameColorBackLabel, colorChangedAction);
        frameColorBorderColorValue = new QRRGBColorPane(QRColorsAndFonts.BORDER_COLOR, showFrameColorBorderLabel, colorChangedAction);
        ButtonEnterColorColorValue = new QRRGBColorPane(QRColorsAndFonts.ENTER_COLOR, showButtonEnterColorLabel, colorChangedAction);
        ButtonPressColorColorValue = new QRRGBColorPane(QRColorsAndFonts.PRESS_COLOR, showButtonPressColorLabel, colorChangedAction);
        ScrollColorColorValue = new QRRGBColorPane(QRColorsAndFonts.SCROLL_COLOR, showScrollColorLabel, colorChangedAction);
        menuLineColorColorValue = new QRRGBColorPane(QRColorsAndFonts.LINE_COLOR, showMenuLineColorLabel, colorChangedAction);
        menuTextColorForeColorValue = new QRRGBColorPane(QRColorsAndFonts.MENU_COLOR, showMenuTextColorForeLabel, colorChangedAction);
        caretColorColorValue = new QRRGBColorPane(QRColorsAndFonts.CARET_COLOR, showCaretColorLabel, colorChangedAction);

        panes = new QRRGBColorPane[]{textColorForeColorValue, textColorBackColorValue, correctColorForeColorValue, correctColorBackColorValue, seniorCorrectColorForeColorValue, frameColorBackColorValue, frameColorBorderColorValue, ButtonEnterColorColorValue, ButtonPressColorColorValue, menuLineColorColorValue, ScrollColorColorValue, menuTextColorForeColorValue, caretColorColorValue};

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
    }

    private void init_labels() {
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
            QRColorsAndFonts.loadColors(colors);
            update();
        }
    }

    private void update() {
        previewFramePanel.componentFresh();
        menuPanel.componentFresh();
        previewTopPanel.componentFresh();
        previewTextPane.componentFresh();
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