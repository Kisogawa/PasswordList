package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import entity.PasswordItem;
import exception.FatalException;
import exception.NormalException;

/**
 * PassListテーブル操作DAO
 */
public class PassListDAO {

	/**
	 * 登録操作を行う
	 *
	 * @param item       登録を行うデータのEntityクラス(ID，登録日，更新日のみ空白可)
	 * @param connection 接続情報
	 * @return 登録したレコード(失敗はnull)
	 */
	static public PasswordItem Register(PasswordItem item, SQLConnection connection) throws FatalException, NormalException {
		if (connection.getConnection() == null) throw new FatalException("DBと接続していません");
		// 情報を複製する
		item = new PasswordItem(item);

		// トランザクション開始
		try {
			connection.getConnection().setAutoCommit(false);
		} catch (SQLException e) {
			throw new FatalException("SQLのエラー", e);
		}

		// 候補ItemID(0無効)
		int itemID = 0;
		// ItemIDの最小値を取得する
		Statement statement1 = null;
		ResultSet resultSet1 = null;
		Statement statement2 = null;
		ResultSet resultSet2 = null;
		try {
			// 1が欠番かどうかの確認
			{
				statement1 = connection.getConnection().createStatement();
				resultSet1 = statement1.executeQuery("SELECT ItemId FROM PassList WHERE ItemId = 1;");
				if (!resultSet1.next()) {
					// 検索結果が無かった場合，1が欠番なので，1とIDとして登録する
					itemID = 1;
				}
			}
			// 1が欠番でなかった場合は欠番の検索を行う
			if (itemID == 0) {
				statement2 = connection.getConnection().createStatement();
				resultSet2 = statement2.executeQuery("SELECT MIN(ItemId + 1) AS ItemId FROM PassList WHERE (ItemId + 1) NOT IN (SELECT ItemId FROM PassList);");
				// 検索結果を番号として登録
				if (resultSet2.next()) itemID = resultSet2.getInt(1);
			}
		} catch (SQLException e) {
			connection.setEnableAutoCommit();
			throw new FatalException("SQLのエラー", e);
		} finally {
			try {
				if (resultSet1 != null) resultSet1.close();
				if (statement1 != null) statement1.close();
				if (statement2 != null) statement2.close();
				if (resultSet2 != null) resultSet2.close();
			} catch (SQLException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

		// IDを設定する
		item.setItemID(itemID);
		// 登録日，更新日を設定する
		Date today = new Date();
		item.setRegisterDate(today);
		item.setUpdateDate(today);

		// 正当性の確認【条件:全データ必須】
		if (item.getItemID() <= 0) {
			connection.setEnableAutoCommit();
			throw new FatalException("自動で設定されたIDが0以下です");
		}
		if (item.getRegisterDate() == null) {
			connection.setEnableAutoCommit();
			throw new FatalException("登録日が設定されませんでした");
		}
		if (item.getUpdateDate() == null) {
			connection.setEnableAutoCommit();
			throw new FatalException("更新日が設定されませんでした");
		}

		if (item.getName() == null) {
			connection.setEnableAutoCommit();
			throw new NormalException("名前を設定してください");
		} else if (item.getName().length() > PasswordItem.MAXLENGTH_Name) {
			connection.setEnableAutoCommit();
			throw new NormalException("名前が長すぎます(最大" + PasswordItem.MAXLENGTH_Name + "文字)");
		}

		if (item.getUserID() == null) {
			connection.setEnableAutoCommit();
			throw new NormalException("UserIDを設定してください");
		} else if (item.getUserID().length() > PasswordItem.MAXLENGTH_UserID) {
			connection.setEnableAutoCommit();
			throw new NormalException("名前が長すぎます(最大" + PasswordItem.MAXLENGTH_UserID + "文字)");
		}

		if (item.getPassword() == null) {
			connection.setEnableAutoCommit();
			throw new NormalException("パスワードを設定してください");
		} else if (item.getPassword().length() > PasswordItem.MAXLENGTH_Password) {
			connection.setEnableAutoCommit();
			throw new NormalException("パスワードが長すぎます(最大" + PasswordItem.MAXLENGTH_Password + "文字)");
		}

		if (item.getComment() == null) {
			connection.setEnableAutoCommit();
			throw new NormalException("コメントを設定してください");
		} else if (item.getComment().length() > PasswordItem.MAXLENGTH_Comment) {
			connection.setEnableAutoCommit();
			throw new NormalException("コメントが長すぎます(最大" + PasswordItem.MAXLENGTH_Comment + "文字)");
		}

		if (item.getMailAddress() == null) {
			connection.setEnableAutoCommit();
			throw new NormalException("メールアドレスを設定してください");
		} else if (item.getMailAddress().length() > PasswordItem.MAXLENGTH_MailAddress) {
			connection.setEnableAutoCommit();
			throw new NormalException("メールアドレスが長すぎます(最大" + PasswordItem.MAXLENGTH_MailAddress + "文字)");
		}

		PreparedStatement preparedStatement = null;
		// SQL文の生成
		try {
			// 追加処理
			preparedStatement = connection.getConnection().prepareStatement("INSERT INTO PassList VALUES(?,?,?,?,?,?,?,?);");
			// 変数の追加
			preparedStatement.setInt(1, item.getItemID());
			preparedStatement.setString(2, item.getName());
			preparedStatement.setTimestamp(3, new java.sql.Timestamp(item.getRegisterDate().getTime()));
			preparedStatement.setTimestamp(4, new java.sql.Timestamp(item.getUpdateDate().getTime()));
			preparedStatement.setString(5, item.getUserID());
			preparedStatement.setString(6, item.getPassword());
			preparedStatement.setString(7, item.getComment());
			preparedStatement.setString(8, item.getMailAddress());

			int result = preparedStatement.executeUpdate();
			preparedStatement.close();
			preparedStatement = null;
			if (result != 1) {
				// 失敗
				throw new FatalException("レコードの新規登録に失敗");
			} else {
				connection.getConnection().commit();
			}
		} catch (SQLException e) {
			throw new FatalException("SQLのエラー", e);
		} finally {
			if (preparedStatement != null) try {
				preparedStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			connection.setEnableAutoCommit();
		}

		return item;
	}

	/**
	 * IDと名前の一覧取得処理を行う
	 *
	 * @param connection 接続情報
	 * @return レコードリスト
	 * @throws FatalException  致命的エラー
	 * @throws NormalException 通常のエラー
	 */
	static public PasswordItem[] List(SQLConnection connection) throws FatalException, NormalException {
		if (connection.getConnection() == null) throw new FatalException("DBと接続していません");

		ArrayList<PasswordItem> passwordItemList = new ArrayList<>();

		// SQL文よりリストの取得を行う
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.getConnection().createStatement();
			resultSet = statement.executeQuery("SELECT ItemId, Name FROM PassList;");
			// 結果一覧を取得する
			while (resultSet.next() == true) {
				PasswordItem item = new PasswordItem();
				item.setItemID(resultSet.getInt(1));
				item.setName(resultSet.getString(2));
				passwordItemList.add(item);
			}
		} catch (SQLException e) {
			throw new FatalException("SQLのエラー", e);
		} finally {
			try {
				if (resultSet != null) resultSet.close();
				if (statement != null) statement.close();
			} catch (SQLException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

		// リストを配列に変換する
		return passwordItemList.toArray(new PasswordItem[passwordItemList.size()]);
	}

	/**
	 * 登録情報の閲覧
	 *
	 * @param ItemID     閲覧したいアイテムのID
	 * @param connection 接続情報
	 * @return 登録情報
	 * @throws FatalException  致命的エラー
	 * @throws NormalException
	 */
	static public PasswordItem Show(int ItemID, SQLConnection connection) throws FatalException, NormalException {
		if (connection.getConnection() == null) throw new FatalException("DBと接続していません");

		PasswordItem passwordItem = null;

		// SQL文より登録データの取得を行う
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection.getConnection().prepareStatement("SELECT ItemId, Name, RegisterDate, UpdateDate, UserID, Password, Comment, MailAddress FROM PassList WHERE ItemId = ?;");
			statement.setInt(1, ItemID);
			resultSet = statement.executeQuery();
			// 結果一覧を取得する
			if (resultSet.next() == true) {
				passwordItem = new PasswordItem();
				passwordItem.setItemID(resultSet.getInt(1));
				passwordItem.setName(resultSet.getString(2));
				passwordItem.setRegisterDate(new Date(resultSet.getTimestamp(3).getTime()));
				passwordItem.setUpdateDate(new Date(resultSet.getTimestamp(4).getTime()));
				passwordItem.setUserID(resultSet.getString(5));
				passwordItem.setPassword(resultSet.getString(6));
				passwordItem.setComment(resultSet.getString(7));
				passwordItem.setMailAddress(resultSet.getString(8));
			} else {
				throw new NormalException("指定したIDに登録情報が存在しませんでした[ID=" + ItemID + "]");
			}
		} catch (SQLException e) {
			throw new FatalException("SQLのエラー", e);
		} finally {
			try {
				if (resultSet != null) resultSet.close();
				if (statement != null) statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return passwordItem;
	}

	/**
	 * 登録変更操作
	 *
	 * @param baseItem   登録変更情報Entity
	 * @param connection 接続情報
	 * @return 変更後の情報
	 * @throws FatalException  致命的エラー
	 * @throws NormalException 通常エラー
	 */
	public static PasswordItem Modification(PasswordItem baseItem, SQLConnection connection) throws FatalException, NormalException {
		if (connection.getConnection() == null) throw new FatalException("DBと接続していません");

		// 既存情報
		PasswordItem item = null;

		// トランザクション開始
		try {
			connection.getConnection().setAutoCommit(false);
		} catch (SQLException e) {
			throw new FatalException("SQLのエラー", e);
		}

		// IDの存在確認と既存の情報の取得
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.getConnection().prepareStatement("SELECT ItemId, Name, RegisterDate, UpdateDate, UserID, Password, Comment, MailAddress FROM PassList WHERE ItemId = ?;");
			statement.setInt(1, baseItem.getItemID());
			resultSet = statement.executeQuery();
			if (resultSet.next()) {
				// 検索結果があった場合は情報を取得する
				item = new PasswordItem();
				item.setItemID(resultSet.getInt(1));
				item.setName(resultSet.getString(2));
				item.setRegisterDate(new Date(resultSet.getTimestamp(3).getTime()));
				item.setUpdateDate(new Date(resultSet.getTimestamp(4).getTime()));
				item.setUserID(resultSet.getString(5));
				item.setPassword(resultSet.getString(6));
				item.setComment(resultSet.getString(7));
				item.setMailAddress(resultSet.getString(8));
			}
		} catch (SQLException e) {
			connection.setEnableAutoCommit();
			throw new FatalException("SQLのエラー", e);
		} finally {
			try {
				if (resultSet != null) resultSet.close();
				if (statement != null) statement.close();
			} catch (SQLException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

		// 情報が取得出来なかった場合は処理を失敗にする
		if (item == null) {
			// トランザクションの終了
			connection.setEnableAutoCommit();
			throw new NormalException("指定したIDに登録情報が存在しませんでした[ID=" + baseItem.getItemID() + "]");
		}

		// 更新日を設定する
		Date now = new Date();
		item.setUpdateDate(now);

		// 変更対象部分を変更する
		if (baseItem.getName() != null) item.setName(baseItem.getName());
		if (baseItem.getUserID() != null) item.setUserID(baseItem.getUserID());
		if (baseItem.getPassword() != null) item.setPassword(baseItem.getPassword());
		if (baseItem.getComment() != null) item.setComment(baseItem.getComment());
		if (baseItem.getMailAddress() != null) item.setMailAddress(baseItem.getMailAddress());

		// 正当性の確認【条件:変更データ必須】
		if (item.getUpdateDate() == null) {
			connection.setEnableAutoCommit();
			throw new FatalException("更新日が設定されませんでした");
		}

		if (item.getName() == null) {
			connection.setEnableAutoCommit();
			throw new NormalException("名前を設定してください");
		} else if (item.getName().length() > PasswordItem.MAXLENGTH_Name) {
			connection.setEnableAutoCommit();
			throw new NormalException("名前が長すぎます(最大" + PasswordItem.MAXLENGTH_Name + "文字)");
		}

		if (item.getUserID() == null) {
			connection.setEnableAutoCommit();
			throw new NormalException("UserIDを設定してください");
		} else if (item.getUserID().length() > PasswordItem.MAXLENGTH_UserID) {
			connection.setEnableAutoCommit();
			throw new NormalException("UserIDが長すぎます(最大" + PasswordItem.MAXLENGTH_UserID + "文字)");
		}

		if (item.getPassword() == null) {
			connection.setEnableAutoCommit();
			throw new NormalException("パスワードを設定してください");
		} else if (item.getPassword().length() > PasswordItem.MAXLENGTH_Password) {
			connection.setEnableAutoCommit();
			throw new NormalException("パスワードが長すぎます(最大" + PasswordItem.MAXLENGTH_Password + "文字)");
		}

		if (item.getComment() == null) {
			connection.setEnableAutoCommit();
			throw new NormalException("コメントを設定してください");
		} else if (item.getComment().length() > PasswordItem.MAXLENGTH_Comment) {
			connection.setEnableAutoCommit();
			throw new NormalException("コメントが長すぎます(最大" + PasswordItem.MAXLENGTH_Comment + "文字)");
		}

		if (item.getMailAddress() == null) {
			connection.setEnableAutoCommit();
			throw new NormalException("メールアドレスを設定してください");
		} else if (item.getMailAddress().length() > PasswordItem.MAXLENGTH_MailAddress) {
			connection.setEnableAutoCommit();
			throw new NormalException("メールアドレスが長すぎます(最大" + PasswordItem.MAXLENGTH_MailAddress + "文字)");
		}

		PreparedStatement preparedStatement = null;
		// SQL文の生成
		try {
			// 追加処理
			preparedStatement = connection.getConnection().prepareStatement("UPDATE PassList SET Name = ?, UpdateDate = ?, UserID = ?, Password = ?, Comment = ?, MailAddress = ? WHERE ItemId = ?;");
			// 変数の追加
			preparedStatement.setString(1, item.getName());
			preparedStatement.setTimestamp(2, new java.sql.Timestamp(item.getUpdateDate().getTime()));
			preparedStatement.setString(3, item.getUserID());
			preparedStatement.setString(4, item.getPassword());
			preparedStatement.setString(5, item.getComment());
			preparedStatement.setString(6, item.getMailAddress());
			preparedStatement.setInt(7, item.getItemID());
			// 実行
			int result = preparedStatement.executeUpdate();
			preparedStatement.close();
			preparedStatement = null;
			if (result != 1) {
				// 失敗
				connection.setEnableAutoCommit();
				throw new FatalException("レコードの変更に失敗");
			} else {
				connection.getConnection().commit();
			}
		} catch (SQLException e) {
			throw new FatalException("SQLのエラー", e);
		} finally {
			if (preparedStatement != null) try {
				preparedStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			connection.setEnableAutoCommit();
		}

		return item;
	}

	/**
	 * 登録削除操作
	 *
	 * @param itemID     削除対象のアイテムID
	 * @param connection 接続情報
	 * @return 削除したアイテムのEntity
	 * @throws FatalException  致命的エラー
	 * @throws NormalException 通常エラー
	 */
	public static PasswordItem Remove(int itemID, SQLConnection connection) throws FatalException, NormalException {
		if (connection.getConnection() == null) throw new FatalException("DBと接続していません");

		// 削除した（する）データ
		PasswordItem passwordItem = null;

		// トランザクション開始
		try {
			connection.getConnection().setAutoCommit(false);
		} catch (SQLException e) {
			throw new FatalException("SQLのエラー", e);
		}

		// SQL文より登録データの取得を行う
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.getConnection().prepareStatement("SELECT ItemId, Name, RegisterDate, UpdateDate, UserID, Password, Comment, MailAddress FROM PassList WHERE ItemId = ?;");
			statement.setInt(1, itemID);
			resultSet = statement.executeQuery();
			// 結果一覧を取得する
			if (resultSet.next() == true) {
				passwordItem = new PasswordItem();
				passwordItem.setItemID(resultSet.getInt(1));
				passwordItem.setName(resultSet.getString(2));
				passwordItem.setRegisterDate(new Date(resultSet.getTimestamp(3).getTime()));
				passwordItem.setUpdateDate(new Date(resultSet.getTimestamp(4).getTime()));
				passwordItem.setUserID(resultSet.getString(5));
				passwordItem.setPassword(resultSet.getString(6));
				passwordItem.setComment(resultSet.getString(7));
				passwordItem.setMailAddress(resultSet.getString(8));
			}
		} catch (SQLException e) {
			connection.setEnableAutoCommit();
			throw new FatalException("SQLのエラー", e);
		} finally {
			try {
				if (resultSet != null) resultSet.close();
				if (statement != null) statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// 情報が取得出来なかった場合は処理を失敗にする
		if (passwordItem == null) {
			// トランザクションの終了
			connection.setEnableAutoCommit();
			throw new NormalException("指定したIDに登録情報が存在しませんでした[ID=" + itemID + "]");
		}

		// 登録情報の削除処理を行う
		// SQL文より登録データの取得を行う
		PreparedStatement statement2 = null;
		try {
			statement2 = connection.getConnection().prepareStatement("DELETE FROM PassList WHERE ItemId = ?;");
			statement2.setInt(1, itemID);
			int result = statement2.executeUpdate();
			if (result != 1) {
				// 失敗
				connection.setEnableAutoCommit();
				throw new FatalException("レコードの削除に失敗");
			} else {
				connection.getConnection().commit();
			}
		} catch (SQLException e) {
			connection.setEnableAutoCommit();
			throw new FatalException("SQLのエラー", e);
		} finally {
			try {
				if (statement2 != null) statement2.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return passwordItem;
	}

}
