package android.ochawanz.passwordlistandroid

import android.ochawanz.passwordlistandroid.utility.AES
import android.ochawanz.passwordlistandroid.utility.ReturnObject
import java.util.*
import android.ochawanz.passwordlistandroid.utility.AES.Companion.KeyLength


/**
 * 鍵情報を扱うクラス
 */
class KeyManager(connectionServerInfo: ConnectionServerInfo) {

	// ブロックサイズ(バイト)
	private val BLOCKSIZE = 16
	/**
	 * IVヘッダ部サイズ(1バイト毎に乱数が入る)(バイト)
	 */
	private val IVHEADSIZE = BLOCKSIZE * 2

	/**
	 * 接続先毎情報
	 */
	private val mConnectionServerInfo = connectionServerInfo

	/**
	 * キーの取得
	 */
	private val Key64: String
		get() {
			return mConnectionServerInfo.Key64
		}

	/**
	 * データの復号を行う
	 */
	fun Decode(src: String): ReturnObject<String> {
		// キー本体を取得する
		val key_bin = Key64.toByteArray(Charsets.UTF_8)

		// 配列に戻す
		val encoded_byte = Base64.getDecoder().decode(src) ?: ByteArray(0)
		// 32バイト以下ならIVが無いことになるので，エラー
		if (encoded_byte.size < IVHEADSIZE) return ReturnObject("", "復号に失敗しました．")

		// IV取得
		val iv_bin = ByteArray(BLOCKSIZE)
		for (i in 0 until iv_bin.size) iv_bin[i] = encoded_byte[i * 2]

		// IVを除いた部分を取得する
		val angouBin = encoded_byte.copyOfRange(IVHEADSIZE, encoded_byte.size)

		// 復号処理を行う
		val aes = AES(AES.Companion.KeyLength.AES256, String(key_bin), String(iv_bin))
		val baseText_bin = aes.decode(angouBin)

		// 文字列に戻す
		val srcText = String(baseText_bin.Value)

		return ReturnObject(srcText)
	}

	/**
	 * データの暗号化を行う
	 */
	fun Encode(src: String): ReturnObject<String> {
		// キー本体を取得する
		val key_bin = Key64.toByteArray(Charsets.UTF_8)

		// IV(初期ベクトル)を生成
		val iv_bin = Base64.getDecoder().decode(generateIV())

		// 文字列のバイナリを取得する
		val srcText_bin = src.toByteArray(Charsets.UTF_8)


		val aes = AES(KeyLength.AES256, String(key_bin), String(iv_bin))
		val enc = aes.encode(srcText_bin)

		if (!enc.state) return ReturnObject("", "暗号化に失敗しました．")

		// IVを記録場所を確保(1バイトおき32バイト分)
		val vi_byte = ByteArray(IVHEADSIZE)
		val random = Random()
		for (i in vi_byte.indices) {
			vi_byte[i] = random.nextInt(255).toByte()
		}
		// IV記録(1バイトおき)
		var i = 0
		while (i < vi_byte.size) {
			vi_byte[i] = iv_bin[i / 2]
			i += 2
		}

		// 一列の配列に戻す(IVを先頭に付加)
		val encoded_byte = ByteArray(vi_byte.size + enc.Value.size)
		System.arraycopy(vi_byte, 0, encoded_byte, 0, vi_byte.size)
		System.arraycopy(enc.Value, 0, encoded_byte, vi_byte.size, enc.Value.size)

		// BASE64エンコード
		val encoded_text = Base64.getEncoder().encodeToString(encoded_byte)
				?: return ReturnObject("", " 暗号化に失敗しました．")

		return ReturnObject(encoded_text)
	}

	/**
	 * 初期ベクトルの生成(16バイト)
	 *
	 * @return 生成した初期ベクトル（Base64エンコード済み）
	 */
	private fun generateIV(): String {
		// 128ビット(16バイト)の文字列を生成
		val random = Random()
		val strByte = ByteArray(BLOCKSIZE)
		for (i in strByte.indices) {
			strByte[i] = random.nextInt(127).toByte()
		}
		return Base64.getEncoder().encodeToString(strByte)
	}

}