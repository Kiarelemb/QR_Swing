package swing.qr.kiarelemb.window.utils;

import swing.qr.kiarelemb.QRGlobalAction;
import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.basic.QRRoundButton;
import swing.qr.kiarelemb.basic.QRTextField;
import swing.qr.kiarelemb.utils.QRComponentUtils;
import swing.qr.kiarelemb.window.basic.QREmptyDialog;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Objects;


/**
 * 单字段文本输入对话框。
 *
 * <p>适合重命名、跳转页码、输入少量参数等简单场景。点击“确定”且
 * {@link #meetCondition()} 与 {@link #setAnswer(String)} 均通过后，
 * {@link #isApproved()} 才会返回 true，调用方再通过 {@link #getAnswer()} 读取结果。</p>
 *
 * <p>子类可重写 {@link #meetCondition()} 做合法性检查，或重写 {@link #setAnswer(String)}
 * 在保存结果前做格式化/转换。</p>
 *
 * @author Kiarelemb QR
 * @create 2024.03.13
 * @apiNote 本类使用方法：
 * <pre><code>
 *     QRValueInputDialog input = new QRValueInputDialog(owner, "请输入名称", "名称");
 *     input.setVisible(true);
 *     if (!input.isApproved()) return;
 *     String answer = input.getAnswer();
 * </code></pre>
 */
public class QRValueInputDialog extends QREmptyDialog {
    protected QRTextField textField;
    protected QRRoundButton cancelButton;
    protected QRRoundButton sureButton;
    protected String answer;
    protected boolean approved = false;
    private String defaultValue;
    private final QRGlobalAction enterAction;
    private final QRGlobalAction escapeAction;

    /**
     * @param owner            父窗体
     * @param textFieldTooltip 输入框的 Tooltip
     * @param inputLabelText   输入内容提示
     */
    public QRValueInputDialog(Window owner, String textFieldTooltip, String inputLabelText) {
        super(owner);
        setAlwaysOnTop(true);

        var width = 320;
        var height = 160;

        contentPane.setLayout(null);

        textField = new QRTextField();
        textField.setToolTipText(textFieldTooltip);

        QRLabel label = new QRLabel(inputLabelText);

	    sureButton = new QRRoundButton("确定");
		sureButton.setToolTipText("Enter");

	    cancelButton = new QRRoundButton("取消");
		cancelButton.setToolTipText("ESC");

        textField.addDocumentListenerActionAll(e -> {
            boolean isBlank = textField.getText().isBlank();
            sureButton.setEnabled(!isBlank);
        });

        QRComponentUtils.setBoundsAndAddToComponent(contentPane, label, 22, 26, 280, 18);
        QRComponentUtils.setBoundsAndAddToComponent(contentPane, textField, 22, 64, 280, 37);
        QRComponentUtils.setBoundsAndAddToComponent(contentPane, sureButton, 236, 118, 66, 29);
        QRComponentUtils.setBoundsAndAddToComponent(contentPane, cancelButton, 158, 118, 66, 29);

        cancelButton.addClickAction(e -> dispose());
        sureButton.addClickAction(e -> {
            if (meetCondition()) {
                if (setAnswer(textField.getText())) {
                    approved = true;
                    dispose();
                }
            }
        });
        enterAction = new QRGlobalAction(e -> sureButton.clickInvokeLater())
                .key(KeyEvent.VK_ENTER)
                .window(this);
        escapeAction = new QRGlobalAction(e -> cancelButton.clickInvokeLater())
                .key(KeyEvent.VK_ESCAPE)
                .window(this);

        setSize(width, height);
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        enterAction.load();
        escapeAction.load();
    }

    @Override
    public void windowClosing(WindowEvent e) {
        enterAction.close();
        escapeAction.close();
    }

    /**
     * 设置默认值，即输入框打开时文本框的初始值。
     *
     * @param defaultValue 默认值
     */
    public void setDefaultValue(String defaultValue){
        this.defaultValue = defaultValue;
        textField.setText(defaultValue);
    }

    /**
     * 要求用户必须修改默认值后才能点击确定。
     *
     * <p>应在 {@link #setDefaultValue(String)} 之后调用。若当前文本与默认值相同，
     * 确定按钮会被禁用。</p>
     */
    public void requireDefaultValueChange(){
        if(defaultValue != null){
            textField.addDocumentListenerActionAll(e -> {
                if (Objects.equals(textField.getText(), this.defaultValue)) {
                    sureButton.setEnabled(false);
                }
            });
        }
    }

    /**
     * 继承请重写
     *
     * @return 是否符合条件
     */
    public boolean meetCondition() {
        return true;
    }

    /**
     * 获取用户是否点击了确定按钮并通过校验。
     *
     * @return true 表示可以读取 {@link #getAnswer()}
     */
    public boolean isApproved() {
        return approved;
    }

    /**
     * 取得输入的内容。
     *
     * <p>经过 <pre><code>if (!input.isApproved()) return;</code></pre> 筛选之后的
     * {@code answer} 必定不为 {@code null}。</p>
     *
     * @return 输入结果
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * 保存输入结果。
     *
     * <p>子类可重写该方法，在写入 {@link #answer} 前做清洗、转换或额外校验。
     * 返回 false 会阻止对话框确认关闭。</p>
     *
     * @param answer 输入框当前文本
     * @return true 表示接受该输入
     */
    protected boolean setAnswer(String answer) {
        this.answer = answer;
        return true;
    }

    public QRTextField textField() {
        return textField;
    }

    public QRRoundButton cancelButton() {
        return cancelButton;
    }

    public QRRoundButton sureButton() {
        return sureButton;
    }
}