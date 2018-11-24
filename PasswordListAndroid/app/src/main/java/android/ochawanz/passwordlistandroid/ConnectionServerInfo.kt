package android.ochawanz.passwordlistandroid

import android.ochawanz.passwordlistandroid.activity.MainActivity
import android.ochawanz.passwordlistandroid.interfaces.IConnectionInfo

/**
 * 接続先毎情報Bean
 * @param mainActivity コンテキスト
 * @param connectionServerInfoItem 編集用接続先毎情報(ID以外は代入時にエラーチェック済)
 */
class ConnectionServerInfo(mainActivity: MainActivity, connectionServerInfoItem: ConnectionServerInfoItem) : IConnectionInfo {

	/**
	 * コンテキスト
	 */
	private val mMainActivity = mainActivity

	override val ID: String = if (connectionServerInfoItem.ID.isNotEmpty()) connectionServerInfoItem.ID else throw IllegalArgumentException("接続先IDが未入力です")
	override val Name = connectionServerInfoItem.Name

	/**
	 * 接続先アドレス
	 */
	val Address = connectionServerInfoItem.Address

	/**
	 * 接続先ポート番号
	 */
	val PortNo = connectionServerInfoItem.PortNo

	/**
	 * 暗号キー
	 */
	val Key64 = connectionServerInfoItem.Key64

	/**
	 * 接続情報(WebRequest書換)
	 */
	override val Request: WebRequest = android.ochawanz.passwordlistandroid.WebRequest(this)

	/**
	 * Key情報
	 */
	val KeyManager = android.ochawanz.passwordlistandroid.KeyManager(this)

	/**
	 * 文字列化
	 */
	override fun toString() = if (Name.isEmpty()) Address + ":" + PortNo else Name

	/**
	 * 編集用情報の出力
	 */
	fun toConnectionServerInfoItem() = ConnectionServerInfoItem(ID, Name, Address, PortNo, Key64)
}