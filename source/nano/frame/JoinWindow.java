package nano.frame;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;

import nano.LogManager;
import nano.image.Capturable;
import nano.image.Capture;
import nano.option.OptionData;

@SuppressWarnings("serial")
public class JoinWindow extends JFrame implements Capturable{
	/* メンバ変数 */
	// 変数
	private SortType sort;		// 表示方向(行優先・列優先・艦隊優先)
	private SizeType size;		// 表示種類(コンパクト・通常・エクストラ)
	private FrameOption option;
	/* コンストラクタ */
	public JoinWindow(FrameOption option, SortType sort, SizeType size){
		this.option = option;
		this.sort = sort;
		// ウィンドウ・オブジェクトの設定
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setAlwaysOnTop(true);
		addKeyListener(getKeyListener());
		setTransferHandler(getDropFileHandler());
		// オブジェクトにおける設定
		getContentPane().setLayout(new GridLayout(option.getRow(), option.getColumn()));
		getContentPane().setBackground(Color.WHITE);
		IntStream.range(0, option.getRow() * option.getColumn()).forEach(i->getContentPane().add(new ImageLabel()));
		setSizeType(size);
	}
	/* アクセッサ */
	public int getIndex(int i){return sort.getMethod(option).applyAsInt(i);}
	public void setSortType(SortType type){
		if(Objects.equals(sort, type)) return;
		// 一度入れ替えたものをもう一度入れ替えることで元の場所に戻す
		IntStream.range(0, getContentPane().getComponentCount()).filter(i->i < getIndex(i)).forEach(i->
		ImageLabel.swapImage((ImageLabel) getContentPane().getComponent(i), (ImageLabel) getContentPane().getComponent(getIndex(i))));
		sort = type;
		// 新しい配置方法を適応する
		IntStream.range(0, getContentPane().getComponentCount()).filter(i->i < getIndex(i)).forEach(i->
		ImageLabel.swapImage((ImageLabel) getContentPane().getComponent(i), (ImageLabel) getContentPane().getComponent(getIndex(i))));
		redraw();
	}
	public void setSizeType(SizeType type){
		if(size != null && size.equals(type)) return;
		size = type;
		setPreferredSize(new Dimension(option.packZoom(size.getWidth()) * option.getColumn(), option.packZoom(size.getHeight() * option.getRow())));
		redraw();
	}
	/* 表示を更新する */
	private void redraw(){
		setTitle(String.format("%s一覧-%s-%s", option.toString(), sort.toString(), size.toString()));
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
		IntStream.range(0, getContentPane().getComponentCount()).map(this::getIndex)
		.mapToObj(p->(ImageLabel) getContentPane().getComponent(p)).filter(il->!il.hasImage()).findFirst().ifPresent(il->{
			il.setImage(image.getSubimage(option.getPositionX(), option.getPositionY(), size.getWidth(), size.getHeight()));
			LogManager.getLogger().appendLog("【画像追加】");
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
			il.setImage(image.getSubimage(option.getPositionX(), option.getPositionY(), size.getWidth(), size.getHeight()));
			LogManager.getLogger().appendLog("【自動取得】");
			LogManager.getLogger().appendLog("位置：" + il.toString());
		});	
	}
	/* 画像を保存する */
	private boolean exclude(int arg, int ...excludes){return !IntStream.of(excludes).anyMatch(exclude->exclude == arg);}
	private void drawFrame(Graphics2D graphics, int x1, int y1, int x2, int y2){
		graphics.draw(new Line2D.Double(size.getWidth(x1), size.getHeight(y1), size.getWidth(x2), size.getHeight(y2)));
	}
	private void addSpecialFrame(Graphics2D graphics, int px1, int py1, int px2, int py2){
		graphics.setStroke(new BasicStroke(option.getStroke()));
		graphics.setPaint(Color.BLUE);
		// 終端を除く各行・列に線を引く
		switch(sort){
			case ROW:
				IntStream.range(py1 + 1, py2 + 1).forEach(y->drawFrame(graphics, px1, y, px2 + 1, y));
				break;
			case COLUMN:
				IntStream.range(px1 + 1, px2 + 1).forEach(x->drawFrame(graphics, x, py1, x, py2 + 1));
				break;
			case TEAM:
				IntStream.of(3).filter(y->exclude(y, py1, py2 + 1)).forEach(y->drawFrame(graphics, px1, y, px2 + 1, y));
				IntStream.of(2, 4).filter(x->exclude(x, px1, px2 + 1)).forEach(x->drawFrame(graphics, x, py1, x, py2 + 1));
				break;
		}
		// 艦隊番号を付与する
		if(option.equals(FrameOption.UNIT)){
			graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			graphics.setFont(new Font("Arial", Font.PLAIN, 40));
			IntStream.range(0, getContentPane().getComponentCount()).forEach(i->{
				ImageLabel l1 = (ImageLabel) getContentPane().getComponent(i), l2 = (ImageLabel) getContentPane().getComponent(getIndex(i));
				graphics.setPaint(Color.WHITE);
				graphics.drawString(String.format("%d-%d", l1.getPY() + 1, l1.getPX() + 1), size.getWidth(l2.getPX() + 1) - 70,  size.getHeight(l2.getPY()) + 40);
				graphics.setPaint(Color.RED);
				graphics.drawString(String.format("%d-%d", l1.getPY() + 1, l1.getPX() + 1), size.getWidth(l2.getPX() + 1) - 72,  size.getHeight(l2.getPY()) + 38);
			});
		}
	}
	@Override
	public void savePicture(){
		// 最大領域を判断する(左隅のラベルと右隅のラベルを取得する)
		List<ImageLabel> list = Arrays.stream(getContentPane().getComponents())
				.map(c->(ImageLabel) c).filter(ImageLabel::hasImage).collect(Collectors.toList());
		if(list.isEmpty()) return;
		ImageLabel min = (ImageLabel) getContentPane().getComponentAt(list.stream().mapToInt(ImageLabel::getX).min().getAsInt(), 
				list.stream().mapToInt(ImageLabel::getY).min().getAsInt());
		ImageLabel max = (ImageLabel) getContentPane().getComponentAt(list.stream().mapToInt(ImageLabel::getX).max().getAsInt(), 
				list.stream().mapToInt(ImageLabel::getY).max().getAsInt());
		// 保存用バッファに画像を配置する
		BufferedImage saveBuffer = new BufferedImage(size.getWidth(option.getColumn()), size.getHeight(option.getRow()), BufferedImage.TYPE_INT_BGR);
		Graphics2D graphics = saveBuffer.createGraphics();
		list.forEach(il->graphics.drawImage(il.getImage(), size.getWidth(il.getPX()), size.getHeight(il.getPY()), this));
		// 特殊な枠線を追加する
		if(OptionData.getData().drawFrame())
			addSpecialFrame(graphics, min.getPX(), min.getPY(), max.getPX(), max.getPY());
		graphics.dispose();
		Capturable.savePicture(saveBuffer.getSubimage(size.getWidth(min.getPX()), size.getHeight(min.getPX()),
				 size.getWidth(max.getPX() - min.getPX() + 1), size.getHeight(max.getPY() - min.getPY() + 1)));
		int option = JOptionPane.showConfirmDialog(this, "画像をクリアしますか？", "記録は大切なの", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(option == JOptionPane.YES_OPTION)
			Arrays.stream(getContentPane().getComponents()).map(c->(ImageLabel) c).forEach(ImageLabel::clearImage);
	}
}
