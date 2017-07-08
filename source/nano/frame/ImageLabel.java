package nano.frame;

import nano.LogManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Optional;

@SuppressWarnings("serial")
class ImageLabel extends JLabel{
	private BufferedImage image;
	// ウインドウの枠線
	private enum LINE{
		NORMAL(new LineBorder(Color.BLACK, 1)),
		RED(new LineBorder(Color.RED, 4)),
		BLUE(new LineBorder(Color.BLUE, 4));
		private Border border;

		LINE(Border border) {
			this.border = border;
		}
		private void set(JComponent ...cs){Arrays.stream(cs).forEach(c->c.setBorder(border));}
	}
	public ImageLabel(){
		super();
		// 枠線を描画する
		LINE.NORMAL.set(this);
		addMouseListener(getMouseListener());
	}
	static void swapImage(ImageLabel il1, ImageLabel il2){
		BufferedImage temp = il1.getImage();
		il1.setImage(il2.getImage());
		il2.setImage(temp);
	}
	private static void changeColor(MouseEvent event, ImageLabel.LINE line){
		Optional.of(event).filter(e->e.getButton() > 0).map(e->(JComponent) e.getSource())
				.filter(c -> !LINE.RED.border.equals(c.getBorder())).ifPresent(line::set);
	}
	public int getPX(){return getX() / getWidth();}
	public int getPY(){return getY() / getHeight();}
	public BufferedImage getImage(){return image;}
	public void setImage(BufferedImage image){
		this.image = image;
		setIcon(Optional.ofNullable(image).map(im->new ImageIcon(im.getScaledInstance(getWidth(), getHeight(), BufferedImage.SCALE_AREA_AVERAGING))).orElse(null));
	}
	public void clearImage(){setImage(null);}
	public boolean hasImage(){return getIcon() != null;}
	@Override
	public String toString(){return String.format("(%d,%d)", getPX(), getPY());}
	/* マウスイベント */
	private MouseAdapter getMouseListener(){
		return new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent event){
				// ダブルクリックした際は、その場所の記録画像を消去する
				Optional.of(event).filter(e->e.getClickCount() >= 2).map(e->(ImageLabel) e.getSource()).filter(ImageLabel::hasImage).ifPresent(il->{
					il.clearImage();
					LogManager.getLogger().appendLog("【画像削除】");
					LogManager.getLogger().appendLog("変更位置：" +  il.toString());
				});
			}
			@Override
			// マウスを押した際は押した位置のラベルを変更する
			public void mousePressed(MouseEvent event){LINE.RED.set((JComponent) event.getSource());}
			@Override
			// マウスを離した際は、その位置のマスとの交換を行う
			public void mouseReleased(MouseEvent event){
				ImageLabel pressed = (ImageLabel) event.getSource();
				ImageLabel released = (ImageLabel) pressed.getParent().getComponentAt(pressed.getX() + event.getX(), pressed.getY() + event.getY());
				if(released != null && !pressed.equals(released)){
					// 画像を入れ替える
					swapImage(pressed, released);
					LogManager.getLogger().appendLog("【画像交換】");
					LogManager.getLogger().appendLog(pressed.toString() + "⇔" + released.toString());
				}
				LINE.NORMAL.set(pressed, released);
			}
			@Override
			// マウスがラベル上に重なった際は、その位置のラベルを変更する
			public void mouseEntered(MouseEvent event){ImageLabel.changeColor(event, LINE.BLUE);}
			@Override
			// マウスがラベルから離れた際は、その位置のラベルを元に戻す
			public void mouseExited(MouseEvent event){ImageLabel.changeColor(event, LINE.NORMAL);}
		};
	}
}