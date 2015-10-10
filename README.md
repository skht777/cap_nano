# cap_nano
**記録は大切なの.jar Ver.1.1**

## 概要
名前通り、Javaで書かれているので複数のOSに対応！  
艦これのスクショを簡単に撮影することができます。  
また、改装画面やソート画面の画像複数から、まとめ画像を作成することも可能です。

## インストール・アンインストール方法
 * インストール方法：zipファイルを解凍するだけです。jarファイルがプログラムの本体です。
 * アンインストール方法：ファイルをそのまま削除するだけです。レジストリは使用しません。

## ファイル
※必須ファイルは強調表示、そうでもないファイルは通常表示です。
 * **nano.jar……実行ファイル。**
 * Readme.txt……この文章のことです。
 * sourceフォルダ……ソースコードです。

## 操作方法
1. 起動すると、ボタンとテキストエリアが配置された小さなウィンドウが出現します。  
以下、このウィンドウのことを「メイン画面」と呼称します。  
また、テキストエリアにはソフトウェアの動作ログが表示されます。

2. このソフトでスクショを撮影するためには、まず艦これの画面を認識する必要があります。  
艦これの画面(100％表示＝800x480限定)が隠れていない状態で、メイン画面の「座標取得」ボタンを押してください。  
正常に取得できた場合は、ログにそのディスプレイ番号および左上座標が表示されます。

3. メイン画面のコンボボックスには、「通常」「改装」「ソート」の3項目が存在します。  
これは、動作モードを切り替えるためのものです。

4. 動作モードが「通常」の場合、「画像保存」ボタンを押せばそのままスクショが800x480のpngファイルとして、nano.jarと同じ位置に置かれます。  
ファイル名は「2015-10-07 16-04-34-295.png」といった感じでミリ秒まで出ます。

5. 動作モードが「改装」の場合、「改装一覧」というタイトルのウィンドウが出現します。  
「画像追加」ボタンを押すと、そのウィンドウに「改装画面における艦娘および装備」が切り取られて追加されます。  
艦娘・装備のまとめ画像を作成する際は、複数回画像を追加した後に、「画像保存」ボタンを押せば保存されます。  
メイン画面の2段目には、画像を追加する際にどのような順番で追加するか、および表示形式を「縮小表示」「通常表示」「拡張表示」のどれにするかを決められます。詳しくは、本文後半の[注意]の項目をご覧ください。  
ファイルの書式および位置は「通常」モードと同じです。また、出力画像は「改装一覧」ウィンドウでの表示と違って縮小および罫線は掛かっていません。  
なお、改装一覧ウィンドウでは、それぞれに区切られた画像を入れ替えることが可能です。マウスで他方をもう一方の位置にドラッグ＆ドロップすれば置換され、その旨がログにも載せられます。この際、最初にマウスボタンを押した側が青枠・ドラッグ先が赤枠で表示されます。

6. 動作モードが「ソート」の場合は、動作モードが「改装」の場合とほぼ同様です。  
「画像追加」ボタンで追加できるケースが、「改装画面」から「編成画面から『変更』ボタンを押した際に表示される艦娘一覧」に変わりますが当然でしょう。  
ただ、改装モードとソートモードでは表示されるウィンドウが異なります。より正確に言えば、改装モードではメイン画面＋改装一覧ウィンドウ、ソートモードではメイン画面＋ソート一覧ウィンドウが表示されます。

7. 「オプション」ボタンでは、「改装」および「ソート」で保存される画像において、分かりやすく罫線を追加するかを選ぶことができます。  
また、「改装」モードでは、罫線だけではなく、艦隊における番号が右上に表示されます。書式は、「艦隊の番号―艦隊における位置(1～6番艦)の位置」です。

8. 「改装」および「ソート」モードの場合において、ソフトのウィンドウがアクティブな状態でAlt+Zキーを押すと、「画像追加」ボタンを押すのと同じ効果が得られます。  
また、画像ファイル(800x480のもの)を、改装一覧およびソート一覧画面にドラッグ＆ドロップしても、同じように「画像追加」できます。

## 注意
 * 艦これの画面の周囲1ピクセルは確実に真っ白(#FFFFFF)な背景が存在する必要があります。  
普通のブラウザで遊ばれている場合には問題ないですが、スクリプト等で艦これの画面「だけ」を別ウィンドウに隔離している場合などは注意が必要です。

 * マルチディスプレイ環境の場合、艦これの画面が複数ディスプレイに跨る位置にある際には正常に座標を取得できません。  
これは割とJavaの仕様なのでご容赦ください。

 * 艦これの画面が当初の位置からズレた場合、そのことをこのソフトは認識できません。  
したがって、ズレた際には「座標取得」ボタンを再び押してください。

 * 動作モードが「改装」および「ソート」モードの場合において、「画像追加」ボタンを押した際の挙動に注意してください。  
「行を優先」だと左から右・上から下の順、「列を優先」だと上から下・左から右に追加していきますが、「艦隊優先」だと次のような順番で画像が追加されていきます。  
要するに、左上の6隻を「第1艦隊」、以下第2～第6艦隊として表示しやすいような配置にしています。  

|**第1**|**第2**|**第3**|
|:---:|:---:|:---:|
|01 02|07 08|13 14|
|03 04|09 10|15 16|
|05 06|11 12|17 18|
|**第4**|**第5**|**第6**|
|19 20|25 26|31 32|
|21 22|27 28|33 34|
|23 24|29 30|35 36|

 * 動作モードが「改装」および「ソート」モードの場合において、表示形式は次のようになっています。もっとも、とりあえず試してみるのが一番わかり易いですが。  

|動作モード|表示形式|表示内容|
|:---------|:-------|:-------|
|改装|縮小表示|艦娘名・装備・ステータスのみ|
|改装|通常表示|縮小表示に加えて、艦娘の画像部分も加わる|  
|ソート|縮小表示|艦種・艦娘名・レベルのみ|  
|ソート|通常表示|縮小表示に加えて、ステータスおよびロック部分も加わる|  
|ソート|拡張表示|通常表示に加えて、ページ数も加わる|

## 謝辞
　Javaの開発に携わったオラクル(サン・マイクロシステムズ)に感謝。

## 更新履歴
ver.1.1 2015/10/11
ソースコードを大幅に書き直した。
追加する順番、および表示形式を設定できるようにした。
改装一覧およびソート一覧画像を保存する際、罫線を表示できるようにした。
ドラッグ＆ドロップで画像の順番を変える際に、枠線を出して分かりやすくした。
画像ファイルをドラッグ＆ドロップして追加できるようにした。
ショートカットキーから画像を追加できるようにした。

ver.1.0 2015/10/07
ソフトウェアを公開。

## 著作権表記
このソフトウェアはMITライセンスを適用しています。

## コメント
全面的に書き直してみました。抽象クラスの扱い等苦労した面は多々ありますが、
勉強になることも多くて楽しかったです。
