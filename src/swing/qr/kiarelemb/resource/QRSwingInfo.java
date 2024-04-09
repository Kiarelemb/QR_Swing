package swing.qr.kiarelemb.resource;

import method.qr.kiarelemb.utils.QRPropertiesUtils;
import swing.qr.kiarelemb.QRSwing;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 资源调取类
 * @create 2022-11-04 15:37
 **/
public class QRSwingInfo {

    /**
     * 加载资源
     *
     * @param fileName 文件名
     * @return url
     */
    public static URL loadUrl(String fileName) {
        return Objects.requireNonNull(QRSwingInfo.class.getResource(fileName));
    }

    /**
     * 加载资源
     *
     * @param fileName 文件名
     * @return url
     */
    public static URI loadURI(String fileName) {
        try {
            return Objects.requireNonNull(QRSwingInfo.class.getResource(fileName)).toURI();
        } catch (URISyntaxException e) {
            return URI.create(".");
        }
    }

    public static Image loadImage(String filePath) {
        File file = new File(filePath);
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException("文件" + file.getName() + "不存在");
        }
    }

    public static BufferedImage loadBufferedImage(String filePath) {
        File file = new File(filePath);
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException("文件" + file.getName() + "不存在");
        }
    }


    /**
     * 从全局默认配置文件中读取配置
     *
     * @param key 键
     * @return 值
     */
    public static String strValue(Map<String, String> defaultMap, String key) {
        String dv = defaultMap.get(key);
        return QRPropertiesUtils.getPropInString(QRSwing.GLOBAL_PROP, key, dv, true);
    }

    /**
     * 从全局默认配置文件中读取配置
     *
     * @param key 键
     * @return 值
     */
    public static int intValue(Map<String, String> defaultMap, String key) {
        String dv = defaultMap.get(key);
        return QRPropertiesUtils.getPropInInteger(QRSwing.GLOBAL_PROP, key, Integer.parseInt(dv), true);
    }

    /**
     * 从全局默认配置文件中读取配置
     *
     * @param key 键
     * @return 值
     */
    public static boolean boolValue(Map<String, String> defaultMap, String key) {
        String dv = defaultMap.get(key);
        return QRPropertiesUtils.getPropInBoolean(QRSwing.GLOBAL_PROP, key, Boolean.parseBoolean(dv), true);
    }

    /**
     * 从全局默认配置文件中读取配置
     *
     * @param key 键
     * @return 值
     */
    public static float floatValue(Map<String, String> defaultMap, String key) {
        String dv = defaultMap.get(key);
        return QRPropertiesUtils.getPropInFloat(QRSwing.GLOBAL_PROP, key, Float.parseFloat(dv), true);
    }
}