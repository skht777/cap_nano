/* ソート一覧ウィンドウ */

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

@SuppressWarnings("serial")
public class SortWindow extends JoinWindow{
	/* メンバ変数 */
	// 定数
	static final int POSITION_X = 392;
	static final int POSITION_Y = 154;
	static final int[] BLOCK_SIZE_X = {190, 382, 382};
	static final int[] BLOCK_SIZE_Y = {279, 279, 315};
	static final int BLOCKS_X = 7;
	static final int BLOCKS_Y = 5;
	static final int ZOOMING = 3;
	public static final String[] SHOW_DIR_STR  = {"行を優先", "列を優先"};
	public static final String[] SHOW_TYPE_STR = {"縮小表示", "通常表示", "拡張表示"};
	static final String TITLE = "ソート一覧";
	/* コンストラクタ */
	SortWindow(){
		super(new Dimension(BLOCK_SIZE_X[0], BLOCK_SIZE_Y[0]), new GridLayout(BLOCKS_X, BLOCKS_Y, 0, 0), ZOOMING);
	}
	/* アクセッサ */
	int getPositionX(){return POSITION_X;}
	int getPositionY(){return POSITION_Y;}
	protected float getStrokeSize(){return 4.0f;}
	String getWindowTitle(){return TITLE + " - " + SHOW_DIR_STR[getSortType()] + " - " + SHOW_TYPE_STR[getSortType()];}
	/* 画像判定 */
	// FIXME
	boolean checkImage(BufferedImage image){
		return checkColor(image, 420, 118, 66,  60,  59) && checkColor(image, 374,  80, 30, 157, 160);
	}
	int checkImageX(BufferedImage image){
		if(!checkImage(image)) return -1;
		return IntStream.range(0, getBlocksX() * getBlocksY()).map(i->getIndex(i))
				.filter(p->((ImageLabel) getContentPane().getComponent(p)).hasImage()).findFirst().orElse(-1);
	}
}
