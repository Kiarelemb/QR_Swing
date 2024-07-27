package swing.qr.kiarelemb.utils;

import method.qr.kiarelemb.utils.QRFontUtils;
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.inter.QRActionRegister;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-23 13:22
 **/
public class QRComponentUtils {

    /**
     * 向 {@code parent} 中添加 {@code comToAdd}，并设置 {@code comToAdd} 的 {@link JComponent#setBounds(int, int, int, int)}
     *
     * @param parent   添加的父容器
     * @param comToAdd 子控件
     * @param x        子控件位置 x
     * @param y        子控件位置 y
     * @param width    子控件位置宽度
     * @param height   子控件位置高度
     */
    public static void setBoundsAndAddToComponent(JComponent parent, JComponent comToAdd, int x, int y, int width,
                                                  int height) {
        comToAdd.setBounds(x, y, width, height);
        parent.add(comToAdd);
    }

    /**
     * 向 {@code parent} 中添加 {@code comToAdd}，并设置 {@code comToAdd} 的 {@link JComponent#setLocation(int, int)}
     *
     * @param parent   添加的父容器
     * @param comToAdd 子控件
     * @param x        子控件位置 x
     * @param y        子控件位置 y
     */
    public static void setBoundsAndAddToComponent(JComponent parent, JComponent comToAdd, int x, int y) {
        comToAdd.setLocation(x, y);
        parent.add(comToAdd);
    }


    /**
     * 横纵居中绘制文字
     *
     * @param com   控件
     * @param g     工具
     * @param text  内容
     * @param font  字体
     * @param color 前景色
     */
    public static void componentStringDraw(JComponent com, Graphics g, String text, Font font, Color color) {
        float y = com.getHeight() / 1.7f;
        componentStringDraw(com, g, text, font, color, y);
    }

    /**
     * 居中绘制文字
     *
     * @param com   控件
     * @param g     工具
     * @param text  内容
     * @param font  字体
     * @param color 前景色
     * @param y     纵位置
     */
    public static void componentStringDraw(JComponent com, Graphics g, String text, Font font, Color color, float y) {
        Graphics2D g2 = (Graphics2D) g;
        Rectangle2D r = QRFontUtils.getStringBounds(text, font);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                com.getClientProperty(RenderingHints.KEY_TEXT_ANTIALIASING));
        g2.setFont(font);
        g2.setColor(color);
        float x = (float) (com.getWidth() / 2 - r.getWidth() / 2);
        g2.drawString(text, x, y);
    }

    /**
     * 创建并返回一个具有指定字体属性的 {@link SimpleAttributeSet} 对象。
     *
     * @param f         字体
     * @param colorFore 文本前景色，用于设置文本的颜色。
     * @param colorBack 文本背景色，用于设置文本的背景颜色。
     * @return 返回一个具有指定字体属性的 {@link SimpleAttributeSet} 对象。
     */
    public static SimpleAttributeSet getSimpleAttributeSet(Font f, Color colorFore, Color colorBack) {
        return getSimpleAttributeSet(f.getFamily(), f.getSize(), f.getStyle(), colorFore, colorBack);
    }

    /**
     * 创建并返回一个具有指定字体属性的 {@link SimpleAttributeSet} 对象。
     *
     * @param fontFamily 字体家族名称，用于设置文本的字体家族。
     * @param fontSize   字体大小，用于设置文本的字体大小。
     * @param fontStyle  字体样式，可以是 {@link Font#PLAIN}, {@link Font#BOLD}, 或 {@link Font#ITALIC}，用于设置文本的字体样式。
     * @param colorFore  文本前景色，用于设置文本的颜色。
     * @param colorBack  文本背景色，用于设置文本的背景颜色。
     * @return 返回一个具有指定字体属性的 {@link SimpleAttributeSet} 对象。
     */
    public static SimpleAttributeSet getSimpleAttributeSet(String fontFamily, int fontSize, int fontStyle,
                                                           Color colorFore, Color colorBack) {
        // 初始化一个SimpleAttributeSet对象，用于存储字体属性
        SimpleAttributeSet sas = new SimpleAttributeSet();
        // 设置字体名称
        StyleConstants.setFontFamily(sas, fontFamily);
        // 设置字体大小
        StyleConstants.setFontSize(sas, fontSize);
        // 根据fontStyle参数设置字体样式，支持粗体和斜体
        switch (fontStyle) {
            case Font.ITALIC -> StyleConstants.setItalic(sas, true);
            case Font.BOLD -> StyleConstants.setBold(sas, true);
        }
        // 设置文本的前景色
        StyleConstants.setForeground(sas, colorFore);
        // 设置文本的背景色
        StyleConstants.setBackground(sas, colorBack);
        // 返回设置好属性的SimpleAttributeSet对象
        return sas;
    }

    /**
     * 将 {@link QRActionRegister} 列表使用 {@code obj} 参数运行，会检查其内容是否为空
     *
     * @param list 任务列表
     */
    public static <T> void runActions(List<QRActionRegister<T>> list, T obj) {
        if (list == null || list.isEmpty()) {
            return;
        }
        ArrayList<QRActionRegister<T>> temp = new ArrayList<>(list);
        temp.forEach(e -> {
            //确保每个都能完成而不影响之后的事件
            try {
                e.action(obj);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    /**
     * 在GUI的事件调度线程中异步执行一系列动作。
     * 此方法用于将一系列操作推迟到GUI线程中执行，以确保这些操作不会阻塞当前线程，
     * 并且能够正确地与GUI组件交互。
     *
     * @param list 操作注册表列表，包含待执行的操作。
     * @param obj  传递给每个操作的对象，用于执行操作时使用。
     */
    public static <T> void runActionsLater(List<QRActionRegister<T>> list, T obj) {
        // 检查列表是否为空，以避免无意义的线程调度
        if (list == null || list.isEmpty()) {
            return;
        }

        // 创建列表的副本，以避免在异步操作中修改原始列表
        ArrayList<QRActionRegister<T>> temp = new ArrayList<>(list);
        // 使用SwingUtilities的invokeLater方法将操作推迟到GUI线程中执行
        SwingUtilities.invokeLater(() -> {
            // 遍历副本列表中的每个操作，并异步执行
            temp.forEach(e -> {
                // 尝试执行操作，并捕获任何异常，以防止线程中断
                // 确保每个都能完成而不影响之后的事件
                try {
                    e.action(obj);
                } catch (Exception ex) {
                    // 将异常转换为运行时异常，以在调用栈中抛出
                    throw new RuntimeException(ex);
                }
            });
        });
    }

    /**
     * 如果设置背景图片，调用此方法。窗体刷新会延迟 30 毫秒
     *
     * @param com 窗体内的一控件
     */
    public static void windowFresh(JComponent com) {
        if (com == null) {
            return;
        }
        runLater(30L, e -> windowFreshRightNow(com));
    }

    /**
     * 如果设置背景图片，调用此方法
     *
     * @param com 窗体内的一控件
     */
    public static void windowFreshRightNow(JComponent com) {
        Window w = SwingUtilities.getWindowAncestor(com);
        if (w != null) {
            w.repaint();
        }
    }

    /**
     * 使用定时器延迟执行指定动作。
     * <p>
     * 本方法通过创建一个Timer对象，并安排一个TimerTask在指定的延迟后执行。
     * 这种延迟执行的机制适用于那些不需要立即执行但又需要在特定时间点之后执行的操作。
     *
     * @param millis 延迟执行的毫秒数，从现在开始计时。
     * @param e      注册的操作接口，包含待执行的具体操作。该操作的参数是 {@code null}
     * @see #runLater(long, QRActionRegister, Object)
     */
    public static void runLater(long millis, QRActionRegister<Object> e) {
        runLater(millis, e, null);
    }


    /**
     * 使用定时器延迟执行指定动作。
     * <p>
     * 本方法通过创建一个Timer对象，并安排一个TimerTask在指定的延迟后执行。
     * 这种延迟执行的机制适用于那些不需要立即执行但又需要在特定时间点之后执行的操作。
     *
     * @param millis 延迟执行的毫秒数，从现在开始计时。
     * @param e      注册的操作接口，包含待执行的具体操作。
     * @param param  传递给动作的参数，用于在动作执行时提供必要的数据。
     */
    public static <T> void runLater(long millis, QRActionRegister<T> e, T param) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                e.action(param);
            }
        }, millis);
    }

    public static void componentLoopToSetOpaque(JComponent com, boolean opaque){
        QRActionRegister<Component> action = e -> {
            if (e instanceof JComponent jComponent) {
                jComponent.setOpaque(opaque);
            }
        };
        QRActionRegister<Component> panel = new QRActionRegister<Component>() {
            @Override
            public void action(Component e) {
                if (e instanceof JComponent jComponent) {
                    action.action(jComponent);
                    if (jComponent.getComponentCount() > 0) {
                        QRComponentUtils.componentLoop(jComponent, JComponent.class, this, action);
                    }
                }
            }
        };
        QRComponentUtils.componentLoop(com, QRPanel.class, panel, action);
    }

    /**
     * 遍历组件数组，对每个组件执行不同的操作取决于它是否是 {@code aClass} 的实例。
     *
     * @param jc            容器面板，从中获取组件数组进行遍历。
     * @param aClass        要匹配的类，用于判断组件是否属于该类，该类是 {@link JComponent} 的子类
     * @param isClassAction 可为 {@code null}，如果组件是 {@code aClass} 的实例，将调用此操作接口，该操作参数是对应的实例 {@link Component}
     */
    public static <T> void componentLoop(JComponent jc, Class<T> aClass, QRActionRegister<T> isClassAction) {
        // 获取控件里的所有组件
        Component[] components = jc.getComponents();
        // 遍历所有组件
        for (Component com : components) {
            // 判断组件是否为 aClass 的实例
            if (aClass.isInstance(com)) {
                // 如果是指定 aClass 的实例，调用 isClassAction
                if (isClassAction != null) {
                    isClassAction.action((T) com);
                }
            }
        }
    }

    /**
     * 遍历组件数组，对每个组件执行不同的操作取决于它是否是 {@code aClass} 的实例。
     *
     * @param jc            容器面板，从中获取组件数组进行遍历。
     * @param aClass        要匹配的类，用于判断组件是否属于该类，该类是 {@link JComponent} 的子类
     * @param isClassAction 可为 {@code null}，如果组件是 {@code aClass} 的实例，将调用此操作接口，该操作参数是对应的实例 {@link Component}
     * @param elseAction    可为 {@code null}，如果组件不是 {@code aClass} 的实例，将调用此操作接口，该操作参数是对应的实例 {@link Component}
     */
    public static void componentLoop(JComponent jc, Class<?> aClass, QRActionRegister<Component> isClassAction, QRActionRegister<Component> elseAction) {
        // 获取控件里的所有组件
        Component[] components = jc.getComponents();
        // 遍历所有组件
        for (Component com : components) {
            // 判断组件是否为 aClass 的实例
            if (aClass.isInstance(com)) {
                // 如果是指定 aClass 的实例，调用 isClassAction
                if (isClassAction != null) {
                    isClassAction.action(com);
                }
            } else {
                // 如果不是 aClass 的实例，调用 elseAction
                if (elseAction != null) {
                    elseAction.action(com);
                }
            }
        }
    }


    /**
     * 设置颜色的透明度（alpha 值）。
     * <p>
     * 该方法通过接收一个已有颜色对象和一个新的透明度值，创建并返回一个新的颜色对象。
     * 新颜色对象的RGB值与输入颜色对象相同，但透明度（alpha）被更新为指定的新值。
     * 透明度值被限制在0到255之间，以确保颜色值在整数范围内，适合颜色对象的构造函数。
     *
     * @param color 原始颜色对象，其RGB值将被新颜色对象继承。
     * @param alpha 新的透明度值，范围为0.0（完全透明）到1.0（完全不透明）。
     * @return 一个新的颜色对象，具有指定的透明度。
     */
    public static Color setColorAlpha(Color color, float alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255));
    }

}