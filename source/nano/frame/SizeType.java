package nano.frame;

public enum SizeType {
	UNIT1("通常表示", 460, 365),
	UNIT2("縮小表示", 230, 365),
	SORT1("通常表示", 382, 279),
	SORT2("縮小表示", 190, 279),
	SORT3("拡大表示", 382, 315);
	private String name;
	private int width;
	private int height;
	private SizeType(String name, int width, int height){
		this.name = name;
		this.width = width;
		this.height = height;
	}
	public int getWidth(){return width;}
	public int getHeight(){return height;}
	public int getWidth(int x){return getWidth() * x;}
	public int getHeight(int y){return getHeight() * y;}
	@Override
	public String toString(){return name;}
}
