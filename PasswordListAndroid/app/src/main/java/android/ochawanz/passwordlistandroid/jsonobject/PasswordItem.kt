package android.ochawanz.passwordlistandroid.jsonobject

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * パスワードアイテム
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class PasswordItem() {
	companion object {
		// 最長の文字数
		val MAXLENGTH_Name = 128
		val MAXLENGTH_UserID = 128
		val MAXLENGTH_Password = 128
		val MAXLENGTH_Comment = 512
		val MAXLENGTH_MailAddress = 128
	}

	/**
	 * コピーコンストラクタ
	 * @param passwordItem コピー元インスタンス
	 */
	constructor(passwordItem: PasswordItem) : this() {
		ItemID = passwordItem.ItemID
		Name = passwordItem.Name
		RegisterDate = passwordItem.RegisterDate
		UpdateDate = passwordItem.UpdateDate
		UserID = passwordItem.UserID
		Comment = passwordItem.Comment
		MailAddress = passwordItem.MailAddress
		Password = passwordItem.Password
	}

	/**
	 * 識別ID(0は無効値)
	 */
	@field:JsonProperty("ItemID")
	var ItemID: Int = 0
		set(value) {
			field = if (value < 0) 0 else value
		}

	/**
	 * 名前
	 */
	@JsonProperty("Name")
	var Name: String? = null

	/**
	 * 登録日
	 */
	@JsonIgnore
	var RegisterDate: Date? = null

	/**
	 * 更新日
	 */
	@JsonIgnore
	var UpdateDate: Date? = null

	/**
	 * ユーザーID
	 */
	@JsonProperty("UserID")
	var UserID: String? = null

	/**
	 * コメント
	 */
	@JsonProperty("Comment")
	var Comment: String? = null

	/**
	 * メールアドレス
	 */
	@JsonProperty("MailAddress")
	var MailAddress: String? = null

	/**
	 * パスワード
	 */
	@JsonProperty("Password")
	var Password: String? = null

	/**
	 * 登録日の文字列版(yyyyMMdd HHmmss)
	 */
	@set:JsonProperty("RegisterDateTime")
	@get:JsonProperty("RegisterDateTime")
	var RegisterDateWithString: String?
		get() = if (RegisterDate == null) null else SimpleDateFormat("yyyyMMdd HHmmss").format(RegisterDate)
		set(value) {
			if (value == null) {
				RegisterDate = null
				return
			}
			RegisterDate = try {
				SimpleDateFormat("yyyyMMdd HHmmss").parse(value)
			} catch (e: ParseException) {
				null
			}
		}

	/**
	 * 更新日の文字列版(yyyyMMdd HHmmss)
	 */
	@set:JsonProperty("UpdateDateTime")
	@get:JsonProperty("UpdateDateTime")
	var UpdateDateWithString: String?
		get() = if (UpdateDate == null) null else SimpleDateFormat("yyyyMMdd HHmmss").format(UpdateDate)
		set(value) {
			if (value == null) {
				UpdateDate = null
				return
			}
			UpdateDate = try {
				SimpleDateFormat("yyyyMMdd HHmmss").parse(value)
			} catch (e: ParseException) {
				null
			}
		}


	override fun toString() = Name + "(#" + java.text.DecimalFormat("0000").format(ItemID) + ")"
}