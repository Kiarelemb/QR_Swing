package swing.qr.kiarelemb.utils;

import method.qr.kiarelemb.utils.QRFontUtils;
import swing.qr.kiarelemb.basic.QRComboBox;
import swing.qr.kiarelemb.task.QRTaskListener;
import swing.qr.kiarelemb.task.QRTaskRunner;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.util.Objects;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 一个加载字体的 {@link QRComboBox}，默认构造器采用懒加载，只在首次点击下拉按钮时才枚举系统字体
 * @create 2023-01-31 16:48
 **/
public class QRFontComboBox extends QRComboBox {

    /**
     * 标识是否已加载系统字体（仅对默认构造器有效）
     */
    private boolean fontsLoaded = false;

    /**
     * 构造对象，使用系统全部字体作为选项（懒加载：首次点击下拉按钮时才加载）。
     */
    public QRFontComboBox() {
        this(true);
    }

    /**
     * 创建对象，可选择是否显示所有字体（懒加载：首次点击下拉按钮时才加载）。
     *
     * @param showAllFont 是否以本字体显示字体名，如果为 {@code true} 则使得每个字体都显示为名称字体，否则只以默认字体显示字体名称
     */
    public QRFontComboBox(boolean showAllFont) {
        super();
        setRenderer(new QRFontComboBoxRenderer(showAllFont));
        installLazyFontLoader(showAllFont);
    }


    /**
     * 构造对象，并设置其渲染器为 {@link QRFontComboBoxRenderer}。
     *
     * @param fontNames 字体名称数组（立即加载）
     */
    public QRFontComboBox(String[] fontNames) {
        super(fontNames);
        fontsLoaded = true;
        setRenderer(new QRFontComboBoxRenderer());
    }


    /**
     * 创建对象，指定字体名称数组和是否以本字体显示字体名（立即加载）
     *
     * @param fontNames   字体名称数组
     * @param showAllFont 是否以本字体显示字体名，如果为 {@code true} 则使得每个字体都显示为名称字体，否则只以默认字体显示字体名称
     */
    public QRFontComboBox(String[] fontNames, boolean showAllFont) {
        super(fontNames);
        fontsLoaded = true;
        setRenderer(new QRFontComboBoxRenderer(showAllFont));
    }


    /**
     * 创建对象，并设置其选择的字体为 {@code fontName}。
     *
     * @param fontName 字体名称
     */
    public QRFontComboBox(String fontName) {
        this();
        setText(fontName);
    }

    public QRFontComboBox(String fontName, boolean showAllFont) {
        this(showAllFont);
        setText(fontName);
    }

    /**
     * 在首次下拉弹出前，先放占位项，然后通过 {@link QRTaskRunner} 在后台加载系统字体，
     * 加载完成后自动替换为完整字体列表，避免阻塞 EDT。
     */
    private void installLazyFontLoader(boolean showAllFont) {
        addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                if (!fontsLoaded) {
                    fontsLoaded = true;
                    // 记住当前选中的值
                    String selected = getSelectedItem() != null ? getSelectedItem().toString() : null;
                    // 先放占位项，让用户看到即时反馈
                    setModel("正在加载字体…");
                    setRenderer(new QRFontComboBoxRenderer(showAllFont));
                    reopenPopupIfUserStillHere();
                    // 后台加载字体，不阻塞 EDT
                    QRTaskRunner.run(context -> QRFontUtils.getSystemFontNames())
                            .addListener(new QRTaskListener<>() {
                                @Override
                                public void succeeded(String[] fontNames) {
                                    setModel(fontNames);
                                    if (selected != null) {
                                        setSelectedItem(selected);
                                    }
                                    setRenderer(new QRFontComboBoxRenderer(showAllFont));
                                    reopenPopupIfUserStillHere();
                                }

                                @Override
                                public void failed(Throwable t) {
                                    setModel("字体加载失败");
                                    reopenPopupIfUserStillHere();
                                }
                            });
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                // 无需处理
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                // 无需处理
            }
        });
    }

    private void reopenPopupIfUserStillHere() {
        SwingUtilities.invokeLater(() -> {
            if (isDisplayable() && isShowing() && isEnabled() && userStillFocusedComboBox()) {
                setPopupVisible(true);
            }
        });
    }

    private boolean userStillFocusedComboBox() {
        Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        return focusOwner == null || focusOwner == this || SwingUtilities.isDescendingFrom(focusOwner, this);
    }

    @Override
    public void setText(String value) {
        super.setText(value);
        setFont(value == null || value.isBlank()
                ? QRColorsAndFonts.DEFAULT_FONT_MENU
                : QRFontUtils.getFont(value, QRColorsAndFonts.DEFAULT_FONT_MENU.getSize()));
    }

    @Override
    public void componentFresh() {
        super.componentFresh();
        setFont(getSelectedItem() == null ? QRColorsAndFonts.DEFAULT_FONT_MENU : QRFontUtils.getFont(getText(), QRColorsAndFonts.DEFAULT_FONT_MENU.getSize()));
    }

    static class QRFontComboBoxRenderer extends BasicComboBoxRenderer {
        /**
         * 默认使得每个字体都显示为名称字体
         */
        private final boolean showAllFont;

        public QRFontComboBoxRenderer() {
            this(true);
        }

        public QRFontComboBoxRenderer(boolean showAllFont) {
            this.showAllFont = showAllFont;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value == null) {
                label.setFont(QRColorsAndFonts.DEFAULT_FONT_MENU);
                return label;
            }
            String fontName = value.toString();
            if (showAllFont) {
                fontUpdate(label, fontName);
            } else {
                if (isSelected) {
                    fontUpdate(label, fontName);
                } else {
                    label.setFont(QRColorsAndFonts.DEFAULT_FONT_MENU);
                }
            }
            return label;
        }

        private void fontUpdate(JLabel label, String fontName) {
            if (!Objects.equals(label.getFont().getFontName(), fontName)) {
                Font font = QRFontUtils.getFont(fontName, QRColorsAndFonts.DEFAULT_FONT_MENU.getSize());
                label.setFont(font);
            }
        }
    }
}