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
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
public class JoinWindow extends JFrame implements Capturable{
	/* メンバ変数 */
	// 変数
	private IntUnaryOperator sort;	// 表示方向(行優先・列優先・艦隊優先)
	private Dimension size;			// 表示種類(コンパクト・通常・エクストラ)
	private FrameOption option;
	private String sortName = "", sizeName = "";
	/* コンストラクタ */
	JoinWindow(FrameOption option){
		this.option = option;
		// ウィンドウ・オブジェクトの設定
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setAlwaysOnTop(true);
		addKeyListener(getKeyListener());
		setTransferHandler(getDropFileHandler());
		// ウィンドウにおける設定
		setSizeType(option.getSizeList().get(0));
		setSortType(option.getSortList().get(0));
		// オブジェクトにおける設定
		getContentPane().setLayout(option.getBlocks());
		getContentPane().setBackground(Color.WHITE);
		
		IntStream.range(0, option.getRow() * option.getColumn()).forEach(i->getContentPane().add(new ImageLabel()));
	}
	/* アクセッサ */
	public int getIndex(int i){return sort.applyAsInt(i);}
	public void setSortType(SortType type){
		sortName = type.toString();
		sort = type.getMethod(option);
		IntStream.range(0, getContentPane().getComponentCount()).forEach(i->
		ImageLabel.swapImage((ImageLabel) getContentPane().getComponent(i), (ImageLabel) getContentPane().getComponent(getIndex(i))));
		redraw();
	}
	public void setSizeType(Pair<Dimension> type){
		sizeName = type.toString();
		size = type.getValue();
		if(!type.getValue().equals(getPreferredSize())) setPreferredSize(option.getWindowSize(type.getValue()));
		redraw();
	}
	/* 表示を更新する */
	private void redraw(){
		setTitle(String.format("%s一覧-%s-%s", option.toString(), sortName, sizeName));
		pack();
		validate();
	}
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
		return new TransferHandler(){
			@Override
			@SuppressWarnings("unchecked")
			public boolean importData(TransferSupport support){
				// ドロップされていないかドロップされたものがファイルではない場合は受け取らない
				if(!support.isDrop() && support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) return false;
				// ドロップ処理、ファイルを受け取り順番に読み込んで追加する
				try{
					((List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor)).forEach(file->addImage(file));
				}catch(Exception e){}
				return true;
			}
		};
	}
	/* 画像を追加する */
	public void addImage(BufferedImage image){
		if(image == null || !option.checkImage(image)) return;
		Arrays.stream(getContentPane().getComponents()).map(c->(ImageLabel) c).filter(il->!il.hasImage()).findFirst().ifPresent(il->{
			LogManager.getLogger().appendLog("【画像追加】");
			il.setImage(image.getSubimage(option.getPositionX(), option.getPositionY(), size.width, size.height));
			LogManager.getLogger().appendLog("位置：" + il.toString());		
		});
	}
	public void addImage(File file){
		try {
			addImage(ImageIO.read(file));
		}catch(IOException error){}
	}
	/* 画像を追加する(位置認識Ver) */
	public void addImageX(BufferedImage image){
		Optional.ofNullable(image).filter(im->option.checkImageX(image) >= 0)
		.map(im->(ImageLabel) getContentPane().getComponent(option.checkImageX(image))).ifPresent(il->{
			// image != null && p >= 0 の際に実行される
			LogManager.getLogger().appendLog("【自動取得】");
			il.setImage(image.getSubimage(option.getPositionX(), option.getPositionY(), size.width, size.height));
			LogManager.getLogger().appendLog("位置：" + il.toString());
		});	
	}
	/* 画像を保存する */
	private boolean exclude(int arg, int ...excludes){return !IntStream.of(excludes).anyMatch(exclude->exclude == arg);}
	private void drawFrame(Graphics2D graphics, double x1, double y1, double x2, double y2){
		graphics.draw(new Line2D.Double(x1 * size.width, y1 * size.height, x2 * size.width, y2 * size.height));
	}
	private void addSpecialFrame(Graphics2D graphics, int px1, int py1, int px2, int py2){
		graphics.setStroke(option.getStroke());
		graphics.setPaint(Color.BLUE);
		switch(sort.toString()){
			case "行を優先":
				IntStream.range(1, option.getColumn()).filter(y->exclude(y, py1, py2 + 1)).forEach(y->IntStream.range(0, option.getRow())
						.forEach(x->drawFrame(graphics, x, y, x + 1, y)));
				break;
			case "列を優先":
				IntStream.range(1, option.getRow()).filter(x->exclude(x, py1, py2 + 1)).forEach(x->IntStream.range(0, option.getRow())
						.forEach(y->drawFrame(graphics, x, y, x, y + 1)));
				break;
			case "艦隊優先":
				IntStream.range(0, option.getRow()).filter(x->exclude(3, py1, py2 + 1)).forEach(x->drawFrame(graphics, x, 3, x + 1, 3));
				IntStream.range(0, option.getColumn()).filter(x->exclude(2, py1, py2 + 1)).forEach(x->drawFrame(graphics, 2, 0, 2, option.getColumn()));
				IntStream.range(0, option.getColumn()).filter(x->exclude(2, py1, py2 + 1)).forEach(x->drawFrame(graphics, 4, 0, 4, option.getColumn()));
				break;
		}
		if(option.equals(FrameOption.UNIT)){
			graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			graphics.setFont(new Font("Arial", Font.PLAIN, 40));
			IntStream.range(0, option.getColumn()).forEach(y->IntStream.range(0, option.getRow()).forEach(x->{
				int px = getIndex(y * option.getColumn() + x) % option.getRow(), py = getIndex(y * option.getColumn() + x) / option.getRow();
				graphics.setPaint(Color.WHITE);
				graphics.drawString(String.format("%d-%d", y + 1, x + 1), (px + 1) * size.width - 70, py * size.height + 40);
				graphics.setPaint(Color.RED);
				graphics.drawString(String.format("%d-%d", y + 1, x + 1), (px + 1) * size.width - 72, py * size.height + 38);
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
		BufferedImage saveBuffer = new BufferedImage(option.getZoomed(getWidth()), option.getZoomed(getHeight()), BufferedImage.TYPE_INT_BGR);
		Graphics2D graphics = saveBuffer.createGraphics();
		list.forEach(il->graphics.drawImage(il.getImage(), option.getZoomed(il.getX()), option.getZoomed(il.getY()), this));
		// 特殊な枠線を追加する
		//if(addFrame)
			addSpecialFrame(graphics, min.getIndexX(), min.getIndexY(), max.getIndexX(), max.getIndexY());
		graphics.dispose();
		Capturable.savePicture(saveBuffer.getSubimage(option.getZoomed(min.getX()), option.getZoomed(min.getY()),
				 option.getZoomed((max.getX() - min.getX())), option.getZoomed((max.getY() - min.getY()))));
		int option = JOptionPane.showConfirmDialog(this, "画像をクリアしますか？", "記録は大切なの", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(option == JOptionPane.YES_OPTION)
			Arrays.stream(getContentPane().getComponents()).map(c->(ImageLabel) c).forEach(ImageLabel::clearImage);
	}
}
