package swing.qr.kiarelemb.test;

import method.qr.kiarelemb.utils.QRStringUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRScrollPane;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.window.basic.QRFrame;
import swing.qr.kiarelemb.window.utils.QRFileSelectDialog;

import javax.swing.*;
import java.awt.*;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 对 {@link QRFileSelectDialog} 的测试，特别关注左侧 {@code treeScrollPane} 的边框绘制
 *               <p>发现：treeScrollPane 的边框似乎只有上、左、下三边，右侧和四角缺失。</p>
 * @create 2026-06-19
 **/
public class QRFileSelectDialogTest {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(QRFileSelectDialogTest::diagnose);
    }

    private static void diagnose() {
        System.out.println("=".repeat(70));
        System.out.println(" QRFileSelectDialog — treeScrollPane 边框诊断");
        System.out.println("=".repeat(70));

        QRSwing.start();
        QRSwing.registerGlobalKeyEvents();

        // 全局设置
        System.out.println("\n► 全局配置");
        System.out.printf("  QRSwing.windowRound    = %b%n", QRSwing.windowRound);
        System.out.printf("  QRSwing.windowImageSet = %b%n", QRSwing.windowImageSet);
        System.out.printf("  BORDER_COLOR           = %s%n", colorToHex(QRColorsAndFonts.BORDER_COLOR));
        System.out.printf("  FRAME_COLOR_BACK       = %s%n", colorToHex(QRColorsAndFonts.FRAME_COLOR_BACK));

        // 创建父窗口（不显示）
        QRFrame parent = new QRFrame("Parent");

        QRFileSelectDialog dialog = new QRFileSelectDialog(parent);
        // 创建对话框
        dialog.pack();

        // 递归查找 treeScrollPane
        QRScrollPane treeScrollPane = findTreeScrollPane(dialog);

        if (treeScrollPane == null) {
            System.err.println("\n✖ 错误：无法在组件树中找到 treeScrollPane (QRScrollPane with borderPaint=true)");
            printComponentTree(dialog.getContentPane(), 0);
            dialog.setVisible(true);
            return;
        }

        // treeScrollPane 基本信息
        System.out.println("\n► treeScrollPane 信息");
        System.out.printf("  borderPaint()          = %b%n", treeScrollPane.borderPaint());
        System.out.printf("  getBorder()            = %s%n", treeScrollPane.getBorder());
        System.out.printf("  getInsets()            = %s%n", treeScrollPane.getInsets());
        System.out.printf("  getWidth()             = %d px%n", treeScrollPane.getWidth());
        System.out.printf("  getHeight()            = %d px%n", treeScrollPane.getHeight());
        System.out.printf("  getPreferredSize()     = %s%n", treeScrollPane.getPreferredSize());
        System.out.printf("  isOpaque()             = %b%n", treeScrollPane.isOpaque());
        System.out.printf("  getBackground()        = %s%n", colorToHex(treeScrollPane.getBackground()));
        System.out.printf("  getX()                 = %d%n", treeScrollPane.getX());
        System.out.printf("  getY()                 = %d%n", treeScrollPane.getY());
        System.out.printf("  getBounds()            = %s%n", treeScrollPane.getBounds());

        // 父容器 (centerPanel) 信息
        Container parentOfScroll = treeScrollPane.getParent();

        // 顶层容器 (mainPanel) 信息
        Container mainPanel = parentOfScroll != null ? parentOfScroll.getParent() : null;
        System.out.println("\n► mainPanel 信息");
        if (mainPanel != null) {
            System.out.printf("  class                  = %s%n", mainPanel.getClass().getName());
            System.out.printf("  layout                 = %s%n", mainPanel.getLayout().getClass().getName());
            System.out.printf("  getInsets()            = %s%n", mainPanel.getInsets());
            System.out.printf("  getBounds()            = %s%n", mainPanel.getBounds());

            if (mainPanel.getLayout() instanceof BorderLayout bl) {
                System.out.printf("  BorderLayout.hgap      = %d%n", bl.getHgap());
                System.out.printf("  BorderLayout.vgap      = %d%n", bl.getVgap());
            }

            // 列出 mainPanel 的所有子组件
            System.out.println("  子组件：");
            for (Component child : mainPanel.getComponents()) {
                System.out.printf("    - %s [%s] bounds=%s%n",
                        child.getClass().getSimpleName(),
                        getBorderLayoutConstraint(mainPanel, child),
                        child.getBounds());
            }
        }

        // 边框绘制方法分析
        System.out.println("\n► 边框绘制分析");
        System.out.println("  paintBorder() 逻辑（当 borderPaint=true 时）：");
        if (QRSwing.windowRound) {
            System.out.println("  模式: 圆角矩形 (drawRoundRect)");
            System.out.println("  - arc=15 → 四角会被圆角替代，视觉上\"没有角\"");
            System.out.println("  - 右侧边框会被绘制，但可能因以下原因不可见：");
            if (parentOfScroll != null && parentOfScroll.getBackground().equals(QRColorsAndFonts.BORDER_COLOR)) {
                System.out.println("    ⚠ 父容器背景色与 BORDER_COLOR 相同，边框可能被背景吞没");
            }
            System.out.println("  - 边框透明度: " + (QRSwing.windowImageSet ? "0.5" : "1.0"));
        } else {
            System.out.println("  模式: 直角矩形 (drawRect)");
            System.out.println("  - 右侧和四角应该完整绘制");
            System.out.println("  - 边框透明度: " + (QRSwing.windowImageSet ? "0.5" : "1.0"));
        }

        // 视觉测试：显示组件树
        System.out.println("\n► 完整组件树");
        printComponentTree(dialog.getContentPane(), 0);

        // 边框区域可视化的分析
        System.out.println("\n► 边框可见性分析");
        if (treeScrollPane.borderPaint()) {
            System.out.println("  ✓ borderPaint = true，自定义边框已启用");
        } else {
            System.out.println("  ✖ borderPaint = false，边框未启用");
        }

        int w = treeScrollPane.getWidth();
        int h = treeScrollPane.getHeight();
        System.out.printf("  绘制区域: drawRect/RoundRect(0, 0, %d, %d)%n", w - 1, h - 1);
        System.out.println("  左边框: (0, 0) → (0, " + (h - 1) + ")     — 应该可见");
        System.out.println("  上边框: (0, 0) → (" + (w - 1) + ", 0)     — 应该可见");
        System.out.println("  右边框: (" + (w - 1) + ", 0) → (" + (w - 1) + ", " + (h - 1) + ") — ⚠ 用户报告不可见");
        System.out.println("  下边框: (0, " + (h - 1) + ") → (" + (w - 1) + ", " + (h - 1) + ") — 应该可见");

        if (parentOfScroll != null && parentOfScroll.getLayout() instanceof BorderLayout) {
            System.out.println("\n  可能原因分析：");
            System.out.println("  1. treeScrollPane 位于 centerPanel 的 WEST 位置,");
            System.out.println("     centerPanel 使用 BorderLayout(8, 0)，右侧 8px 水平间距");
            System.out.println("  2. 右侧边框绘制在 treeScrollPane 的右边缘 (x=" + (w - 1) + ")");
            System.out.println("     紧邻 8px 间距区域，若背景色与边框色接近则难以分辨");
            System.out.println("  3. 当 windowRound=true 时，drawRoundRect 的 arc=15 使四角圆化,");
            System.out.println("     视觉上\"没有角\"");
        }

        // 显示对话框
        System.out.println("\n► 正在显示对话框，请观察左侧 treeScrollPane 的边框...");
        System.out.println("  关闭对话框后将退出程序。");
        dialog.setVisible(true);

        System.out.println("\n✓ 对话框已关闭，诊断结束。");
    }

    /**
     * 递归查找第一个 {@code borderPaint() == true} 的 {@link QRScrollPane}。
     */
    private static QRScrollPane findTreeScrollPane(Container root) {
        if (root instanceof QRScrollPane sp && sp.borderPaint()) {
            return sp;
        }
        for (Component comp : root.getComponents()) {
            if (comp instanceof Container child) {
                QRScrollPane found = findTreeScrollPane(child);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * 递归打印组件树。
     */
    private static void printComponentTree(Container container, int depth) {
        String indent = "  ".repeat(depth);
        for (Component comp : container.getComponents()) {
            String constraint = "";
            if (container.getLayout() instanceof BorderLayout bl) {
                for (String key : new String[]{BorderLayout.NORTH, BorderLayout.SOUTH,
                        BorderLayout.WEST, BorderLayout.CENTER, BorderLayout.EAST}) {
                    if (bl.getLayoutComponent(key) == comp) {
                        constraint = " [" + key + "]";
                        break;
                    }
                }
            }
            String borderInfo = "";
            if (comp instanceof QRScrollPane sp) {
                borderInfo = String.format(" (borderPaint=%b, opaque=%b, border=%s)",
                        sp.borderPaint(), sp.isOpaque(), sp.getBorder());
            }
            System.out.printf("%s%s%s%s%n", indent,
                    comp.getClass().getSimpleName(), constraint, borderInfo);
            if (comp instanceof Container child && child.getComponentCount() > 0) {
                printComponentTree(child, depth + 1);
            }
        }
    }

    /**
     * 获取组件在 BorderLayout 容器中的约束名。
     */
    private static String getBorderLayoutConstraint(Container parent, Component comp) {
        if (parent.getLayout() instanceof BorderLayout bl) {
            for (String key : new String[]{BorderLayout.NORTH, BorderLayout.SOUTH,
                    BorderLayout.WEST, BorderLayout.CENTER, BorderLayout.EAST}) {
                if (bl.getLayoutComponent(key) == comp) {
                    return key;
                }
            }
        }
        return "?";
    }

    /**
     * 将 Color 转为 #RRGGBB 格式字符串。
     */
    private static String colorToHex(Color color) {
        if (color == null) return "null";
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }
}