package swing.qr.kiarelemb.window.enhance;

import method.qr.kiarelemb.utils.QRSystemUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.utils.QRComponentUtils;
import swing.qr.kiarelemb.window.basic.QREmptyDialog;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

/**
 * 自动关闭的轻量提示窗口。
 *
 * <p>适合显示“已保存”“复制成功”这类不中断用户流程的短消息。与
 * {@link QROpinionDialog} 不同，本类默认非模态，调用 {@link #display(Window, String)}
 * 后会立即返回，并在指定时间后自动关闭。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRSmallTipShow.display(this, "已保存");
 * QRSmallTipShow.display(this, "导出完成", 1200);
 *
 * QRSmallTipShow tip = QRSmallTipShow.getInstance(this, "正在等待...");
 * tip.setVisible(true);
 * // 后续手动 tip.dispose();
 * </code></pre>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2022-11-21 19:20
 **/
public final class QRSmallTipShow extends QREmptyDialog {
    private long closeWaitTime = 500;
    private boolean isExisting = false;
    private boolean autoClose = true;

    /**
     * @param owner 父窗体
     * @param text  欲显示的内容
     */
    private QRSmallTipShow(Window owner, String text) {
        super(owner, false);
        setAlwaysOnTop(true);
        QRPanel q = getContentPane();

        QRPanel qp = new QRPanel();
        qp.setLayout(new BorderLayout());
        qp.setBorder(new LineBorder(this.backgroundColor, 8));
        qp.setBackground(this.backgroundColor);
        QRLabel nameLabel = new QRLabel(text);
        qp.add(nameLabel, BorderLayout.CENTER);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        q.add(qp, BorderLayout.CENTER);
        q.setBorder(new LineBorder(QRColorsAndFonts.BORDER_COLOR, 1));
        setSize(100, 50);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                if (QRSmallTipShow.this.autoClose) {
                    Timer timer = new Timer(closeDelay(), event -> {
                        QRSmallTipShow.this.isExisting = true;
                        QRSmallTipShow.this.dispose();
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        });
    }

    /**
     * 关闭自动关闭行为。
     *
     * <p>调用后需要由调用方在合适时机手动 {@link #dispose()}。</p>
     */
    public void setAutoCloseFalse() {
        this.autoClose = false;
    }

    /**
     * 设置自动关闭等待时间。
     *
     * @param closeWaitTime 等待毫秒数，小于 0 时按 0 处理，大于 {@link Integer#MAX_VALUE} 时按最大值处理
     */
    public void setCloseWaitTime(long closeWaitTime) {
        if (this.closeWaitTime != closeWaitTime) {
            this.closeWaitTime = closeWaitTime;
        }
    }

    /**
     * 创建一个不会自动关闭的提示实例。
     *
     * <p>该方法会确保实例在 EDT 创建，但不会自动显示。适用于需要手动控制关闭时机的临时提示。</p>
     *
     * @param owner   父窗体，可为 null
     * @param message 提示文本
     * @return 提示窗口实例
     */
    public static QRSmallTipShow getInstance(Window owner, String message) {
        if (SwingUtilities.isEventDispatchThread()) {
            return createTip(owner, message, 10000000, false);
        }
        final QRSmallTipShow[] result = new QRSmallTipShow[1];
        try {
            SwingUtilities.invokeAndWait(() -> result[0] = createTip(owner, message, 10000000, false));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
        return result[0];
    }

    /**
     * 显示一个 500 毫秒后自动关闭的轻提示。
     *
     * @param owner   父窗体，可为 null
     * @param message 提示文本
     */
    public static void display(Window owner, String message) {
        display(owner, message, 500);
    }

    /**
     * 显示一个指定时间后自动关闭的轻提示。
     *
     * <p>可从任意线程调用，内部会切换到 EDT 创建并显示窗口。</p>
     *
     * @param owner         父窗体，可为 null
     * @param message       提示文本
     * @param closeWaitTime 自动关闭等待毫秒数
     */
    public static void display(Window owner, String message, long closeWaitTime) {
        QRComponentUtils.runOnEdt(() -> createTip(owner, message, closeWaitTime, true).setVisible(true));
    }

    /**
     * 在屏幕中央显示一个指定时间后自动关闭的轻提示。
     *
     * @param message       提示文本
     * @param closeWaitTime 自动关闭等待毫秒数
     */
    public static void display(String message, long closeWaitTime) {
        QRComponentUtils.runOnEdt(() -> createTip(null, message, closeWaitTime, true).setVisible(true));
    }

    private static QRSmallTipShow createTip(Window owner, String message, long closeWaitTime, boolean autoClose) {
        QRSmallTipShow gns = new QRSmallTipShow(owner, message);
        gns.setCloseWaitTime(closeWaitTime);
        if (!autoClose) {
            gns.setAutoCloseFalse();
        }
        gns.pack();
        if (owner == null) {
            gns.setLocationRelativeTo(null);
        } else {
            gns.setLocation(owner.getX() + owner.getWidth() / 2 - gns.getWidth() / 2,
                    owner.getY() + owner.getHeight() / 2 - gns.getHeight() / 2);
        }
        if (QRSwing.windowRound) {
            QRSystemUtils.setWindowRound(gns, QRSwing.windowTransparency);
        } else {
            QRSystemUtils.setWindowTrans(gns, QRSwing.windowTransparency);
        }
        return gns;
    }

    private int closeDelay() {
        return (int) Math.max(0, Math.min(Integer.MAX_VALUE, closeWaitTime));
    }
}
