import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Optional;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
class ImageLabel extends JLabel{
	private Image image;
	// ウインドウの枠線
	private enum LINE{
		NORMAL(new LineBorder(Color.BLACK, 1)),
		RED(new LineBorder(Color.RED, 4)),
		BLUE(new LineBorder(Color.BLUE, 4));
		private Border border;
		private LINE(Border border){this.border = border;}
		private void set(JComponent ...targets){Arrays.stream(targets).forEach(target->target.setBorder(border));}
	}
	public ImageLabel(){
		super();
		// 枠線を描画する
		LINE.NORMAL.set(this);
		addMouseListener(getMouseListener());
	}
	static void swapImage(ImageLabel il1, ImageLabel il2){
		Image temp = il1.getImage();
		il1.setImage(il2.getImage());
		il2.setImage(temp);
	}
	private static void changeColor(MouseEvent event, ImageLabel.LINE line){
		Optional.of(event).filter(e->e.getButton() > 0).map(e->(JComponent) e.getSource())
		.filter(target->!LINE.RED.border.equals(target.getBorder())).ifPresent(target->line.set(target));
	}
	public int getIndexX(){return getX() / getWidth();}
	public int getIndexY(){return getY() / getHeight();}
	public Image getImage(){return image;}
	public void setImage(Image image) {
		this.image = image;
		setIcon(image == null ? null : new ImageIcon(this.image
				.getScaledInstance(getWidth(), getHeight(), Image.SCALE_AREA_AVERAGING)));
	}
	public void clearImage(){setImage(null);}
	public boolean hasImage(){return getIcon() != null;}
	@Override
	public String toString(){return String.format("(%d,%d)", getIndexX(), getIndexY());}
	/* マウスイベント */
	private MouseAdapter getMouseListener(){
		return new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent event){
				// ダブルクリックした際は、その場所の記録画像を消去する
				Optional.of(event).filter(e->e.getClickCount() >= 2).map(e->(ImageLabel) e.getSource()).filter(ImageLabel::hasImage).ifPresent(il->{
					LogManager.getLogger().appendLog("【画像削除】");
					il.clearImage();
					LogManager.getLogger().appendLog("追加位置：" +  il.toString());
				});
			}
			@Override
			public void mousePressed(MouseEvent event){
				// マウスを押した際は押した位置のラベルを変更する
				LINE.RED.set((JComponent) event.getSource());
			}
			@Override
			public void mouseReleased(MouseEvent event){
				// マウスを離した際は、その位置のマスとの交換を行う
				ImageLabel pressed = (ImageLabel) event.getSource();
				ImageLabel released = (ImageLabel) pressed.getParent().getComponentAt(pressed.getX() + event.getX(), pressed.getY() + event.getY());
				if(released != null && !pressed.equals(released)){
					LogManager.getLogger().appendLog("【画像交換】");
					// 画像を入れ替える
					swapImage(pressed, released);
					LogManager.getLogger().appendLog(pressed.toString() + "⇔" + released.toString());
				}
				LINE.NORMAL.set(pressed, released);
			}
			@Override
			public void mouseEntered(MouseEvent event){
				// マウスがラベル上に重なった歳は、その位置のラベルを変更する
				ImageLabel.changeColor(event, LINE.BLUE);
			}
			@Override
			public void mouseExited(MouseEvent event){
				// マウスがラベルから離れた際はその位置のラベルを元に戻す
				ImageLabel.changeColor(event, LINE.NORMAL);
			}
		};
	}
}