package nano.frame;
/**/

import java.awt.image.BufferedImage;
import java.util.function.ToIntFunction;

import nano.Scan;

public enum FrameOption{
	UNIT("改装", 
			327, 103, 
			6, 6, 
			4, 8f,
			new Scan[]{Scan.UNIT},
			image->{
				int i = Scan.UNIT1.check(image, 4, 30, 0), j = Scan.UNIT2.check(image, 6, 0, 54);
				return i == -1 || j == -1 ? -1 : i * 6 + j;}){
	},
	SORT("ソート", 
			392, 154,
			7, 5, 
			3, 4f, 
			new Scan[]{Scan.SORT1, Scan.SORT2}, image->-1);
	private String title;						// フレームのタイトル
	private int positionX, positionY; 			// スクリーンショット開始位置
	private int blocksX, blocksY;				// 画像を配置するブロックの個数
	private int zooming;						// 実画像に対するウインドウの縮小率
	private float stroke;						// 画像保存時のストロークサイズ
	private Scan[] scans;						//
	private ToIntFunction<BufferedImage> check;	//
	private FrameOption(String title, int positionX, int positionY, int blocksX, int blocksY, int zooming, float stroke,
			Scan[] scans, ToIntFunction<BufferedImage> check){
		this.title = title;
		this.positionX = positionX;
		this.positionY = positionY;
		this.blocksX = blocksX;
		this.blocksY = blocksY;
		this.zooming = zooming;
		this.stroke = stroke;
		this.scans = scans;
		this.check = check;
	}
	int getPositionX(){return positionX;}
	int getPositionY() {return positionY;}
	int getRow(){return blocksY;}
	int getColumn(){return blocksX;}
	int packZoom(int size){return size / zooming;}
	float getStroke(){return stroke;}
	/* 画像判定 */
	public boolean checkImage(BufferedImage image){return Scan.check(image, scans);}
	public int checkImageX(BufferedImage image){return Scan.check(image, scans) ? check.applyAsInt(image) : -1;};
	@Override
	public String toString(){return title;}
}
