/* 改装一覧ウィンドウ */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.geom.Line2D;

@SuppressWarnings("serial")
public class UnitWindow extends JoinWindow{
	/* メンバ変数 */
	// 定数
	static final int[] POSITION_X = {327, 327};
	static final int[] POSITION_Y = {103, 103};
	static final int[] BLOCK_SIZE_X = {230, 460};
	static final int[] BLOCK_SIZE_Y = {365, 365};
	static final int BLOCKSX = 6;
	static final int BLOCKSY = 6;
	static final int ZOOMING = 4;
	public static final String[] SHOW_DIR_STR  = {"行を優先", "列を優先", "艦隊優先"};
	public static final String[] SHOW_TYPE_STR = {"縮小表示", "通常表示"};
	static final String TITLE = "改装一覧";
	static final BasicStroke WIDE_STROKE = new BasicStroke(8.0f);
	// 変数
	static int showDir  = 0;	//表示方向(行優先・列優先)
	static int showType = 1;	//表示種類(コンパクト・通常)
	static int[] index = new int[BLOCKSX * BLOCKSY];
	/* コンストラクタ */
	UnitWindow(){
		super();
		int count = 0;
		for(int j = 0; j < 2; j++){
			for(int i = 0; i < 3; i++){
				for(int n = 0; n < 3; n++){
					for(int m = 0; m < 2; m++){
						int x = i * 2 + m, y = j * 3 + n;
						index[y * BLOCKSX + x] = count;
						count++;
					}
				}
			}
		}
	}
	/* アクセッサ */
	int getPositionX(){return POSITION_X[showType];}
	int getPositionY(){return POSITION_Y[showType];}
	int getBlockSizeX(){return BLOCK_SIZE_X[showType];}
	int getBlockSizeY(){return BLOCK_SIZE_Y[showType];}
	int getBlocksX(){return BLOCKSX;}
	int getBlocksY(){return BLOCKSY;}
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
				int x = i / BLOCKSY, y = i % BLOCKSY;
				return y * BLOCKSX + x;
			case 2:	//ハイブリッド
				return index[i];
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
				for(int y = 1; y < BLOCKSY; y++){
					for(int x = 0; x < BLOCKSX; x++){
						if((y != py1) && (y != py2 + 1)){
							graphics2d.draw(new Line2D.Double(getSX(x), getSY(y), getSX(x + 1), getSY(y)));
						}
					}
				}
				break;
			case 1:	//列を優先
				for(int x = 1; x < BLOCKSX; x++){
					for(int y = 0; y < BLOCKSY; y++){
						if((x != px1) && (x != px2 + 1)){
							graphics2d.draw(new Line2D.Double(getSX(x), getSY(y), getSX(x), getSY(y + 1)));
						}
					}
				}
				break;
			case 2:	//艦隊優先
				for(int x = 0; x < BLOCKSX; x++){
					if((3 != py1) && (3 != py2 + 1)){
						graphics2d.draw(new Line2D.Double(getSX(x), getSY(3), getSX(x + 1), getSY(3)));
					}
				}
				for(int y = 0; y < BLOCKSY; y++){
					if((2 != px1) && (2 != px2 + 1)){
						graphics2d.draw(new Line2D.Double(getSX(2), 0, getSX(2), getSY(BLOCKSY)));
					}
					if((4 != px1) && (4 != px2 + 1)){
						graphics2d.draw(new Line2D.Double(getSX(4), 0, getSX(4), getSY(BLOCKSY)));
					}
				}
				break;
		}
		graphics2d.setPaint(Color.WHITE);
		graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Font font = new Font("Arial", Font.PLAIN, 40);
		graphics2d.setFont(font);
		for(int i = 0; i < BLOCKSX * BLOCKSY; i++){
			int p = getIndex(i);
			int px = p % BLOCKSX, py = p / BLOCKSX;
			graphics2d.drawString("" + (i / BLOCKSX + 1) + "-" + ((i % BLOCKSX) + 1), getSX(px + 1) - 70, getSY(py) + 40);
		}
		graphics2d.setPaint(Color.RED);
		graphics2d.setFont(font);
		for(int i = 0; i < BLOCKSX * BLOCKSY; i++){
			int p = getIndex(i);
			int px = p % BLOCKSX, py = p / BLOCKSX;
			graphics2d.drawString("" + (i / BLOCKSX + 1) + "-" + ((i % BLOCKSX) + 1), getSX(px + 1) - 72, getSY(py) + 38);
		}
		graphics2d.dispose();
	}
	/* 画像判定 */
	boolean checkImage(BufferedImage image){
		return checkColor(image, 300, 172, 241, 191, 119);
	}
	int checkImageX(BufferedImage image){
		if(!checkColor(image, 300, 172, 241, 191, 119)) return -1;
		for(int i = 0; i < 4; i++){
			if(checkColor(image, 136 + 30 * i, 122, 33, 150, 151)){
				for(int j = 0; j < 6; j++){
					if(checkColor(image, 294, 145 + 54 * j, 255, 102, 29)){
						return i * 6 + j;
					}
				}
			}
		}
		return -1;
	}
}
