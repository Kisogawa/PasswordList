package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import entity.Setting;

/**
 * PassListテーブルのデータベースへの接続と処理を行う
 */
public class SQLConnection {

	/**
	 * 接続オブジェクト
	 */
	private Connection PasswordListConnection = null;

	/**
	 * 設定
	 */
	private Setting mSetting;

	public SQLConnection(Setting setting) {
		mSetting = setting;
//		Connection();
	}

	// DBに接続する
	public boolean Connection() {
		// 接続を終了する
		close();
		// MySQLに接続
		try {
			// JDBCドライバのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			PasswordListConnection = DriverManager.getConnection(mSetting.getDBUrl(), mSetting.getDBUser(), mSetting.getDBPassword());
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("JDBCドライバのロードに失敗しました。");
			return false;
		}
		return true;
	}

	/**
	 * DBとの接続を終了する
	 */
	public void close() {
		if (PasswordListConnection == null) return;
		try {
			PasswordListConnection.close();
		} catch (SQLException e) {
			System.out.println("MySQLのクローズに失敗しました。");
		}
	}

	/**
	 * SQLサーバーとの接続を取得する（切断されている場合は接続し直す）
	 *
	 * @return SQLサーバーとの接続オブジェクト
	 */
	Connection getConnection() {
		// SELECT文を実行し，コネクションの確立がされているか確認する
		if (PasswordListConnection != null) {
			Statement statement1 = null;
			ResultSet resultSet1 = null;
			try {
				statement1 = PasswordListConnection.createStatement();
				resultSet1 = statement1.executeQuery("SELECT ItemId FROM PassList WHERE ItemId = 1;");
			} catch (SQLException e) {
				PasswordListConnection = null;
			} finally {
				try {
					if (resultSet1 != null) resultSet1.close();
					if (statement1 != null) statement1.close();
				} catch (SQLException e) {
				}
			}
		}

		// 再接続
		if (PasswordListConnection == null) Connection();

		return PasswordListConnection;
	}

	/**
	 * 自動コミットモードを有効にする(エラー無し,ロールバック)
	 *
	 * @return エラーならfalse,正常終了ならtrue
	 */
	boolean setEnableAutoCommit() {
		if (getConnection() == null) return false;
		try {
			getConnection().rollback();
		} catch (SQLException e) {
		}

		try {
			getConnection().setAutoCommit(true);
		} catch (SQLException e) {
			return false;
		}
		return true;
	}
}
