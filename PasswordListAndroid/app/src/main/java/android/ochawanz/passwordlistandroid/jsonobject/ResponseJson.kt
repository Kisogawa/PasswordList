package android.ochawanz.passwordlistandroid.jsonobject

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Json返却データクラス
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class ResponseJson(responseInfomation: ResponseInfomation, PasswordItems: List<PasswordItem>) {

	constructor() : this(ResponseInfomation(), listOf())

	/**
	 * 応答データクラス
	 */
	@JsonProperty("responseInfomation")
	val responseInfomation = responseInfomation

	/**
	 * パスワードデータ格納配列
	 */
	@JsonProperty("passwordItems")
	val PasswordItems = PasswordItems
}