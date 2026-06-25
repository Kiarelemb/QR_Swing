package swing.qr.kiarelemb.test;

import method.qr.kiarelemb.utils.QRFileUtils;
import method.qr.kiarelemb.utils.QRStringUtils;
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.inter.QRActionRegister;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className Test
 * @description TODO
 * @create 2024/7/25 下午7:46
 */
public class Test {
    static LinkedList<String> list = new LinkedList<>();
    static TreeSet<String> classSet = new TreeSet<>();

    static String dir = "/home/kylan/IdeaProjects/ui-automation/src/main/java/";
    static String path = dir + "mmarquee/demo/NTQQAutomation.java";
    static String prefix = "import mmarquee";
    static String packageName = "package ";
    static String extendsStr = "extends";
    static AtomicReference<String> packagePath = new AtomicReference<>("");

    public static void main(String[] args) {

        QRPanel panel = new QRPanel();
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // do something...
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // do something...
            }
        });


        QRFileUtils.fileReaderWithUtf8(path, (Test::lineTextProcess));
        var action = new LinkedListQRActionRegister(dir, prefix);
        action.action(new LinkedList<>(list));
    }


    private static void lineTextProcess(String lineText) {
        lineText = lineText.trim();
        if (lineText.contains("extends Container")) {
            System.out.println();
        }
        if (lineText.startsWith(packageName)) {
            packagePath.set(lineText.substring(packageName.length()).replace(";", ".").trim());
        }
        if (lineText.startsWith("//") || lineText.startsWith("/*") || lineText.startsWith("*")) {
            return;
        }
        if (lineText.startsWith(prefix)) {
            var classPath = lineText.substring(7).replace(";", "");
            if (classSet.add(classPath)) {
                list.add(classPath.replaceAll("\\.", "/"));
            }
        }
        var imStr = "implements";
        var i = lineText.indexOf(imStr);
        var anImplements = lineText.contains(imStr);
        var markEnds = lineText.endsWith("{");
        if ((lineText.contains(extendsStr) && lineText.contains("class")) || lineText.startsWith(extendsStr)) {
            int e = lineText.indexOf(extendsStr) + extendsStr.length() + 1;
            var chars = lineText.substring(e).toCharArray();
            StringBuilder sb = new StringBuilder();
            for (char c : chars) {
                if (QRStringUtils.isAlphabet(c)) {
                    sb.append(c);
                } else {
                    break;
                }
            }
            var rest = sb.toString();
            if (!suffixContains(list, rest)) {
                var superClassPath = packagePath.get() + rest;
                if (classSet.add(superClassPath)) {
                    list.add(superClassPath);
                }
            }
        }
        if (anImplements) {
            var rest = lineText.substring(i + imStr.length(), markEnds ? lineText.length() - 1 : lineText.length());
            var parts = rest.split(",");
            for (String part : parts) {
                var className = part.trim();
                if (!suffixContains(list, className)) {
                    var superClassPath = packagePath.get() + className;
                    if (classSet.add(superClassPath)) {
                        list.add(superClassPath);
                    }
                }
            }
        }
    }

    private static boolean suffixContains(LinkedList<String> list, String suffix) {
        for (String s : list) {
            if (s.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    private record LinkedListQRActionRegister(String dir,
                                              String prefix) implements QRActionRegister<LinkedList<String>> {
        @Override
        public void action(LinkedList<String> lists) {
            list.clear();
            lists.forEach(s -> {
                var filePath = dir + s.replaceAll("\\.", "/").replace(';', '.');
                var p = filePath + ".java";
                if (QRFileUtils.fileExists(p)) {
                    QRFileUtils.fileReaderWithUtf8(p, (Test::lineTextProcess));
                } else {
                    System.out.println("不存在：" + p);
                }
            });
            if (!list.isEmpty()) {
                action(new LinkedList<>(list));
            } else {
                classSet.forEach(System.out::println);
                findEnd();
            }
        }

        private void findEnd() {
            classSet.forEach(path -> {
                var filePath = dir + path.replaceAll("\\.", "/") + ".java";
                list.add(filePath);
            });
            QRFileUtils.dirLoop(dir, file -> {
                var absolutePath = file.getAbsolutePath();
                boolean isFile = !file.isDirectory();
                if (!list.contains(absolutePath) && isFile) {
                    if (file.delete()) {
                        System.out.println("删除：" + absolutePath);
                    }
                } else if (isFile) {
                    System.out.println("留下：" + absolutePath);
                }
            });
        }
    }
}