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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public enum FrameOption {
	UNIT("改装一覧", 
			327, 103, 
			6, 6, 
			4, 8f, 
			Arrays.asList(getPair("縮小表示", 230, 365), getPair("通常表示", 460, 365)),
			Arrays.asList("行を優先", "列を優先", "艦隊優先")){
		@Override
		public boolean checkImage(BufferedImage image){return Savable.checkColor(image, 300, 172, 241, 191, 119);}
		private int checkImageX(int range, IntPredicate judge){return IntStream.range(0, range).filter(judge).findFirst().orElse(0);}
		@Override
		public int checkImageX(JoinWindow parent, BufferedImage image){
			if(!checkImage(image)) return -1;
			int i = checkImageX(4, n->Savable.checkColor(image, 136 + 30 * n, 122, 33, 150, 151));
			int j = checkImageX(6, n->Savable.checkColor(image, 294, 145 + 54 * n, 255, 102, 29));
			return i * j != 0 ? i * 6 + j : -1;
		}
	},
	SORT("ソート一覧", 
			392, 154,
			7, 5, 
			3, 4f, 
			Arrays.asList(getPair("縮小表示", 190, 279), getPair("通常表示", 382, 279), getPair("拡大表示", 382, 315)),
			Arrays.asList("行を優先", "列を優先")){
		@Override
		public boolean checkImage(BufferedImage image){
			return Savable.checkColor(image, 420, 118, 66,  60,  59) && Savable.checkColor(image, 374,  80, 30, 157, 160);
		}
		@Override
		public int checkImageX(JoinWindow parent, BufferedImage image) {
			if(!checkImage(image)) return -1;
			return IntStream.range(0, getBlocks().getRows() * getBlocks().getColumns()).map(i->parent.getIndex(i))
					.filter(p->((ImageLabel) parent.getContentPane().getComponent(p)).hasImage()).findFirst().orElse(-1);
		}
	};
	
	private String title;							// フレームのタイトル
	private Dimension position; 					// スクリーンショット開始位置
	private int blocksX, blocksY;					// 画像を配置するブロックの個数
	private int zooming;							// 実画像に対するウインドウの縮小率
	private float stroke;							// 画像保存時のストロークサイズ
	private List<Pair<Dimension>> blockSize;		// 画像を配置するブロックの大きさ
	private List<Pair<IntUnaryOperator>> sortType;	// ソートアルゴリズム
	static private Pair<Dimension> getPair(String name, int width, int height){return new Pair<>(name, new Dimension(width, height));}
	private FrameOption(String title, int positionX, int positionY, int blocksX, int blocksY, int zooming, float stroke,
			List<Pair<Dimension>> blocksize, List<String> sortType){
		this.title = title;
		this.position = new Dimension(positionX, positionY);
		this.blocksX = blocksX;
		this.blocksY = blocksY;
		this.zooming = zooming;
		this.stroke = stroke;
		this.blockSize = blocksize;
		this.sortType = sortType.stream().map(s->new Pair<>(s, getMethod(s))).collect(Collectors.toList());
	}
	private IntUnaryOperator getMethod(String index){
		switch(index){
			case "行を優先":
				return i->i;
			case "列を優先":
				return i->{
					int x = i / getBlocks().getRows(), y = i % getBlocks().getColumns();
					return y * getBlocks().getRows() + x;};
			case "艦隊優先":
				return i->new int[]{
					1,  2,  7,  8,  13, 14,
					3,  4,  9,  10, 15, 16,
					5,  6,  11, 12, 17, 18,
					19, 20, 25, 26, 31, 32, 
					21, 22, 27, 28, 33, 34,
					23, 24, 29, 30, 35, 36}[i];
			default: return i->0;
		}
	}
	public int getPositionX(){return position.width;}
	public int getPositionY() {return position.height;}
	public GridLayout getBlocks(){return new GridLayout(blocksX, blocksY, 0, 0);}
	public int getZooming(){return zooming;}
	public Stroke getStroke(){return new BasicStroke(stroke);}
	public List<Pair<Dimension>> getSizeList(){return Collections.unmodifiableList(blockSize);}
	public List<Pair<IntUnaryOperator>> getSortList(){return Collections.unmodifiableList(sortType);}
	/* 画像判定 */
	public abstract boolean checkImage(BufferedImage image);
	public abstract int checkImageX(JoinWindow parent, BufferedImage image);
	@Override
	public String toString(){return title;}
}
