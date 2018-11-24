package android.ochawanz.passwordlistandroid.utility

class ReturnObject<T>(value: T) {
	/**
	 * 状態(true:成功,false:失敗)
	 */
	var state: Boolean = true
		private set(value) {
			field = value
		}

	/**
	 * コメント
	 */
	var comment: String = ""
		private set(value) {
			field = value
		}

	/**
	 * エラーオブジェクト
	 */
	var causeException: Exception? = null
		private set(value) {
			field = value
		}

	/**
	 *  戻り値
	 */
	val Value: T = value

	/**
	 * コメントを設定(関数は失敗判定)
	 */
	constructor(value: T, comment: String) : this(value) {
		state = false
		this.comment = comment
		for(item in Thread.currentThread().stackTrace) item.toString()
	}

	/**
	 * 例外を設定して初期化(メッセージは自動で入ります)(関数は失敗判定)
	 */
	constructor(value: T, causeException: Exception) : this(value) {
		state = false
		this.causeException = causeException
		comment = causeException.localizedMessage
		for(item in Thread.currentThread().stackTrace) item.toString()
	}

	/**
	 * 例外とコメントを設定して初期化(関数は失敗判定)
	 */
	constructor(value: T, causeException: Exception, comment: String) : this(value) {
		state = false
		this.causeException = causeException
		this.comment = comment
		for(item in Thread.currentThread().stackTrace) item.toString()
	}

	/**
	 * 戻り値を設定する
	 */
	constructor(value: T, returnObject: ReturnObject<*>) : this(value) {
		causeException = returnObject.causeException
		comment = returnObject.comment
		state = returnObject.state
		if (!state)
			for(item in Thread.currentThread().stackTrace) item.toString()
	}

	init {
		if (!state)
			for(item in Thread.currentThread().stackTrace) item.toString()
	}

	override fun toString(): String {
		// 成功している場合
		if (state) return Value.toString()

		// 失敗している場合はエラーメッセージを良い感じに出す
		var text = comment
		if (causeException != null) {
			text += "\n" + causeException?.javaClass?.name
		}
		return text
	}
}