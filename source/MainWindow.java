/* メインウィンドウ */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class MainWindow extends JFrame implements ActionListener{
	/* メンバ変数 */
	// 定数
//	static final int OBJECT_X = 80;
//	static final int OBJECT_Y = 24;
//	static final int OBJECT_SPACE = 10;
	static final String SOFTWARE = "記録は大切なの";
	static final String[] MODE_TYPE_STR = {"通常", "改装", "ソート"};
	static final String[] SAVE_TYPE_STR = {"通常", "編成", "資材"};
	static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS");
	// 変数
	static int saveType = 0;
	public static JComboBox<String> comboBox1;
	static JComboBox<String> comboBox2, joinComboDir, joinComboType;
	static DefaultComboBoxModel<String> modelSave, modelDir, modelType;
	public static JTextArea textArea;
	public static Timer timer;
	/* コンストラクタ */
	MainWindow(){
		// ウィンドウの設定
		setTitle(SOFTWARE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setAlwaysOnTop(true);
		
		// オブジェクトの設定
		JButton button1 = new JButton("座標取得");
		button1.addActionListener(this);
		button1.setActionCommand("座標取得");
		
		comboBox1 = new JComboBox<>(MODE_TYPE_STR);
		comboBox1.addActionListener(this);
		comboBox1.setActionCommand("モード変更");
		
		JButton button2 = new JButton("画像追加");
		button2.addActionListener(this);
		button2.setActionCommand("画像追加");
		button2.setMnemonic(KeyEvent.VK_Z);
		
		JButton button3 = new JButton("画像保存");
		button3.addActionListener(this);
		button3.setActionCommand("画像保存");
		
		modelSave = new DefaultComboBoxModel<>(SAVE_TYPE_STR);
		comboBox2 = new JComboBox<>(modelSave);
		comboBox2.addActionListener(this);
		comboBox2.setActionCommand("保存種別");
		
		modelDir = new DefaultComboBoxModel<>();
		joinComboDir = new JComboBox<>(modelDir);
		joinComboDir.addActionListener(this);
		joinComboDir.setActionCommand("方向変更");
		
		modelType = new DefaultComboBoxModel<>();
		joinComboType = new JComboBox<>(modelType);
		joinComboType.addActionListener(this);
		joinComboType.setActionCommand("種類変更");
		
		JButton button4 = new JButton("オプション");
		button4.addActionListener(this);
		button4.setActionCommand("オプション");
		
		textArea = new JTextArea();
		textArea.setRows(4);
		textArea.setEditable(false);
		
		// オブジェクトの配置
		getContentPane().setLayout(new BorderLayout(10, 10));
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 4, 10, 10));
		panel.add(button1);
		panel.add(comboBox1);
		panel.add(button2);
		panel.add(button3);
		panel.add(comboBox2);
		panel.add(joinComboDir);
		panel.add(joinComboType);
		panel.add(button4);
		joinComboDir.setEnabled(false);
		joinComboType.setEnabled(false);
		
		getContentPane().add(panel, BorderLayout.NORTH);
		getContentPane().add(new JScrollPane(textArea), BorderLayout.SOUTH);
		pack();
		setVisible(true);
		
		timer = new Timer(500, this);
		timer.start();
	}
	/* イベント処理用 */
	public void actionPerformed(ActionEvent event){
		// メインウィンドウの入力に対する処理
		String commandStr = event.getActionCommand(), modeStr = (String)comboBox1.getSelectedItem();
//		System.out.println(commandStr + " " + modeStr);
		if(commandStr == null){
			// タイマーイベントの処理
			if(OptionWindow.checkbox2.isSelected()){
				switch(modeStr){
				case "改装":
					Nano.unitFrame.addImageX(Capture.getImage());
					break;
				}
			}
			if(OptionWindow.fps != 0){
				switch(modeStr){
				case "通常":
					savePicture();
					break;
				}
			}
			return;
		}
		switch(commandStr){
		case "座標取得":
			Capture.getKancollePosition();
			break;
		case "モード変更":
			switch(modeStr){
			case "通常":
				Nano.unitFrame.setVisible(false);
				Nano.sortFrame.setVisible(false);
				modelSave = new DefaultComboBoxModel<>(SAVE_TYPE_STR);
				modelDir  = new DefaultComboBoxModel<>();
				modelType = new DefaultComboBoxModel<>();
				modelSave.setSelectedItem(modelSave.getElementAt(saveType));
				comboBox2.setModel(modelSave);
				joinComboDir.setModel(modelDir);
				joinComboType.setModel(modelType);
				comboBox2.setEnabled(true);
				joinComboDir.setEnabled(false);
				joinComboType.setEnabled(false);
				if(OptionWindow.fps != 0){
					MainWindow.timer.restart();
					timer.setDelay(1000 / OptionWindow.fps);
				}else{
					timer.setDelay(1000 * 60 * 60 * 24);
				}
				break;
			case "改装":
				Nano.unitFrame.setVisible(true);
				Nano.sortFrame.setVisible(false);
				modelSave = new DefaultComboBoxModel<>();
				modelDir  = new DefaultComboBoxModel<>(UnitWindow.SHOW_DIR_STR);
				modelType = new DefaultComboBoxModel<>(UnitWindow.SHOW_TYPE_STR);
				modelDir.setSelectedItem(modelDir.getElementAt(UnitWindow.showDir));
				modelType.setSelectedItem(modelType.getElementAt(UnitWindow.showType));
				comboBox2.setModel(modelSave);
				joinComboDir.setModel(modelDir);
				joinComboType.setModel(modelType);
				comboBox2.setEnabled(false);
				joinComboDir.setEnabled(true);
				joinComboType.setEnabled(true);
				if(OptionWindow.checkbox2.isSelected()){
					MainWindow.timer.restart();
					timer.setDelay(500);
				}else{
					timer.setDelay(1000 * 60 * 60 * 24);
				}
				break;
			case "ソート":
				Nano.unitFrame.setVisible(false);
				Nano.sortFrame.setVisible(true);
				modelSave = new DefaultComboBoxModel<>();
				modelDir  = new DefaultComboBoxModel<>(SortWindow.SHOW_DIR_STR);
				modelType = new DefaultComboBoxModel<>(SortWindow.SHOW_TYPE_STR);
				modelDir.setSelectedItem(modelDir.getElementAt(SortWindow.showDir));
				modelType.setSelectedItem(modelType.getElementAt(SortWindow.showType));
				comboBox2.setModel(modelSave);
				joinComboDir.setModel(modelDir);
				joinComboType.setModel(modelType);
				comboBox2.setEnabled(false);
				joinComboDir.setEnabled(true);
				joinComboType.setEnabled(true);
				timer.setDelay(1000 * 60 * 60 * 24);
				break;
			}
			break;
		case "方向変更":
			switch(modeStr){
			case "改装":
				Nano.unitFrame.changeMode(joinComboDir.getSelectedIndex(), joinComboType.getSelectedIndex());
				break;
			case "ソート":
				Nano.sortFrame.changeMode(joinComboDir.getSelectedIndex(), joinComboType.getSelectedIndex());
				break;
			}
			break;
		case "種類変更":
			switch(modeStr){
			case "改装":
				Nano.unitFrame.changeMode(joinComboDir.getSelectedIndex(), joinComboType.getSelectedIndex());
				break;
			case "ソート":
				Nano.sortFrame.changeMode(joinComboDir.getSelectedIndex(), joinComboType.getSelectedIndex());
				break;
			}
			break;
		case "画像追加":
			switch(modeStr){
			case "改装":
				Nano.unitFrame.addImage(Capture.getImage());
				break;
			case "ソート":
				Nano.sortFrame.addImage(Capture.getImage());
				break;
			}
			break;
		case "画像保存":
			switch(modeStr){
			case "通常":
				savePicture();
				break;
			case "改装":
				Nano.unitFrame.savePicture();
				break;
			case "ソート":
				Nano.sortFrame.savePicture();
				break;
			}
			break;
		case "オプション":
			Nano.optionFrame.setVisible(true);
			break;
		}
	}
	/* テキストエリアにテキストを追加する */
	public static void putLog(String message){
		textArea.append(message + "¥n");
	}
	/* 名前隠し機能 */
	private static void disableName(BufferedImage image){
		Graphics graphics = image.getGraphics();
		// 母港左上の提督名
		if(checkHome(image)){
			graphics.setColor(new Color(38, 38, 38));
			graphics.fillRect(111, 0, 162, 25);
		}
		// 艦隊司令部情報
		if(JoinWindow.checkColor(image, 306,276,84,84,84)){
			if(JoinWindow.checkColor(image, 251,203,35,158,159)){
				if(JoinWindow.checkColor(image, 272,479,159,155,61)){
					graphics.setColor(new Color(241, 234, 221));
					graphics.fillRect(201, 123, 295, 30);
				}
			}
		}
		// ランキング
		if(JoinWindow.checkColor(image, 87, 189, 79, 152, 139)){
			if(JoinWindow.checkColor(image, 158, 81, 196, 169, 87)){
				if(JoinWindow.checkColor(image, 47, 333, 115, 166, 202)){
					graphics.setColor(new Color(54, 54, 54));
					graphics.fillRect(225, 153, 150, 298);
				}
			}
		}
		// 演習一覧
		if(JoinWindow.checkColor(image, 140, 131, 103, 83, 46)){
			if(JoinWindow.checkColor(image, 654,119,255,191,96)){
				if(JoinWindow.checkColor(image, 654,119,255,191,96)){
					graphics.setColor(new Color(225, 209, 181));
					graphics.fillRect(338, 178, 165, 14);
					graphics.setColor(new Color(237, 223, 207));
					graphics.fillRect(338, 233, 165, 14);
					graphics.setColor(new Color(225, 209, 181));
					graphics.fillRect(338, 288, 165, 14);
					graphics.setColor(new Color(237, 223, 207));
					graphics.fillRect(338, 343, 165, 14);
					graphics.setColor(new Color(225, 209, 181));
					graphics.fillRect(338, 398, 165, 14);
				}
			}
		}
		// 演習個別
		if(JoinWindow.checkColor(image, 0,0,0,0,0)){
			if(JoinWindow.checkColor(image, 168,165,17,156,160)){
				if(JoinWindow.checkColor(image, 635,444,224,217,204)){
					graphics.setColor(new Color(246, 239, 228));
					graphics.fillRect(130, 87, 295, 30);
				}
			}
		}
		// 戦果報告
		if(JoinWindow.checkColor(image, 51,77,255,246,242)){
			if(JoinWindow.checkColor(image, 395,289,255,246,242)){
				if(JoinWindow.checkColor(image, 0,0,36,54,63)){
					graphics.setColor(new Color(37, 44, 47));
					graphics.fillRect(56, 82, 172, 24);
				}
			}
		}
		graphics.dispose();
	}
	private static boolean checkHome(BufferedImage image){
		if(!JoinWindow.checkColor(image, 665, 42, 83, 159, 73)) return false;
		return JoinWindow.checkColor(image, 736, 61, 172, 128, 95);
	}
	/* 画像保存 */
	private static void savePicture(){
		try{
			if(Capture.displayIndex < 0) return;
			BufferedImage flashImage = Capture.getImage();
			if(flashImage == null) return;
			String saveName = DATE_FORMAT.format(Calendar.getInstance().getTime()) + ".png";
			switch((String)comboBox2.getSelectedItem()){
			case "通常":
				if(OptionWindow.checkbox3.isSelected()) disableName(flashImage);
				putLog("【画像保存】");
				ImageIO.write(flashImage, "png", new File(saveName));
				break;
			case "編成":
				if(!JoinWindow.checkColor(flashImage, 420,118,195,180,169)) return;
				if(!JoinWindow.checkColor(flashImage, 506,114,115,180,191)) return;
				putLog("【画像保存】");
				ImageIO.write(flashImage.getSubimage(100, 94, 700, 386), "png", new File(saveName));
				break;
			case "資材":
				BufferedImage supplyImage = new BufferedImage(229, 60, BufferedImage.TYPE_INT_BGR);
				Graphics graphics = supplyImage.getGraphics();
				graphics.drawImage(flashImage.getSubimage(9, 407, 86, 60), 0, 0, null);	//時刻
				graphics.drawImage(flashImage.getSubimage(657, 9, 143, 60), 86, 0, null);	//資材
				graphics.dispose();
				putLog("【画像保存】");
				ImageIO.write(supplyImage, "png", new File(saveName));
				break;
			}
			putLog(saveName);
		}
		catch(Exception error){
			error.printStackTrace();
		}
	}
}
