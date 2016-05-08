/* オプションウィンドウ */

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class OptionWindow extends JFrame implements ActionListener{
	/* メンバ変数 */
	// 変数
	private JCheckBox checkbox1, checkbox2, checkbox3;
	private JComboBox<FPS> comboBox;
	/* コンストラクタ */
	OptionWindow(OptionData option){
		// ウィンドウの設定
		setTitle("オプション");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setAlwaysOnTop(true);
		
		// オブジェクトの設定
		checkbox1 = new JCheckBox("罫線表示");
		checkbox1.addActionListener(this);
		checkbox1.setActionCommand("罫線表示");
		
		checkbox2 = new JCheckBox("自動取得");
		checkbox2.addActionListener(this);
		checkbox2.setActionCommand("自動取得");
		
		checkbox3 = new JCheckBox("名前隠し");
		checkbox3.addActionListener(this);
		checkbox3.setActionCommand("名前隠し");
		
		comboBox = new JComboBox<>(FPS.values());
		comboBox.addActionListener(option.getFPSAction());
		
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
	/* イベント処理用 */
	public void actionPerformed(ActionEvent event){
//		String commandStr = event.getActionCommand();
//		if(commandStr == null) return;
//		if(commandStr.equals("罫線表示")){
//			// FIXME: 罫線の変更
//			//Nano.sortFrame.panel.repaint();
//			//Nano.unitFrame.panel.repaint();
//		}
//		if(commandStr.equals("自動取得")){
//			if(checkbox2.isSelected()){
//				if(MainWindow.comboBox1.getSelectedIndex() != 0){
//					MainWindow.timer.restart();
//					MainWindow.timer.setDelay(500);
//				}
//			}else{
//				if(MainWindow.comboBox1.getSelectedIndex() != 0){
//					MainWindow.timer.setDelay(1000 * 60 * 60 * 24);
//				}
//			}
//		}
//		if(commandStr.equals("fps変更")){
//			fps = FPS_INT[comboBox.getSelectedIndex()];
//			if(fps != 0){
//				MainWindow.putLog("【連射機能】");
//				MainWindow.putLog("fps：" + fps + "fps");
//				if(MainWindow.comboBox1.getSelectedIndex() == 0){
//					MainWindow.timer.restart();
//					MainWindow.timer.setDelay(1000 / fps);
//				}
//			}else{
//				MainWindow.putLog("【連射機能】");
//				MainWindow.putLog("fps：OFF");
//				if(MainWindow.comboBox1.getSelectedIndex() == 0){
//					MainWindow.timer.setDelay(1000 * 60 * 60 * 24);
//				}
//			}
//		}
	}
}
