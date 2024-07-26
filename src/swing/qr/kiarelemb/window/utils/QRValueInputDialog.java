package swing.qr.kiarelemb.window.utils;

import method.qr.kiarelemb.utils.QRTools;
import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.basic.QRRoundButton;
import swing.qr.kiarelemb.basic.QRTextField;
import swing.qr.kiarelemb.listener.QRKeyListener;
import swing.qr.kiarelemb.utils.QRComponentUtils;
import swing.qr.kiarelemb.window.basic.QREmptyDialog;

import java.awt.*;
import java.awt.event.KeyEvent;


/**
 * @author Kiarelemb QR
 * @create 2024.03.13
 * @apiNote 本类使用方法：
 * <p>Input input = new Input(owner, textFieldTooltip, inputLabelText);
 * <p>input.setVisible(true);
 * <p>String answer = input.getAnswer();
 */
public class QRValueInputDialog extends QREmptyDialog {
    protected String answer;
    protected QRTextField textField;

    /**
     * @param owner            父窗体
     * @param textFieldTooltip 输入框的 Tooltip
     * @param inputLabelText   输入内容提示
     */
    public QRValueInputDialog(Window owner, String textFieldTooltip, String inputLabelText) {
        super(owner);
        var width = 320;
        var height = 160;
        setSize(width, height);
        setLocationRelativeTo(owner);
        contentPane.setLayout(null);

        textField = new QRTextField();
        textField.setToolTipText(textFieldTooltip);
        textField.addKeyListener();

        var label = new QRLabel(inputLabelText);

        var buttonSure = new QRRoundButton("确定");
        buttonSure.setToolTipText("Enter");

        var buttonCancel = new QRRoundButton("取消");
        buttonCancel.setToolTipText("ESC");

        QRComponentUtils.setBoundsAndAddToComponent(contentPane, label, 22, 26, 280, 18);
        QRComponentUtils.setBoundsAndAddToComponent(contentPane, textField, 22, 64, 280, 37);
        QRComponentUtils.setBoundsAndAddToComponent(contentPane, buttonSure, 236, 118, 66, 29);
        QRComponentUtils.setBoundsAndAddToComponent(contentPane, buttonCancel, 158, 118, 66, 29);

        buttonCancel.addClickAction(e -> dispose());
        buttonSure.addClickAction(e -> {
            if (meetCondition()) {
                if (setAnswer(textField.getText())) {
                    dispose();
                }
            }
        });
        textField.addKeyListenerAction(QRKeyListener.TYPE.PRESS, e -> {
            switch (e.getKeyChar()) {
                case KeyEvent.VK_ENTER:
                    buttonSure.click();
                    break;
                case KeyEvent.VK_ESCAPE:
                    buttonCancel.click();
                    break;
                default:
                    QRTools.doNothing();
            }
        });
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
     * 取得输入的内容
     */
    public String getAnswer() {
        return answer == null ? "" : answer;
    }

    boolean setAnswer(String answer) {
        this.answer = answer;
        return true;
    }
}