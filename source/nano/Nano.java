package nano;
/* 記録は大切なの.jar Ver.1.2 */

import javax.swing.*;

public class Nano {
	public static void main(String args[]){
		// メイン画面
		SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
	}
}
