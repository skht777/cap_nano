
public enum FPS{
	DISABLE("無効", 0),
	ONE("1fps", 1),
	TWO("2fps", 2),
	THREE("3fps", 3),
	FIVE("5fps", 5),
	TEN("10fps", 10);
	private String name;
	private int value;
	private FPS(String name, int value){
		this.name = name;
		this.value = value;
	}
	public int getDelay(){
		return equals(DISABLE) ? 0 : 1000 / value;
	}
	@Override
	public String toString(){return name;}
}