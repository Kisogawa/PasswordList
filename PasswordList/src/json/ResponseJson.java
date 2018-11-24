package json;

import entity.PasswordItem;
import response.ResponseInfomation;

/**
 * ブラウザからの応答データ
 */
public class ResponseJson {

	/**
	 * 応答データクラス
	 */
	private ResponseInfomation responseInfomation = new ResponseInfomation();

	/**
	 * パスワードデータ格納配列
	 */
	private PasswordItem[] passwordItems = new PasswordItem[0];

	/**
	 * 標準コンストラクタ
	 */
	public ResponseJson() {
	}

	/**
	 * 作成用コンストラクタ
	 */
	public ResponseJson(ResponseInfomation responseInfomation) {
		setResponseInfomation(responseInfomation);
	}

	/**
	 * 作成用コンストラクタ
	 */
	public ResponseJson(ResponseInfomation responseInfomation, PasswordItem[] passwordItems) {
		setResponseInfomation(responseInfomation);
		setPasswordItems(passwordItems);
	}

	/**
	 * 応答データクラスの取得
	 *
	 * @return 応答データクラス
	 */
	public ResponseInfomation getResponseInfomation() {
		return responseInfomation;
	}

	/**
	 * 応答データクラスの設定
	 *
	 * @param 応答データクラス
	 */
	public void setResponseInfomation(ResponseInfomation responseInfomation) {
		this.responseInfomation = responseInfomation;
	}

	/**
	 * パスワードデータ格納配列の取得
	 *
	 * @return passwordItems パスワードデータ格納配列
	 */
	public PasswordItem[] getPasswordItems() {
		return passwordItems;
	}

	/**
	 * パスワードデータ格納配列の設定
	 *
	 * @param passwordItems パスワードデータ格納配列
	 */
	public void setPasswordItems(PasswordItem[] passwordItems) {
		if (passwordItems == null) passwordItems = new PasswordItem[0];
		this.passwordItems = passwordItems;
	}

}
