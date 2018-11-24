package android.ochawanz.passwordlistandroid.interfaces

/**
 * 接続情報(読み取り専用)
 */
interface IConnectionInfo {
	/**
	 * 識別ID(32文字)
	 */
	val ID: String

	/**
	 * 接続先名称
	 */
	val Name: String

	/**
	 * 接続情報
	 */
	val Request: IRequest

	/**
	 * 文字列化
	 */
	override fun toString(): String
}