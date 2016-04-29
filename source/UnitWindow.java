/* 改装一覧ウィンドウ */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

@SuppressWarnings("serial")
public class UnitWindow extends JoinWindow{
	/* メンバ変数 */
	// 定数
	static final int POSITION_X = 327;
	static final int POSITION_Y = 103;
	static final Dimension[] BLOCK_SIZE = {new Dimension(230, 365), new Dimension(460, 365)};
	static final int BLOCKS_X = 6;
	static final int BLOCKS_Y = 6;
	static final int ZOOMING = 4;
	public static final String[] SHOW_DIR_STR  = {"行を優先", "列を優先", "艦隊優先"};
	public static final String[] SHOW_TYPE_STR = {"縮小表示", "通常表示"};
	final String TITLE = "改装一覧";
	/* コンストラクタ */
	UnitWindow(){
		super(new Dimension(BLOCK_SIZE[0].width, BLOCK_SIZE[0].height), new GridLayout(BLOCKS_X, BLOCKS_Y, 0, 0), ZOOMING);
	}
	/* アクセッサ */
	int getPositionX(){return POSITION_X;}
	int getPositionY(){return POSITION_Y;}
	protected float getStrokeSize(){return 8.0f;}
	String getWindowTitle(){return TITLE + " - " + SHOW_DIR_STR[getSortType()] + " - " + SHOW_TYPE_STR[getSizeType()];}
	/* 画像処理 */
	protected void addSpecialFrame(Graphics2D graphics, int px1, int py1, int px2, int py2){
		super.addSpecialFrame(graphics, px1, py1, px2, py2);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setFont(new Font("Arial", Font.PLAIN, 40));
		IntStream.range(0, getBlocksY()).forEach(y->IntStream.range(0, getBlocksX()).forEach(x->{
			int px = getIndex(y * getBlocksY() + x) % getBlocksX(), py = getIndex(y * getBlocksY() + x) / getBlocksX();
			graphics.setPaint(Color.WHITE);
			graphics.drawString(String.format("%d-%d", y + 1, x + 1), (px + 1) * getBlockSizeX() - 70, py * getBlockSizeY() + 40);
			graphics.setPaint(Color.RED);
			graphics.drawString(String.format("%d-%d", y + 1, x + 1), (px + 1) * getBlockSizeX() - 72, py * getBlockSizeY() + 38);
		}));
	}
	/* 画像判定 */
	// FIXME
	boolean checkImage(BufferedImage image){
		return checkColor(image, 300, 172, 241, 191, 119);
	}
	private int checkImageX(int range, IntPredicate judge) {
		return IntStream.range(0, range).filter(judge).findFirst().orElse(0);
	}
	int checkImageX(BufferedImage image){
		if(!checkImage(image)) return -1;
		int i = checkImageX(4, n->checkColor(image, 136 + 30 * n, 122, 33, 150, 151));
		int j = checkImageX(6, n->checkColor(image, 294, 145 + 54 * n, 255, 102, 29));
		return i * j != 0 ? i * 6 + j : -1;
	}
}
