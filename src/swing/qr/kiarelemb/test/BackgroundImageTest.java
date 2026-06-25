package swing.qr.kiarelemb.test;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.*;
import swing.qr.kiarelemb.listener.QRMouseListener;
import swing.qr.kiarelemb.utils.QRComponentUtils;
import swing.qr.kiarelemb.window.basic.QRDialog;
import swing.qr.kiarelemb.window.basic.QRFrame;

import java.awt.event.WindowEvent;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className BackgroundImageTest
 * @description TODO
 * @create 2024/7/14 下午12:11
 */
public class BackgroundImageTest extends QRFrame {


    public static void main(String[] args) {
        QRSwing.start("setting.properties");
        QRSwing.setWindowTitleMenu(true);

        QRSwing.setWindowRound(false);
        QRSwing.registerGlobalKeyEvents();
        BackgroundImageTest window = new BackgroundImageTest();
        window.setBackgroundImage("/home/kylan/图片/背景图.png");
        //设置窗体可见
        window.setVisible(true);
    }

    private final QRTextPane area;

    private BackgroundImageTest() {
        setTitle("选择背景图片");
        setTitlePlace(QRDialog.CENTER);
        setCloseButtonSystemExit();
        setSize(740, 475);
        setBackgroundImage(null);

        this.mainPanel.setLayout(null);
        this.mainPanel.addMouseListener();
        this.mainPanel.addMouseAction(QRMouseListener.TYPE.CLICK, e -> BackgroundImageTest.this.mainPanel.grabFocus());

        QRSlider alphaSlider = getAlphaSlider();
        alphaSlider.addChangeListener(e -> {
            setBackgroundImageAlpha(alphaSlider.getValue() / 100f);
        });
        QRButton btn = new QRButton("测试使用");

        area = new QRTextPane();
        area.setOpaque(false);
        area.setLineSpacing(0.8f);
        QRScrollPane scrollPane = area.addScrollPane();

        QRRoundButton rbtn = new QRRoundButton("测试使用");
        QRComponentUtils.setBoundsAndAddToComponent(this.mainPanel, scrollPane, 36, 10, 575, 343);
        QRComponentUtils.setBoundsAndAddToComponent(this.mainPanel, alphaSlider, 405, 360, 200, 40);
        QRComponentUtils.setBoundsAndAddToComponent(this.mainPanel, btn, 20, 360, 100, 30);
        QRComponentUtils.setBoundsAndAddToComponent(this.mainPanel, rbtn, 120, 360, 100, 30);
    }


    @Override
    public void windowOpened(WindowEvent e) {
        area.print("可以用一块糖和好，被亲爹揍得喊娘一拿到零花钱就笑，暗恋的男生想追班花还大方地帮他写情书，失恋了考砸了毕个业所有人都走了就哭一鼻子没什么大不了。可现在做不到了，再没办法相信伤害过自己的人，再承受不起任何形式的离开，连哭都哭得没底气，怕吵醒人。好多时候我们都是被动的，甚至解释都是无用的，只能悄悄接受，暗暗忍受。让时间慢慢平息淡化吧，相信都会理解的。人生就是一次无悔的经历，告诉我们无需后悔，无需遗憾。很多的时候我们失望，怨恨，伤痛，就是缘于常常遗憾，每每后悔。人生活是一张千疮百孔的网，所谓的同学聚会，就是在多年以后给所有到场的人一个机会，看看什么叫沧海桑田，看看什么叫岁月如刀，看看什么叫物是人非。我多的时候我们失望，怨恨，伤痛，就是缘于常常遗憾，每每后悔。人生活是一张千疮百孔的网，所谓的同学聚会，就是在多年以后给所有到场的人一个机会，看看什么叫沧海桑田，看看什么叫岁月如刀，看看什么叫物是人非。我多的时候我们失望，怨恨，伤痛，就是缘于常常遗憾，每每后悔。人生活是一张千疮百孔的网，所谓的同学聚会，就是在多年以后给所有到场的人一个机会，看看什么叫沧海桑田，看看什么叫岁月如刀，看看什么叫物是人非。我多的时候我们失望，怨恨，伤痛，就是缘于常常遗憾，每每后悔。人生活是一张千疮百孔的网，所谓的同学聚会，就是在多年以后给所有到场的人一个机会，看看什么叫沧海桑田，看看什么叫岁月如刀，看看什么叫物是人非。我在工作或学习中遇到不开心的时候，不妨静下来好好想想，自己到底是对是错。生活中不是你对别人好，别人就该对你好，你要明白这个道理，每个人都有自己的原则，有人功利，有人善良，你不可能要求别人什么。有时间的话，不妨到处走走，在雄伟的高山之间，放声大喊，一吐心中的阴郁，在浪漫的大海之间，看潮起潮落，感悟人生的起伏跌宕，在落日余晖中感受天地的宁静，洗涤心中的贪念。");
    }

    private QRSlider getAlphaSlider() {
        QRSlider alphaSlider = new QRSlider();
        alphaSlider.setBoundValue(50, 95);
        alphaSlider.setValue((int) (100 - 100 * QRSwing.windowBackgroundImageAlpha));
        return alphaSlider;
    }
}