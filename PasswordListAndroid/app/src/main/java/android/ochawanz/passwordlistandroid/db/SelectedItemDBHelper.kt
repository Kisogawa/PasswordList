package android.ochawanz.passwordlistandroid.db

import android.content.ContentValues
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.ochawanz.passwordlistandroid.activity.MainActivity

/**
 * 選択している接続先情報IDを管理するテーブル
 */
class SelectedItemDBHelper(context: MainActivity) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
	companion object {
		// SQLiteファイル名
		private const val DATABASE_NAME = "SelectedCnnectionServerInfo.db"
		// テーブル名
		private const val TABLE_NAME = "SelectedCnnectionServerInfo"
		// カラム名(識別ID)
		private const val COLUMN_NAME_ID = "SelectedID"
		// カラム長(識別ID)
		const val COLUMN_LENGTH_ID = ServerInfoDBHelper.COLUMN_LENGTH_ID
		// DBバージョン
		private const val DATABASE_VERSION = 1
		// テーブル作成用SQL
		private const val SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
				COLUMN_NAME_ID + " VARCHAR(" + COLUMN_LENGTH_ID + "), " +
				" PRIMARY KEY(" + COLUMN_NAME_ID + "))"
	}

	/**
	 *  接続先情報を取り出す
	 */
	var SelectedCnnectionServerInfoID: String
		get() {
			// SELECT文を実行し結果を取得する(指定カラムの全項目を取得する)
			val cursor = readableDatabase.query(
					TABLE_NAME,                                            // テーブル名
					arrayOf(COLUMN_NAME_ID),    // 取得するカラム
					null,                                        // WHERE句1
					null,                                    // WHERE句2
					null,                                        // GROUPE BY 句1
					null,                                        // GROUPE BY 句2
					null                                        // 整頓
			)

			// 取得結果の確認
			while (cursor.moveToNext()) {
				val s = cursor.getString(0)
				cursor.close()
				return s
			}

			cursor.close()

			return ""
		}
		set(value) {
			try {
				// トランザクション開始
				writableDatabase.beginTransaction()
				// テーブルの削除
				writableDatabase.delete(TABLE_NAME, null, null)
				// 追加する要素の定義

				val contentValues = ContentValues(5)
				contentValues.put(COLUMN_NAME_ID, value)
				// テーブルに追加
				writableDatabase.insertWithOnConflict(TABLE_NAME, "", contentValues, SQLiteDatabase.CONFLICT_IGNORE)

				// 成功フラグ
				writableDatabase.setTransactionSuccessful()
			} catch (e: SQLException) {
				// SQLエラー
				e.printStackTrace()
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