package android.ochawanz.passwordlistandroid

import android.ochawanz.passwordlistandroid.db.ServerInfoDBHelper
import android.ochawanz.passwordlistandroid.interfaces.IConnectionInfo
import android.ochawanz.passwordlistandroid.utility.ReturnObject

/**
 * 接続先情報(編集用)
 * @param id32 ID(空文字で指定無し)(空文字or32文字)
 * @param name 接続先名称
 * @param address 接続先アドレス
 * @param portNo 接続先ポート番号
 * @param key64 暗号キー
 */
class ConnectionServerInfoItem(id32: String, name: String, address: String = "", portNo: Int = 0, key64: String) {

	companion object {
		/**
		 * 新たなインスタンスを生成する(例外は返さない)
		 * @param id32 ID(空文字で指定無し)(空文字or32文字)
		 * @param name 接続先名称
		 * @param address 接続先アドレス
		 * @param portNo 接続先ポート番号
		 * @param key64 暗号キー
		 * @param forgetempty trueなら空文字でエラー無し
		 */
		fun createInstance(id32: String, name: String, address: String = "", portNo: Int = 0, key64: String, forgetempty: Boolean): ReturnObject<ConnectionServerInfoItem?> {
			// 各変数のエラーチェック
			val ret_id32 = checkID(id32, forgetempty)
			if (!ret_id32.state) return ReturnObject(null, ret_id32)

			val ret_name = checkName(name)
			if (!ret_name.state) return ReturnObject(null, ret_name)

			val ret_address = checkAddress(address)
			if (!ret_address.state) return ReturnObject(null, ret_address)

			val ret_portNo = checkPortNo(portNo)
			if (!ret_portNo.state) return ReturnObject(null, ret_portNo)

			val ret_key64 = checkKey64(key64)
			if (!ret_key64.state) return ReturnObject(null, ret_key64)

			// エラーがなければオブジェクトの生成
			return ReturnObject(ConnectionServerInfoItem(id32, name, address, portNo, key64))
		}

		/**
		 * 識別ID 正当性チェック
		 * @param id 識別ID
		 * @param forgetempty trueなら空文字でエラー無し
		 */
		private fun checkID(id: String, forgetempty: Boolean): ReturnObject<Unit> {
			if (!forgetempty && id.isEmpty()) return ReturnObject(Unit, "IDの入力は必須です")
			if (id.isNotEmpty() && id.length != 32) return ReturnObject(Unit, "IDの長さは32文字である必要があります")
			return ReturnObject(Unit)
		}

		/**
		 * 接続先名称 正当性チェック
		 * @param name 接続先名称
		 */
		private fun checkName(name: String): ReturnObject<Unit> {
			if (name.length > ServerInfoDBHelper.COLUMN_LENGTH_NAME) return ReturnObject(Unit, "接続先名称は" + ServerInfoDBHelper.COLUMN_LENGTH_NAME + "文字以下である必要があります")
			return ReturnObject(Unit)
		}

		/**
		 * 接続先アドレス 正当性チェック
		 * @param address 接続先アドレス
		 */
		private fun checkAddress(address: String): ReturnObject<Unit> {
			if (address.isEmpty()) return ReturnObject(Unit, "接続先アドレスの入力は必須です")
			if (address.length > ServerInfoDBHelper.COLUMN_LENGTH_ADDRESS) return ReturnObject(Unit, "接続先アドレスは" + ServerInfoDBHelper.COLUMN_LENGTH_ADDRESS + "文字以下である必要があります")
			return ReturnObject(Unit)
		}

		/**
		 * 接続先ポート番号 正当性チェック
		 * @param portNo 接続先ポート番号
		 */
		private fun checkPortNo(portNo: Int): ReturnObject<Unit> {
			if (portNo < 0 || 65535 < portNo) return ReturnObject(Unit, "ポート番号は0～65535の間を設定してください")
			return ReturnObject(Unit)
		}

		/**
		 * 暗号キー 正当性チェック
		 * @param key64 暗号キー
		 */
		private fun checkKey64(key64: String): ReturnObject<Unit> {
			if (key64.isEmpty()) return ReturnObject(Unit, "暗号キーの入力は必須です")
			if (key64.length > ServerInfoDBHelper.COLUMN_LENGTH_KEY64) return ReturnObject(Unit, "暗号キーは" + ServerInfoDBHelper.COLUMN_LENGTH_KEY64 + "文字以下である必要があります")
			return ReturnObject(Unit)
		}
	}


	/**
	 * 識別ID(32文字)
	 */
	var ID: String = ""
		set(value) {
			val ret = checkID(value, true)
			field = if (ret.state) value else throw IllegalArgumentException(ret.comment)
		}

	/**
	 * 接続先名称
	 */
	var Name = ""
		set(value) {
			val ret = checkName(value)
			field = if (ret.state) value else throw IllegalArgumentException(ret.comment)
		}

	/**
	 * 接続先アドレス
	 */
	var Address = ""
		set(value) {
			val ret = checkAddress(value)
			field = if (ret.state) value else throw IllegalArgumentException(ret.comment)
		}

	/**
	 * 接続先ポート番号
	 */
	var PortNo = 0
		set(value) {
			val ret = checkPortNo(value)
			field = if (ret.state) value else throw IllegalArgumentException(ret.comment)
		}

	/**
	 * 暗号キー
	 */
	var Key64 = ""
		set(value) {
			val ret = checkKey64(value)
			field = if (ret.state) value else throw IllegalArgumentException(ret.comment)
		}

	init {
		ID = id32
		Name = name
		Address = address
		PortNo = portNo
		Key64 = key64
	}

	/**
	 * すべての要素を書き換える
	 */
	fun updateAll(connectionServerInfoItem: ConnectionServerInfoItem) {
		ID = connectionServerInfoItem.ID
		Name = connectionServerInfoItem.Name
		Address = connectionServerInfoItem.Address
		PortNo = connectionServerInfoItem.PortNo
		Key64 = connectionServerInfoItem.Key64
	}

	/**
	 * 文字列化
	 */
	override fun toString() = if (Name.isEmpty()) Address + ":" + PortNo else Name
}