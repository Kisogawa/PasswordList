package android.ochawanz.passwordlistandroid.db

import android.content.ContentValues
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.ochawanz.passwordlistandroid.ConnectionServerInfo
import android.ochawanz.passwordlistandroid.ConnectionServerInfoItem
import android.ochawanz.passwordlistandroid.activity.MainActivity
import android.ochawanz.passwordlistandroid.utility.RandomString
import android.util.Log

/**
 * 接続先情報記録DBアクセスクラス
 */
class ServerInfoDBHelper(context: MainActivity) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
	companion object {
		// SQLiteファイル名
		private const val DATABASE_NAME = "ServerInfo.db"
		// テーブル名
		private const val TABLE_NAME = "ServerInfo"
		// カラム名(識別ID)
		private const val COLUMN_NAME_ID = "ID"
		// カラム長(識別ID)
		const val COLUMN_LENGTH_ID = 32
		// カラム名(接続先名)
		private const val COLUMN_NAME_NAME = "Name"
		// カラム長(接続先名)
		const val COLUMN_LENGTH_NAME = 128
		// カラム名(接続先アドレス)
		private const val COLUMN_NAME_ADDRESS = "Address"
		// カラム長(接続先アドレス)
		const val COLUMN_LENGTH_ADDRESS = 128
		// カラム名(接続先ポート番号)
		private const val COLUMN_NAME_PORTNO = "PortNo"
		// カラム名(暗号キー)
		private const val COLUMN_NAME_KEY64 = "Key64"
		// カラム長(暗号キー)
		const val COLUMN_LENGTH_KEY64 = 64
		// DBバージョン
		private const val DATABASE_VERSION = 1
		// テーブル作成用SQL
		private const val SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
				COLUMN_NAME_ID + " VARCHAR(" + COLUMN_LENGTH_ID + "), " +
				COLUMN_NAME_NAME + " VARCHAR(" + COLUMN_LENGTH_NAME + "), " +
				COLUMN_NAME_ADDRESS + " VARCHAR(" + COLUMN_LENGTH_ADDRESS + "), " +
				COLUMN_NAME_PORTNO + " INTEGER, " +
				COLUMN_NAME_KEY64 + " VARCHAR(" + COLUMN_LENGTH_KEY64 + "), " +
				" PRIMARY KEY(" + COLUMN_NAME_ID + "))"
	}

	private val mContext = context

	/**
	 *  接続先情報を取り出す
	 */
	val ConnectionServerInfos: List<ConnectionServerInfo>
		get() {
			// SELECT文を実行し結果を取得する(指定カラムの全項目を取得する)
			val cursor = readableDatabase.query(
					TABLE_NAME,                                            // テーブル名
					arrayOf(COLUMN_NAME_ID, COLUMN_NAME_NAME, COLUMN_NAME_ADDRESS, COLUMN_NAME_PORTNO, COLUMN_NAME_KEY64),    // 取得するカラム
					null,                                        // WHERE句1
					null,                                    // WHERE句2
					null,                                        // GROUPE BY 句1
					null,                                        // GROUPE BY 句2
					null                                        // 整頓
			)

			// リストの生成
			val resultList = mutableListOf<ConnectionServerInfo>()

			// 取得結果の確認
			while (cursor.moveToNext()) {
				// サーバ情報を作成する
				val mainServerInfo = ConnectionServerInfoItem.createInstance(cursor.getString(0),
						cursor.getString(1),
						cursor.getString(2),
						cursor.getInt(3),
						cursor.getString(4),
						false)

				if (!mainServerInfo.state) {
					Log.w("PasswordListAndroid", "接続先情報の取得失敗:" + mainServerInfo.comment)
					continue
				}

				resultList.add(ConnectionServerInfo(mContext, mainServerInfo.Value!!))
			}

			cursor.close()

			// 配列に変換し戻す
			return resultList.toList()
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

	/**
	 * データを更新する(全取り替え)
	 */
	fun SetAllConnectionServerInfo(connectionServerInfoItems: List<ConnectionServerInfoItem>): List<ConnectionServerInfo> {
		// テーブルの全項目削除
		try {
			// トランザクション開始
			writableDatabase.beginTransaction()
			// IDが空文字のリストにIDを設定する
			for (item in connectionServerInfoItems) {
				if (item.ID.isEmpty()) {
					while (true) {
						// ランダムな文字を取得
						val idStr = RandomString.generateRandomString(32)
						// 同一IDフラグ
						var flag = false
						// 同じIDを持つ要素が無いか確認する
						for (checkitem in connectionServerInfoItems) {
							if (checkitem.ID == idStr) {
								flag = true
								break
							}
						}
						// 同じIDを持っている場合は，再度やり直し
						if (flag) continue
						// IDを設定し無限ループを抜ける
						item.ID = idStr
						break
					}
				}
			}

			// テーブルの削除
			writableDatabase.delete(TABLE_NAME, null, null)

			// 追加する要素の定義
			for (item in connectionServerInfoItems) {
				val contentValues = ContentValues(5)
				contentValues.put(COLUMN_NAME_ID, item.ID)
				contentValues.put(COLUMN_NAME_NAME, item.Name)
				contentValues.put(COLUMN_NAME_ADDRESS, item.Address)
				contentValues.put(COLUMN_NAME_PORTNO, item.PortNo)
				contentValues.put(COLUMN_NAME_KEY64, item.Key64)
				// テーブルに追加
				writableDatabase.insertWithOnConflict(TABLE_NAME, "", contentValues, SQLiteDatabase.CONFLICT_IGNORE)
			}

			// 成功フラグ
			writableDatabase.setTransactionSuccessful()
		} catch (e: SQLException) {
			// SQLエラー
			e.printStackTrace()
		} finally {
			writableDatabase.endTransaction()
		}
		return ConnectionServerInfos
	}
}