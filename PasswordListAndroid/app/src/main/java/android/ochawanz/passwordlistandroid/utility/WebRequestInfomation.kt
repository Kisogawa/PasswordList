package android.ochawanz.passwordlistandroid.utility


/**
 * Webリクエストの状況詳細情報
 */
class WebRequestInfomation {
	/**
	 * 接続先(IPアドレスorドメイン)
	 */
	var hostName = ""
		get() = synchronized(this) {
			return field
		}
		set(value) {
			synchronized(this) {
				field = value
			}
		}

	/**
	 * 接続先ポート番号
	 */
	var portNo = 0
		get() = synchronized(this) {
			return field
		}
		set(value) {
			synchronized(this) {
				field = value
			}
		}
	/**
	 * 接続先パス
	 */
	var path = ""
		get() = synchronized(this) {
			return field
		}
		set(value) {
			synchronized(this) {
				field = value
			}
		}

	/**
	 * 接続状況テキスト(接続中等)
	 */
	var status = ""
		get() = synchronized(this) {
			return field
		}
		set(value) {
			synchronized(this) {
				field = value
			}
		}

	/**
	 * ダウンロード時のファイルサイズ(Byte)
	 */
	var totalFileSize: Int? = null
		get() = synchronized(this) {
			return field
		}
		set(value) {
			synchronized(this) {
				field = value
			}
		}

	/**
	 * ダウンロードの進捗(Byte)
	 */
	var fileSize = 0
		get() = synchronized(this) {
			return field
		}
		set(value) {
			synchronized(this) {
				field = value
			}
		}


	val downloadStateText: String
		get() {
			// 最大ファイルサイズが分かっている場合
			if (totalFileSize != null) {
				return String.format("%1$,3d/%2$,3d[Byte]", fileSize, totalFileSize)
			} else {
				return String.format("%1$,3d[Byte]", fileSize)

			}
		}


	// 変数の初期化
	fun init() {
		hostName = ""
		portNo = 0
		path = ""
		status = ""
		totalFileSize = null
		fileSize = 0
	}
}