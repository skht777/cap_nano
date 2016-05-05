/*コンボボックス設定用クラス*/

public class BasicPair<T> implements Pair<T>{
	private String name;
	private T value;
	public BasicPair(String name, T value){
		this.name = name;
		this.value = value;
	}
	@Override
	public T getValue(){return value;}
	@Override
	public String toString(){return name;}
}
