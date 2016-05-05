/**/
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComboBox;

public class OptionData {
	/* メンバ変数 */
	private int fps;
	private boolean drawLine;
	private boolean auto;
	private boolean disableName;
	private enum FPS implements Pair<Integer>{
		DISABLE("無効", 0),
		FPS1("1fps", 1),
		FPS2("2fps", 2),
		FPS3("3fps", 3),
		FPS5("5fps", 5),
		FPS10("10fps", 10);
		private String name;
		private int value;
		private FPS(String name, int value) {
			this.name = name;
			this.value = value;
		}
		@Override
		public Integer getValue(){return value;}
		@Override
		public String toString(){return name;}
	}
	public OptionData() {
		fps = FPS.DISABLE.getValue();
		drawLine = true;
		disableName = true;
	}
	public int getFPS(){return fps;}
	public boolean isDrawLine(){return isDrawLine();}
	public boolean isAuto(){return auto;}
	public boolean disableName(){return disableName;}
	public List<Pair<Integer>> getFPSList(){return Arrays.asList(FPS.values());}
	public ActionListener getFPSAction(){return event->setFPS(((JComboBox<?>) event.getSource()).getSelectedIndex());}
	private void setFPS(int n){fps = FPS.values()[n].getValue();}
}
