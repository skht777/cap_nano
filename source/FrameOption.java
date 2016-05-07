/**/

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum FrameOption {
	UNIT("改装", 
			327, 103, 
			6, 6, 
			4, 8f, 
			Arrays.asList(getPair("通常表示", 460, 365), getPair("縮小表示", 230, 365)),
			Arrays.asList(SortType.ROW, SortType.COLUMN, SortType.TEAM),
			new Scan[]{Scan.UNIT}){
		@Override
		public int checkImageX(BufferedImage image){
			if(!checkImage(image)) return -1;
			int i = Scan.UNIT1.check(image, 4, 30, 0), j = Scan.UNIT2.check(image, 6, 0, 54);
			return i == -1 || j == -1 ? -1 : i * 6 + j;
		}
	},
	SORT("ソート", 
			392, 154,
			7, 5, 
			3, 4f, 
			Arrays.asList(getPair("通常表示", 382, 279), getPair("縮小表示", 190, 279),  getPair("拡大表示", 382, 315)),
			Arrays.asList(SortType.ROW, SortType.COLUMN),
			new Scan[]{Scan.SORT1, Scan.SORT2}){
		@Override
		public int checkImageX(BufferedImage image){return -1;}
	};
	private String title;							// フレームのタイトル
	private int positionX, positionY; 				// スクリーンショット開始位置
	private int blocksX, blocksY;					// 画像を配置するブロックの個数
	private int zooming;							// 実画像に対するウインドウの縮小率
	private float stroke;							// 画像保存時のストロークサイズ
	private List<Pair<Dimension>> blockSize;		// 画像を配置するブロックの大きさ
	private List<SortType> sortType;				// ソートアルゴリズム
	private Scan[] scans;
	/* 画像判定 */
	public abstract int checkImageX(BufferedImage image);
	
	static private Pair<Dimension> getPair(String name, int width, int height){return new Pair<>(name, new Dimension(width, height));}
	private FrameOption(String title, int positionX, int positionY, int blocksX, int blocksY, int zooming, float stroke,
			List<Pair<Dimension>> blocksize, List<SortType> sortType, Scan[] scans){
		this.title = title;
		this.positionX = positionX;
		this.positionY = positionY;
		this.blocksX = blocksX;
		this.blocksY = blocksY;
		this.zooming = zooming;
		this.stroke = stroke;
		this.blockSize = Collections.unmodifiableList(blocksize);
		this.sortType = Collections.unmodifiableList(sortType);
		this.scans = scans;
	}
	public int getPositionX(){return positionX;}
	public int getPositionY() {return positionY;}
	public int getRow(){return blocksX;}
	public int getColumn(){return blocksY;}
	public GridLayout getBlocks(){return new GridLayout(blocksX, blocksY, 0, 0);}
	public int getZoomed(int size){return size * zooming;}
	public Stroke getStroke(){return new BasicStroke(stroke);}
	public List<Pair<Dimension>> getSizeList(){return blockSize;}
	public List<SortType> getSortList(){return sortType;}
	public boolean checkImage(BufferedImage image){return Scan.check(image, scans);}
	public Dimension getWindowSize(Dimension size){return new Dimension(size.width / zooming * blocksX, size.height / zooming * blocksY);}
	@Override
	public String toString(){return title;}
}
