/* オプションウィンドウ */

import java.awt.FlowLayout;
import java.util.function.Consumer;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class OptionWindow extends JFrame{
	/* メンバ変数 */
	// 変数
	private JCheckBox checkbox1, checkbox2, checkbox3;
	private JComboBox<FPS> comboBox;
	/* コンストラクタ */
	OptionWindow(Consumer<FPS> action){
		// ウィンドウの設定
		setTitle("オプション");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setAlwaysOnTop(true);
		
		// オブジェクトの設定
		checkbox1 = new JCheckBox("罫線表示");
		checkbox1.addActionListener(event->OptionData.getData().setDrawFrame(checkbox1.isSelected()));
		
		checkbox2 = new JCheckBox("自動取得");
		checkbox2.addActionListener(event->OptionData.getData().setAuto(checkbox2.isSelected()));
		
		checkbox3 = new JCheckBox("名前隠し");
		checkbox3.addActionListener(event->OptionData.getData().setDisableName(checkbox3.isSelected()));
		
		comboBox = new JComboBox<>(FPS.values());
		comboBox.addActionListener(event->{
			OptionData.getData().setFPS((FPS) comboBox.getItemAt(comboBox.getSelectedIndex()));
			action.accept(OptionData.getData().getFPS());
		});
		
		// オブジェクトの配置
		getContentPane().setLayout(new FlowLayout(10, 10, 10));
		getContentPane().add(checkbox1);
		getContentPane().add(checkbox2);
		getContentPane().add(checkbox3);
		getContentPane().add(comboBox);
		checkbox1.setSelected(true);
		checkbox3.setSelected(true);
		pack();
	}
}
