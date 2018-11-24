package exception;

/**
 * 致命的なエラー
 */
public class FatalException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	private Exception CauseException;

	/**
	 * 致命的エラーの生成
	 *
	 * @param message エラー理由
	 */
	public FatalException(String message) {
		super(message);
	}

	/**
	 * 致命的エラーの生成
	 *
	 * @param message        エラー理由
	 * @param causeException 元エラー
	 */
	public FatalException(String message, Exception causeException) {
		super(message);
		CauseException = causeException;
	}

	@Override
	public String toString() {
		return "[PasswordList]致命的エラー:" + getMessage();
	}

	@Override
	public void printStackTrace() {
		if (CauseException != null) CauseException.printStackTrace();
		super.printStackTrace();
	}
}
