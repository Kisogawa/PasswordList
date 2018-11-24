package android.ochawanz.passwordlistandroid.utility

import android.util.Log
import java.util.*
import kotlin.experimental.xor

/**
 * AES暗号化(CBCモード)
 * @param keyLength 暗号鍵の長さ
 * @param key 暗号鍵(1文字以上)
 * @param iv 初期ベクトル
 */
class AES(keyLength: KeyLength, key: String, iv: String) {
	companion object {
		/**
		 * 暗号キーの長さ(bit)
		 */
		enum class KeyLength {
			AES128,
			AES192,
			AES256
		}
	}

	/**
	 * 別バイト置き換え用データ
	 */
	private val Sbox = byteArrayOf(0x63.toByte(), 0x7c.toByte(), 0x77.toByte(), 0x7b.toByte(), 0xf2.toByte(), 0x6b.toByte(), 0x6f.toByte(), 0xc5.toByte(), 0x30.toByte(), 0x01.toByte(), 0x67.toByte(), 0x2b.toByte(), 0xfe.toByte(), 0xd7.toByte(), 0xab.toByte(), 0x76.toByte(), 0xca.toByte(), 0x82.toByte(), 0xc9.toByte(), 0x7d.toByte(), 0xfa.toByte(), 0x59.toByte(), 0x47.toByte(), 0xf0.toByte(), 0xad.toByte(), 0xd4.toByte(), 0xa2.toByte(), 0xaf.toByte(), 0x9c.toByte(), 0xa4.toByte(), 0x72.toByte(), 0xc0.toByte(), 0xb7.toByte(), 0xfd.toByte(), 0x93.toByte(), 0x26.toByte(), 0x36.toByte(), 0x3f.toByte(), 0xf7.toByte(), 0xcc.toByte(), 0x34.toByte(), 0xa5.toByte(), 0xe5.toByte(), 0xf1.toByte(), 0x71.toByte(), 0xd8.toByte(), 0x31.toByte(), 0x15.toByte(), 0x04.toByte(), 0xc7.toByte(), 0x23.toByte(), 0xc3.toByte(), 0x18.toByte(), 0x96.toByte(), 0x05.toByte(), 0x9a.toByte(), 0x07.toByte(), 0x12.toByte(), 0x80.toByte(), 0xe2.toByte(), 0xeb.toByte(), 0x27.toByte(), 0xb2.toByte(), 0x75.toByte(), 0x09.toByte(), 0x83.toByte(), 0x2c.toByte(), 0x1a.toByte(), 0x1b.toByte(), 0x6e.toByte(), 0x5a.toByte(), 0xa0.toByte(), 0x52.toByte(), 0x3b.toByte(), 0xd6.toByte(), 0xb3.toByte(), 0x29.toByte(), 0xe3.toByte(), 0x2f.toByte(), 0x84.toByte(), 0x53.toByte(), 0xd1.toByte(), 0x00.toByte(), 0xed.toByte(), 0x20.toByte(), 0xfc.toByte(), 0xb1.toByte(), 0x5b.toByte(), 0x6a.toByte(), 0xcb.toByte(), 0xbe.toByte(), 0x39.toByte(), 0x4a.toByte(), 0x4c.toByte(), 0x58.toByte(), 0xcf.toByte(), 0xd0.toByte(), 0xef.toByte(), 0xaa.toByte(), 0xfb.toByte(), 0x43.toByte(), 0x4d.toByte(), 0x33.toByte(), 0x85.toByte(), 0x45.toByte(), 0xf9.toByte(), 0x02.toByte(), 0x7f.toByte(), 0x50.toByte(), 0x3c.toByte(), 0x9f.toByte(), 0xa8.toByte(), 0x51.toByte(), 0xa3.toByte(), 0x40.toByte(), 0x8f.toByte(), 0x92.toByte(), 0x9d.toByte(), 0x38.toByte(), 0xf5.toByte(), 0xbc.toByte(), 0xb6.toByte(), 0xda.toByte(), 0x21.toByte(), 0x10.toByte(), 0xff.toByte(), 0xf3.toByte(), 0xd2.toByte(), 0xcd.toByte(), 0x0c.toByte(), 0x13.toByte(), 0xec.toByte(), 0x5f.toByte(), 0x97.toByte(), 0x44.toByte(), 0x17.toByte(), 0xc4.toByte(), 0xa7.toByte(), 0x7e.toByte(), 0x3d.toByte(), 0x64.toByte(), 0x5d.toByte(), 0x19.toByte(), 0x73.toByte(), 0x60.toByte(), 0x81.toByte(), 0x4f.toByte(), 0xdc.toByte(), 0x22.toByte(), 0x2a.toByte(), 0x90.toByte(), 0x88.toByte(), 0x46.toByte(), 0xee.toByte(), 0xb8.toByte(), 0x14.toByte(), 0xde.toByte(), 0x5e.toByte(), 0x0b.toByte(), 0xdb.toByte(), 0xe0.toByte(), 0x32.toByte(), 0x3a.toByte(), 0x0a.toByte(), 0x49.toByte(), 0x06.toByte(), 0x24.toByte(), 0x5c.toByte(), 0xc2.toByte(), 0xd3.toByte(), 0xac.toByte(), 0x62.toByte(), 0x91.toByte(), 0x95.toByte(), 0xe4.toByte(), 0x79.toByte(), 0xe7.toByte(), 0xc8.toByte(), 0x37.toByte(), 0x6d.toByte(), 0x8d.toByte(), 0xd5.toByte(), 0x4e.toByte(), 0xa9.toByte(), 0x6c.toByte(), 0x56.toByte(), 0xf4.toByte(), 0xea.toByte(), 0x65.toByte(), 0x7a.toByte(), 0xae.toByte(), 0x08.toByte(), 0xba.toByte(), 0x78.toByte(), 0x25.toByte(), 0x2e.toByte(), 0x1c.toByte(), 0xa6.toByte(), 0xb4.toByte(), 0xc6.toByte(), 0xe8.toByte(), 0xdd.toByte(), 0x74.toByte(), 0x1f.toByte(), 0x4b.toByte(), 0xbd.toByte(), 0x8b.toByte(), 0x8a.toByte(), 0x70.toByte(), 0x3e.toByte(), 0xb5.toByte(), 0x66.toByte(), 0x48.toByte(), 0x03.toByte(), 0xf6.toByte(), 0x0e.toByte(), 0x61.toByte(), 0x35.toByte(), 0x57.toByte(), 0xb9.toByte(), 0x86.toByte(), 0xc1.toByte(), 0x1d.toByte(), 0x9e.toByte(), 0xe1.toByte(), 0xf8.toByte(), 0x98.toByte(), 0x11.toByte(), 0x69.toByte(), 0xd9.toByte(), 0x8e.toByte(), 0x94.toByte(), 0x9b.toByte(), 0x1e.toByte(), 0x87.toByte(), 0xe9.toByte(), 0xce.toByte(), 0x55.toByte(), 0x28.toByte(), 0xdf.toByte(), 0x8c.toByte(), 0xa1.toByte(), 0x89.toByte(), 0x0d.toByte(), 0xbf.toByte(), 0xe6.toByte(), 0x42.toByte(), 0x68.toByte(), 0x41.toByte(), 0x99.toByte(), 0x2d.toByte(), 0x0f.toByte(), 0xb0.toByte(), 0x54.toByte(), 0xbb.toByte(), 0x16.toByte())

	/**
	 * 別バイト置き換え用データ(復号)
	 */
	private val invSbox = byteArrayOf(0x52.toByte(), 0x09.toByte(), 0x6a.toByte(), 0xd5.toByte(), 0x30.toByte(), 0x36.toByte(), 0xa5.toByte(), 0x38.toByte(), 0xbf.toByte(), 0x40.toByte(), 0xa3.toByte(), 0x9e.toByte(), 0x81.toByte(), 0xf3.toByte(), 0xd7.toByte(), 0xfb.toByte(), 0x7c.toByte(), 0xe3.toByte(), 0x39.toByte(), 0x82.toByte(), 0x9b.toByte(), 0x2f.toByte(), 0xff.toByte(), 0x87.toByte(), 0x34.toByte(), 0x8e.toByte(), 0x43.toByte(), 0x44.toByte(), 0xc4.toByte(), 0xde.toByte(), 0xe9.toByte(), 0xcb.toByte(), 0x54.toByte(), 0x7b.toByte(), 0x94.toByte(), 0x32.toByte(), 0xa6.toByte(), 0xc2.toByte(), 0x23.toByte(), 0x3d.toByte(), 0xee.toByte(), 0x4c.toByte(), 0x95.toByte(), 0x0b.toByte(), 0x42.toByte(), 0xfa.toByte(), 0xc3.toByte(), 0x4e.toByte(), 0x08.toByte(), 0x2e.toByte(), 0xa1.toByte(), 0x66.toByte(), 0x28.toByte(), 0xd9.toByte(), 0x24.toByte(), 0xb2.toByte(), 0x76.toByte(), 0x5b.toByte(), 0xa2.toByte(), 0x49.toByte(), 0x6d.toByte(), 0x8b.toByte(), 0xd1.toByte(), 0x25.toByte(), 0x72.toByte(), 0xf8.toByte(), 0xf6.toByte(), 0x64.toByte(), 0x86.toByte(), 0x68.toByte(), 0x98.toByte(), 0x16.toByte(), 0xd4.toByte(), 0xa4.toByte(), 0x5c.toByte(), 0xcc.toByte(), 0x5d.toByte(), 0x65.toByte(), 0xb6.toByte(), 0x92.toByte(), 0x6c.toByte(), 0x70.toByte(), 0x48.toByte(), 0x50.toByte(), 0xfd.toByte(), 0xed.toByte(), 0xb9.toByte(), 0xda.toByte(), 0x5e.toByte(), 0x15.toByte(), 0x46.toByte(), 0x57.toByte(), 0xa7.toByte(), 0x8d.toByte(), 0x9d.toByte(), 0x84.toByte(), 0x90.toByte(), 0xd8.toByte(), 0xab.toByte(), 0x00.toByte(), 0x8c.toByte(), 0xbc.toByte(), 0xd3.toByte(), 0x0a.toByte(), 0xf7.toByte(), 0xe4.toByte(), 0x58.toByte(), 0x05.toByte(), 0xb8.toByte(), 0xb3.toByte(), 0x45.toByte(), 0x06.toByte(), 0xd0.toByte(), 0x2c.toByte(), 0x1e.toByte(), 0x8f.toByte(), 0xca.toByte(), 0x3f.toByte(), 0x0f.toByte(), 0x02.toByte(), 0xc1.toByte(), 0xaf.toByte(), 0xbd.toByte(), 0x03.toByte(), 0x01.toByte(), 0x13.toByte(), 0x8a.toByte(), 0x6b.toByte(), 0x3a.toByte(), 0x91.toByte(), 0x11.toByte(), 0x41.toByte(), 0x4f.toByte(), 0x67.toByte(), 0xdc.toByte(), 0xea.toByte(), 0x97.toByte(), 0xf2.toByte(), 0xcf.toByte(), 0xce.toByte(), 0xf0.toByte(), 0xb4.toByte(), 0xe6.toByte(), 0x73.toByte(), 0x96.toByte(), 0xac.toByte(), 0x74.toByte(), 0x22.toByte(), 0xe7.toByte(), 0xad.toByte(), 0x35.toByte(), 0x85.toByte(), 0xe2.toByte(), 0xf9.toByte(), 0x37.toByte(), 0xe8.toByte(), 0x1c.toByte(), 0x75.toByte(), 0xdf.toByte(), 0x6e.toByte(), 0x47.toByte(), 0xf1.toByte(), 0x1a.toByte(), 0x71.toByte(), 0x1d.toByte(), 0x29.toByte(), 0xc5.toByte(), 0x89.toByte(), 0x6f.toByte(), 0xb7.toByte(), 0x62.toByte(), 0x0e.toByte(), 0xaa.toByte(), 0x18.toByte(), 0xbe.toByte(), 0x1b.toByte(), 0xfc.toByte(), 0x56.toByte(), 0x3e.toByte(), 0x4b.toByte(), 0xc6.toByte(), 0xd2.toByte(), 0x79.toByte(), 0x20.toByte(), 0x9a.toByte(), 0xdb.toByte(), 0xc0.toByte(), 0xfe.toByte(), 0x78.toByte(), 0xcd.toByte(), 0x5a.toByte(), 0xf4.toByte(), 0x1f.toByte(), 0xdd.toByte(), 0xa8.toByte(), 0x33.toByte(), 0x88.toByte(), 0x07.toByte(), 0xc7.toByte(), 0x31.toByte(), 0xb1.toByte(), 0x12.toByte(), 0x10.toByte(), 0x59.toByte(), 0x27.toByte(), 0x80.toByte(), 0xec.toByte(), 0x5f.toByte(), 0x60.toByte(), 0x51.toByte(), 0x7f.toByte(), 0xa9.toByte(), 0x19.toByte(), 0xb5.toByte(), 0x4a.toByte(), 0x0d.toByte(), 0x2d.toByte(), 0xe5.toByte(), 0x7a.toByte(), 0x9f.toByte(), 0x93.toByte(), 0xc9.toByte(), 0x9c.toByte(), 0xef.toByte(), 0xa0.toByte(), 0xe0.toByte(), 0x3b.toByte(), 0x4d.toByte(), 0xae.toByte(), 0x2a.toByte(), 0xf5.toByte(), 0xb0.toByte(), 0xc8.toByte(), 0xeb.toByte(), 0xbb.toByte(), 0x3c.toByte(), 0x83.toByte(), 0x53.toByte(), 0x99.toByte(), 0x61.toByte(), 0x17.toByte(), 0x2b.toByte(), 0x04.toByte(), 0x7e.toByte(), 0xba.toByte(), 0x77.toByte(), 0xd6.toByte(), 0x26.toByte(), 0xe1.toByte(), 0x69.toByte(), 0x14.toByte(), 0x63.toByte(), 0x55.toByte(), 0x21.toByte(), 0x0c.toByte(), 0x7d.toByte())


	/**
	 * 鍵の長さ(enum定義)
	 */
	private val mKeyLengthEnum = keyLength

	/**
	 * 鍵の長さ(ビット長)
	 */
	private val mKeyLengthBit = when (mKeyLengthEnum) {
		KeyLength.AES128 -> 128
		KeyLength.AES192 -> 192
		KeyLength.AES256 -> 256
//		else -> throw  ExceptionInInitializerError("不明な鍵長です")
	}

	/**
	 * 暗号キーの長さ(byte)
	 */
	private val mKeyLengthByte: Int = mKeyLengthBit / 8

	/**
	 * 暗号化ブロックサイズ(byte)
	 */
	private val NBb = 16

	/**
	 * ラウンド数(鍵長128bit→10,192bit→12,256bit→14)
	 */
	private val mRoundNum: Int = mKeyLengthBit / 32 + 6

	/**
	 * 暗号鍵
	 */
	private val mKey: ByteArray

	/**
	 * ラウンド鍵(暗号インスタンスで共有)
	 */
	private val mRoundKey: ByteArray

	/**
	 * 初期ベクトル
	 */
	private val mIV: ByteArray

	// 初期化処理
	init {
		// 暗号キー生成
		// 鍵文字列をバイト配列に変換する
		val keys = key.toByteArray(Charsets.UTF_8)
		// 長さ0はエラー
		if (keys.isEmpty()) throw ExceptionInInitializerError("暗号鍵は1文字以上入力してください")
		// 鍵データを作成する
		val keydata = ByteArray(mKeyLengthByte) { keys[it % keys.size] }
		// 残りの鍵情報で排他的論理和を計算する
		for (i in mKeyLengthByte until keys.size) {
			keydata[i % mKeyLengthByte] = (keydata[i % mKeyLengthByte] xor keys[i])
		}
		mKey = keydata
		// ラウンド鍵生成
		val ret = KeyExpansion(keydata)
		if (!ret.state) throw ExceptionInInitializerError("鍵の生成に失敗[" + ret.toString() + "]")
		mRoundKey = ret.Value

		// 初期ベクトル生成
		// 鍵文字列をバイト配列に変換する
		val ivs = if (iv.isEmpty()) byteArrayOf(0) else iv.toByteArray(Charsets.UTF_8)
		// 初期ベクトルデータを作成する
		val ivdata = ByteArray(NBb) { ivs[it % ivs.size] }
		// 残りの初期ベクトル情報で排他的論理和を計算する
		for (i in NBb until ivs.size) {
			ivdata[i % NBb] = (ivdata[i % NBb] xor ivs[i])
		}
		mIV = ivdata

		// キーとIVのダンプデータ出力
//		datadump("KEY:       ", mKey)
//		datadump("IV :       ", mIV)
	}


	fun test(data: String) {
		val d = Base64.getEncoder().encodeToString(data.toByteArray(Charsets.UTF_8))


		println("PLAINTEXT: " + d)
		datadump("KEY:       ", mKey)
		datadump("IV:        ", mIV)
		datadump("RoundKey:  ", mRoundKey)
		datadump("Sbox:      ", Sbox)
		datadump("invSbox:   ", invSbox)

		val encData = encodeBASE64(d)
		println("暗号化:    " + encData)
		val decData = decodeBASE64(encData.Value)
		println("復号化:    " + decData)

		println("暗号化テスト終了")
	}

	/**
	 * 暗号化を行う
	 * @param inDataBASE64 暗号化を行うデータ(BASE64エンコード済み)
	 */
	fun encodeBASE64(inDataBASE64: String): ReturnObject<String> {
		// 入力データをバイト化(BASE64デコード)
		val inDataByte = Base64.getDecoder().decode(inDataBASE64)
				?: return ReturnObject("", "暗号化に失敗")
		val ret = encode(inDataByte)
		if (!ret.state) return ReturnObject("", ret)
		// BASE64デコードを行い，処理終了
		return ReturnObject(Base64.getEncoder().encodeToString(ret.Value))
	}

	/**
	 * 暗号化を行う
	 * @param inData 暗号化を行うデータ
	 */
	fun encode(inData: ByteArray): ReturnObject<ByteArray> {
		// 入力データをブロックサイズの倍数にパディングし分割する
		val dataPading = paddingByteWithSplit(inData, NBb)
		// 初期ベクトルをベクトルに設定
		var vector = mIV
		// 暗号化を行う
		for (d in dataPading) {
			// ベクトル演算(CBC)
			xorBlock(d, vector)
			// ブロックの暗号化処理
			Cipher(d)
			// 暗号化結果を次回に使用するベクトルとして記録
			vector = d
		}
		// 結果データを連結する
		val encodedData = ByteArray(dataPading.size * NBb) { dataPading[it / NBb][it % NBb] }
		// BASE64デコードを行い，処理終了
		return ReturnObject(encodedData)
	}

	/**
	 * 復号を行う
	 * @param inDataBASE64 復号を行うデータ(BASE64エンコード済み)
	 */
	fun decodeBASE64(inDataBASE64: String): ReturnObject<String> {
		// 入力データをバイト化(BASE64デコード)
		val inDataByte = Base64.getDecoder().decode(inDataBASE64)
				?: return ReturnObject("", "復号に失敗")
		val ret = decode(inDataByte)
		if (!ret.state) return ReturnObject("", ret)
		// BASE64デコードを行い，処理終了
		return ReturnObject(Base64.getEncoder().encodeToString(ret.Value))
	}

	/**
	 * 復号を行う
	 * @param inData 復号を行うデータ
	 */
	fun decode(inData: ByteArray): ReturnObject<ByteArray> {
		// 入力データをブロックサイズに分割する
		val dataSplit = split(inData, NBb) ?: return ReturnObject(byteArrayOf(), "復号に失敗")
		// 初期ベクトルをベクトルに設定
		var vector = mIV
		// 復号を行う
		for (d in dataSplit) {
			// 今回使用するベクトル
			val v = ByteArray(d.size) { d[it] }
			// ブロックの復号処理
			invCipher(d)
			// ベクトル演算(CBC)
			xorBlock(d, vector)
			// 暗号化結果を次回に使用するベクトルとして記録
			vector = v
		}
		// 結果データを連結し，パディング文字を削除する
		return ReturnObject(invPaddingByteWithSplit(dataSplit)
				?: return ReturnObject(byteArrayOf(), "復号に失敗"))
	}

	/**
	 * パディングを行い，パディングサイズ毎にデータを分割する
	 * @param byteArray パディングするデータ
	 * @return パディングされたデータ
	 */
	private fun paddingByteWithSplit(byteArray: ByteArray, maxPaddingSize: Int): List<ByteArray> {
		if (maxPaddingSize < 1 || 256 <= maxPaddingSize) throw IllegalArgumentException("パディングサイズは1～255の間である必要があります")
		// パディング文字数
		val padCount = maxPaddingSize - (byteArray.size % maxPaddingSize)
		// パディング文字(パディング文字数のバイト変換後)
		val padCountByte = (padCount and 0xff).toByte()
		// データ分割数を計算する
		val splitNum = (byteArray.size + padCount) / maxPaddingSize

		// 戻り値データを用意する
		return List(splitNum) { listIndex ->
			ByteArray(maxPaddingSize) { byteArrayIndex ->
				// データ読み出し位置
				val pos = listIndex * maxPaddingSize + byteArrayIndex
				return@ByteArray byteArray.getOrNull(pos) ?: padCountByte
			}
		}
	}

	/**
	 * データの結合を行い，パディング文字を削除する(分割データはすべて同じ長さの必要あり)
	 * @param byteArray パディングされたデータ
	 * @return パディングされる前のデータ
	 */
	private fun invPaddingByteWithSplit(byteArray: List<ByteArray>): ByteArray? {
		if (byteArray.isEmpty()) return null
		// 一番最後の値を取得する
		val paddingByteArray = byteArray.last()
		if (paddingByteArray.isEmpty()) return null
		val paddingByte = paddingByteArray.last()
		// リストの長さがすべて同一か確認する
		for (ba in byteArray) {
			if (ba.size != byteArray[0].size) return null
		}

		// パディング文字数(int変換後)
		val padCount = paddingByte.toInt() and 0xff
		// 文字の長さがパディング文字数以下ならエラー
		if (byteArray.size * byteArray[0].size < padCount) return null
		var count = padCount
		// パディングにより除去されるデータがパディング文字かどうか確認を行う
		for (i in byteArray.size - 1 downTo 0) {
			for (j in byteArray[i].size - 1 downTo 0) {
				if (count <= 0) break
				if (byteArray[i][j] != paddingByte) return null
				count--
			}
		}

		// 結合後の配列サイズ
		val size = byteArray.size * byteArray[0].size - padCount

		// 結合を行う(パディング分は入らないはず)
		return ByteArray(size) {
			byteArray[it / byteArray[0].size][it % byteArray[0].size]
		}
	}

	/**
	 * データを分割する
	 * splitSizeで割り切れない場合はnullを返します
	 * @param byteArray 分割するデータ
	 * @param splitSize 分割サイズ
	 * @return 分割されたデータ
	 */
	private fun split(byteArray: ByteArray, splitSize: Int): List<ByteArray>? {
		// 割り切れるか計算
		if (byteArray.size % splitSize != 0) return null
		// データ分割数を計算する
		val splitNum = byteArray.size / splitSize

		// 戻り値データを用意する
		return List(splitNum) { listIndex ->
			ByteArray(splitSize) { byteArrayIndex ->
				// データ読み出し位置
				val pos = listIndex * splitSize + byteArrayIndex
				return@ByteArray byteArray.getOrNull(pos) ?: 0
			}
		}
	}

	/**
	 * 16進数でデータを表示する
	 * @param headString 先頭部に表示する文字列
	 * @param dumpData 表示するバイトデータ
	 */
	private fun datadump(headString: String, dumpData: ByteArray) {
		// 表示する文字列
		val dumpText = StringBuilder(headString)

		// 16進数データ作成
		for (data in dumpData) {
			val n = (data.toInt() and 0xff)
			dumpText.append(Integer.toHexString(n / 16))
			dumpText.append(Integer.toHexString(n % 16))
		}
		Log.d("PasswoedListAndroid", dumpText.toString())
	}

	/**
	 * xor演算する(ブロックサイズ限定)
	 * @param data 処理対象データ(書き換わります)
	 * @param vector xorデータ
	 */
	private fun xorBlock(data: ByteArray, vector: ByteArray): ReturnObject<Unit> {

		if (data.size != NBb) return ReturnObject(Unit, "処理対象データは" + NBb + "バイトである必要があります[入力:" + data.size + "]")
		if (data.size > vector.size) return ReturnObject(Unit, "xorデータは入力データより長い必要があります[入力データ:" + data.size + ",xorデータ:" + vector.size + "]")

		for (i in 0 until data.size) data[i] = (data[i] xor vector[i])

		return ReturnObject(Unit)
	}

	/**
	 * ブロックの暗号化を行う
	 * @param data 暗号化するデータ(サイズは16のみ)(中身が直接変更されます)
	 * @return 成否
	 */
	private fun Cipher(data: ByteArray): ReturnObject<Unit> {

		// サイズ確認
		if (data.size != 16) return ReturnObject(Unit, "暗号化するブロックのデータサイズは16バイトにする必要があります[入力:" + data.size + "byte]")

		var i = 0
		// ラウンドキー計算
		AddRoundKey(data, i++)

		// ラウンド処理
		while (i < mRoundNum) {
			SubBytes(data)
			ShiftRows(data)
			MixColumns(data)
			AddRoundKey(data, i++)

		}

		// 暗号化最終処理
		SubBytes(data)
		ShiftRows(data)
		AddRoundKey(data, i)

		return ReturnObject(Unit)
	}

	/**
	 * 復号を行う
	 * @param data 復号するデータ(サイズは16のみ)(中身が直接変更されます)
	 * @return 成否
	 */
	private fun invCipher(data: ByteArray): ReturnObject<Unit> {

		// サイズ確認
		if (data.size != 16) return ReturnObject(Unit, "復号するブロックのデータサイズは16バイトにする必要があります[入力:" + data.size + "byte]")

		// ラウンドキー計算
		AddRoundKey(data, mRoundNum)

		var i: Int = mRoundNum - 1
		while (i > 0) {
			invShiftRows(data)
			invSubBytes(data)
			AddRoundKey(data, i)
			invMixColumns(data)
			i--
		}

		invShiftRows(data)
		invSubBytes(data)
		AddRoundKey(data, 0)

		return ReturnObject(Unit)
	}

	/**
	 * 入力バイトごとに対応表に基いて別のバイトに変換する
	 * @param data 暗号化するデータ(サイズは16のみ)(中身が直接変更されます)
	 * @return 成否
	 */
	private fun SubBytes(data: ByteArray): ReturnObject<Unit> {
		// サイズ確認
		if (data.size != 16) return ReturnObject(Unit, "暗号化するブロックのデータサイズは16バイトにする必要があります[入力:" + data.size + "byte]")
		for (i in 0 until NBb) {
			data[i] = Sbox[data[i].toInt() and 0xff]
		}
		return ReturnObject(Unit)
	}

	/**
	 * 入力バイトごとに対応表に基いて別のバイトに変換する(復号)
	 * @param data 復号するデータ(サイズは16のみ)(中身が直接変更されます)
	 * @return 成否
	 */
	private fun invSubBytes(data: ByteArray): ReturnObject<Unit> {
		// サイズ確認
		if (data.size != 16) return ReturnObject(Unit, "復号するブロックのデータサイズは16バイトにする必要があります[入力:" + data.size + "byte]")
		for (i in 0 until NBb) {
			data[i] = invSbox[data[i].toInt() and 0xff]
		}
		return ReturnObject(Unit)
	}

	/**
	 * 4バイト単位でまとめた行を左に規則的にシフトして混ぜこぜにする
	 * @param data 暗号化するデータ(サイズは16のみ)(中身が直接変更されます)
	 * @return 成否
	 */
	private fun ShiftRows(data: ByteArray): ReturnObject<Unit> {
		// サイズ確認
		if (data.size != 16) return ReturnObject(Unit, "暗号化するブロックのデータサイズは16バイトにする必要があります[入力:" + data.size + "byte]")
		// 配列の複製
		val cw = ByteArray(16) { data[it] }
		for (i in 0 until NBb step 4 * 4) {
			for (j in 1 until 4) {
				cw[i + j + 0 * 4] = data[i + j + (j + 0 and 3) * 4]
				cw[i + j + 1 * 4] = data[i + j + (j + 1 and 3) * 4]
				cw[i + j + 2 * 4] = data[i + j + (j + 2 and 3) * 4]
				cw[i + j + 3 * 4] = data[i + j + (j + 3 and 3) * 4]
			}
		}
		System.arraycopy(cw, 0, data, 0, NBb)
		return ReturnObject(Unit)
	}

	/**
	 * 4バイト単位でまとめた行を左に規則的にシフトして混ぜこぜにする(復号)
	 * @param data 復号するデータ(サイズは16のみ)(中身が直接変更されます)
	 * @return 成否
	 */
	private fun invShiftRows(data: ByteArray): ReturnObject<Unit> {
		// サイズ確認
		if (data.size != 16) return ReturnObject(Unit, "復号するブロックのデータサイズは16バイトにする必要があります[入力:" + data.size + "byte]")
		// 配列の複製
		val cw = ByteArray(16) { data[it] }
		for (i in 0 until NBb step 4 * 4) {
			for (j in 1 until 4) {
				cw[i + j + (j + 0 and 3) * 4] = data[i + j + 0 * 4]
				cw[i + j + (j + 1 and 3) * 4] = data[i + j + 1 * 4]
				cw[i + j + (j + 2 and 3) * 4] = data[i + j + 2 * 4]
				cw[i + j + (j + 3 and 3) * 4] = data[i + j + 3 * 4]
			}
		}
		System.arraycopy(cw, 0, data, 0, NBb)
		return ReturnObject(Unit)
	}

	private fun mul(dt: Int, n: Int): Int {
		var i: Int
		var x = 0
		i = 8
		while (i > 0) {
			x = x shl 1
			if (x and 0x100 != 0)
				x = x xor 0x1b and 0xff
			if (n and i != 0)
				x = x xor dt
			i = i shr 1
		}
		return x
	}

	/**
	 * 4バイトの値をビット演算を用いて別の4バイトに変換する
	 * @param data 暗号化するデータ(サイズは16のみ)(中身が直接変更されます)
	 * @return 成否
	 */
	private fun MixColumns(data: ByteArray): ReturnObject<Unit> {
		// サイズ確認
		if (data.size != 16) return ReturnObject(Unit, "暗号化するブロックのデータサイズは16バイトにする必要があります[入力:" + data.size + "byte]")
		val x = ByteArray(16)
		for (i in 0 until NBb step 4) {
			x[i + 0] = (mul(data[i + 0].toInt(), 2) xor
					mul(data[i + 1].toInt(), 3) xor
					mul(data[i + 2].toInt(), 1) xor
					mul(data[i + 3].toInt(), 1)).toByte()
			x[i + 1] = (mul(data[i + 1].toInt(), 2) xor
					mul(data[i + 2].toInt(), 3) xor
					mul(data[i + 3].toInt(), 1) xor
					mul(data[i + 0].toInt(), 1)).toByte()
			x[i + 2] = (mul(data[i + 2].toInt(), 2) xor
					mul(data[i + 3].toInt(), 3) xor
					mul(data[i + 0].toInt(), 1) xor
					mul(data[i + 1].toInt(), 1)).toByte()
			x[i + 3] = (mul(data[i + 3].toInt(), 2) xor
					mul(data[i + 0].toInt(), 3) xor
					mul(data[i + 1].toInt(), 1) xor
					mul(data[i + 2].toInt(), 1)).toByte()
		}
		System.arraycopy(x, 0, data, 0, NBb)
		return ReturnObject(Unit)
	}

	/**
	 * 4バイトの値をビット演算を用いて別の4バイトに変換する(復号)
	 * @param data 復号するデータ(サイズは16のみ)(中身が直接変更されます)
	 * @return 成否
	 */
	private fun invMixColumns(data: ByteArray): ReturnObject<Unit> {
		// サイズ確認
		if (data.size != 16) return ReturnObject(Unit, "復号するブロックのデータサイズは16バイトにする必要があります[入力:" + data.size + "byte]")
		val x = ByteArray(16)
		for (i in 0 until NBb step 4) {
			x[i + 0] = (mul(data[i + 0].toInt(), 14) xor
					mul(data[i + 1].toInt(), 11) xor
					mul(data[i + 2].toInt(), 13) xor
					mul(data[i + 3].toInt(), 9)).toByte()
			x[i + 1] = (mul(data[i + 1].toInt(), 14) xor
					mul(data[i + 2].toInt(), 11) xor
					mul(data[i + 3].toInt(), 13) xor
					mul(data[i + 0].toInt(), 9)).toByte()
			x[i + 2] = (mul(data[i + 2].toInt(), 14) xor
					mul(data[i + 3].toInt(), 11) xor
					mul(data[i + 0].toInt(), 13) xor
					mul(data[i + 1].toInt(), 9)).toByte()
			x[i + 3] = (mul(data[i + 3].toInt(), 14) xor
					mul(data[i + 0].toInt(), 11) xor
					mul(data[i + 1].toInt(), 13) xor
					mul(data[i + 2].toInt(), 9)).toByte()
		}
		System.arraycopy(x, 0, data, 0, NBb)
		return ReturnObject(Unit)
	}

	/**
	 * 指定した4バイトのデータの排他的論理和を計算する
	 * @param data 排他的論理和を行うデータ1(書き換わります)
	 * @param pos1 排他的論理和を行う先頭のバイト位置1
	 * @param w 排他的論理和を行うデータ2
	 * @param pos2 排他的論理和を行う先頭のバイト位置2
	 */
	private fun Exor(data: ByteArray, pos1: Int, w: ByteArray, pos2: Int) {
		data[pos1 + 0] = (data[pos1 + 0] xor w[pos2 + 0])
		data[pos1 + 1] = (data[pos1 + 1] xor w[pos2 + 1])
		data[pos1 + 2] = (data[pos1 + 2] xor w[pos2 + 2])
		data[pos1 + 3] = (data[pos1 + 3] xor w[pos2 + 3])
	}

	/**
	 * MixColumns の出力 (4バイト) とラウンド鍵 (4バイト) との XOR を取る
	 * @param data 暗号化するデータ(これ自体が書き換わります)
	 * @param n 不明
	 */
	private fun AddRoundKey(data: ByteArray, n: Int) {
		for (i in 0 until NBb step 4) {
			Exor(data, i, mRoundKey, i + NBb * n)// data[i] ^= mRoundKey[i+NB*n];
		}
	}

	private fun SubWord(`in`: ByteArray): ByteArray {
		`in`[0] = Sbox[`in`[0].toInt() and 0xff]
		`in`[1] = Sbox[`in`[1].toInt() and 0xff]
		`in`[2] = Sbox[`in`[2].toInt() and 0xff]
		`in`[3] = Sbox[`in`[3].toInt() and 0xff]
		return `in`
	}

	private fun RotWord(`in`: ByteArray): ByteArray {
		val x = `in`[0]
		`in`[0] = `in`[1]
		`in`[1] = `in`[2]
		`in`[2] = `in`[3]
		`in`[3] = x
		return `in`
	}

	private fun Exor(w: ByteArray, pos1: Int, pos2: Int, temp: ByteArray) {
		w[pos1 + 0] = (w[pos2 + 0] xor temp[0])
		w[pos1 + 1] = (w[pos2 + 1] xor temp[1])
		w[pos1 + 2] = (w[pos2 + 2] xor temp[2])
		w[pos1 + 3] = (w[pos2 + 3] xor temp[3])
		return
	}

	/**
	 * ラウンド鍵の準備
	 * @param key 元となるキー
	 * @return ラウンド鍵
	 */
	private fun KeyExpansion(key: ByteArray): ReturnObject<ByteArray> {

		if (key.size != mKeyLengthByte) return ReturnObject(byteArrayOf(), "キーの長さは" + mKeyLengthByte + "にする必要があります．[入力:" + key.size + "]")

		val Rcon = byteArrayOf(0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80.toByte(), 0x1b, 0x36)
		var temp = ByteArray(4)

		val roundKey = ByteArray(NBb * (mRoundNum + 1))
		System.arraycopy(key, 0, roundKey, 0, mKeyLengthByte)
		for (i in mKeyLengthByte until roundKey.size step 4) {
			System.arraycopy(roundKey, i - 4, temp, 0, 4)// temp = mRoundKey[i-1];
			if (i % mKeyLengthByte == 0) {
				temp = SubWord(RotWord(temp))
				temp[0] = temp[0] xor Rcon[i / mKeyLengthByte - 1]
			} else if (mKeyLengthByte > 6 * 4 && i % mKeyLengthByte == 4 * 4)
				temp = SubWord(temp)
			Exor(roundKey, i, i - mKeyLengthByte, temp)// mRoundKey[i] = mRoundKey[i-mKeyLengthByte] ^ temp;
		}
		return ReturnObject(roundKey)
	}
}
