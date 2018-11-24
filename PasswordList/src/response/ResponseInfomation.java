package response;

/**
 * 応答データクラス
 */
public class ResponseInfomation {

	/**
	 * 成功時のデータ
	 *
	 * @return 成功時のデータ
	 */
	public static ResponseInfomation Success() {
		ResponseInfomation responseInfomation = new ResponseInfomation();
		responseInfomation.setResult("Success");
		return responseInfomation;
	}

	/**
	 * 結果情報
	 */
	private String result = "";

	/**
	 * コメント
	 */
	private String comment = "";

	/**
	 * 結果情報の取得
	 *
	 * @return 結果情報
	 */
	public String getResult() {
		return result;
	}

	/**
	 * 結果情報の設定
	 *
	 * @param 結果情報
	 */
	public void setResult(String result) {
		if (result == null) result = "";
		this.result = result;
	}

	/**
	 * 結果情報のコメント取得
	 *
	 * @return 結果情報のコメント
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * 結果情報のコメント設定
	 *
	 * @param 結果情報のコメント
	 */
	public void setComment(String comment) {
		if (comment == null) comment = "";
		this.comment = comment;
	}
}
