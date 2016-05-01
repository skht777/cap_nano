/*コンボボックス設定用クラス*/

public class Pair<T>{
	private String name;
	private T value;
	public Pair(String name, T value) {
		this.name = name;
		this.value = value;
	}
	public T get(){return value;}
	@Override
	public String toString(){return name;}
}
