package swing.qr.kiarelemb.window.enhance;

import method.qr.kiarelemb.utils.QRStringUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.basic.QRRoundButton;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.listener.QRWindowListener;
import swing.qr.kiarelemb.resource.QRSwingInfo;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.utils.QRMessageLabel;
import swing.qr.kiarelemb.window.basic.QRDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-21 18:58
 **/
public final class QROpinionDialog extends QRDialog {
    public static final int OK = 0;
    public static final int CANCEL = 1;
    private final QRMessageLabel message;
    private final QRLabel image;
    private int selection = CANCEL;
    private final static String INFO = "提示";
    private final static String ERROR = "错误";
    private final static String MSG = "信息";

    private final QRActionRegister<KeyStroke> sureDisposeAction;

    private QROpinionDialog(Window parent) {
        super(parent);
        setSize(380, 220);
        message = new QRMessageLabel();
        message.setHorizontalAlignment(SwingConstants.LEFT);
        message.setForeground(QRColorsAndFonts.DEFAULT_COLOR_LABEL);
        mainPanel.add(message);
        image = new QRLabel();
        sureDisposeAction = e -> {
            if (QROpinionDialog.this.isFocused()) {
                sure();
            }
        };
        KeyStroke keyStroke = QRStringUtils.getKeyStroke(KeyEvent.VK_ENTER);
        QRSwing.registerGlobalAction(keyStroke, sureDisposeAction, false);
    }

    private void setImage(String imageFileName) {
        message.setBounds(117, 23, 228, 95);
        image.setForeground(Color.RED);
        image.setBounds(30, 42, 64, 64);
        image.setIcon(new ImageIcon(QRSwingInfo.loadUrl(imageFileName)));
        mainPanel.add(image);
    }

    private void sure() {
        selection = OK;
        dispose();
    }

    private void cancel() {
        selection = CANCEL;
        dispose();
    }

    @Override
    public void dispose() {
        setVisible(false);
        QRSwing.registerGlobalActionRemove(QRStringUtils.getKeyStroke(KeyEvent.VK_ESCAPE), disposeAction, false);
        QRSwing.registerGlobalActionRemove(QRStringUtils.getKeyStroke(KeyEvent.VK_ENTER), sureDisposeAction, false);
    }

    private int getSelection() {
        return selection;
    }

    private void setMessage(String message) {
        this.message.setMessage(message);
        this.message.componentFresh();
    }

    private void sureOpinion(String message, String title) {
        setTitle(title);
        setMessage(message);
        QRRoundButton sureButton = new QRRoundButton("确定");
        sureButton.setToolTipText("Enter");
        sureButton.setBounds(155, 124, 70, 33);
        sureButton.addClickAction(e -> sure());
        mainPanel.add(sureButton);
        setVisible(true);
    }

    private void sureAndCancelOpinion(String message, String title) {
        setTitle(title);
        setMessage(message);
        QRRoundButton sureButton = new QRRoundButton("确定");
        sureButton.setToolTipText("Enter");
        sureButton.addClickAction(e -> sure());
        mainPanel.add(sureButton);

        QRRoundButton cancelButton = new QRRoundButton("取消");
        cancelButton.setToolTipText("Esc");
        cancelButton.addClickAction(e -> cancel());
        mainPanel.add(cancelButton);
        sureButton.setBounds(104, 124, 71, 33);
        cancelButton.setBounds(204, 124, 71, 33);
        addWindowAction(QRWindowListener.TYPE.OPEN, e -> requestFocus());
        setVisible(true);
    }

    /**
     * 该单向信息提示框采用的是sureOpinion()方法
     *
     * @param parentComponent 主窗体
     * @param message         内容
     */
    public static void messageTellShow(Window parentComponent, String message) {
        if (message == null) {
            return;
        }
        QROpinionDialog qod = new QROpinionDialog(parentComponent);
        qod.setImage("tell.png");
        qod.sureOpinion(message, QROpinionDialog.MSG);
    }

    /**
     * 该信息提示框采用的是sureAndCancelOpinion()方法
     *
     * @param parentComponent 主窗体
     * @param message         内容
     * @return 选译
     */
    public static int messageInfoShow(Window parentComponent, String message) {
        if (message == null) {
            throw new RuntimeException("信息不能为空");
        }
        QROpinionDialog qod = new QROpinionDialog(parentComponent);
        qod.setImage("info.png");
        qod.sureAndCancelOpinion(message, QROpinionDialog.INFO);
        return qod.getSelection();
    }

    /**
     * 该错误提示框采用的是sureOpinion()方法
     *
     * @param parentComponent 主窗体
     * @param message         内容
     */
    public static void messageErrShow(Window parentComponent, String message) {
        if (message == null) {
            return;
        }
        QROpinionDialog qod = new QROpinionDialog(parentComponent);
        qod.setImage("err.png");
        qod.sureOpinion(message, QROpinionDialog.ERROR);
    }

}