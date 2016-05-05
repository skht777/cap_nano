/* メインウィンドウ */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

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
	private JComboBox<Pair<FrameOption>> comboBox1;
	private JComboBox<Pair<UnaryOperator<BufferedImage>>> comboBox2;
	private JComboBox<Pair<IntUnaryOperator>> joinSortCombo; 
	private JComboBox<Pair<Dimension>> joinSizeCombo;
	private JoinWindow unit, sort, visible;
	public static JTextArea textArea;
	private Timer timer;
	private OptionData option;
	/* コンストラクタ */
	MainWindow(){
		// ウィンドウの設定
		setTitle("記録は大切なの");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setAlwaysOnTop(true);
		unit = new JoinWindow(FrameOption.UNIT);
		sort = new JoinWindow(FrameOption.SORT);
		option = new OptionData();
		
		// オブジェクトの設定
		JButton button1 = new JButton("座標取得");
		button1.addActionListener(event->Capture.getKancollePosition());
		
		comboBox1 = new JComboBox<>(new Vector<>(getModeList()));
		comboBox1.addActionListener(event->{
			switch(getSelectedItem(comboBox1).getValue()){
				default:
//					if(option.getFPS() != 0){
//						//timer.restart();
//						timer.setDelay(1000 / option.getFPS());
//					}else{
//						timer.setDelay(1000 * 60 * 60 * 24);
//					}
					break;
				case UNIT:
					comboBox2.setEnabled(false);
//					if(option.isAuto()){
//						timer.restart();
//						timer.setDelay(500);
//					}else{
//						timer.setDelay(1000 * 60 * 60 * 24);
//					}
					visible = unit;
					break;
				case SORT:
					comboBox2.setEnabled(false);
					//timer.setDelay(1000 * 60 * 60 * 24);
					visible = sort;
					break;
				}
				joinSortCombo.setEnabled(true);
				joinSizeCombo.setEnabled(true);
				joinSortCombo.setModel(new DefaultComboBoxModel<>(new Vector<>(getSelectedItem(comboBox1).getValue().getSortList())));
				joinSizeCombo.setModel(new DefaultComboBoxModel<>(new Vector<>(getSelectedItem(comboBox1).getValue().getSizeList())));
				visible.setVisible(true);
		});
		
		JButton button2 = new JButton("画像追加");
		button2.addActionListener(event->Optional.ofNullable(visible).ifPresent(v->v.addImage(Capture.getImage())));
		button2.setMnemonic(KeyEvent.VK_Z);
		
		JButton button3 = new JButton("画像保存");
		button3.addActionListener(event->(visible == null ? this : visible).savePicture());
		
		comboBox2 = new JComboBox<>(new Vector<>(getSaveTypeList()));
		//comboBox2.addActionListener(this);
		comboBox2.setActionCommand("保存種別");
		
		joinSortCombo = new JComboBox<>();
		joinSortCombo.addActionListener(event->visible.setSortType(getSelectedItem(joinSortCombo)));
		
		joinSizeCombo = new JComboBox<>();
		joinSizeCombo.addActionListener(event->visible.setSizeType(getSelectedItem(joinSizeCombo)));
		
		JButton button4 = new JButton("オプション");
		button4.addActionListener(e->new OptionWindow(option));
		button2.setEnabled(false);
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
		panel.add(joinSortCombo);
		panel.add(joinSizeCombo);
		panel.add(button4);
		
		joinSortCombo.setEnabled(false);
		joinSizeCombo.setEnabled(false);
		
		getContentPane().add(panel, BorderLayout.NORTH);
		getContentPane().add(new JScrollPane(textArea), BorderLayout.SOUTH);
		pack();
		setVisible(true);
		
		timer = new Timer(500, event->{
			// タイマーイベントの処理
			switch(getSelectedItem(comboBox1).getValue()){
			case UNIT:
				if(option.isAuto()) visible.addImageX(Capture.getImage());
				break;
			default:
				if(option.getFPS() != 0) savePicture();
				break;
			}
		});
		//timer.start();
	}
	private <T> T getSelectedItem(JComboBox<T> comboBox){return comboBox.getModel().getElementAt(comboBox.getSelectedIndex());}
	/* テキストエリアにテキストを追加する */
	public static void putLog(String message){
		textArea.append(message + "\n");
	}
	/* 名前隠し機能 */
	private void colorRect(Graphics graphics, int x, int y, int width, int height, int r, int g, int b){
		graphics.setColor(new Color(r, g, b));
		graphics.fillRect(x, y, width, height);
	}
	private void disableName(BufferedImage image){
		Graphics graphics = image.getGraphics();
		// 母港左上の提督名
		if(checkHome(image)){
			colorRect(graphics, 111, 0, 162, 25, 38, 38, 38);
		}
		// 艦隊司令部情報
		if(Capturable.checkColor(image, 306,276,84,84,84) &&
				Capturable.checkColor(image, 251,203,35,158,159) &&
				Capturable.checkColor(image, 272,479,159,155,61)){
			colorRect(graphics, 201, 123, 295, 30, 241, 234, 221);
		}
		// ランキング
		if(Capturable.checkColor(image, 87, 189, 79, 152, 139) &&
				Capturable.checkColor(image, 158, 81, 196, 169, 87) &&
				Capturable.checkColor(image, 47, 333, 115, 166, 202)){
			colorRect(graphics, 225, 153, 150, 298, 54, 54, 54);
		}
		// 演習一覧
		if(Capturable.checkColor(image, 140, 131, 103, 83, 46) &&
				Capturable.checkColor(image, 654,119,255,191,96) &&
				Capturable.checkColor(image, 654,119,255,191,96)){
			IntStream.of(1, 3, 5).forEach(i->colorRect(graphics, 338, 178 + 55 * i, 165, 14, 225, 209, 181));
			IntStream.of(2, 4).forEach(i->colorRect(graphics, 338, 178 + 55 * i, 165, 14, 237, 223, 207));
		}
		// 演習個別
		if(Capturable.checkColor(image, 0,0,0,0,0) && 
				Capturable.checkColor(image, 168,165,17,156,160) &&
				Capturable.checkColor(image, 635,444,224,217,204)){
			colorRect(graphics, 130, 87, 295, 30, 246, 239, 228);
		}
		// 戦果報告
		if(Capturable.checkColor(image, 51,77,255,246,242) &&
				Capturable.checkColor(image, 395,289,255,246,242) &&
				Capturable.checkColor(image, 0,0,36,54,63)){
			colorRect(graphics, 56, 82, 172, 24, 37, 44, 47);
		}
		graphics.dispose();
	}
	// FIXME: OSやブラウザによる色の違いへの対処が求められる
	private boolean checkHome(BufferedImage image){
		return true;
		//return Capturable.checkColor(image, 665, 42, 83, 159, 73) && Capturable.checkColor(image, 736, 61, 172, 128, 95);
	}
	/* 画像保存 */
	@Override
	public void savePicture(){
		if(Capture.displayIndex < 0) return;
		Optional.ofNullable(Capture.getImage())
		.ifPresent(image->Capturable.savePicture(getSelectedItem(comboBox2).getValue().apply(image)));
	}
	private List<BasicPair<FrameOption>> getModeList(){
		return Arrays.asList(new BasicPair<>("通常", null), new BasicPair<>("改装", FrameOption.UNIT), new BasicPair<>("ソート", FrameOption.SORT));
	}
	private List<BasicPair<UnaryOperator<BufferedImage>>> getSaveTypeList(){
		return Arrays.asList(new BasicPair<UnaryOperator<BufferedImage>>("通常", image->{
			if(option.disableName()) disableName(image);
			return image;
		}), new BasicPair<UnaryOperator<BufferedImage>>("編成", image->{
			if(Capturable.checkColor(image, 420,118,195,180,169) && 
					Capturable.checkColor(image, 506,114,115,180,191)) return image.getSubimage(100, 94, 700, 386);
			return null;
		}), new BasicPair<UnaryOperator<BufferedImage>>("資材", image->{
			BufferedImage supplyImage = new BufferedImage(229, 60, BufferedImage.TYPE_INT_BGR);
			Graphics graphics = supplyImage.getGraphics();
			graphics.drawImage(image.getSubimage(9, 407, 86, 60), 0, 0, null);		//時刻
			graphics.drawImage(image.getSubimage(657, 9, 143, 60), 86, 0, null);	//資材
			graphics.dispose();
			return supplyImage;
		}));
	}
}
