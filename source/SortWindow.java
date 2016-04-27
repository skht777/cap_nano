/* ソート一覧ウィンドウ */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;
import java.awt.geom.Line2D;

@SuppressWarnings("serial")
public class SortWindow extends JoinWindow{
	/* メンバ変数 */
	// 定数
	static final int[] POSITION_X = {392, 392, 392};
	static final int[] POSITION_Y = {154, 154, 154};
	static final int[] BLOCK_SIZE_X = {190, 382, 382};
	static final int[] BLOCK_SIZE_Y = {279, 279, 315};
	static final int BLOCKS_X = 7;
	static final int BLOCKS_Y = 5;
	static final int ZOOMING = 3;
	public static final String[] SHOW_DIR_STR  = {"行を優先", "列を優先"};
	public static final String[] SHOW_TYPE_STR = {"縮小表示", "通常表示", "拡張表示"};
	static final String TITLE = "ソート一覧";
	static final BasicStroke WIDE_STROKE = new BasicStroke(4.0f);
	// 変数
	static int showDir  = 0;	//表示方向(行優先・列優先)
	static int showType = 0;	//表示種類(コンパクト・通常・エクストラ)
	/* コンストラクタ */
	SortWindow(){
		super();
	}
	/* アクセッサ */
	int getPositionX(){return POSITION_X[showType];}
	int getPositionY(){return POSITION_Y[showType];}
	int getBlockSizeX(){return BLOCK_SIZE_X[showType];}
	int getBlockSizeY(){return BLOCK_SIZE_Y[showType];}
	int getBlocksX(){return BLOCKS_X;}
	int getBlocksY(){return BLOCKS_Y;}
	void setDir(int dir){showDir = dir;}
	void setType(int type){showType = type;}
	int getBlockSizeX_(){return BLOCK_SIZE_X[showType] / ZOOMING;}
	int getBlockSizeY_(){return BLOCK_SIZE_Y[showType] / ZOOMING;}
	int getSX(int x){return getBlockSizeX() * x;}
	int getSY(int y){return getBlockSizeY() * y;}
	int getSX_(int x){return getBlockSizeX_() * x;}
	int getSY_(int y){return getBlockSizeY_() * y;}
	String getWindowTitle(){return TITLE + " - " + SHOW_DIR_STR[showDir] + " - " + SHOW_TYPE_STR[showType];}
	int getIndex(int i){
		switch(showDir){
			case 0:	//行を優先
				return i;
			case 1:	//列を優先
				int x = i / BLOCKS_Y, y = i % BLOCKS_Y;
				return y * BLOCKS_X + x;
		}
		return 0;
	}
	/* 画像処理 */
	void addSpecialFrame(BufferedImage image, int px1, int py1, int px2, int py2){
		Graphics2D graphics2d = (Graphics2D)image.getGraphics();
		graphics2d.setStroke(WIDE_STROKE);
		graphics2d.setPaint(Color.BLUE);
		switch(showDir){
			case 0:	//行を優先
				for(int y = 1; y < BLOCKS_Y; y++){
					for(int x = 0; x < BLOCKS_X; x++){
						if((y != py1) && (y != py2 + 1)){
							graphics2d.draw(new Line2D.Double(getSX(x), getSY(y), getSX(x + 1), getSY(y)));
						}
					}
				}
				break;
			case 1:	//列を優先
				for(int x = 1; x < BLOCKS_X; x++){
					for(int y = 0; y < BLOCKS_Y; y++){
						if((x != px1) && (x != px2 + 1)){
							graphics2d.draw(new Line2D.Double(getSX(x), getSY(y), getSX(x), getSY(y + 1)));
						}
					}
				}
				break;
		}
		graphics2d.dispose();
	}
	/* 画像判定 */
	// FIXME
	boolean checkImage(BufferedImage image){
		return checkColor(image, 420, 118, 66,  60,  59) && checkColor(image, 374,  80, 30, 157, 160);
	}
	int checkImageX(BufferedImage image){
		if(!checkImage(image)) return -1;
		return IntStream.range(0, blocksSize).map(i->getIndex(i))
				.filter(p->((ImageLabel) getContentPane().getComponent(p)).hasImage()).findFirst().orElse(-1);
	}
}
