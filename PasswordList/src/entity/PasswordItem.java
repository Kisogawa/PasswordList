package entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * パスワード情報を記録
 */
public class PasswordItem {

	// 最長の文字数
	static public final int MAXLENGTH_Name = 128;
	static public final int MAXLENGTH_UserID = 128;
	static public final int MAXLENGTH_Password = 128;
	static public final int MAXLENGTH_Comment = 512;
	static public final int MAXLENGTH_MailAddress = 128;

	/**
	 * 識別ID
	 */
	private int ItemID = 0;

	/**
	 * 名前
	 */
	private String Name = null;

	/**
	 * 登録時間(秒まで)
	 */
	private Date RegisterDate = null;

	/**
	 * 更新時間(秒まで)
	 */
	private Date UpdateDate = null;

	/**
	 * ユーザーID
	 */
	private String UserID = null;

	/**
	 * パスワード
	 */
	private String Password = null;

	/**
	 * コメント
	 */
	private String Comment = null;

	/**
	 * メールアドレス
	 */
	private String MailAddress = null;

	// ----------------------------------------------------------------------

	// デフォルトコンストラクタ
	public PasswordItem() {
	}

	/**
	 * オブジェクトの複製を行う【コピーコンストラクタ】
	 *
	 * @param item コピー元オブジェクト
	 */
	public PasswordItem(PasswordItem item) {
		ItemID = item.ItemID;
		Name = item.Name;
		if (item.RegisterDate != null) RegisterDate = new Date(item.RegisterDate.getTime());
		if (item.UpdateDate != null) UpdateDate = new Date(item.UpdateDate.getTime());
		UserID = item.UserID;
		Password = item.Password;
		Comment = item.Comment;
		MailAddress = item.MailAddress;
	}

	// ----------------------------------------------------------------------

	/**
	 * 識別IDを取得する
	 *
	 * @return 識別ID(0は無効値)
	 */
	public int getItemID() {
		return ItemID;
	}

	/**
	 * 識別IDを設定する
	 *
	 * @param itemID 設定する識別ID(0以下は無効値)
	 */
	public void setItemID(int itemID) {
		if (itemID < 0) itemID = 0;
		ItemID = itemID;
	}

	/**
	 * 名前を取得する
	 *
	 * @return 名前(nullは無効値)
	 */
	public String getName() {
		return Name;
	}

	/**
	 * 名前を設定する
	 *
	 * @param 名前(nullは無効値)
	 */
	public void setName(String name) {
		Name = name;
	}

	/**
	 * 登録日を取得する(Json化では使用されない)
	 *
	 * @return 登録日(nullは無効値)
	 */
	@JsonIgnore
	public Date getRegisterDate() {
		return RegisterDate;
	}

	/**
	 * 登録日を取得する(Json化では使用されない)
	 *
	 * @return 登録日(nullは無効値)
	 */
	@JsonProperty("RegisterDateTime")
	public String getRegisterDateStr() {
		if (getRegisterDate() == null) return null;
		// SimpleDateFormatクラスでフォーマットパターンを設定する
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss");
		return sdf.format(getRegisterDate());
	}

	/**
	 * 登録日を設定する
	 *
	 * @param 登録日(nullは無効値)
	 */
	public void setRegisterDate(Date registerDate) {
		RegisterDate = new Date(registerDate.getTime());
	}

	/**
	 * 更新日を取得する
	 *
	 * @return 更新日(nullは無効値)
	 */
	@JsonIgnore
	public Date getUpdateDate() {
		return UpdateDate;
	}

	/**
	 * 更新日を取得する
	 *
	 * @return 更新日(nullは無効値)
	 */
	@JsonProperty("UpdateDateTime")
	public String getUpdateDateStr() {
		if (getRegisterDate() == null) return null;
		// SimpleDateFormatクラスでフォーマットパターンを設定する
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss");
		return sdf.format(getUpdateDate());
	}

	/**
	 * 更新日を設定する
	 *
	 * @param 更新日(nullは無効値)
	 */
	public void setUpdateDate(Date updateDate) {
		UpdateDate = new Date(updateDate.getTime());
	}

	/**
	 * ユーザーIDを取得する
	 *
	 * @return ユーザーID(nullは無効値)
	 */
	public String getUserID() {
		return UserID;
	}

	/**
	 * ユーザーIDを設定する
	 *
	 * @param ユーザーID(nullは無効値)
	 */
	public void setUserID(String userID) {
		UserID = userID;
	}

	/**
	 * パスワードを取得する
	 *
	 * @return パスワード(nullは無効値)
	 */
	public String getPassword() {
		return Password;
	}

	/**
	 * パスワードを設定する
	 *
	 * @param パスワード(nullは無効値)
	 */
	public void setPassword(String password) {
		Password = password;
	}

	/**
	 * コメントを取得する
	 *
	 * @return コメント(nullは無効値)
	 */
	public String getComment() {
		return Comment;
	}

	/**
	 * コメントを設定する
	 *
	 * @param コメント(nullは無効値)
	 */
	public void setComment(String comment) {
		Comment = comment;
	}

	/**
	 * メールアドレスを取得する
	 *
	 * @return メールアドレス(nullは無効値)
	 */
	public String getMailAddress() {
		return MailAddress;
	}

	/**
	 * メールアドレスを設定する
	 *
	 * @param getMailAddress
	 */
	public void setMailAddress(String mailAddress) {
		MailAddress = mailAddress;
	}
}
