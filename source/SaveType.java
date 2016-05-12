import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

enum SaveType{
	NORMAL("通常",image->{
		if(OptionData.getData().disableName()) disableName(image);
		return image;
	}),
	FORMATION("編成", image->Scan.check(image, Scan.TEAM1, Scan.TEAM2) ? image.getSubimage(100, 94, 700, 386) : null),
	RESOURCE("資材", image->{
		BufferedImage supplyImage = new BufferedImage(229, 60, BufferedImage.TYPE_INT_BGR);
		Graphics graphics = supplyImage.getGraphics();
		graphics.drawImage(image.getSubimage(9, 407, 86, 60), 0, 0, null);		//時刻
		graphics.drawImage(image.getSubimage(657, 9, 143, 60), 86, 0, null);	//資材
		graphics.dispose();
		return supplyImage;
	});
	/* 名前隠し機能 */
	private static void colorRect(Graphics graphics, int x, int y, int w, int h, int r, int g, int b){
		graphics.setColor(new Color(r, g, b));
		graphics.fillRect(x, y, w, h);
	}
	private static void disableName(BufferedImage image){
		Graphics graphics = image.getGraphics();
		// 母港左上の提督名
		if(Scan.check(image, Scan.HOME1, Scan.HOME2))
			colorRect(graphics, 111, 0, 162, 25, 38, 38, 38);
		// 艦隊司令部情報
		if(Scan.check(image, Scan.INFO1, Scan.INFO2, Scan.INFO3))
			colorRect(graphics, 201, 123, 295, 30, 241, 234, 221);
		// ランキング
		if(Scan.check(image, Scan.RANK1, Scan.RANK2, Scan.RANK3))
			colorRect(graphics, 225, 153, 150, 298, 54, 54, 54);
		// 演習一覧
		if(Scan.check(image, Scan.PLIST1, Scan.PLIST2)){
			IntStream.of(1, 3, 5).forEach(i->colorRect(graphics, 338, 178 + 55 * i, 165, 14, 225, 209, 181));
			IntStream.of(2, 4).forEach(i->colorRect(graphics, 338, 178 + 55 * i, 165, 14, 237, 223, 207));
		}
		// 演習個別
		if(Scan.check(image, Scan.PINVI1, Scan.PINVI2, Scan.PINVI1))
			colorRect(graphics, 130, 87, 295, 30, 246, 239, 228);
		// 戦果報告
		if(Scan.check(image, Scan.RESULT1, Scan.RESULT2, Scan.RESULT3))
			colorRect(graphics, 56, 82, 172, 24, 37, 44, 47);
		graphics.dispose();
	}
	private String name;
	private UnaryOperator<BufferedImage> method;
	private SaveType(String name, UnaryOperator<BufferedImage> method){
		this.name = name;
		this.method = method;
	}
	public BufferedImage apply(BufferedImage image){return method.apply(image);}
	@Override
	public String toString(){return name;}
}