/**/

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.function.ToIntBiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public enum FrameOption {
	UNIT("改装一覧", 
			327, 103, 
			6, 6, 
			4, 8f, 
			Arrays.asList(getPair("縮小表示", 230, 365), getPair("通常表示", 460, 365)),
			Arrays.asList(SortType.ROW, SortType.COLUMN, SortType.TEAM)){
		@Override
		public boolean checkImage(BufferedImage image){return Capturable.checkColor(image, 300, 172, 241, 191, 119);}
		private int checkImageX(int range, IntPredicate judge){return IntStream.range(0, range).filter(judge).findFirst().orElse(0);}
		@Override
		public ImageLabel checkImageX(JoinWindow parent, BufferedImage image){
			if(!checkImage(image)) return null;
			int i = checkImageX(4, n->Capturable.checkColor(image, 136 + 30 * n, 122, 33, 150, 151));
			int j = checkImageX(6, n->Capturable.checkColor(image, 294, 145 + 54 * n, 255, 102, 29));
			return i * j != 0 ? (ImageLabel) parent.getContentPane().getComponent(i * 6 + j) : null;
		}
	},
	SORT("ソート一覧", 
			392, 154,
			7, 5, 
			3, 4f, 
			Arrays.asList(getPair("縮小表示", 190, 279), getPair("通常表示", 382, 279), getPair("拡大表示", 382, 315)),
			Arrays.asList(SortType.ROW, SortType.COLUMN)){
		@Override
		public boolean checkImage(BufferedImage image){
			return Capturable.checkColor(image, 420, 118, 66,  60,  59) && Capturable.checkColor(image, 374,  80, 30, 157, 160);
		}
		@Override
		public ImageLabel checkImageX(JoinWindow parent, BufferedImage image) {
			if(!checkImage(image)) return null;
			return IntStream.range(0, parent.getContentPane().getComponentCount()).map(i->parent.getIndex(i))
					.mapToObj(p->(ImageLabel) parent.getContentPane().getComponent(p)).filter(ImageLabel::hasImage).findFirst().orElse(null);
		}
	};
	private String title;							// フレームのタイトル
	private int positionX, positionY; 				// スクリーンショット開始位置
	private int blocksX, blocksY;					// 画像を配置するブロックの個数
	private int zooming;							// 実画像に対するウインドウの縮小率
	private float stroke;							// 画像保存時のストロークサイズ
	private List<Pair<Dimension>> blockSize;		// 画像を配置するブロックの大きさ
	private List<Pair<IntUnaryOperator>> sortType;	// ソートアルゴリズム
	/* 画像判定 */
	public abstract boolean checkImage(BufferedImage image);
	public abstract ImageLabel checkImageX(JoinWindow parent, BufferedImage image);
	
	static private BasicPair<Dimension> getPair(String name, int width, int height){return new BasicPair<>(name, new Dimension(width, height));}
	private FrameOption(String title, int positionX, int positionY, int blocksX, int blocksY, int zooming, float stroke,
			List<BasicPair<Dimension>> blocksize, List<SortType> sortType){
		this.title = title;
		this.positionX = positionX;
		this.positionY = positionY;
		this.blocksX = blocksX;
		this.blocksY = blocksY;
		this.zooming = zooming;
		this.stroke = stroke;
		this.blockSize = Collections.unmodifiableList(blocksize);
		this.sortType = Collections.unmodifiableList(sortType.stream()
				.map(s->new BasicPair<>(s.toString(), s.getMethod(this))).collect(Collectors.toList()));
	}
	private static enum SortType{
		ROW("行を優先", (i,o)->i),
		COLUMN("列を優先", (i, o)->{
			int x = i / o.getRow(), y = i % o.getColumn();
			return y * o.getRow() + x;
				}),
		TEAM("艦隊優先", (i, o)->new int[]{
				1,  2,  7,  8,  13, 14,
				3,  4,  9,  10, 15, 16,
				5,  6,  11, 12, 17, 18,
				19, 20, 25, 26, 31, 32, 
				21, 22, 27, 28, 33, 34,
				23, 24, 29, 30, 35, 36}[i]);
		private String name;
		private ToIntBiFunction<Integer, FrameOption> method;
		private SortType(String name, ToIntBiFunction<Integer, FrameOption> method){
			this.name = name;
			this.method = method;
		}
		public IntUnaryOperator getMethod(FrameOption option){return i->method.applyAsInt(i, option);}
		@Override
		public String toString(){
			return name;
		}
	}
	public int getPositionX(){return positionX;}
	public int getPositionY() {return positionY;}
	public int getRow(){return blocksX;}
	public int getColumn(){return blocksY;}
	public GridLayout getBlocks(){return new GridLayout(blocksX, blocksY, 0, 0);}
	public int getZoomed(int size){return size * zooming;}
	public Stroke getStroke(){return new BasicStroke(stroke);}
	public List<Pair<Dimension>> getSizeList(){return blockSize;}
	public List<Pair<IntUnaryOperator>> getSortList(){return sortType;}
	public Dimension getWindowSize(Dimension size){return new Dimension(size.width / zooming * blocksX, size.height / zooming * blocksY);}
	@Override
	public String toString(){return title;}
}
