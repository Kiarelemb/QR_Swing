package swing.qr.kiarelemb.utils;

import swing.qr.kiarelemb.basic.QRLabel;

/**
 * 显示行列及附加数字的标签。
 *
 * <p>该组件把 line、row 和可选的其他数字用指定分隔符拼接显示，同时保留这些数值供调用方读取。
 * 常用于文本编辑器状态栏，例如 {@code 行:列} 或 {@code line/row/total}。</p>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 这是一个显示行号内容的标签，用设定的符号将多个数字分开
 * @create 2022-11-25 08:00
 **/
public class QRLineAndRowLabel extends QRLabel {
    private final String split;
    private int line;
    private int row;
    private int[] other;

    public QRLineAndRowLabel(String split) {
        this(split, 0, 0);
    }

    public QRLineAndRowLabel(String split, int line, int row, int... others) {
        this.split = split;
        setText(line, row, others);
    }

    /**
     * 更新行列和附加数字。
     *
     * @param line   行号
     * @param row    列号
     * @param others 其他要追加显示的数字
     */
    public void setText(int line, int row, int... others) {
        this.line = line;
        this.row = row;
        this.other = others;
        StringBuilder sb = new StringBuilder(line + split + row);
        if (others != null) {
            for (int i : others) {
                sb.append(split).append(i);
            }
        }
        setText(sb.toString());
    }

    /**
     * @return 分隔符
     */
    public String split() {
        return split;
    }

    /**
     * @return 当前行号
     */
    public int line() {
        return line;
    }

    /**
     * @return 当前列号
     */
    public int row() {
        return row;
    }

    /**
     * @return 附加数字数组
     */
    public int[] other() {
        return other;
    }
}
