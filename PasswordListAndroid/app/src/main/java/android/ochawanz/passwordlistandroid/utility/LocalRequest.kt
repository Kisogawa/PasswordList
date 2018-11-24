package android.ochawanz.passwordlistandroid.utility

import android.ochawanz.passwordlistandroid.activity.MainActivity
import android.ochawanz.passwordlistandroid.db.LocalPasswordItemDBHelper
import android.ochawanz.passwordlistandroid.interfaces.IRequest
import android.ochawanz.passwordlistandroid.jsonobject.PasswordItem
import android.ochawanz.passwordlistandroid.jsonobject.ResponseInfomation
import android.ochawanz.passwordlistandroid.jsonobject.ResponseJson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class LocalRequest(mainActivity: MainActivity) : IRequest {

	// DB接続する
	private val mDBHelper = LocalPasswordItemDBHelper(mainActivity)


	/**
	 * 一覧情報を取得する
	 * @param webRequestInfomation リクエスト取得状況
	 * @return 成否(結果オブジェクト)
	 */
	override fun List(webRequestInfomation: WebRequestInfomation) = GlobalScope.async {
		try {
			webRequestInfomation.init()
			webRequestInfomation.status = "データ取得中"

			val passwordItems = mDBHelper.ItemListALL()

			webRequestInfomation.status = "データ解析中"

			// Jsonオブジェクトにパースする
			val hoge = ResponseJson(ResponseInfomation("Success", ""), passwordItems)

			webRequestInfomation.status = "完了"
			return@async ReturnObject(hoge)
		} catch (e: Exception) {
			webRequestInfomation.status = "中止"
			e.printStackTrace()
			return@async ReturnObject(ResponseJson(), e)
		}
	}

	/**
	 * 詳細情報を取得する
	 * @param passwordItem 詳細取得を行データエンティティ(有効情報:ID)
	 * @param webRequestInfomation リクエスト取得状況
	 * @return 成否(結果オブジェクト)
	 */
	override fun Show(passwordItem: PasswordItem, webRequestInfomation: WebRequestInfomation) = GlobalScope.async {
		try {
			webRequestInfomation.init()
			webRequestInfomation.status = "データ取得中"

			if (passwordItem.ItemID == 0) return@async ReturnObject(ResponseJson(), "IDを指定してください")

			val requestResult = mDBHelper.Item(passwordItem)

			// 失敗している場合(キャンセル等)
			if (!requestResult.state) {
				webRequestInfomation.status = "中止"
				return@async ReturnObject(ResponseJson(), requestResult)
			}

			webRequestInfomation.status = "データ解析中"

			// Jsonオブジェクトにパースする
			val hoge = ResponseJson(ResponseInfomation("Success", ""), listOf(requestResult.Value))

			webRequestInfomation.status = "完了"
			return@async ReturnObject(hoge)
		} catch (e: Exception) {
			webRequestInfomation.status = "中止"
			e.printStackTrace()
			return@async ReturnObject(ResponseJson(), e)
		}
	}

	/**
	 * 登録する
	 * @param passwordItem 登録データエンティティ(有効情報:ID以外)
	 * @param webRequestInfomation リクエスト取得状況
	 * @return 成否(結果オブジェクト)
	 */
	override fun Register(passwordItem: PasswordItem, webRequestInfomation: WebRequestInfomation) = GlobalScope.async {
		try {
			webRequestInfomation.init()

			// 登録用データの確認
			if (passwordItem.Name == null) ReturnObject(ResponseJson(), "Name要素はnullに出来ません")
			if (passwordItem.UserID == null) ReturnObject(ResponseJson(), "UserID要素はnullに出来ません")
			if (passwordItem.Password == null) ReturnObject(ResponseJson(), "Password要素はnullに出来ません")
			if (passwordItem.Comment == null) ReturnObject(ResponseJson(), "Comment要素はnullに出来ません")
			if (passwordItem.MailAddress == null) ReturnObject(ResponseJson(), "MailAddress要素はnullに出来ません")

			webRequestInfomation.status = "データ取得中"

			val requestResult = mDBHelper.Register(passwordItem)

			// 失敗している場合(キャンセル等)
			if (!requestResult.state) {
				webRequestInfomation.status = "中止"
				return@async ReturnObject(ResponseJson(), requestResult)
			}

			webRequestInfomation.status = "データ解析中"

			// Jsonオブジェクトにパースする
			val hoge = ResponseJson(ResponseInfomation("Success", ""), listOf(requestResult.Value))

			webRequestInfomation.status = "完了"
			return@async ReturnObject(hoge)
		} catch (e: Exception) {
			webRequestInfomation.status = "中止"
			e.printStackTrace()
			return@async ReturnObject(ResponseJson(), e)
		}
	}

	/**
	 * 変更する
	 * @param passwordItem 変更データエンティティ(有効情報:ID,変更部分)
	 * @param webRequestInfomation リクエスト取得状況
	 * @return 成否(結果オブジェクト)
	 */
	override fun Update(passwordItem: PasswordItem, webRequestInfomation: WebRequestInfomation) = GlobalScope.async {
		try {
			webRequestInfomation.init()

			// 登録用データの確認
			if (passwordItem.ItemID == 0) return@async ReturnObject(ResponseJson(), "IDを指定してください")

			webRequestInfomation.status = "データ取得中"

			val requestResult = mDBHelper.Update(passwordItem)

			// 失敗している場合(キャンセル等)
			if (!requestResult.state) {
				webRequestInfomation.status = "中止"
				return@async ReturnObject(ResponseJson(), requestResult)
			}

			webRequestInfomation.status = "データ解析中"

			// Jsonオブジェクトにパースする
			val hoge = ResponseJson(ResponseInfomation("Success", ""), listOf(requestResult.Value))

			webRequestInfomation.status = "完了"
			return@async ReturnObject(hoge)
		} catch (e: Exception) {
			webRequestInfomation.status = "中止"
			e.printStackTrace()
			return@async ReturnObject(ResponseJson(), e)
		}
	}

	/**
	 * 削除する
	 * @param passwordItem 削除データエンティティ(有効情報:ID)
	 * @param webRequestInfomation リクエスト取得状況
	 * @return 成否(結果オブジェクト)
	 */
	override fun Remove(passwordItem: PasswordItem, webRequestInfomation: WebRequestInfomation) = GlobalScope.async {
		try {
			webRequestInfomation.init()

			// 登録用データの確認
			if (passwordItem.ItemID == 0) return@async ReturnObject(ResponseJson(), "IDを指定してください")

			webRequestInfomation.status = "データ取得中"

			val requestResult = mDBHelper.Remove(passwordItem)

			// 失敗している場合(キャンセル等)
			if (!requestResult.state) {
				webRequestInfomation.status = "中止"
				return@async ReturnObject(ResponseJson(), requestResult)
			}

			webRequestInfomation.status = "データ解析中"

			// Jsonオブジェクトにパースする
			val hoge = ResponseJson(ResponseInfomation("Success", ""), listOf(requestResult.Value))

			webRequestInfomation.status = "完了"
			return@async ReturnObject(hoge)
		} catch (e: Exception) {
			webRequestInfomation.status = "中止"
			e.printStackTrace()
			return@async ReturnObject(ResponseJson(), e)
		}
	}
}