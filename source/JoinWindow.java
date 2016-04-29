/* 一覧ウィンドウ(抽象クラス) */

/* getIndex()において、デフォルトの番号付けは次の通り。
 * 00 01 02 03 04 05
 * 06 07 08 09 10 11
 * 12 13 14 15 16 17
 * 18 19 20 21 22 23
 * 24 25 26 27 28 29
 * 30 31 32 33 34 35
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
abstract class JoinWindow extends JFrame{
	/* メンバ変数 */
	// 定数
	static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS");
	// 変数
	private int zooming = 0;	// 実画像に対するウインドウの縮小率
	private int sortType  = 0;	// 表示方向(行優先・列優先・艦隊優先)
	private int sizeType = 0;	// 表示種類(コンパクト・通常・エクストラ)
	Dimension blockSize;
	/* コンストラクタ */
	JoinWindow(Dimension blockSize, GridLayout blockLayout, int zooming){
		this.zooming = zooming;
		this.blockSize = blockSize;
		// ウィンドウ・オブジェクトの設定
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setAlwaysOnTop(true);
		addKeyListener(getKeyListener());
		setTransferHandler(getDropFileHandler());
		// ウィンドウにおける設定
		setTitle(getWindowTitle());
		getContentPane().setPreferredSize(
				new Dimension(blockSize.width * blockLayout.getRows() / zooming, blockSize.height * blockLayout.getColumns() / zooming));
		// オブジェクトにおける設定
		getContentPane().setLayout(blockLayout);
		getContentPane().setBackground(Color.WHITE);
		IntStream.range(0, blockLayout.getRows() * blockLayout.getColumns()).map(this::getIndex)
		.mapToObj(i->new ImageLabel()).forEach(l->getContentPane().add(l));
		pack();
	}
	/* アクセッサ */
	abstract int getPositionX();
	abstract int getPositionY();
	abstract String getWindowTitle();
	protected abstract float getStrokeSize();
	protected int getBlockSizeX(){return blockSize.width;}
	protected int getBlockSizeY(){return blockSize.height;}
	protected int getBlocksX(){return ((GridLayout) getContentPane().getLayout()).getRows();}
	protected int getBlocksY(){return ((GridLayout) getContentPane().getLayout()).getColumns();}
	protected int getBlockSizeX_(){return getBlockSizeX() / zooming;}
	protected int getBlockSizeY_(){return getBlockSizeY() / zooming;}
	protected int getSX(int x){return getBlockSizeX() * x;}
	protected int getSY(int y){return getBlockSizeY() * y;}
	protected int getSX_(int x){return getBlockSizeX_() * x;}
	protected int getSY_(int y){return getBlockSizeY_() * y;}
	protected int getIndex(int i){
		switch(sortType){
			case 0:	//行を優先
				return i;
			case 1:	//列を優先
				int x = i / getBlocksX(), y = i % getBlocksY();
				return y * getBlocksX() + x;
			case 2:	//ハイブリッド
				return new int[]{
					1,  2,  7,  8,  13, 14,
					3,  4,  9,  10, 15, 16,
					5,  6,  11, 12, 17, 18,
					19, 20, 25, 26, 31, 32, 
					21, 22, 27, 28, 33, 34,
					23, 24, 29, 30, 35, 36}[i];
		}
		return 0;
	}
	protected int getSortType(){return sortType;}
	protected int getSizeType(){return sizeType;}
	void setSortType(int type){sortType = type;}
	void setSizeType(int type){sizeType = type;}
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
	/* 表示を更新する */
	void redraw(){
		
	}
	/* コンボボックスの状態から、ウィンドウの表示を変更する */
	public void changeMode(int dir, int type){
		//setSortType(dir);
		//setType(type);
		pack();
	}
	/* 画像を追加する */
	public void addImage(BufferedImage image){
		if(image == null || !checkImage(image)) return;
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
		Optional.ofNullable(image).map(this::checkImageX).filter(p->p >= 0)
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
		graphics.setStroke(new BasicStroke(getStrokeSize()));
		graphics.setPaint(Color.BLUE);
		switch(getSortType()){
			case 0:	//行を優先
				IntStream.range(1, getBlocksY()).filter(y->exclude(y, py1, py2 + 1)).forEach(y->IntStream.range(0, getBlocksX())
						.forEach(x->drawFrame(graphics, x, y, x + 1, y)));
				break;
			case 1:	//列を優先
				IntStream.range(1, getBlocksX()).filter(x->exclude(x, py1, py2 + 1)).forEach(x->IntStream.range(0, getBlocksX())
						.forEach(y->drawFrame(graphics, x, y, x, y + 1)));
				break;
			case 2:	//艦隊優先
				IntStream.range(0, getBlocksX()).filter(x->exclude(3, py1, py2 + 1)).forEach(x->drawFrame(graphics, x, 3, x + 1, 3));
				IntStream.range(0, getBlocksY()).filter(x->exclude(2, py1, py2 + 1)).forEach(x->drawFrame(graphics, 2, 0, 2, getBlocksY()));
				IntStream.range(0, getBlocksY()).filter(x->exclude(2, py1, py2 + 1)).forEach(x->drawFrame(graphics, 4, 0, 4, getBlocksY()));
				break;
		}
	}
	public void savePicture(boolean addFrame){
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
		graphics.dispose();
		// 特殊な枠線を追加する
		if(addFrame) 
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
	/* 画像判定 */
	abstract boolean checkImage(BufferedImage image);
	abstract int checkImageX(BufferedImage image);
	// FIXME: OSやブラウザによる色の違いへの対処が求められる
	public static boolean checkColor(BufferedImage image, int x, int y, int r, int g, int b){
		return true;
//		Color color = new Color(image.getRGB(x, y));
//		int diffR = color.getRed() - r, diffG = color.getGreen() - g, diffB = color.getBlue() - b;
//		int diff = diffR * diffR + diffG * diffG + diffB * diffB;
//		if(diff < 500) return true;
//		return false;
	}
	protected static class ImageLabel extends JLabel{
		private Image image;
		// ウインドウの枠線
		private enum LINE{
			NORMAL(new LineBorder(Color.BLACK, 1)),
			RED(new LineBorder(Color.RED, 4)),
			BLUE(new LineBorder(Color.BLUE, 4));
			private Border border;
			private LINE(Border border) {
				this.border = border;
			}
			void set(JComponent ...targets) {
				Arrays.stream(targets).forEach(target->target.setBorder(border));
			}
		}
		public ImageLabel() {
			super();
			// 枠線を描画する
			LINE.NORMAL.set(this);
			addMouseListener(getMouseListener());
		}
		private static void changeColor(MouseEvent event, LINE line) {
			Optional.of(event).filter(e->e.getButton() > 0).map(e->(JComponent) e.getSource())
			.filter(target->!LINE.RED.border.equals(target.getBorder())).ifPresent(target->line.set(target));
		}
		public int getIndexX() {
			return getX() / getWidth();
		}
		public int getIndexY() {
			return getY() / getHeight();
		}
		public Image getImage() {
			return image;
		}
		public void setImage(Image image) {
			this.image = image;
			setIcon(image == null ? null : new ImageIcon(this.image
					.getScaledInstance(getWidth(), getHeight(), Image.SCALE_AREA_AVERAGING)));
		}
		public void clearImage() {
			setImage(null);
		}
		public boolean hasImage(){
			return getIcon() != null;
		}
		@Override
		public String toString() {
			return String.format("(%d,%d)", getIndexX(), getIndexY());
		}
		/* マウスイベント */
		private MouseAdapter getMouseListener(){
			return new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent event){
					// ダブルクリックした際は、その場所の記録画像を消去する
					Optional.of(event).filter(e->e.getClickCount() >= 2).map(e->(ImageLabel) e.getSource()).filter(ImageLabel::hasImage).ifPresent(il->{
						MainWindow.putLog("【画像削除】");
						il.clearImage();
						MainWindow.putLog("追加位置：" +  il.toString());
					});
				}
				@Override
				public void mousePressed(MouseEvent event){
					// マウスを押した際は押した位置のラベルを変更する
					LINE.RED.set((JComponent) event.getSource());
				}
				@Override
				public void mouseReleased(MouseEvent event){
					// マウスを離した際は、その位置のマスとの交換を行う
					ImageLabel pressed = (ImageLabel) event.getSource();
					ImageLabel released = (ImageLabel) pressed.getParent().getComponentAt(pressed.getX() + event.getX(), pressed.getY() + event.getY());
					if(released != null && !pressed.equals(released)){
						MainWindow.putLog("【画像交換】");
						// 画像を入れ替える
						Image temp = pressed.getImage();
						pressed.setImage(released.getImage());
						released.setImage(temp);
						MainWindow.putLog(pressed.toString() + "⇔" + released.toString());
					}
					LINE.NORMAL.set(pressed, released);
				}
				@Override
				public void mouseEntered(MouseEvent event){
					// マウスがラベル上に重なった歳は、その位置のラベルを変更する
					ImageLabel.changeColor(event, LINE.BLUE);
				}
				@Override
				public void mouseExited(MouseEvent event){
					// マウスがラベルから離れた際はその位置のラベルを元に戻す
					ImageLabel.changeColor(event, LINE.NORMAL);
				}
			};
		}
	}
}
