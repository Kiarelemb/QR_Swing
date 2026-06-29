package swing.qr.kiarelemb.assembly;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 带选中索引读取能力的按钮组。
 *
 * <p>该类继承 Swing {@link ButtonGroup}，在每次 {@link #add(AbstractButton)} 时记录按钮加入顺序，
 * 之后可通过 {@link #getSelectedIndex()} 取得当前选中按钮的索引。适合一组选项需要直接映射到配置值、
 * 枚举下标或数组下标的场景。</p>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2022-11-21 18:37
 **/
public class QRButtonGroup extends ButtonGroup {

    Map<AbstractButton, Integer> data = new HashMap<>();
    private int nextIndex = 0;

    @Override
    public void add(AbstractButton b) {
        super.add(b);
        data.put(b, nextIndex++);
    }

    /**
     * 返回当前选中按钮的加入顺序索引。
     *
     * <p>如果没有任何按钮被选中，当前实现返回最后一个加入按钮的索引。</p>
     *
     * @return 当前选中按钮索引
     */
    public int getSelectedIndex() {
        final Set<AbstractButton> abstractButtons = data.keySet();
        for (AbstractButton abstractButton : abstractButtons) {
            if (abstractButton.isSelected()) {
                return data.get(abstractButton);
            }
        }
        return nextIndex - 1;
    }
}
