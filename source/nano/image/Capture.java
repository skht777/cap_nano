package nano.image;
/* 画像取得用 */

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import nano.LogManager;

public class Capture{
	/* メンバ変数 */
	// 定数
	public static final int FLASH_X = 800;
	public static final int FLASH_Y = 480;
	static final int JUDGE_X = FLASH_X + 2;
	static final int JUDGE_Y = FLASH_Y + 2;
	static final int WHITE = 0xffffff;
	// 変数
	static List<Integer> gdIndex, gcIndex;
	public static int displayIndex = -1, flashPX, flashPY;
	/* 艦これの座標を取得する */
	public static void getKancollePosition(){
		displayIndex = -1;
		LogManager.getLogger().appendLog("【座標取得】");
		// 座標を取得する処理
		// まず、全てのディスプレイにおけるスクショを取得する
		List<BufferedImage> images = new ArrayList<>();
		gdIndex = new ArrayList<>();
		gcIndex = new ArrayList<>();
		try{
			// 全てのグラフィックスデバイスに関する情報を取得する
			GraphicsDevice[] gds = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
			int i = 0, j;
			for(GraphicsDevice gd : gds){
				// 各グラフィックデバイスにおけるグラフィックス特性を取得する
				GraphicsConfiguration[] gcs = gd.getConfigurations();
				// 各グラフィックス特性に従い、その座標を取得してスクショを撮る
				Robot robot = new Robot(gd);
				j = 0;
				for(GraphicsConfiguration gc : gcs){
					images.add(robot.createScreenCapture(gc.getBounds()));
					gdIndex.add(i);
					gcIndex.add(j);
					j++;
				}
				i++;
			}
			// 各スクショにおいて、艦これの画面となりうる800x480の画像を探索する
			int n = 0;
			for(BufferedImage image : images){
				int width  = image.getWidth();
				int height = image.getHeight();
				// 艦これの画面が存在しえないほどのスクショサイズなら探索しない
				if((width < JUDGE_X) || (height < JUDGE_Y)) continue;
				// 艦これの画面を検索する
				int searchX = width  - JUDGE_X;
				int searchY = height - JUDGE_Y;
				for(int x = 0; x <= searchX; x++){
					for(int y = 0; y <= searchY; y++){
						// 「左上」
						//[0xFFFFFF]0xFFFFFF
						// 0xFFFFFF 0x??????
						// ↑まず参照するのは[]の位置。上記パターンを見つけてから他の3角を見つける
						if((image.getRGB(x    , y    ) & WHITE) != WHITE) continue;
						if((image.getRGB(x + 1, y    ) & WHITE) != WHITE) continue;
						if((image.getRGB(x    , y + 1) & WHITE) != WHITE) continue;
						if((image.getRGB(x + 1, y + 1) & WHITE) == WHITE) continue;
						// 「右上」
						if((image.getRGB(x + FLASH_X    , y    ) & WHITE) != WHITE) continue;
						if((image.getRGB(x + FLASH_X + 1, y    ) & WHITE) != WHITE) continue;
						if((image.getRGB(x + FLASH_X    , y + 1) & WHITE) == WHITE) continue;
						if((image.getRGB(x + FLASH_X + 1, y + 1) & WHITE) != WHITE) continue;
						// 「左下」
						if((image.getRGB(x    , y + FLASH_Y    ) & WHITE) != WHITE) continue;
						if((image.getRGB(x + 1, y + FLASH_Y    ) & WHITE) == WHITE) continue;
						if((image.getRGB(x    , y + FLASH_Y + 1) & WHITE) != WHITE) continue;
						if((image.getRGB(x + 1, y + FLASH_Y + 1) & WHITE) != WHITE) continue;
						// 「右下」
						if((image.getRGB(x + FLASH_X    , y + FLASH_Y    ) & WHITE) == WHITE) continue;
						if((image.getRGB(x + FLASH_X + 1, y + FLASH_Y    ) & WHITE) != WHITE) continue;
						if((image.getRGB(x + FLASH_X    , y + FLASH_Y + 1) & WHITE) != WHITE) continue;
						if((image.getRGB(x + FLASH_X + 1, y + FLASH_Y + 1) & WHITE) != WHITE) continue;
						// 検出できたので、そのディスプレイの番号および座標を取得する
						displayIndex = n;
						flashPX = x + 1;
						flashPY = y + 1;
						break;
					}
					if(displayIndex >= 0) break;
				}
				n++;
			}
			if(displayIndex >= 0){
			  LogManager.getLogger().appendLog("ディスプレイ番号-左上座標：" + displayIndex + "-" + flashPX + "," + flashPY);
			}else{
			  LogManager.getLogger().appendLog("艦これの画面を取得できませんでした。");
			}
		}
		catch(Exception error){
			error.printStackTrace();
		}
	}
	/* 艦これの画面を取得する */
	public static BufferedImage getImage(){
		try{
			if(displayIndex < 0) return null;
			GraphicsDevice[] all_gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
			GraphicsConfiguration[] all_gc = all_gd[gdIndex.get(displayIndex)].getConfigurations();
			Robot robot = new Robot(all_gd[gdIndex.get(displayIndex)]);
			return robot.createScreenCapture(all_gc[gcIndex.get(displayIndex)].getBounds()).getSubimage(flashPX, flashPY, FLASH_X, FLASH_Y);
		}
		catch(Exception error){
			error.printStackTrace();
			return null;
		}
	}
}
