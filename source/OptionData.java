/**/

public class OptionData{
	/* メンバ変数 */
	private static OptionData data = new OptionData();
	private FPS fps;
	private boolean drawFrame;
	private boolean auto;
	private boolean disableName;
	public static OptionData getData(){return data;}
	private OptionData(){
		fps = FPS.DISABLE;
		drawFrame = true;
		disableName = true;
	}
	public FPS getFPS(){return fps;}
	public boolean isAuto(){return auto;}
	public boolean disableName(){return disableName;}
	public boolean drawFrame() {return drawFrame;}
	void setDrawFrame(boolean status){drawFrame = status;}
	void setAuto(boolean status){auto = status;}
	void setDisableName(boolean status){disableName = status;}
	void setFPS(FPS fps){
		this.fps = fps;
		LogManager.getLogger().appendLog("【連射機能】");
		LogManager.getLogger().appendLog(String.format("fps：%s", FPS.DISABLE.equals(fps) ? "OFF" : fps.toString()));
	}
}
