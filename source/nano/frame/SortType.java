package nano.frame;
import java.util.function.IntUnaryOperator;
import java.util.function.ToIntBiFunction;

public enum SortType{
	ROW("行を優先", (i,o)->i),
	COLUMN("列を優先", (i, o)->{
		int x = i / o.getColumn(), y = i % o.getRow();
		return y * o.getColumn() + x;
			}),
	TEAM("艦隊優先", (i, o)->new int[]{
			1,  2,  7,  8,  13, 14,
			3,  4,  9,  10, 15, 16,
			5,  6,  11, 12, 17, 18,
			19, 20, 25, 26, 31, 32, 
			21, 22, 27, 28, 33, 34,
			23, 24, 29, 30, 35, 36}[i] - 1);
	private String name;
	private ToIntBiFunction<Integer, FrameOption> method;
	private SortType(String name, ToIntBiFunction<Integer, FrameOption> method){
		this.name = name;
		this.method = method;
	}
	public IntUnaryOperator getMethod(FrameOption option){return i->method.applyAsInt(i, option);}
	@Override
	public String toString(){return name;}
}