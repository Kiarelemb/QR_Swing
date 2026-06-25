package swing.qr.kiarelemb.test;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRTextPane;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.window.basic.QRFrame;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

public class MyFrame extends QRFrame {

    private QRTextPane scaleHelpTextPane;

    public static void main(String[] args) {
        // 本工具包开始于此方法。在所有窗体或控件调用前，都必须先调用该方法
        // 在调用本方法时，将使用默认的设置进行配置，其文件 GLOBAL_PROP_PATH 和 WINDOW_PROP_PATH 将会创建在程序的根目录下
        QRSwing.start();
        // 注册全局键盘事件，以方便对话窗能按 ESC 关闭窗体
        QRSwing.registerGlobalKeyEvents();
        // 实例化主窗体
        MyFrame frame = new MyFrame();
        // 注册全局键盘事件的主窗体
        QRSwing.registerGlobalEventWindow(frame);
        // 显示窗体
        frame.setVisible(true);
    }

    public MyFrame() {
        // 设置窗体标题
        super("QRTextPane 鼠标索引测试");
        // 将窗体标题居中
        setTitleCenter();
        // 设置单击关闭按钮后窗体淡化退出并结束程序
        setCloseButtonSystemExit();
        // 在最开始时，窗体大小和窗体位置是自动计算的，长宽为屏幕的 1/2 ，位置自动据此居中

        mainPanel.setLayout(new BorderLayout());
        this.scaleHelpTextPane = new QRTextPane();
        this.scaleHelpTextPane.setText("""
				尺度算法说明
				
				用途：
				尺度算分根据每道题的全班正确率调整小题分值。它不是简单平均分配满分，而是先把每道题换算成权重，再在每个计分大题内部按权重分配该大题总分。这样可以让试卷中不同难度的题对成绩产生不同影响。
				
				基本流程：
				1. 统计每题正确率 p。
				2. 将 p 限制在 min.p 到 max.p 之间，避免极端正确率导致权重过大或过小。
				3. 按所选函数把 p 转换为权重。
				4. 将权重限制在 min.weight 到 max.weight 之间。
				5. 在每个计分大题内按权重比例分配满分。
				
				函数/算法：
				INVERSE：正确率越低，分值越高；正确率越高，分值越低。适合强调难题。
				
				NEG_LOG：也是难题权重更高，但比 INVERSE 更平滑。
				
				LOGIT_ABS：日常推荐用此算法。它以 center.p 为最低点，正确率越远离 center.p，权重越高。适合把“过易题”和“过难题”都拉开，同时让接近中心正确率的题分值较低。
				
				参数：
				logit.power：控制 LOGIT_ABS 的曲线强度。越大，远离 center.p 的题权重增长越明显，题目分差越大；越小，分差越平缓。使用其他函数时，该参数不作用。
				
				center.p：LOGIT_ABS 的中心正确率，也是权重最低点。默认 0.5。若希望以 40% 正确率为最低点，可设为 0.4。使用其他函数时，该参数不作用。
				
				min.p / max.p：正确率裁剪范围。默认 0.1 到 0.9，可避免 0% 或 100% 这类极端值把权重推得过分。
				
				min.weight / max.weight：权重裁剪范围。用于限制最终权重的最低和最高值，避免小题赋分过小或过大。
				
				epsilon：极小保护值，防止对数、除法出现 0。一般保持默认即可。
				
				调参建议：
				如果想让难题分越高，优先使用 NEG_LOG 或 INVERSE。
				如果想围绕某个正确率形成低点，并强调偏离该正确率的题，使用 LOGIT_ABS。
				如果小题分差过大，降低 logit.power 或 max.weight。
				如果小题分差过小，提高 logit.power 或 max.weight。
				""");

        mainPanel.add(this.scaleHelpTextPane.addInternalScrollPane(), BorderLayout.CENTER);

        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setForeground(attrs, QRColorsAndFonts.DEFAULT_COLOR_LABEL);
        StyleConstants.setFontSize(attrs, 24);
        StyleConstants.setBold(attrs, true);
        scaleHelpTextPane.changeTextStyle("尺度算法说明", attrs);

        SimpleAttributeSet strong = new SimpleAttributeSet();
        StyleConstants.setBold(strong, true);
        StyleConstants.setForeground(strong, QRColorsAndFonts.CARET_COLOR);
        scaleHelpTextPane.changeTextStyle("根据每道题的全班正确率", strong);
        scaleHelpTextPane.changeTextStyle("正确率越低，分值越高", strong);
        scaleHelpTextPane.changeTextStyle("也是难题权重更高", strong);
        scaleHelpTextPane.changeTextStyle("日常推荐用此算法", strong);
        scaleHelpTextPane.changeTextStyle("权重最低点", strong);
        scaleHelpTextPane.changeTextStyle("限制最终权重的最低和最高值", strong);
    }
}