/* オプションウィンドウ */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class OptionWindow extends JFrame implements ActionListener{
	/* メンバ変数 */
	// 定数
	static final int OBJECTX = 80;
	static final int OBJECTY = 24;
	static final int OBJECT_SPACE = 10;
	static final int[] FPS_INT = {0, 1, 2, 3, 5, 10};
	static final String[] FPS_STR = {"無効", "1fps", "2fps", "3fps", "5fps", "10fps"};
	// 変数
	public static int fps;
	public static JCheckBox checkbox1, checkbox2, checkbox3;
	static JComboBox<String> comboBox;
	/* コンストラクタ */
	OptionWindow(){
		// ウィンドウの設定
		setTitle("オプション");
		getContentPane().setPreferredSize(new Dimension(positionX(3), positionY(2)));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setAlwaysOnTop(true);
		// オブジェクトの設定
		JPanel panel = new JPanel();
		panel.setLayout(null);
		checkbox1 = new JCheckBox("罫線表示");
			checkbox1.setBounds(positionX(0), positionY(0), sizeX(1), sizeY(1));
			checkbox1.setMargin(new Insets(0, 0, 0, 0));
			checkbox1.addActionListener(this);
			checkbox1.setActionCommand("罫線表示");
			checkbox1.setSelected(true);
			panel.add(checkbox1);
		checkbox2 = new JCheckBox("自動取得");
			checkbox2.setBounds(positionX(1), positionY(0), sizeX(1), sizeY(1));
			checkbox2.setMargin(new Insets(0, 0, 0, 0));
			checkbox2.addActionListener(this);
			checkbox2.setActionCommand("自動取得");
			panel.add(checkbox2);
		checkbox3 = new JCheckBox("名前隠し");
			checkbox3.setBounds(positionX(2), positionY(0), sizeX(1), sizeY(1));
			checkbox3.setMargin(new Insets(0, 0, 0, 0));
			checkbox3.addActionListener(this);
			checkbox3.setActionCommand("名前隠し");
			checkbox3.setSelected(true);
			panel.add(checkbox3);
		comboBox = new JComboBox<>(FPS_STR);
			comboBox.setBounds(positionX(0), positionY(1), sizeX(1), sizeY(1));
			comboBox.addActionListener(this);
			comboBox.setActionCommand("fps変更");
			panel.add(comboBox);
		getContentPane().add(panel, BorderLayout.CENTER);
		pack();
	}
	/* イベント処理用 */
	public void actionPerformed(ActionEvent event){
		String commandStr = event.getActionCommand();
		if(commandStr == null) return;
		if(commandStr.equals("罫線表示")){
			Nano.sortFrame.panel.repaint();
			Nano.unitFrame.panel.repaint();
		}
		if(commandStr.equals("自動取得")){
			if(checkbox2.isSelected()){
				if(MainWindow.comboBox1.getSelectedIndex() != 0){
					MainWindow.timer.restart();
					MainWindow.timer.setDelay(500);
				}
			}else{
				if(MainWindow.comboBox1.getSelectedIndex() != 0){
					MainWindow.timer.setDelay(1000 * 60 * 60 * 24);
				}
			}
		}
		if(commandStr.equals("fps変更")){
			fps = FPS_INT[comboBox.getSelectedIndex()];
			if(fps != 0){
				MainWindow.putLog("【連射機能】");
				MainWindow.putLog("fps：" + fps + "fps");
				if(MainWindow.comboBox1.getSelectedIndex() == 0){
					MainWindow.timer.restart();
					MainWindow.timer.setDelay(1000 / fps);
				}
			}else{
				MainWindow.putLog("【連射機能】");
				MainWindow.putLog("fps：OFF");
				if(MainWindow.comboBox1.getSelectedIndex() == 0){
					MainWindow.timer.setDelay(1000 * 60 * 60 * 24);
				}
			}
		}
	}
	/* オブジェクト用定数を計算する */
	private static int positionX(int x){
		return OBJECT_SPACE * (x + 1) + OBJECTX * x;
	}
	private static int positionY(int y){
		return OBJECT_SPACE * (y + 1) + OBJECTY * y;
	}
	private static int sizeX(int x){
		return OBJECT_SPACE * (x - 1) + OBJECTX * x;
	}
	private static int sizeY(int y){
		return OBJECT_SPACE * (y - 1) + OBJECTY * y;
	}
}
