import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

import javax.imageio.ImageIO;

public interface Capturable {
	static void savePicture(BufferedImage image) {
		// 画像の保存処理
		Optional.ofNullable(image).ifPresent(im->{
			String saveName = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS").format(Calendar.getInstance().getTime()) + ".png";
			try{
				ImageIO.write(image, "png", new File(saveName));
			}catch(Exception error){
				error.printStackTrace();
			}
			//putLog("【画像保存】");
			//putLog(saveName);
		});
	}
	public void savePicture();
}
