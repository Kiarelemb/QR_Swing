package swing.qr.kiarelemb.window.utils;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.basic.QRRoundButton;
import swing.qr.kiarelemb.basic.QRTextField;
import swing.qr.kiarelemb.utils.QRComponentUtils;
import swing.qr.kiarelemb.window.basic.QREmptyDialog;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;


/**
 * @author Kiarelemb QR
 * @create 2024.03.13
 * @apiNote 本类使用方法：
 * <pre><code>
 *     Input input = new Input(owner, textFieldTooltip, inputLabelText);
 *     input.setVisible(true);
 *     String answer = input.getAnswer();
 * </code></pre>
 */
public class QRValueInputDialog extends QREmptyDialog {
    protected QRTextField textField;
    protected QRRoundButton cancelButton;
    protected QRRoundButton sureButton;
    protected String answer;
    protected boolean approved = false;

    /**
     * @param owner            父窗体
     * @param textFieldTooltip 输入框的 Tooltip
     * @param inputLabelText   输入内容提示
     */
    public QRValueInputDialog(Window owner, String textFieldTooltip, String inputLabelText) {
        super(owner);
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

        setSize(width, height);
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        QRSwing.registerGlobalAction(KeyEvent.VK_ENTER, e1 -> sureButton.clickInvokeLater(), true);
        QRSwing.registerGlobalAction(KeyEvent.VK_ESCAPE, e1 -> cancelButton.clickInvokeLater(), true);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        QRSwing.registerGlobalActionRemove(KeyEvent.VK_ENTER, true);
        QRSwing.registerGlobalActionRemove(KeyEvent.VK_ESCAPE, true);
    }

    public void setDefaultValue(String value){
        textField.setText(value);
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
     * 获取用户是否点击了确定按钮
     */
    public boolean isApproved() {
        return approved;
    }

    /**
     * 取得输入的内容
     */
    public String getAnswer() {
        return answer == null ? "" : answer;
    }

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