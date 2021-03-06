/* 記録は大切なの.jar Ver.1.2 */

/* TODO:
 * 実紹
 * 装介
 * ○○早めにdisposeしておく
 * ○　改装及びソート一覧のデフォルトでの表示位置を変更
 * ○　画像全削除機能(画像保存時に確認する形)
 * ○○自動画像取得機能(取得速度は1fps程度にしておく)
 * ○○名前隠し機能(自身および演習相手の名前)
 * ○○連射機能(一応速度調整できるようにしておく)
 * ○○クロップ機能—編成画面で編成部分だけ切り取って保存
 * ○○クロップ機能—母港で資源および時計部分だけ切り取って保存
 * 以上を実装したらリリースする
 */

public class Nano{
	public static MainWindow mainFrame;
	public static UnitWindow unitFrame;
	public static SortWindow sortFrame;
	public static OptionWindow optionFrame;
	public static void main(String args[]){
		// 改装一覧用
		unitFrame = new UnitWindow();
		// ソート一覧用
		sortFrame = new SortWindow();
		// メイン画面
		mainFrame = new MainWindow();
		// オプション画面
		optionFrame = new OptionWindow();
	}
}
