package android.ochawanz.passwordlistandroid.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.ochawanz.passwordlistandroid.activity.MainActivity
import android.ochawanz.passwordlistandroid.jsonobject.PasswordItem
import android.ochawanz.passwordlistandroid.utility.ReturnObject
import java.lang.Exception
import java.util.*

/**
 * 選択している接続先情報IDを管理するテーブル
 */
class LocalPasswordItemDBHelper(context: MainActivity) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
	companion object {
		// SQLiteファイル名
		private const val DATABASE_NAME = "PasswordList.db"
		// テーブル名
		private const val TABLE_NAME = "PassList"
		// カラム名(アイテムID)
		private const val COLUMN_NAME_ITEMID = "ItemId"
		// カラム名(アイテム名)
		private const val COLUMN_NAME_NAME = "Name"
		// カラム長(アイテム名)
		private const val COLUMN_LENGTH_NAME = 128
		// カラム名(登録日)
		private const val COLUMN_NAME_REGISTERDATE = "RegisterDate"
		// カラム名(更新日)
		private const val COLUMN_NAME_UPDATEDATE = "UpdateDate"
		// カラム名(ID)
		private const val COLUMN_NAME_USERID = "UserID"
		// カラム長(ID)
		private const val COLUMN_LENGTH_USERID = 128
		// カラム名(パスワード)
		private const val COLUMN_NAME_PASSWORD = "Password"
		// カラム長(パスワード)
		private const val COLUMN_LENGTH_PASSWORD = 128
		// カラム名(コメント)
		private const val COLUMN_NAME_COMMENT = "Comment"
		// カラム長(コメント)
		private const val COLUMN_LENGTH_COMMENT = 512
		// カラム名(コメント)
		private const val COLUMN_NAME_MAILADDRESS = "MailAddress"
		// カラム長(コメント)
		private const val COLUMN_LENGTH_MAILADDRESS = 128
		// DBバージョン
		private const val DATABASE_VERSION = 1
		// テーブル作成用SQL
		private const val SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
				COLUMN_NAME_ITEMID + " INT, " +
				COLUMN_NAME_NAME + " VARCHAR(" + COLUMN_LENGTH_NAME + "), " +
				COLUMN_NAME_REGISTERDATE + " DATETIME, " +
				COLUMN_NAME_UPDATEDATE + " DATETIME, " +
				COLUMN_NAME_USERID + " VARCHAR(" + COLUMN_LENGTH_USERID + "), " +
				COLUMN_NAME_PASSWORD + " VARCHAR(" + COLUMN_LENGTH_PASSWORD + "), " +
				COLUMN_NAME_COMMENT + " VARCHAR(" + COLUMN_LENGTH_COMMENT + "), " +
				COLUMN_NAME_MAILADDRESS + " VARCHAR(" + COLUMN_LENGTH_MAILADDRESS + "), " +
				" PRIMARY KEY(" + COLUMN_NAME_ITEMID + "))"
	}

	/**
	 * 一覧を取得する(ItemId,Name要素のみ取得)
	 */
	fun ItemListALL(): List<PasswordItem> {
		// アイテム全要素取得
		val cursor = readableDatabase.query(
				TABLE_NAME,                                            // テーブル名
				arrayOf(COLUMN_NAME_ITEMID, COLUMN_NAME_NAME),         // 取得するカラム
				null,                                        // WHERE句1
				null,                                    // WHERE句2
				null,                                         // GROUPE BY 句1
				null,                                          // GROUPE BY 句2
				null                                          // 整頓
		)
		// 結果格納用リスト
		val items = mutableListOf<PasswordItem>()
		// 取得結果の確認
		while (cursor.moveToNext()) {
			val p = PasswordItem()
			p.ItemID = cursor.getInt(0)
			p.Name = cursor.getString(1)
			items.add(p)
		}
		cursor.close()
		return items
	}

	/**
	 * アイテムを取得する(ItemId指定)
	 */
	fun Item(passwordItem: PasswordItem): ReturnObject<PasswordItem> {
		// アイテム全要素取得
		val cursor = readableDatabase.query(
				TABLE_NAME,                                            // テーブル名
				arrayOf(COLUMN_NAME_ITEMID, COLUMN_NAME_NAME, COLUMN_NAME_REGISTERDATE, COLUMN_NAME_UPDATEDATE, COLUMN_NAME_USERID, COLUMN_NAME_PASSWORD, COLUMN_NAME_COMMENT, COLUMN_NAME_MAILADDRESS),         // 取得するカラム
				COLUMN_NAME_ITEMID + "=?",                                        // WHERE句1
				arrayOf(passwordItem.ItemID.toString()),                                    // WHERE句2
				null,                                         // GROUPE BY 句1
				null,                                          // GROUPE BY 句2
				null                                          // 整頓
		)
		// 結果格納用リスト
		var items: PasswordItem? = null
		// 取得結果の確認
		while (cursor.moveToNext()) {
			val p = PasswordItem()
			p.ItemID = cursor.getInt(0)
			p.Name = cursor.getString(1)
			p.RegisterDateWithString = cursor.getString(2)
			p.UpdateDateWithString = cursor.getString(3)
			p.UserID = cursor.getString(4)
			p.Password = cursor.getString(5)
			p.Comment = cursor.getString(6)
			p.MailAddress = cursor.getString(7)
			items = p
		}
		cursor.close()

		return if (items != null) ReturnObject(items) else ReturnObject(PasswordItem(), "該当アイテム無し")
	}

	/**:
	 * アイテムを追加する
	 */
	fun Register(passwordItem: PasswordItem): ReturnObject<PasswordItem> {
		// 入力データ確認
		// 正当性の確認【条件:全データ必須】

		if (passwordItem.Name == null) return ReturnObject(PasswordItem(), "名前を設定してください")
		else if (passwordItem.Name!!.length > PasswordItem.MAXLENGTH_Name) return ReturnObject(PasswordItem(), "名前が長すぎます(最大" + PasswordItem.MAXLENGTH_Name + "文字)")

		if (passwordItem.UserID == null) return ReturnObject(PasswordItem(), "UserIDを設定してください");
		else if (passwordItem.UserID!!.length > PasswordItem.MAXLENGTH_UserID) return ReturnObject(PasswordItem(), "名前が長すぎます(最大" + PasswordItem.MAXLENGTH_UserID + "文字)")

		if (passwordItem.Password == null) return ReturnObject(PasswordItem(), "パスワードを設定してください");
		else if (passwordItem.Password!!.length > PasswordItem.MAXLENGTH_Password) return ReturnObject(PasswordItem(), "パスワードが長すぎます(最大" + PasswordItem.MAXLENGTH_Password + "文字)")

		if (passwordItem.Comment == null) return ReturnObject(PasswordItem(), "コメントを設定してください");
		else if (passwordItem.Comment!!.length > PasswordItem.MAXLENGTH_Comment) return ReturnObject(PasswordItem(), "コメントが長すぎます(最大" + PasswordItem.MAXLENGTH_Comment + "文字)")

		if (passwordItem.MailAddress == null) return ReturnObject(PasswordItem(), "メールアドレスを設定してください")
		else if (passwordItem.MailAddress!!.length > PasswordItem.MAXLENGTH_MailAddress) return ReturnObject(PasswordItem(), "メールアドレスが長すぎます(最大" + PasswordItem.MAXLENGTH_MailAddress + "文字)")

		// 入力データの複製
		val pItem = PasswordItem(passwordItem)

		// 登録日，更新日を設定する
		val today = Date()
		pItem.RegisterDate = today
		pItem.UpdateDate = today

		// トランザクション開始
		writableDatabase.beginTransaction()
		try {

			// 候補ItemID(0無効)
			var itemID = 0
			// アイテムIDが1の要素が存在するか確認
			val cursor = readableDatabase.query(
					TABLE_NAME,                          // テーブル名
					arrayOf(COLUMN_NAME_ITEMID),         // 取得するカラム
					COLUMN_NAME_ITEMID + "=?", // WHERE句1
					arrayOf(1.toString()),               // WHERE句2
					null,                       // GROUPE BY 句1
					null,                        // GROUPE BY 句2
					null                        // 整頓
			)
			if (cursor.count == 0) itemID = 1
			cursor.close()
			// 1番が存在する場合は欠番の検索を行う
			if (itemID == 0) {
				val cursor2 = readableDatabase.rawQuery("SELECT ItemId FROM PassList ORDER BY ItemId ASC;", null)
				// 抜け番候補
				var c = 1
				// 検索結果を番号として登録
				while (cursor2.moveToNext()) {
					val cc = cursor2.getInt(0)
					if (cc != c) break
					c++
				}
				cursor2.close()
				itemID = c
			}

			// IDの設定
			pItem.ItemID = itemID

			// 確認していなかった要素の確認
			if (pItem.ItemID <= 0) return ReturnObject(PasswordItem(), "自動で設定されたIDが0以下です")
//		if (pItem.RegisterDate == null) return ReturnObject(PasswordItem(), "登録日が設定されませんでした")
//		if (pItem.UpdateDate == null) return ReturnObject(PasswordItem(), "更新日が設定されませんでした")

			// 登録データ作成
			val contentValues = ContentValues()
			contentValues.put(COLUMN_NAME_ITEMID, pItem.ItemID)
			contentValues.put(COLUMN_NAME_NAME, pItem.Name)
			contentValues.put(COLUMN_NAME_REGISTERDATE, pItem.RegisterDateWithString)
			contentValues.put(COLUMN_NAME_UPDATEDATE, pItem.UpdateDateWithString)
			contentValues.put(COLUMN_NAME_USERID, pItem.UserID)
			contentValues.put(COLUMN_NAME_PASSWORD, pItem.Password)
			contentValues.put(COLUMN_NAME_COMMENT, pItem.Comment)
			contentValues.put(COLUMN_NAME_MAILADDRESS, pItem.MailAddress)

			// 登録用SQL
			val cursorRegister = readableDatabase.insert(
					TABLE_NAME,
					"",
					contentValues
			)

			// 確認
			if (cursorRegister == -1L) {
				return ReturnObject(PasswordItem(), "登録に失敗")
			}
			// トランザクションを成功
			writableDatabase.setTransactionSuccessful()

			return ReturnObject(pItem)
		} catch (e: Exception) {
			return ReturnObject(PasswordItem(), "登録に失敗")
		} finally {
			writableDatabase.endTransaction()
		}
	}

	/**:
	 * アイテムを更新する
	 */
	fun Update(passwordItem: PasswordItem): ReturnObject<PasswordItem> {
		// 入力データ確認
		if (passwordItem.ItemID <= 0) return ReturnObject(PasswordItem(), "自動で設定されたIDが0以下です")

		// 入力データの正当性確認
		if (passwordItem.Name != null && passwordItem.Name!!.length > PasswordItem.MAXLENGTH_Name) return ReturnObject(PasswordItem(), "名前が長すぎます(最大" + PasswordItem.MAXLENGTH_Name + "文字)")

		if (passwordItem.UserID != null && passwordItem.UserID!!.length > PasswordItem.MAXLENGTH_UserID) return ReturnObject(PasswordItem(), "名前が長すぎます(最大" + PasswordItem.MAXLENGTH_UserID + "文字)")

		if (passwordItem.Password != null && passwordItem.Password!!.length > PasswordItem.MAXLENGTH_Password) return ReturnObject(PasswordItem(), "パスワードが長すぎます(最大" + PasswordItem.MAXLENGTH_Password + "文字)")

		if (passwordItem.Comment != null && passwordItem.Comment!!.length > PasswordItem.MAXLENGTH_Comment) return ReturnObject(PasswordItem(), "コメントが長すぎます(最大" + PasswordItem.MAXLENGTH_Comment + "文字)")

		if (passwordItem.MailAddress != null && passwordItem.MailAddress!!.length > PasswordItem.MAXLENGTH_MailAddress) return ReturnObject(PasswordItem(), "メールアドレスが長すぎます(最大" + PasswordItem.MAXLENGTH_MailAddress + "文字)")

		// 入力データの複製
		val pItem = PasswordItem(passwordItem)

		// 更新日を設定する
		val today = Date()
		pItem.UpdateDate = today

		// トランザクション開始
		writableDatabase.beginTransaction()
		try {
			// 更新対象のアイテムを取得する
			// アイテム全要素取得
			val cursor = readableDatabase.query(
					TABLE_NAME,                                            // テーブル名
					arrayOf(COLUMN_NAME_ITEMID, COLUMN_NAME_NAME, COLUMN_NAME_REGISTERDATE, COLUMN_NAME_UPDATEDATE, COLUMN_NAME_USERID, COLUMN_NAME_PASSWORD, COLUMN_NAME_COMMENT, COLUMN_NAME_MAILADDRESS),         // 取得するカラム
					COLUMN_NAME_ITEMID + "=?",                                        // WHERE句1
					arrayOf(pItem.ItemID.toString()),                                    // WHERE句2
					null,                                         // GROUPE BY 句1
					null,                                          // GROUPE BY 句2
					null                                          // 整頓
			)
			// 結果格納用リスト
			var baseItem: PasswordItem? = null
			// 取得結果の確認
			if (cursor.moveToNext()) {
				val p = PasswordItem()
				p.ItemID = cursor.getInt(0)
				p.Name = cursor.getString(1)
				p.RegisterDateWithString = cursor.getString(2)
				p.UpdateDateWithString = cursor.getString(3)
				p.UserID = cursor.getString(4)
				p.Password = cursor.getString(5)
				p.Comment = cursor.getString(6)
				p.MailAddress = cursor.getString(7)
				baseItem = p
			}
			cursor.close()
			if (baseItem == null) return ReturnObject(PasswordItem(), "指定したIDに登録情報が存在しませんでした[ID=" + pItem.ItemID + "]")

			// 足りない情報を補完する
			if (pItem.Name == null) pItem.Name = baseItem.Name
			if (pItem.UserID == null) pItem.UserID = baseItem.UserID
			if (pItem.Password == null) pItem.Password = baseItem.Password
			if (pItem.Comment == null) pItem.Comment = baseItem.Comment
			if (pItem.MailAddress == null) pItem.MailAddress = baseItem.MailAddress

			if (pItem.RegisterDate == null) pItem.RegisterDate = baseItem.RegisterDate

			// 更新操作
			val contentValues = ContentValues()
			contentValues.put(COLUMN_NAME_ITEMID, pItem.ItemID)
			contentValues.put(COLUMN_NAME_NAME, pItem.Name)
			contentValues.put(COLUMN_NAME_REGISTERDATE, pItem.RegisterDateWithString)
			contentValues.put(COLUMN_NAME_UPDATEDATE, pItem.UpdateDateWithString)
			contentValues.put(COLUMN_NAME_USERID, pItem.UserID)
			contentValues.put(COLUMN_NAME_PASSWORD, pItem.Password)
			contentValues.put(COLUMN_NAME_COMMENT, pItem.Comment)
			contentValues.put(COLUMN_NAME_MAILADDRESS, pItem.MailAddress)

			// 登録用SQL
			val cursorRegister = readableDatabase.update(
					TABLE_NAME,
					contentValues,
					COLUMN_NAME_ITEMID + " = ?",
					arrayOf(pItem.ItemID.toString())
			)

			// 確認
			if (cursorRegister != 1) {
				return ReturnObject(PasswordItem(), "登録に失敗")
			}
			// トランザクションを成功
			writableDatabase.setTransactionSuccessful()

			return ReturnObject(pItem)
		} catch (e: Exception) {
			return ReturnObject(PasswordItem(), "登録に失敗")
		} finally {
			writableDatabase.endTransaction()
		}
	}

	/**:
	 * アイテムを削除する
	 */
	fun Remove(passwordItem: PasswordItem): ReturnObject<PasswordItem> {
		// 入力データ確認
		if (passwordItem.ItemID <= 0) return ReturnObject(PasswordItem(), "自動で設定されたIDが0以下です")

		// 入力データの複製
		val pItem = PasswordItem(passwordItem)

		// トランザクション開始
		writableDatabase.beginTransaction()
		try {
			// 更新対象のアイテムを取得する
			// アイテム全要素取得
			val cursor = readableDatabase.query(
					TABLE_NAME,                                            // テーブル名
					arrayOf(COLUMN_NAME_ITEMID, COLUMN_NAME_NAME, COLUMN_NAME_REGISTERDATE, COLUMN_NAME_UPDATEDATE, COLUMN_NAME_USERID, COLUMN_NAME_PASSWORD, COLUMN_NAME_COMMENT, COLUMN_NAME_MAILADDRESS),         // 取得するカラム
					COLUMN_NAME_ITEMID + "=?",                   // WHERE句1
					arrayOf(pItem.ItemID.toString()),                      // WHERE句2
					null,                                         // GROUPE BY 句1
					null,                                          // GROUPE BY 句2
					null                                          // 整頓
			)
			// 結果格納用リスト
			var baseItem: PasswordItem? = null
			// 取得結果の確認
			if (cursor.moveToNext()) {
				val p = PasswordItem()
				p.ItemID = cursor.getInt(0)
				p.Name = cursor.getString(1)
				p.RegisterDateWithString = cursor.getString(2)
				p.UpdateDateWithString = cursor.getString(3)
				p.UserID = cursor.getString(4)
				p.Password = cursor.getString(5)
				p.Comment = cursor.getString(6)
				p.MailAddress = cursor.getString(7)
				baseItem = p
			}
			cursor.close()
			if (baseItem == null) return ReturnObject(PasswordItem(), "指定したIDに登録情報が存在しませんでした[ID=" + pItem.ItemID + "]")

			// 登録用SQL
			val cursorRegister = readableDatabase.delete(
					TABLE_NAME,
					COLUMN_NAME_ITEMID + " = ?",
					arrayOf(pItem.ItemID.toString())
			)

			// 確認
			if (cursorRegister != 1) {
				return ReturnObject(PasswordItem(), "登録に失敗")
			}
			// トランザクションを成功
			writableDatabase.setTransactionSuccessful()

			return ReturnObject(pItem)
		} catch (e: Exception) {
			return ReturnObject(PasswordItem(), "登録に失敗")
		} finally {
			writableDatabase.endTransaction()
		}
	}

	// SQLデータベースが存在しないとき(新規に作成されるとき)
	override fun onCreate(db: SQLiteDatabase?) {
		// DBテーブルが存在しないため作成
		db?.execSQL(SQL_CREATE_TABLE)
	}

	// DBのバージョンが上がったとき
	override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
		// 処理無し
	}
}