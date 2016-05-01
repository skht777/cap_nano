import java.awt.image.BufferedImage;

public interface Savable {
	// FIXME: OSやブラウザによる色の違いへの対処が求められる
	public static boolean checkColor(BufferedImage image, int x, int y, int r, int g, int b){
		return true;
//		Color color = new Color(image.getRGB(x, y));
//		int diffR = color.getRed() - r, diffG = color.getGreen() - g, diffB = color.getBlue() - b;
//		int diff = diffR * diffR + diffG * diffG + diffB * diffB;
//		if(diff < 500) return true;
//		return false;
	}
	public void savePicture();
}
