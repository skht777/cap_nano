/* メインウィンドウ */

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.util.Optional;
import java.util.stream.Stream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class MainWindow extends JFrame implements Capturable{
	/* メンバ変数 */
	// 変数
	private JComboBox<ModeType> comboBox1;
	private JComboBox<SaveType> comboBox2;
	private JComboBox<SortType> joinSortCombo; 
	private JComboBox<SizeType> joinSizeCombo;
	private JoinWindow visible;
	private JTextArea textArea;
	private Timer timer;
	/* コンストラクタ */
	MainWindow(){
		// ウィンドウの設定
		setTitle("記録は大切なの");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setAlwaysOnTop(true);
		
		// オブジェクトの設定
		JButton button1 = new JButton("座標取得");
		button1.addActionListener(event->Capture.getKancollePosition());
		
		comboBox1 = new JComboBox<>(ModeType.values());
		comboBox1.addActionListener(event->changeMode());
		
		JButton button2 = new JButton("画像追加");
		button2.addActionListener(event->Optional.ofNullable(visible).ifPresent(v->v.addImage(Capture.getImage())));
		button2.setMnemonic(KeyEvent.VK_Z);
		
		JButton button3 = new JButton("画像保存");
		button3.addActionListener(event->(visible == null ? this : visible).savePicture());
		
		comboBox2 = new JComboBox<>(SaveType.values());
		comboBox2.setActionCommand("保存種別");
		
		joinSortCombo = new JComboBox<>();
		joinSortCombo.addActionListener(event->visible.setSortType(getItem(joinSortCombo)));
		
		joinSizeCombo = new JComboBox<>();
		joinSizeCombo.addActionListener(event->visible.setSizeType(getItem(joinSizeCombo)));
		
		JButton button4 = new JButton("オプション");
		button4.addActionListener(e->new OptionWindow(fps->{
			if(ModeType.MAIN.equals(getItem(comboBox1))){
				timer.restart();
				timer.setDelay(fps.getDelay());
			}
		}).setVisible(true));
		button2.setEnabled(false);
		textArea = new JTextArea();
		textArea.setRows(4);
		textArea.setEditable(false);
		// ロガーのセット
		LogManager.setLogger(message -> textArea.append(message+"\n"));
		
		// オブジェクトの配置
		getContentPane().setLayout(new BorderLayout(10, 10));
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 4, 10, 10));
		panel.add(button1);
		panel.add(comboBox1);
		panel.add(button2);
		panel.add(button3);
		panel.add(comboBox2);
		panel.add(joinSortCombo);
		panel.add(joinSizeCombo);
		panel.add(button4);
		
		getContentPane().add(panel, BorderLayout.NORTH);
		getContentPane().add(new JScrollPane(textArea), BorderLayout.SOUTH);
		pack();
		// タイマーイベントの処理
		timer = new Timer(500, event->{
			if(ModeType.MAIN.equals(getItem(comboBox1)) && OptionData.getData().isAuto()) visible.addImageX(Capture.getImage());
			else if(ModeType.UNIT.equals(getItem(comboBox1)) && !FPS.DISABLE.equals(OptionData.getData().getFPS())) savePicture();
		});
		reset();
	}
	private <T> T getItem(JComboBox<T> comboBox){return comboBox.getItemAt(comboBox.getSelectedIndex());}
	private void reset(){
		Optional.ofNullable(visible).ifPresent(window->window.setVisible(false));
		visible = null;
		comboBox2.setEnabled(true);
		Stream.of(joinSortCombo, joinSizeCombo).forEach(comboBox->{
			comboBox.setModel(new DefaultComboBoxModel<>());
			comboBox.setEnabled(false);
		});
		timer.restart();
		timer.setDelay(OptionData.getData().getFPS().getDelay());
	}
	private void changeMode(){
		reset();
		if(ModeType.MAIN.equals(getItem(comboBox1))) return;
		timer.setDelay(FPS.TWO.getDelay());
		joinSortCombo.setEnabled(true);
		joinSizeCombo.setEnabled(true);
		joinSortCombo.setModel(new DefaultComboBoxModel<>(getItem(comboBox1).getSortList()));
		joinSizeCombo.setModel(new DefaultComboBoxModel<>(getItem(comboBox1).getSizeList()));
		visible = new JoinWindow(getItem(comboBox1).getOption(), joinSortCombo.getItemAt(0), joinSizeCombo.getItemAt(0));
		visible.setVisible(true);
	}
	/* 画像保存 */
	@Override
	public void savePicture(){
		if(Capture.displayIndex < 0) return;
		Optional.ofNullable(Capture.getImage())
		.ifPresent(image->Capturable.savePicture(getItem(comboBox2).apply(image)));
	}
}
