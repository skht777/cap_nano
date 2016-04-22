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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

@SuppressWarnings("serial")
abstract class JoinWindow extends JFrame implements MouseListener, KeyListener, MouseMotionListener{
	/* メンバ変数 */
	// 定数
	static final float FRAME_WIDTH = 4.0f;
	static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS");
	// 変数
	boolean pressFlag = false;
	int windowX, windowY, blocksSize, pressPosition, enterPosition, type = -1;
	BufferedImage showImage, blankImage;
	public joinPanel panel;
	List<BufferedImage> ssBuffer;
	List<Boolean> ssBufferFlag;
	/* コンストラクタ */
	JoinWindow(){
		// ウィンドウ・オブジェクトの設定
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setAlwaysOnTop(true);
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		windowX = getSX_(getBlocksX());
		windowY = getSY_(getBlocksY());
		redraw();
		setTransferHandler(new DropFileHandler());
		// バッファの設定
		blocksSize = getBlocksX() * getBlocksY();
		ssBuffer = new ArrayList<>();
		ssBufferFlag = new ArrayList<>();
		blankImage = new BufferedImage(Capture.FLASH_X, Capture.FLASH_Y, BufferedImage.TYPE_INT_BGR);
		Graphics graphics = blankImage.getGraphics();
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, Capture.FLASH_X, Capture.FLASH_Y);
		for(int i = 0; i < blocksSize; i++){
			ssBuffer.add(clone(blankImage));
			ssBufferFlag.add(false);
		}
		showImage = new BufferedImage(windowX, windowY, BufferedImage.TYPE_INT_BGR);
		graphics = showImage.getGraphics();
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, windowX, windowY);
		graphics.dispose();
	}
	/* アクセッサ */
	abstract int getPositionX();
	abstract int getPositionY();
	abstract int getBlockSizeX();
	abstract int getBlockSizeY();
	abstract int getBlocksX();
	abstract int getBlocksY();
	abstract void setDir(int dir);
	abstract void setType(int type);
	abstract int getBlockSizeX_();
	abstract int getBlockSizeY_();
	abstract int getSX(int x);
	abstract int getSY(int y);
	abstract int getSX_(int x);
	abstract int getSY_(int y);
	abstract String getWindowTitle();
	abstract int getIndex(int i);
	/* マウスイベント */
	public void mouseClicked(MouseEvent event){
		// ダブルクリックした際は、その場所の記録画像を消去する
		if (event.getClickCount() < 2) return;
		int deletePosition = getPosition(event);
		if(ssBufferFlag.get(deletePosition) == false) return;
		MainWindow.putLog("【画像削除】");
			// showImage(画面表示)上での削除
			int x = deletePosition % getBlocksX(), y = deletePosition / getBlocksX();
			Graphics graphics = showImage.getGraphics();
			graphics.setColor(Color.white);
			graphics.fillRect(getSX_(x), getSY_(y), getBlockSizeX_(), getBlockSizeY_());
			graphics.dispose();
			// バッファの削除
			ssBuffer.set(deletePosition, clone(blankImage));
			// フラグの削除
			ssBufferFlag.set(deletePosition, false);
		MainWindow.putLog("追加位置：(" + x+  "," + y + ")");
		panel.repaint();
	}
	public void mousePressed(MouseEvent event){
		if(!pressFlag){
			// マウスを押した際は、押した位置を記憶しておく
			pressPosition = getPosition(event);
			pressFlag = true;
			panel.repaint();
		}else{
			// マウスでドラッグしている間は、対象の位置も記憶して処理する
			enterPosition = getPosition(event);
			panel.repaint();
		}
	}
	public void mouseReleased(MouseEvent event){
		// マウスを離した際は、その位置のマスとの交換を行う
		if(pressFlag == false) return;
		int releasePosition = getPosition(event);
		if(pressPosition != releasePosition){
			MainWindow.putLog("【画像交換】");
				// showImage(画面表示)上での交換
				int x1 =   pressPosition % getBlocksX(), y1 =   pressPosition / getBlocksX();
				int x2 = releasePosition % getBlocksX(), y2 = releasePosition / getBlocksX();
				BufferedImage temp1 = clone(showImage.getSubimage(getSX_(x1), getSY_(y1), getBlockSizeX_(), getBlockSizeY_()));
				BufferedImage temp2 = clone(showImage.getSubimage(getSX_(x2), getSY_(y2), getBlockSizeX_(), getBlockSizeY_()));
				Graphics graphics = showImage.getGraphics();
				graphics.drawImage(temp1, getSX_(x2), getSY_(y2), this);
				graphics.drawImage(temp2, getSX_(x1), getSY_(y1), this);
				graphics.dispose();
				// バッファの交換
				BufferedImage buffer2 = clone(ssBuffer.get(pressPosition));
				ssBuffer.set(pressPosition, clone(ssBuffer.get(releasePosition)));
				ssBuffer.set(releasePosition, buffer2);
				// フラグの交換
				boolean flag = ssBufferFlag.get(pressPosition);
				ssBufferFlag.set(pressPosition, ssBufferFlag.get(releasePosition));
				ssBufferFlag.set(releasePosition, flag);
			MainWindow.putLog("(" + x2 + "," + y2 + ")⇔(" + x1 + "," + y1 + ")");
		}
		pressFlag = false;
		panel.repaint();
	}
	public void mouseEntered(MouseEvent event){}
	public void mouseExited(MouseEvent event){}
	public void mouseMoved(MouseEvent event){}
	public void mouseDragged(MouseEvent event){
		enterPosition = getPosition(event);
		panel.repaint();
	}
	/* キーイベント */
	public void keyPressed(KeyEvent event){
		// Alt+Zに反応する
		int keycode = event.getKeyCode();
		if (keycode != KeyEvent.VK_Z) return;
		int modifer = event.getModifiersEx();
		if((modifer & InputEvent.ALT_DOWN_MASK) != 0){
			addImage(Capture.getImage());
		}
	}
	public void keyReleased(KeyEvent e){}
	public void keyTyped(KeyEvent e){}
	/* 表示を更新する */
	void redraw(){
		// ウィンドウにおける設定
		setTitle(getWindowTitle());
		getContentPane().setPreferredSize(new Dimension(windowX, windowY));
		setLocationRelativeTo(null);
		// オブジェクトにおける設定
		panel = new joinPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		pack();
		panel.repaint();
	}
	/* コンボボックスの状態から、ウィンドウの表示を変更する */
	public void changeMode(int dir, int type){
		setDir(dir);
		setType(type);
		if(this.type != type){
			windowX = getSX_(getBlocksX());
			windowY = getSY_(getBlocksY());
			showImage = new BufferedImage(windowX, windowY, BufferedImage.TYPE_INT_BGR);
			Graphics graphics = showImage.getGraphics();
			for(int x = 0; x < getBlocksX(); x++){
				for(int y = 0; y < getBlocksY(); y++){
					BufferedImage temp = ssBuffer.get(y * getBlocksX() + x).getSubimage(getPositionX(), getPositionY(), getBlockSizeX(), getBlockSizeY());
					graphics.drawImage(temp.getScaledInstance(getBlockSizeX_(), getBlockSizeY_(), Image.SCALE_AREA_AVERAGING), getSX_(x), getSY_(y), this);
				}
			}
			graphics.dispose();
			this.type = type;
		}
		redraw();
	}
	/* 画像を追加する */
	public void addImage(BufferedImage image){
		if(image == null) return;
		if(checkImage(image) == false) return;
		for(int i = 0; i < blocksSize; i++){
			int p = getIndex(i);
			if(ssBufferFlag.get(p) == false){
				MainWindow.putLog("【画像追加】");
					ssBuffer.set(p, image);
					ssBufferFlag.set(p, true);
					int px = p % getBlocksX(), py = p / getBlocksX();
					BufferedImage temp = image.getSubimage(getPositionX(), getPositionY(), getBlockSizeX(), getBlockSizeY());
					Graphics graphics = showImage.getGraphics();
					graphics.drawImage(temp.getScaledInstance(getBlockSizeX_(), getBlockSizeY_(), Image.SCALE_AREA_AVERAGING), getSX_(px), getSY_(py), this);
					graphics.dispose();
					panel.repaint();
				MainWindow.putLog("位置：(" + px + "," + py + ")");
				return;
			}
		}
	}
	/* 画像を追加する(位置認識Ver) */
	public void addImageX(BufferedImage image){
		if(image == null) return;
		int position = checkImageX(image);
		if(position < 0) return;
		position = getIndex(position);
		MainWindow.putLog("【自動取得】");
			ssBuffer.set(position, image);
			ssBufferFlag.set(position, true);
			int px = position % getBlocksX(), py = position / getBlocksX();
			BufferedImage temp = image.getSubimage(getPositionX(), getPositionY(), getBlockSizeX(), getBlockSizeY());
			Graphics graphics = showImage.getGraphics();
			graphics.drawImage(temp.getScaledInstance(getBlockSizeX_(), getBlockSizeY_(), Image.SCALE_AREA_AVERAGING), getSX_(px), getSY_(py), this);
			graphics.dispose();
			panel.repaint();
		MainWindow.putLog("位置：(" + px + "," + py + ")");
	}
	/* 画像を保存する */
	abstract void addSpecialFrame(BufferedImage image, int px1, int py1, int px2, int py2);
	public void savePicture(){
		// 最大領域を判断する
		int px1 = getBlocksX(), py1 = getBlocksY(), px2 = -1, py2 = -1;
		for(int x = 0; x < getBlocksX(); x++){
			for(int y = 0; y < getBlocksY(); y++){
				if(ssBufferFlag.get(y * getBlocksX() + x)){
					px1 = Math.min(px1, x);
					py1 = Math.min(py1, y);
					px2 = Math.max(px2, x);
					py2 = Math.max(py2, y);
				}
			}
		}
		if(px2 - px1 < 0) return;
		// 保存用バッファに画像を配置する
		MainWindow.putLog("【画像保存】");
		BufferedImage saveBuffer = new BufferedImage(getSX(getBlocksX()), getSY(getBlocksY()), BufferedImage.TYPE_INT_BGR);
		Graphics graphics = saveBuffer.getGraphics();
		for(int x = 0; x < getBlocksX(); x++){
			for(int y = 0; y < getBlocksY(); y++){
				BufferedImage temp = ssBuffer.get(y * getBlocksX() + x).getSubimage(getPositionX(), getPositionY(), getBlockSizeX(), getBlockSizeY());
				graphics.drawImage(temp, getSX(x), getSY(y), this);
			}
		}
		graphics.dispose();
		// 特殊な枠線を追加する
		if(OptionWindow.checkbox1.isSelected()){
			addSpecialFrame(saveBuffer, px1, py1, px2, py2);
		}
		// 画像の保存処理
		String saveName = DATE_FORMAT.format(Calendar.getInstance().getTime()) + ".png";
		try{
			ImageIO.write(saveBuffer.getSubimage(px1 * getBlockSizeX(), py1 * getBlockSizeY(), (px2 - px1 + 1) * getBlockSizeX(), (py2 - py1 + 1) * getBlockSizeY()), "png", new File(saveName));
			MainWindow.putLog(saveName);
			int option = JOptionPane.showConfirmDialog(this, "画像をクリアしますか？", "記録は大切なの", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(option == JOptionPane.YES_OPTION){
				// showImageの削除
				graphics = showImage.getGraphics();
				graphics.setColor(Color.white);
				graphics.fillRect(0, 0, windowX, windowY);
				graphics.dispose();
				// バッファ・フラグの削除
				for(int i = 0; i < blocksSize; i++){
					ssBuffer.set(i, clone(blankImage));
					ssBufferFlag.set(i, false);
				}
				panel.repaint();
			}
		}
		catch(Exception error){
			error.printStackTrace();
		}
	}
	/* 画像判定 */
	abstract boolean checkImage(BufferedImage image);
	abstract int checkImageX(BufferedImage image);
	public static boolean checkColor(BufferedImage image, int x, int y, int r, int g, int b){
		Color color = new Color(image.getRGB(x, y));
		int diffR = color.getRed() - r, diffG = color.getGreen() - g, diffB = color.getBlue() - b;
		int diff = diffR * diffR + diffG * diffG + diffB * diffB;
		if(diff < 500) return true;
		return false;
	}
	/* 座標取得 */
	int getPosition(MouseEvent event){
		Point point = event.getPoint();
		int mx = point.x / getBlockSizeX_(), my = point.y / getBlockSizeY_();
		if(mx < 0) mx = 0;
		if(mx >= getBlocksX()) mx = getBlocksX() - 1;
		if(my < 0) my = 0;
		if(my >= getBlocksY()) my = getBlocksY() - 1;
		return my * getBlocksX() + mx;
	}
	/* 画像をディープコピーする */
	BufferedImage clone(BufferedImage image){
		BufferedImage clone = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		Graphics graphics = clone.createGraphics();
		graphics.drawImage(image, 0, 0, null);
		graphics.dispose();
		return clone;
	}
	/* 別クラス */
	class joinPanel extends JPanel{
		@Override
		public void paintComponent(Graphics graphics){
			// 背景の絵を描写する
			graphics.drawImage(showImage, 0, 0, this);
			Graphics2D graphics2d = (Graphics2D)graphics;
			// マーカーを描写する
			if(pressFlag){
				BasicStroke wideStroke = new BasicStroke(FRAME_WIDTH);
				graphics2d.setStroke(wideStroke);
				int x1 = pressPosition % getBlocksX(), y1 = pressPosition / getBlocksX();
				int x2 = enterPosition % getBlocksX(), y2 = enterPosition / getBlocksX();
				graphics2d.setPaint(Color.BLUE);
				graphics2d.draw(new Rectangle2D.Double(getSX_(x1) + FRAME_WIDTH / 2, getSY_(y1) + FRAME_WIDTH / 2, getBlockSizeX_() - FRAME_WIDTH, getBlockSizeY_() - FRAME_WIDTH));
				graphics2d.setPaint(Color.RED);
				graphics2d.draw(new Rectangle2D.Double(getSX_(x2) + FRAME_WIDTH / 2, getSY_(y2) + FRAME_WIDTH / 2, getBlockSizeX_() - FRAME_WIDTH, getBlockSizeY_() - FRAME_WIDTH));
				BasicStroke normalStroke = new BasicStroke(1.0f);
				graphics2d.setStroke(normalStroke);
				graphics2d.setPaint(Color.BLACK);
			}
			// 枠線を描写する
			for(int x = 1; x <= getBlocksX() - 1; x++){
				graphics2d.draw(new Line2D.Double(getSX_(x), 0, getSX_(x), windowY));
			}
			for(int y = 1; y <= getBlocksY() - 1; y++){
				graphics2d.draw(new Line2D.Double(0, getSY_(y), windowX, getSY_(y)));
			}
			graphics.dispose();
			graphics2d.dispose();
		}
	}
	class DropFileHandler extends TransferHandler{
		@Override
		public boolean canImport(TransferSupport support){
			// ドロップされていない場合は受け取らない
			if(!support.isDrop()) return false;
			// ドロップされたものがファイルではない場合は受け取らない
			if(!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) return false;
			return true;
		}
		@Override
		public boolean importData(TransferSupport support){
			// 受け取っていいものか確認する
			if(!canImport(support)) return false;
			// ドロップ処理
			Transferable transferable = support.getTransferable();
			try{
				// ファイルを受け取る
				@SuppressWarnings("unchecked")
				List<File> files = (List<File>)transferable.getTransferData(DataFlavor.javaFileListFlavor);
				// 順番に読み込んで追加する
				for(File file : files){
					BufferedImage loadImage = ImageIO.read(file);
					addImage(loadImage);
				}
			}
			catch(Exception error){
				error.printStackTrace();
			}
			return true;
		}
	}
}
