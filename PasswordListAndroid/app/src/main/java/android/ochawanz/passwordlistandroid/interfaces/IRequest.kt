package android.ochawanz.passwordlistandroid.interfaces

import android.ochawanz.passwordlistandroid.jsonobject.PasswordItem
import android.ochawanz.passwordlistandroid.jsonobject.ResponseJson
import android.ochawanz.passwordlistandroid.utility.ReturnObject
import android.ochawanz.passwordlistandroid.utility.WebRequestInfomation
import kotlinx.coroutines.Deferred

/**
 * リクエスト発行
 */
interface IRequest {
	/**
	 * 一覧情報を取得する
	 * @param webRequestInfomation リクエスト取得状況
	 * @return 成否(結果オブジェクト)
	 */
	fun List(webRequestInfomation: WebRequestInfomation): Deferred<ReturnObject<ResponseJson>>

	/**
	 * 詳細情報を取得する
	 * @param passwordItem 詳細取得を行データエンティティ(有効情報:ID)
	 * @param webRequestInfomation リクエスト取得状況
	 * @return 成否(結果オブジェクト)
	 */
	fun Show(passwordItem: PasswordItem, webRequestInfomation: WebRequestInfomation): Deferred<ReturnObject<ResponseJson>>

	/**
	 * 登録する
	 * @param passwordItem 登録データエンティティ(有効情報:ID以外)
	 * @param webRequestInfomation リクエスト取得状況
	 * @return 成否(結果オブジェクト)
	 */
	fun Register(passwordItem: PasswordItem, webRequestInfomation: WebRequestInfomation): Deferred<ReturnObject<ResponseJson>>

	/**
	 * 変更する
	 * @param passwordItem 変更データエンティティ(有効情報:ID,変更部分)
	 * @param webRequestInfomation リクエスト取得状況
	 * @return 成否(結果オブジェクト)
	 */
	fun Update(passwordItem: PasswordItem, webRequestInfomation: WebRequestInfomation): Deferred<ReturnObject<ResponseJson>>

	/**
	 * 削除する
	 * @param passwordItem 削除データエンティティ(有効情報:ID)
	 * @param webRequestInfomation リクエスト取得状況
	 * @return 成否(結果オブジェクト)
	 */
	fun Remove(passwordItem: PasswordItem, webRequestInfomation: WebRequestInfomation): Deferred<ReturnObject<ResponseJson>>
}