/* 一覧ウィンドウ(抽象クラス) */

/* getIndex()において、デフォルトの番号付けは次の通り。
 * 00 01 02 03 04 05
 * 06 07 08 09 10 11
 * 12 13 14 15 16 17
 * 18 19 20 21 22 23
 * 24 25 26 27 28 29
 * 30 31 32 33 34 35
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;

@SuppressWarnings("serial")
public class JoinWindow extends JFrame implements Savable{
	/* メンバ変数 */
	// 定数
	final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS");
	// 変数
	private int zooming = 0;					// 実画像に対するウインドウの縮小率
	private Pair<IntUnaryOperator> sortType;	// 表示方向(行優先・列優先・艦隊優先)
	private Pair<Dimension> sizeType;			// 表示種類(コンパクト・通常・エクストラ)
	private FrameOption option;
	/* コンストラクタ */
	JoinWindow(){
		// ウィンドウ・オブジェクトの設定
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setAlwaysOnTop(true);
		addKeyListener(getKeyListener());
		setTransferHandler(getDropFileHandler());
		// ウィンドウにおける設定
		setTitle(String.format("%s-%s-%s", option.toString(), sortType.toString(), sizeType.toString()));
		//getContentPane().setPreferredSize();
		// オブジェクトにおける設定
		getContentPane().setLayout(option.getBlocks());
		getContentPane().setBackground(Color.WHITE);
		IntStream.range(0, getBlocksX() * getBlocksY()).map(this::getIndex)
		.mapToObj(i->new ImageLabel()).forEach(l->getContentPane().add(l));
		pack();
	}
	/* アクセッサ */
	protected int getPositionX(){return option.getPositionX();}
	protected int getPositionY(){return option.getPositionY();}
	protected int getBlockSizeX(){return sizeType.get().width;}
	protected int getBlockSizeY(){return sizeType.get().height;}
	protected int getBlocksX(){return ((GridLayout) getContentPane().getLayout()).getRows();}
	protected int getBlocksY(){return ((GridLayout) getContentPane().getLayout()).getColumns();}
	protected int getBlockSizeX_(){return getBlockSizeX() / zooming;}
	protected int getBlockSizeY_(){return getBlockSizeY() / zooming;}
	protected int getSX(int x){return getBlockSizeX() * x;}
	protected int getSY(int y){return getBlockSizeY() * y;}
	protected int getSX_(int x){return getBlockSizeX_() * x;}
	protected int getSY_(int y){return getBlockSizeY_() * y;}
	protected int getIndex(int i){return sortType.get().applyAsInt(i);}
//	void setSortType(int type){sortType = type;}
//	void setSizeType(int type){sizeType = type;}
	/* キーイベント */
	private KeyAdapter getKeyListener(){
		return new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent event){
				// Alt+Zに反応する
				if(event.getKeyCode() == KeyEvent.VK_Z && event.isAltDown()) addImage(Capture.getImage());
			}
		};
	}
	/* ドロップイベント */
	private TransferHandler getDropFileHandler(){
		return new TransferHandler() {
			@Override
			@SuppressWarnings("unchecked")
			public boolean importData(TransferSupport support){
				// ドロップされていないかドロップされたものがファイルではない場合は受け取らない
				if(!support.isDrop() && support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) return false;
				// ドロップ処理、ファイルを受け取り順番に読み込んで追加する
				try {
					((List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor)).forEach(file->addImage(file));
				}catch(Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		};
	}
	/* コンボボックスの状態から、ウィンドウの表示を変更する */
	public void changeMode(int dir, int type){
		//setSortType(dir);
		//setType(type);
		pack();
	}
	/* 画像を追加する */
	public void addImage(BufferedImage image){
		if(image == null || !option.checkImage(image)) return;
		Arrays.stream(getContentPane().getComponents()).map(c->(ImageLabel) c).filter(il->!il.hasImage()).findFirst().ifPresent(il->{
			MainWindow.putLog("【画像追加】");
			il.setImage(image.getSubimage(getPositionX(), getPositionY(), getBlockSizeX(), getBlockSizeY()));
			MainWindow.putLog("位置：" + il.toString());		
		});
	}
	public void addImage(File file) {
		try {
			addImage(ImageIO.read(file));
		}catch(IOException error) {
			error.printStackTrace();
		}
	}
	/* 画像を追加する(位置認識Ver) */
	public void addImageX(BufferedImage image){
		Optional.ofNullable(image).map(bimage->option.checkImageX(this, bimage)).filter(p->p >= 0)
		.map(p->(ImageLabel) getContentPane().getComponent(getIndex(p))).ifPresent(il->{
			// image != null && p >= 0 の際に実行される
			MainWindow.putLog("【自動取得】");
			il.setImage(image.getSubimage(getPositionX(), getPositionY(), getBlockSizeX(), getBlockSizeY()));
			MainWindow.putLog("位置：" + il.toString());
		});	
	}
	/* 画像を保存する */
	private boolean exclude(int arg, int ...excludes){
		return !IntStream.of(excludes).anyMatch(exclude->exclude == arg);
	}
	private void drawFrame(Graphics2D graphics, double x1, double y1, double x2, double y2){
		graphics.draw(new Line2D.Double(x1 * getBlockSizeX(), y1 * getBlockSizeY(), x2 * getBlockSizeX(), y2 * getBlockSizeY()));
	}
	protected void addSpecialFrame(Graphics2D graphics, int px1, int py1, int px2, int py2){
		graphics.setStroke(option.getStroke());
		graphics.setPaint(Color.BLUE);
		switch(sortType.toString()){
			case "行を優先":
				IntStream.range(1, getBlocksY()).filter(y->exclude(y, py1, py2 + 1)).forEach(y->IntStream.range(0, getBlocksX())
						.forEach(x->drawFrame(graphics, x, y, x + 1, y)));
				break;
			case "列を優先":
				IntStream.range(1, getBlocksX()).filter(x->exclude(x, py1, py2 + 1)).forEach(x->IntStream.range(0, getBlocksX())
						.forEach(y->drawFrame(graphics, x, y, x, y + 1)));
				break;
			case "艦隊優先":
				IntStream.range(0, getBlocksX()).filter(x->exclude(3, py1, py2 + 1)).forEach(x->drawFrame(graphics, x, 3, x + 1, 3));
				IntStream.range(0, getBlocksY()).filter(x->exclude(2, py1, py2 + 1)).forEach(x->drawFrame(graphics, 2, 0, 2, getBlocksY()));
				IntStream.range(0, getBlocksY()).filter(x->exclude(2, py1, py2 + 1)).forEach(x->drawFrame(graphics, 4, 0, 4, getBlocksY()));
				break;
		}
		if(option.equals(FrameOption.UNIT)){
			graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			graphics.setFont(new Font("Arial", Font.PLAIN, 40));
			IntStream.range(0, getBlocksY()).forEach(y->IntStream.range(0, getBlocksX()).forEach(x->{
				int px = getIndex(y * getBlocksY() + x) % getBlocksX(), py = getIndex(y * getBlocksY() + x) / getBlocksX();
				graphics.setPaint(Color.WHITE);
				graphics.drawString(String.format("%d-%d", y + 1, x + 1), (px + 1) * getBlockSizeX() - 70, py * getBlockSizeY() + 40);
				graphics.setPaint(Color.RED);
				graphics.drawString(String.format("%d-%d", y + 1, x + 1), (px + 1) * getBlockSizeX() - 72, py * getBlockSizeY() + 38);
			}));
		}
	}
	@Override
	public void savePicture(){
		// 最大領域を判断する(左隅のラベルと右隅のラベルを取得する)
		List<ImageLabel> list = Arrays.stream(getContentPane().getComponents())
				.map(c->(ImageLabel) c).filter(ImageLabel::hasImage).collect(Collectors.toList());
		ImageLabel min = (ImageLabel) getContentPane().getComponentAt(list.stream().mapToInt(ImageLabel::getX).min().getAsInt(), 
				list.stream().mapToInt(ImageLabel::getY).min().getAsInt());
		ImageLabel max = (ImageLabel) getContentPane().getComponentAt(list.stream().mapToInt(ImageLabel::getX).max().getAsInt(), 
				list.stream().mapToInt(ImageLabel::getY).max().getAsInt());
		if(min.equals(max)) return;
		// 保存用バッファに画像を配置する
		MainWindow.putLog("【画像保存】");
		// * ZOOMING
		BufferedImage saveBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_BGR);
		Graphics2D graphics = saveBuffer.createGraphics();
		// * ZOOMING
		list.forEach(il->graphics.drawImage(il.getImage(), il.getX(), il.getY(), this));
		// 特殊な枠線を追加する
		//if(addFrame)
			addSpecialFrame(graphics, min.getIndexX(), min.getIndexY(), max.getIndexX(), max.getIndexY());
		graphics.dispose();
		// 画像の保存処理
		String saveName = DATE_FORMAT.format(Calendar.getInstance().getTime()) + ".png";
		try{
			// * ZOOMING
			ImageIO.write(saveBuffer.getSubimage(min.getX(), min.getY(), max.getX() + min.getWidth(), max.getY() + min.getHeight()), "png", new File(saveName));
		}
		catch(Exception error){
			error.printStackTrace();
		}
		MainWindow.putLog(saveName);
		int option = JOptionPane.showConfirmDialog(this, "画像をクリアしますか？", "記録は大切なの", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(option == JOptionPane.YES_OPTION){
			Arrays.stream(getContentPane().getComponents()).map(c->(ImageLabel) c).forEach(ImageLabel::clearImage);
		}
	}
}
