package android.ochawanz.passwordlistandroid.jsonobject

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Json応答情報
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class ResponseInfomation(result: String, comment: String) {

	constructor() : this("", "")

	/**
	 * 結果情報
	 */
	@JsonProperty("result")
	val result: String = result

	/**
	 * コメント
	 */
	@JsonProperty("comment")
	val comment: String = comment
}