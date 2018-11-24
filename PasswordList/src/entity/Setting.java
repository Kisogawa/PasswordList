package entity;

/**
 * パスワード情報を記録
 */
public class Setting {

	/**
	 * バージョン番号
	 */
	private int VersionNo = 1;

	/**
	 * 暗号化用パスワード
	 */
	private String Password = "";

	/**
	 * DB接続URL
	 */
	private String DBUrl = "";

	/**
	 * DB接続ユーザー名
	 */
	private String DBUser = "";

	/**
	 * DB接続パスワード
	 */
	private String DBPassword = "";

	// ----------------------------------------------------------------------

	// デフォルトコンストラクタ
	public Setting() {
	}

	// ----------------------------------------------------------------------

	/**
	 * バージョン番号を取得する
	 *
	 * @return バージョン番号
	 */
	public int getVersionNo() {
		return VersionNo;
	}

	/**
	 * バージョン番号を設定する
	 *
	 * @param versionNo 設定するバージョン番号
	 */
	public void setVersionNo(int versionNo) {
		VersionNo = versionNo;
	}

	/**
	 * 暗号化用パスワードを取得する
	 *
	 * @return 暗号化用パスワード
	 */
	public String getPassword() {
		return Password;
	}

	/**
	 * 暗号化用パスワードを設定する
	 *
	 * @param 暗号化用パスワード
	 */
	public void setPassword(String password) {
		if (password == null) password = "";
		Password = password;
	}

	/**
	 * DB接続URLを取得する
	 *
	 * @return DB接続URL
	 */
	public String getDBUrl() {
		return DBUrl;
	}

	/**
	 * DB接続URLを設定する
	 *
	 * @param DB接続URL
	 */
	public void setDBUrl(String dBUrl) {
		if (dBUrl == null) dBUrl = "";
		DBUrl = dBUrl;
	}

	/**
	 * DB接続ユーザー名を取得する
	 *
	 * @return DB接続ユーザー名
	 */
	public String getDBUser() {
		return DBUser;
	}

	/**
	 * DB接続ユーザー名を設定する
	 *
	 * @param DB接続ユーザー名
	 */
	public void setDBUser(String dBUser) {
		if (dBUser == null) dBUser = "";
		DBUser = dBUser;
	}

	/**
	 * DB接続パスワードを取得する
	 *
	 * @return DB接続パスワード
	 */
	public String getDBPassword() {
		return DBPassword;
	}

	/**
	 * DB接続パスワードを設定する
	 *
	 * @param DB接続パスワード
	 */
	public void setDBPassword(String dBPassword) {
		if (dBPassword == null) dBPassword = "";
		DBPassword = dBPassword;
	}
}
