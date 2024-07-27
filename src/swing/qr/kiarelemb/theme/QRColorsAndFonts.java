package swing.qr.kiarelemb.theme;

import method.qr.kiarelemb.utils.*;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.resource.QRSwingInfo;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 颜色和字体库
 * @create 2022-11-04 15:24
 **/
public class QRColorsAndFonts {
    public static final String[] BASIC_THEMES = {"深色", "浅色", "粉色", "棕色", "灰色"};
    /**
     * 主题文件的扩展名
     */
    public static final String THEME_FILE_EXTENSION = ".qr.th";
    public static final Font PROCESS_BUTTON_FONT = QRFontUtils.loadFontFromURL(QRSwingInfo.loadUrl("seguisym.ttf"));
    /**
     * 默认字体为 微软雅黑，可通过 {@link QRSwing#customFontName(String)} 自定义字体
     */
    public static Font MENU_ITEM_DEFAULT_FONT = QRFontUtils.getFontInSize(15);
    public static Font DEFAULT_FONT_MENU = QRFontUtils.getFontInSize(16);
    public static Font STANDARD_FONT_TEXT = QRFontUtils.getFontInSize(17);
    /**
     * 按钮按下的颜色
     */
    public static final Color BLUE_LIGHT = new Color(65, 165, 238);
    /**
     * 标签下边线颜色
     */
    public static final Color DEFAULT_COLOR_LABEL = new Color(238, 119, 98);
    public static final Color DIALOG_COLOR_BACK = new Color(37, 37, 37);
    public static final Color GRAY_DARK = new Color(77, 77, 77);
    public static final Color GRAY_LIGHT = new Color(100, 100, 100);
    public static final Color LINE_NUMBER_BACK = new Color(40, 40, 40);
    public static final Color DARK_LIGHT = new Color(190, 190, 190);
    public static final Color LIGHT_GREEN = new Color(13, 255, 122);
    public static final Color POPUP_COLOR_BACK = new Color(50, 50, 50);
    public static final Color RED_NORMAL = new Color(220, 50, 50);
    /**
     * 光标颜色
     */
    public static Color CARET_COLOR = new Color(252, 229, 102);
    public static Color SENIOR_RANDOM_COLOR_BACK = new Color(30, 30, 30);
    /**
     * 分割线的颜色
     */
    public static Color LINE_COLOR = new Color(50, 50, 50);
    /**
     * 边框色
     */
    public static Color BORDER_COLOR = new Color(110, 110, 110);
    /**
     * 打对字前景色
     */
    public static Color CORRECT_COLOR_FORE = new Color(90, 90, 90);
    /**
     * 打对字背景色
     */
    public static Color CORRECT_COLOR_BACK = GRAY_DARK;
    /**
     * 窗体背景色
     */
    public static Color FRAME_COLOR_BACK = new Color(35, 35, 35);
    /**
     * 字体前景色
     */
    public static Color TEXT_COLOR_FORE = new Color(235, 235, 235);
    /**
     * 文本面板背景色
     */
    public static Color TEXT_COLOR_BACK = new Color(35, 35, 35);
    /**
     * 按钮进入的颜色
     */
    public static Color ENTER_COLOR = new Color(63, 63, 63);
    /**
     * 按钮按下的颜色
     */
    public static Color PRESS_COLOR = new Color(0, 122, 204);
    /**
     * 滚动条的颜色
     */
    public static Color SCROLL_COLOR = new Color(66, 66, 66);
    /**
     * 菜单的颜色，默认与TEXT_COLOR_FORE一致
     */
    public static Color MENU_COLOR = new Color(235, 235, 235);
    public static String[] COLOR_ATTRIBUTES = new String[]{"TEXT_COLOR_FORE", "TEXT_COLOR_BACK", "CORRECT_COLOR_FORE", "CORRECT_COLOR_BACK", "SENIOR_RANDOM_COLOR_BACK", "FRAME_COLOR_BACK", "BORDER_COLOR", "ENTER_COLOR", "PRESS_COLOR", "LINE_COLOR", "SCROLL_COLOR", "MENU_COLOR", "CARET_COLOR"};
    /**
     * 所有主题
     */
    public static String[] THEME_CLASS;

    /**
     * 加载主题
     */
    public static void loadTheme() {
        Color[] colors = getThemeColors(QRSwing.theme);
        loadColors(colors);
        UIManager.put("ToolTip.background", FRAME_COLOR_BACK);
        UIManager.put("ToolTip.foreground", TEXT_COLOR_FORE);

        LinkedList<String> themes = new LinkedList<>(Arrays.asList(BASIC_THEMES));
        File themeDir = new File(QRSwing.THEME_DIRECTORY);
        final File[] files = themeDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (isThemeFile(file)) {
                    themes.add(getThemeFileName(file));
                }
            }
        }
        THEME_CLASS = themes.toArray(QRStringUtils.ARR_EMPTY);
    }

    public static void loadColors(Color[] colors) {
        if (colors != null) {
            int index = 0;
            TEXT_COLOR_FORE = colors[index++];
            TEXT_COLOR_BACK = colors[index++];
            CORRECT_COLOR_FORE = colors[index++];
            CORRECT_COLOR_BACK = colors[index++];
            SENIOR_RANDOM_COLOR_BACK = colors[index++];
            FRAME_COLOR_BACK = colors[index++];
            BORDER_COLOR = colors[index++];
            ENTER_COLOR = colors[index++];
            PRESS_COLOR = colors[index++];
            LINE_COLOR = colors[index++];
            SCROLL_COLOR = colors[index++];
            MENU_COLOR = colors[index++];
            CARET_COLOR = colors[index];
        } else {
            QRSwing.setTheme(BASIC_THEMES[0]);
        }
    }

    public static Color[] getThemeColors(String themeName) {

        LinkedList<String> themes = new LinkedList<>(Arrays.asList(BASIC_THEMES));
        //基础主题
        Color[] colors = new Color[13];
        if (themes.contains(themeName)) {
            int index = 0;
            switch (themeName) {
                case "浅色":
                    colors[index++] = new Color(30, 30, 30);
                    colors[index++] = new Color(245, 245, 245);
                    colors[index++] = new Color(220, 220, 220);
                    colors[index++] = new Color(205, 205, 205);
                    colors[index++] = new Color(220, 220, 220);
                    colors[index++] = new Color(245, 245, 245);
                    colors[index++] = new Color(192, 192, 192);
                    colors[index++] = new Color(200, 200, 200);
                    colors[index++] = new Color(0, 122, 204);
                    colors[index++] = new Color(194, 194, 194);
                    colors[index++] = new Color(215, 213, 213);
                    colors[index++] = new Color(30, 30, 30);
                    colors[index] = new Color(0, 200, 0);
                    return colors;
                case "灰色":
                    colors[index++] = new Color(30, 30, 30);
                    colors[index++] = new Color(195, 195, 195);
                    colors[index++] = new Color(90, 90, 90);
                    colors[index++] = GRAY_DARK;
                    colors[index++] = new Color(100, 100, 100);
                    colors[index++] = new Color(215, 215, 215);
                    colors[index++] = new Color(102, 102, 102);
                    colors[index++] = new Color(117, 117, 117);
                    colors[index++] = new Color(0, 122, 204);
                    colors[index++] = new Color(161, 161, 161);
                    colors[index++] = new Color(122, 122, 122);
                    colors[index++] = new Color(30, 30, 30);
                    colors[index] = new Color(252, 229, 102);
                    return colors;
                case "粉色":
                    colors[index++] = new Color(245, 245, 245);
                    colors[index++] = new Color(250, 136, 175);
                    colors[index++] = new Color(253, 104, 151);
                    colors[index++] = new Color(253, 66, 125);
                    colors[index++] = new Color(245, 117, 161);
                    colors[index++] = new Color(254, 103, 154);
                    colors[index++] = new Color(254, 171, 215);
                    colors[index++] = new Color(255, 154, 212);
                    colors[index++] = new Color(255, 60, 171);
                    colors[index++] = new Color(225, 91, 136);
                    colors[index++] = new Color(243, 97, 146);
                    colors[index++] = new Color(245, 245, 245);
                    colors[index] = new Color(252, 229, 102);
                    return colors;
                case "棕色":
                    colors[index++] = new Color(30, 30, 30);
                    colors[index++] = new Color(238, 221, 187);
                    colors[index++] = new Color(190, 177, 150);
                    colors[index++] = new Color(208, 193, 163);
                    colors[index++] = new Color(45, 45, 45);
                    colors[index++] = new Color(90, 90, 90);
                    colors[index++] = new Color(120, 120, 120);
                    colors[index++] = new Color(140, 140, 140);
                    colors[index++] = new Color(0, 122, 204);
                    colors[index++] = new Color(50, 50, 50);
                    colors[index++] = new Color(122, 122, 122);
                    colors[index++] = new Color(30, 30, 30);
                    colors[index] = new Color(252, 229, 102);
                    return colors;
                case "深色":
                    colors[index++] = new Color(235, 235, 235);
                    colors[index++] = new Color(35, 35, 35);
                    colors[index++] = new Color(100, 100, 100);
                    colors[index++] = GRAY_DARK;
                    colors[index++] = new Color(30, 30, 30);
                    colors[index++] = new Color(35, 35, 35);
                    colors[index++] = new Color(110, 110, 110);
                    colors[index++] = new Color(63, 63, 63);
                    colors[index++] = new Color(0, 122, 204);
                    colors[index++] = new Color(50, 50, 50);
                    colors[index++] = new Color(66, 66, 66);
                    colors[index++] = new Color(235, 235, 235);
                    colors[index] = new Color(252, 229, 102);
                    return colors;
            }
        } else {
            File themeDir = new File(QRSwing.THEME_DIRECTORY);
            final File[] files = themeDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (themeName.equals(getThemeFileName(file)) && isThemeFile(file)) {
                        final LinkedList<String> list = QRFileUtils.fileReaderWithUtf8(file.getAbsolutePath());
                        final int length = COLOR_ATTRIBUTES.length;
                        Map<String, Color> attr = new HashMap<>(length);
                        for (String l : list) {
                            if (l.isBlank() || l.startsWith("#")) {
                                continue;
                            }
                            String lineText = QRCodePack.decrypt(l, "theme");
                            final String[] split = lineText.split("=");
                            attr.put(split[0], parseColor(split[1], ','));
                        }
                        for (int i = 0; i < length; i++) {
                            colors[i] = attr.get(COLOR_ATTRIBUTES[i]);
                        }
                        return colors;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 取得主题文件的文件名
     */
    public static String getThemeFileName(File f) {
        final String name = f.getName();
        final int extension = name.lastIndexOf(THEME_FILE_EXTENSION);
        if (extension == -1) {
            return "晚星";
        }
        return name.substring(0, extension);
    }

    /**
     * 判断该文件是否为主题文件
     *
     * @param f 文件
     * @return 是否为主题文件
     */
    public static boolean isThemeFile(File f) {
        final String name = f.getName();
        if (!name.endsWith(THEME_FILE_EXTENSION)) {
            return false;
        }
        Set<String> attributesSet = new HashSet<>(Arrays.asList(COLOR_ATTRIBUTES));
        final LinkedList<String> list = QRFileUtils.fileReaderWithUtf8(f.getAbsolutePath());
        boolean flag = true;
        for (String l : list) {
            if (l.isBlank() || l.startsWith("#")) {
                continue;
            }
            String lineText;
            try {
                lineText = QRCodePack.decrypt(l, "theme");
                if (lineText == null) {
                    throw new NullPointerException("读取失败！");
                }
            } catch (Exception e) {
                flag = false;
                break;
            }
            if (QRStringUtils.findOnlyOnce(lineText, "=") && lineText.charAt(0) != '=') {
                final String[] split = lineText.split("=");
                if (!attributesSet.contains(split[0])) {
                    flag = false;
                    break;
                }
                try {
                    parseColor(split[1], ',');
                    attributesSet.remove(split[0]);
                } catch (Exception e) {
                    flag = false;
                    break;
                }
            }
        }
        return flag && attributesSet.size() == 0;
    }

    /**
     * 将用指定分割符分开的rgb色值字符串转为 Color
     *
     * @param rgb       rgb色值
     * @param seperator 分割符
     * @return Color类
     */
    private static Color parseColor(String rgb, char seperator) {
        int[] values = QRArrayUtils.splitToInt(rgb, seperator);
        return new Color(values[0], values[1], values[2]);
    }

    /**
     * 主题名称转换
     *
     * @param englishTopic 主题名
     * @return 转为中文
     */
    public static String topicEnglishToChinese(String englishTopic) {
        return switch (englishTopic) {
            case "gray" -> "灰色";
            case "light" -> "浅色";
            case "pink" -> "粉色";
            case "brown" -> "棕色";
            case "dark" -> "深色";
            default -> englishTopic;
        };
    }

    /**
     * 主题名称转换
     *
     * @param chineseTopic 主题名
     * @return 转为英文
     */
    public static String topicChineseToEnglish(String chineseTopic) {
        return switch (chineseTopic) {
            case "灰色" -> "gray";
            case "浅色" -> "light";
            case "粉色" -> "pink";
            case "棕色" -> "brown";
            case "深色" -> "dark";
            default -> chineseTopic;
        };
    }
}