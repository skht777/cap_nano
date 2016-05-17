import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.stream.IntStream;

public enum Scan {
	UNIT(300, 172, 241, 191, 119),
	UNIT1(136, 122, 33, 150, 151),
	UNIT2(294, 145, 255, 102, 29),
	SORT1(420, 118, 66,  60,  59),
	SORT2(374,  80, 30, 157, 160),
	TEAM1(420, 118, 195, 180, 169),
	TEAM2(506, 114,115, 180, 191),
	HOME1(665, 42, 83, 159, 73),
	HOME2(736, 61, 172, 128, 95),
	INFO1(306, 276, 84, 84, 84),
	INFO2(251, 203, 35, 158, 159),
	INFO3(47, 333, 115, 166, 202),
	RANK1(87, 189, 79, 152, 139),
	RANK2(158, 81, 196, 169, 87),
	RANK3(47, 333, 115, 166, 202),
	PLIST1(140, 131, 103, 83, 46),
	PLIST2(654, 119, 255, 191, 96),
	PINVI1(0, 0, 0, 0, 0),
	PINVI2(168, 165, 17, 156, 160),
	PINVI3(635, 444, 224, 217, 204),
	RESULT1(51, 77, 255, 246, 242),
	RESULT2(395, 289, 255, 246, 242),
	RESULT3(0, 0, 36, 54, 63);
	int x, y, r, g, b;
	private Scan(int x, int y, int r, int g, int b){
		this.x = x;
		this.y = y;
		this.r = r;
		this.g = g;
		this.b = b;
	}
	public static boolean check(BufferedImage image, Scan ...checks){
		return Arrays.stream(checks).allMatch(check->check.check(image));
	}
	// FIXME: OSやブラウザによる色の違いへの対処が求められる
	private boolean checkColor(BufferedImage image, int x, int y, int r, int g, int b){
		Color color = new Color(image.getRGB(x, y));
		Color diff = new Color(color.getRed() - r, color.getGreen() - g, color.getBlue() - b);
		System.out.println(diff);
//		return true;
		return Math.pow(diff.getRed(), 2) + Math.pow(diff.getGreen(), 2) + Math.pow(diff.getBlue(), 2) < 500;
	}
	public int check(BufferedImage image, int n, int dx, int dy){
		return IntStream.range(0, n).filter(i->checkColor(image, x + dx * i, y + dy * i, r, g, b)).findFirst().orElse(-1);
	}
	public boolean check(BufferedImage image){return checkColor(image, x, y, r, g, b);}
}
