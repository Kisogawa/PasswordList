package exception;

/**
 * 通常の例外
 */
public class NormalException extends Exception {

	private Exception CauseException;

	/**
	 * 致命的エラーの生成
	 *
	 * @param message エラー理由
	 */
	public NormalException(String message) {
		super(message);
	}

	/**
	 * 致命的エラーの生成
	 *
	 * @param message エラー理由
	 * @param causeException 元エラー
	 */
	public NormalException(String message, Exception causeException) {
		super(message);
		CauseException = causeException;
	}

	@Override
	public void printStackTrace() {
		if (CauseException != null) CauseException.printStackTrace();
		super.printStackTrace();
	}
}
