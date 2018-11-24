package android.ochawanz.passwordlistandroid.utility

import android.ochawanz.passwordlistandroid.activity.MainActivity
import android.ochawanz.passwordlistandroid.interfaces.IConnectionInfo

/**
 * ローカル接続先
 */
class ConnectionLocalInfo(mainActivity: MainActivity) : IConnectionInfo {
	/**
	 * 接続情報
	 */
	override val Request = LocalRequest(mainActivity)

	/**
	 * 識別ID(32文字)
	 */
	override val ID = "00000000000000000000000000000000"

	/**
	 * 接続先名称
	 */
	override val Name = "本体"

	/**
	 * 文字列化
	 */
	override fun toString() = Name
}