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
	public OptionData() {
		fps = FPS.DISABLE.getValue();
		drawLine = true;
		disableName = true;
	}
	public int getFPS(){return fps;}
	public boolean isDrawLine(){return isDrawLine();}
	public boolean isAuto(){return auto;}
	public boolean disableName(){return disableName;}
	public ActionListener getFPSAction(){return event->setFPS(((JComboBox<?>) event.getSource()).getSelectedIndex());}
	private void setFPS(int n){fps = FPS.values()[n].getValue();}
}
