package swing.qr.kiarelemb.utils;

import method.qr.kiarelemb.utils.QRFileUtils;
import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.basic.QRTextField;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;

/**
 * 带清除按钮的文本输入组合组件。
 *
 * <p>该组件由一个 {@link QRTextField} 和一个清除按钮组成。默认清除按钮在右侧，
 * 点击后清空文本。组件自身负责绘制焦点/空值/合法/非法状态边框。</p>
 *
 * <p>可以通过特殊构造器启用文件路径模式，此时内部文本框会使用 {@link QRFilePathTextField}，
 * 并可联动“确定”等按钮的启用状态。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRClearableTextField field = new QRClearableTextField();
 * field.textField.setText("hello");
 *
 * QRClearableTextField fileField =
 *         new QRClearableTextField(true, true, "/tmp/a.txt", okButton);
 * </code></pre>
 *
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRClearableTextField
 * @description 带清除按钮的文本框
 * @create 2024/3/25 22:27
 */
public class QRClearableTextField extends QRPanel {
    public final QRTextField textField;
    public final QRButton clearButton;

    /**
     * 默认清空按钮在右侧
     */
    public QRClearableTextField() {
        this(true);
    }

    public QRClearableTextField(boolean right) {
        super(false, new BorderLayout());
        textField = new TextField();

        add(textField, BorderLayout.CENTER);
        clearButton = new ClearButton();
        add(clearButton, right ? BorderLayout.EAST : BorderLayout.WEST);
        setEmptyBorder();
    }

    /**
     * 此构造器用于将 {@link QRTextField} 设置成 {@link QRFilePathTextField}
     *
     * @param right         清空按钮是否在右侧
     * @param filePathModel 是否设置为 {@link QRFilePathTextField}
     * @param path          文件路径，可为 {@code null}
     * @param btn           确定按钮，可为 {@code null}
     */
    public QRClearableTextField(boolean right, boolean filePathModel, String path, JButton btn) {
        clearButton = new ClearButton();
        if (filePathModel) {
            textField = new QRFilePathTextField(path, btn) {
                @Override
                protected boolean meetCondition() {
                    return QRClearableTextField.this.meetCondition();
                }

                @Override
                public void setBorder(Border border) {
                    super.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                }

                @Override
                protected void focusGained(FocusEvent e) {
                    QRClearableTextField.this.focusGainedAction();
                }

                @Override
                protected void focusLost(FocusEvent e) {
                    QRClearableTextField.this.focusLostAction();
                }
            };
        } else {
            textField = new TextField();
        }
        setLayout(new BorderLayout());
        add(textField, BorderLayout.CENTER);
        add(clearButton, right ? BorderLayout.EAST : BorderLayout.WEST);
        setEmptyBorder();
        Dimension dimension = this.getPreferredSize();
        clearButton.setPreferredSize(dimension.height, dimension.height);
    }

    /**
     * 判断当前文本是否符合业务条件。
     *
     * <p>如果 {@link #textField} 实体是 {@link QRFilePathTextField}，默认会检查文本路径对应文件是否存在。
     * 子类可重写该方法实现自定义校验，校验结果会影响边框颜色和联动按钮状态。</p>
     *
     * @return 是否符合条件
     */
    protected boolean meetCondition() {
        if (textField instanceof QRFilePathTextField field) {
            return QRFileUtils.fileExists(field.getText());
        }
        return true;
    }

    /**
     * 获得焦点时的处理，子类可重写。
     */
    protected void focusGainedAction() {
        setEnterBorder();
    }


    /**
     * 清除按钮点击处理，默认清空内部文本框。
     *
     * @param o 点击事件
     */
    protected void clearAction(ActionEvent o) {
        textField.clear();
    }

    /**
     * 已自动添加监听器，可直接重写，但不建议完全重写
     */
    protected void focusLostAction() {
        if (textField == null) {
            return;
        }
        String text = textField.getText();
        if (text.isEmpty()) {
            //内容为空的边框
            setEmptyBorder();
            return;
        }
        if (meetCondition()) {
            //符合条件的边框
            setRightBorder();
            return;
        }
        //不符合条件的边框
        setErrorBorder();
    }

    //region 边框设置

    /**
     * 获得焦点时的边框
     */
    protected void setEnterBorder() {
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, QRColorsAndFonts.BLUE_LIGHT));
    }

    /**
     * 内容为空时的边框
     */
    protected void setEmptyBorder() {
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.YELLOW));
    }

    /**
     * 符合条件的边框
     */
    protected void setRightBorder() {
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, QRColorsAndFonts.LIGHT_GREEN));
    }

    /**
     * 不符合条件的边框
     */
    protected void setErrorBorder() {
        setBorder(BorderFactory.createLineBorder(QRColorsAndFonts.RED_NORMAL, 1));
    }
    //endregion 边框设置

    //region 类重写

    private class ClearButton extends QRCloseButton {
        public ClearButton() {
            setToolTipText("清除");
        }

        @Override
        protected void actionEvent(ActionEvent o) {
            QRClearableTextField.this.clearAction(o);
        }
    }

    private class TextField extends QRTextField {

        @Override
        protected boolean meetCondition() {
            return QRClearableTextField.this.meetCondition();
        }

        @Override
        public void setBorder(Border border) {
            super.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }

        @Override
        protected void focusGained(FocusEvent e) {
            QRClearableTextField.this.focusGainedAction();
        }

        @Override
        protected void focusLost(FocusEvent e) {
            QRClearableTextField.this.focusLostAction();
        }

    }
    //endregion 类重写
}
