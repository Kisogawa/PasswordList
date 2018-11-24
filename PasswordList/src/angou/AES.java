package angou;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import exception.FatalException;

/**
 * AES暗号化(CBCモード)
 *
 * @param keyLength 暗号鍵の長さ
 * @param key       暗号鍵(1文字以上)
 * @param iv        初期ベクトル
 */
class AES {

	/**
	 * 暗号キーの長さ(bit)
	 */
	enum KeyLength {
	AES128, AES192, AES256
	}

	/**
	 * 別バイト置き換え用データ
	 */
	final private byte[] Sbox = { (byte) 0x63, (byte) 0x7c, (byte) 0x77, (byte) 0x7b, (byte) 0xf2, (byte) 0x6b, (byte) 0x6f, (byte) 0xc5, (byte) 0x30, (byte) 0x01, (byte) 0x67, (byte) 0x2b, (byte) 0xfe, (byte) 0xd7, (byte) 0xab, (byte) 0x76, (byte) 0xca, (byte) 0x82, (byte) 0xc9, (byte) 0x7d, (byte) 0xfa, (byte) 0x59, (byte) 0x47, (byte) 0xf0, (byte) 0xad, (byte) 0xd4, (byte) 0xa2, (byte) 0xaf, (byte) 0x9c, (byte) 0xa4, (byte) 0x72, (byte) 0xc0, (byte) 0xb7, (byte) 0xfd, (byte) 0x93, (byte) 0x26,
			(byte) 0x36, (byte) 0x3f, (byte) 0xf7, (byte) 0xcc, (byte) 0x34, (byte) 0xa5, (byte) 0xe5, (byte) 0xf1, (byte) 0x71, (byte) 0xd8, (byte) 0x31, (byte) 0x15, (byte) 0x04, (byte) 0xc7, (byte) 0x23, (byte) 0xc3, (byte) 0x18, (byte) 0x96, (byte) 0x05, (byte) 0x9a, (byte) 0x07, (byte) 0x12, (byte) 0x80, (byte) 0xe2, (byte) 0xeb, (byte) 0x27, (byte) 0xb2, (byte) 0x75, (byte) 0x09, (byte) 0x83, (byte) 0x2c, (byte) 0x1a, (byte) 0x1b, (byte) 0x6e, (byte) 0x5a, (byte) 0xa0, (byte) 0x52, (byte) 0x3b,
			(byte) 0xd6, (byte) 0xb3, (byte) 0x29, (byte) 0xe3, (byte) 0x2f, (byte) 0x84, (byte) 0x53, (byte) 0xd1, (byte) 0x00, (byte) 0xed, (byte) 0x20, (byte) 0xfc, (byte) 0xb1, (byte) 0x5b, (byte) 0x6a, (byte) 0xcb, (byte) 0xbe, (byte) 0x39, (byte) 0x4a, (byte) 0x4c, (byte) 0x58, (byte) 0xcf, (byte) 0xd0, (byte) 0xef, (byte) 0xaa, (byte) 0xfb, (byte) 0x43, (byte) 0x4d, (byte) 0x33, (byte) 0x85, (byte) 0x45, (byte) 0xf9, (byte) 0x02, (byte) 0x7f, (byte) 0x50, (byte) 0x3c, (byte) 0x9f, (byte) 0xa8,
			(byte) 0x51, (byte) 0xa3, (byte) 0x40, (byte) 0x8f, (byte) 0x92, (byte) 0x9d, (byte) 0x38, (byte) 0xf5, (byte) 0xbc, (byte) 0xb6, (byte) 0xda, (byte) 0x21, (byte) 0x10, (byte) 0xff, (byte) 0xf3, (byte) 0xd2, (byte) 0xcd, (byte) 0x0c, (byte) 0x13, (byte) 0xec, (byte) 0x5f, (byte) 0x97, (byte) 0x44, (byte) 0x17, (byte) 0xc4, (byte) 0xa7, (byte) 0x7e, (byte) 0x3d, (byte) 0x64, (byte) 0x5d, (byte) 0x19, (byte) 0x73, (byte) 0x60, (byte) 0x81, (byte) 0x4f, (byte) 0xdc, (byte) 0x22, (byte) 0x2a,
			(byte) 0x90, (byte) 0x88, (byte) 0x46, (byte) 0xee, (byte) 0xb8, (byte) 0x14, (byte) 0xde, (byte) 0x5e, (byte) 0x0b, (byte) 0xdb, (byte) 0xe0, (byte) 0x32, (byte) 0x3a, (byte) 0x0a, (byte) 0x49, (byte) 0x06, (byte) 0x24, (byte) 0x5c, (byte) 0xc2, (byte) 0xd3, (byte) 0xac, (byte) 0x62, (byte) 0x91, (byte) 0x95, (byte) 0xe4, (byte) 0x79, (byte) 0xe7, (byte) 0xc8, (byte) 0x37, (byte) 0x6d, (byte) 0x8d, (byte) 0xd5, (byte) 0x4e, (byte) 0xa9, (byte) 0x6c, (byte) 0x56, (byte) 0xf4, (byte) 0xea,
			(byte) 0x65, (byte) 0x7a, (byte) 0xae, (byte) 0x08, (byte) 0xba, (byte) 0x78, (byte) 0x25, (byte) 0x2e, (byte) 0x1c, (byte) 0xa6, (byte) 0xb4, (byte) 0xc6, (byte) 0xe8, (byte) 0xdd, (byte) 0x74, (byte) 0x1f, (byte) 0x4b, (byte) 0xbd, (byte) 0x8b, (byte) 0x8a, (byte) 0x70, (byte) 0x3e, (byte) 0xb5, (byte) 0x66, (byte) 0x48, (byte) 0x03, (byte) 0xf6, (byte) 0x0e, (byte) 0x61, (byte) 0x35, (byte) 0x57, (byte) 0xb9, (byte) 0x86, (byte) 0xc1, (byte) 0x1d, (byte) 0x9e, (byte) 0xe1, (byte) 0xf8,
			(byte) 0x98, (byte) 0x11, (byte) 0x69, (byte) 0xd9, (byte) 0x8e, (byte) 0x94, (byte) 0x9b, (byte) 0x1e, (byte) 0x87, (byte) 0xe9, (byte) 0xce, (byte) 0x55, (byte) 0x28, (byte) 0xdf, (byte) 0x8c, (byte) 0xa1, (byte) 0x89, (byte) 0x0d, (byte) 0xbf, (byte) 0xe6, (byte) 0x42, (byte) 0x68, (byte) 0x41, (byte) 0x99, (byte) 0x2d, (byte) 0x0f, (byte) 0xb0, (byte) 0x54, (byte) 0xbb, (byte) 0x16 };

	/**
	 * 別バイト置き換え用データ(復号)
	 */
	final private byte[] invSbox = { (byte) 0x52, (byte) 0x09, (byte) 0x6a, (byte) 0xd5, (byte) 0x30, (byte) 0x36, (byte) 0xa5, (byte) 0x38, (byte) 0xbf, (byte) 0x40, (byte) 0xa3, (byte) 0x9e, (byte) 0x81, (byte) 0xf3, (byte) 0xd7, (byte) 0xfb, (byte) 0x7c, (byte) 0xe3, (byte) 0x39, (byte) 0x82, (byte) 0x9b, (byte) 0x2f, (byte) 0xff, (byte) 0x87, (byte) 0x34, (byte) 0x8e, (byte) 0x43, (byte) 0x44, (byte) 0xc4, (byte) 0xde, (byte) 0xe9, (byte) 0xcb, (byte) 0x54, (byte) 0x7b, (byte) 0x94, (byte) 0x32,
			(byte) 0xa6, (byte) 0xc2, (byte) 0x23, (byte) 0x3d, (byte) 0xee, (byte) 0x4c, (byte) 0x95, (byte) 0x0b, (byte) 0x42, (byte) 0xfa, (byte) 0xc3, (byte) 0x4e, (byte) 0x08, (byte) 0x2e, (byte) 0xa1, (byte) 0x66, (byte) 0x28, (byte) 0xd9, (byte) 0x24, (byte) 0xb2, (byte) 0x76, (byte) 0x5b, (byte) 0xa2, (byte) 0x49, (byte) 0x6d, (byte) 0x8b, (byte) 0xd1, (byte) 0x25, (byte) 0x72, (byte) 0xf8, (byte) 0xf6, (byte) 0x64, (byte) 0x86, (byte) 0x68, (byte) 0x98, (byte) 0x16, (byte) 0xd4, (byte) 0xa4,
			(byte) 0x5c, (byte) 0xcc, (byte) 0x5d, (byte) 0x65, (byte) 0xb6, (byte) 0x92, (byte) 0x6c, (byte) 0x70, (byte) 0x48, (byte) 0x50, (byte) 0xfd, (byte) 0xed, (byte) 0xb9, (byte) 0xda, (byte) 0x5e, (byte) 0x15, (byte) 0x46, (byte) 0x57, (byte) 0xa7, (byte) 0x8d, (byte) 0x9d, (byte) 0x84, (byte) 0x90, (byte) 0xd8, (byte) 0xab, (byte) 0x00, (byte) 0x8c, (byte) 0xbc, (byte) 0xd3, (byte) 0x0a, (byte) 0xf7, (byte) 0xe4, (byte) 0x58, (byte) 0x05, (byte) 0xb8, (byte) 0xb3, (byte) 0x45, (byte) 0x06,
			(byte) 0xd0, (byte) 0x2c, (byte) 0x1e, (byte) 0x8f, (byte) 0xca, (byte) 0x3f, (byte) 0x0f, (byte) 0x02, (byte) 0xc1, (byte) 0xaf, (byte) 0xbd, (byte) 0x03, (byte) 0x01, (byte) 0x13, (byte) 0x8a, (byte) 0x6b, (byte) 0x3a, (byte) 0x91, (byte) 0x11, (byte) 0x41, (byte) 0x4f, (byte) 0x67, (byte) 0xdc, (byte) 0xea, (byte) 0x97, (byte) 0xf2, (byte) 0xcf, (byte) 0xce, (byte) 0xf0, (byte) 0xb4, (byte) 0xe6, (byte) 0x73, (byte) 0x96, (byte) 0xac, (byte) 0x74, (byte) 0x22, (byte) 0xe7, (byte) 0xad,
			(byte) 0x35, (byte) 0x85, (byte) 0xe2, (byte) 0xf9, (byte) 0x37, (byte) 0xe8, (byte) 0x1c, (byte) 0x75, (byte) 0xdf, (byte) 0x6e, (byte) 0x47, (byte) 0xf1, (byte) 0x1a, (byte) 0x71, (byte) 0x1d, (byte) 0x29, (byte) 0xc5, (byte) 0x89, (byte) 0x6f, (byte) 0xb7, (byte) 0x62, (byte) 0x0e, (byte) 0xaa, (byte) 0x18, (byte) 0xbe, (byte) 0x1b, (byte) 0xfc, (byte) 0x56, (byte) 0x3e, (byte) 0x4b, (byte) 0xc6, (byte) 0xd2, (byte) 0x79, (byte) 0x20, (byte) 0x9a, (byte) 0xdb, (byte) 0xc0, (byte) 0xfe,
			(byte) 0x78, (byte) 0xcd, (byte) 0x5a, (byte) 0xf4, (byte) 0x1f, (byte) 0xdd, (byte) 0xa8, (byte) 0x33, (byte) 0x88, (byte) 0x07, (byte) 0xc7, (byte) 0x31, (byte) 0xb1, (byte) 0x12, (byte) 0x10, (byte) 0x59, (byte) 0x27, (byte) 0x80, (byte) 0xec, (byte) 0x5f, (byte) 0x60, (byte) 0x51, (byte) 0x7f, (byte) 0xa9, (byte) 0x19, (byte) 0xb5, (byte) 0x4a, (byte) 0x0d, (byte) 0x2d, (byte) 0xe5, (byte) 0x7a, (byte) 0x9f, (byte) 0x93, (byte) 0xc9, (byte) 0x9c, (byte) 0xef, (byte) 0xa0, (byte) 0xe0,
			(byte) 0x3b, (byte) 0x4d, (byte) 0xae, (byte) 0x2a, (byte) 0xf5, (byte) 0xb0, (byte) 0xc8, (byte) 0xeb, (byte) 0xbb, (byte) 0x3c, (byte) 0x83, (byte) 0x53, (byte) 0x99, (byte) 0x61, (byte) 0x17, (byte) 0x2b, (byte) 0x04, (byte) 0x7e, (byte) 0xba, (byte) 0x77, (byte) 0xd6, (byte) 0x26, (byte) 0xe1, (byte) 0x69, (byte) 0x14, (byte) 0x63, (byte) 0x55, (byte) 0x21, (byte) 0x0c, (byte) 0x7d };

	/**
	 * 鍵の長さ(enum定義)
	 */
	final private KeyLength mKeyLengthEnum;

	/**
	 * 鍵の長さ(ビット長)
	 */
	final private int mKeyLengthBit;

	/**
	 * 暗号キーの長さ(byte)
	 */
	final private int mKeyLengthByte;

	/**
	 * 暗号化ブロックサイズ(byte)
	 */
	final private int NBb = 16;

	/**
	 * ラウンド数(鍵長128bit→10,192bit→12,256bit→14)
	 */
	final private int mRoundNum;

	/**
	 * 暗号鍵
	 */
	final private byte[] mKey;

	/**
	 * ラウンド鍵(暗号インスタンスで共有)
	 */
	final private byte[] mRoundKey;

	/**
	 * 初期ベクトル
	 */
	final private byte[] mIV;

	/**
	 * コンストラクタ
	 *
	 * @param keyLength 暗号鍵の長さ
	 * @param key       暗号鍵(1文字以上)
	 * @param iv        初期ベクトル
	 */
	public AES(KeyLength keyLength, String key, String iv) throws FatalException {
		mKeyLengthEnum = keyLength;

		switch (keyLength) {
			case AES128:
				mKeyLengthBit = 128;
				break;
			case AES192:
				mKeyLengthBit = 192;
				break;
			case AES256:
				mKeyLengthBit = 256;
				break;
			default:
				throw new ExceptionInInitializerError("不明な鍵長です");
		}

		mKeyLengthByte = mKeyLengthBit / 8;

		mRoundNum = mKeyLengthBit / 32 + 6;

		// 暗号キー生成
		// 鍵文字列をバイト配列に変換する
		byte[] keys;
		try {
			keys = key.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new FatalException("文字コード指定エラー", e);
		}
		// 長さ0はエラー
		if (keys.length <= 0) throw new FatalException("暗号鍵は1文字以上入力してください");
		// 鍵データを作成する
		byte[] keydata = new byte[mKeyLengthByte];
		for (int i = 0; i < keydata.length; i++)
			keydata[i] = keys[i % keys.length];
		// 残りの鍵情報で排他的論理和を計算する
		for (int i = mKeyLengthByte; i < keys.length; i++) {
			keydata[i % mKeyLengthByte] = (byte) (keydata[i % mKeyLengthByte] ^ keys[i]);
		}
		mKey = keydata;
		// ラウンド鍵生成
		mRoundKey = KeyExpansion(keydata);

		// 初期ベクトル生成
		// 鍵文字列をバイト配列に変換する
		byte[] ivs;
		if (iv.isEmpty()) ivs = new byte[0];
		else try {
			ivs = iv.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new FatalException("文字コード指定エラー", e);
		}
		// 初期ベクトルデータを作成する
		byte[] ivdata = new byte[NBb];
		for (int i = 0; i < ivdata.length; i++)
			ivdata[i] = ivs[i % ivs.length];
		// 残りの初期ベクトル情報で排他的論理和を計算する
		for (int i = NBb; i < ivs.length; i++) {
			ivdata[i % NBb] = (byte) (ivdata[i % NBb] ^ ivs[i]);
		}
		mIV = ivdata;

		// キーとIVのダンプデータ出力
//		datadump("KEY:       ", mKey);
//		datadump("IV :       ", mIV);

	}

	public void test(String data2) {
		String data = null;
		try {
			data = Base64.getEncoder().encodeToString(data2.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}

		System.out.println("PLAINTEXT: " + data);
		datadump("KEY:       ", mKey);
		datadump("IV:        ", mIV);
		datadump("RoundKey:  ", mRoundKey);
		datadump("Sbox:      ", Sbox);
		datadump("invSbox:   ", invSbox);

		String encData;
		try {
			encData = encodeBASE64(data);
			System.out.println("暗号化:    " + encData);
			String decData = decodeBASE64(encData);
			System.out.println("復号化:    " + decData);

			System.out.println("暗号化テスト終了");
		} catch (FatalException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * 暗号化を行う
	 *
	 * @param inDataBASE64 暗号化を行うデータ(BASE64エンコード済み)
	 */
	public String encodeBASE64(String inDataBASE64) throws FatalException {
		byte[] inDataByte = null;
		try {
			// 入力データをバイト化(BASE64デコード)
			inDataByte = Base64.getDecoder().decode(inDataBASE64);
		} catch (IllegalArgumentException e) {
			throw new FatalException("暗号化に失敗");
		}
		byte[] value = encode(inDataByte);

		// BASE64デコードを行い，処理終了
		return Base64.getEncoder().encodeToString(value);
	}

	/**
	 * 暗号化を行う
	 *
	 * @param inData 暗号化を行うデータ
	 */
	public byte[] encode(byte[] inData) throws FatalException {
		// 入力データをブロックサイズの倍数にパディングし分割する
		List<byte[]> dataPading = paddingByteWithSplit(inData, NBb);
		// 初期ベクトルをベクトルに設定
		byte[] vector = mIV;
		// 暗号化を行う
		for (byte[] d : dataPading) {
			// ベクトル演算(CBC)
			xorBlock(d, vector);
			// ブロックの暗号化処理
			Cipher(d);
			// 暗号化結果を次回に使用するベクトルとして記録
			vector = d;
		}
		// 結果データを連結する
		byte[] encodedData = new byte[dataPading.size() * NBb];
		for (int i = 0; i < encodedData.length; i++) {
			encodedData[i] = dataPading.get(i / NBb)[i % NBb];
		}
		// BASE64デコードを行い，処理終了
		return encodedData;
	}

	/**
	 * 復号を行う
	 *
	 * @param inDataBASE64 復号を行うデータ(BASE64エンコード済み)
	 */
	public String decodeBASE64(String inDataBASE64) throws FatalException {
		byte[] inDataByte = null;
		try {
			// 入力データをバイト化(BASE64デコード)
			inDataByte = Base64.getDecoder().decode(inDataBASE64);
		} catch (IllegalArgumentException e) {
			throw new FatalException("復号に失敗");
		}
		byte[] value = decode(inDataByte);
		// BASE64デコードを行い，処理終了
		return Base64.getEncoder().encodeToString(value);
	}

	/**
	 * 復号を行う
	 *
	 * @param inData 復号を行うデータ
	 */
	public byte[] decode(byte[] inData) throws FatalException {
		// 入力データをブロックサイズに分割する
		List<byte[]> dataSplit = split(inData, NBb);
		if (dataSplit == null) throw new FatalException("復号に失敗");
		// 初期ベクトルをベクトルに設定
		byte[] vector = mIV;
		// 復号を行う
		for (byte[] d : dataSplit) {
			// 今回使用するベクトル
			byte[] v = d.clone();
			// ブロックの復号処理
			invCipher(d);
			// ベクトル演算(CBC)
			xorBlock(d, vector);
			// 暗号化結果を次回に使用するベクトルとして記録
			vector = v;
		}
		// 結果データを連結し，パディング文字を削除する
		byte[] ret = invPaddingByteWithSplit(dataSplit);
		if (ret == null) throw new FatalException("復号に失敗");

		return ret;
	}

	/**
	 * パディングを行い，パディングサイズ毎にデータを分割する
	 *
	 * @param byteArray パディングするデータ
	 * @return パディングされたデータ
	 */
	private List<byte[]> paddingByteWithSplit(byte[] byteArray, int maxPaddingSize) throws FatalException {
		if (maxPaddingSize < 1 || 256 <= maxPaddingSize) throw new FatalException("パディングサイズは1～255の間である必要があります");
		// パディング文字数
		int padCount = maxPaddingSize - (byteArray.length % maxPaddingSize);
		// パディング文字(パディング文字数のバイト変換後)
		byte padCountByte = (byte) (padCount & 0xff);
		// データ分割数を計算する
		int splitNum = (byteArray.length + padCount) / maxPaddingSize;

		// 戻り値データを用意する
		List<byte[]> ret = new ArrayList<byte[]>();
		for (int i = 0; i < splitNum; i++) {
			byte[] ret_b = new byte[maxPaddingSize];
			for (int j = 0; j < ret_b.length; j++) {
				// データ読み出し位置
				int pos = i * maxPaddingSize + j;
				if (pos < byteArray.length) ret_b[j] = byteArray[pos];
				else ret_b[j] = padCountByte;

			}
			ret.add(ret_b);
		}
		return ret;
	}

	/**
	 * データの結合を行い，パディング文字を削除する(分割データはすべて同じ長さの必要あり)
	 *
	 * @param byteArray パディングされたデータ
	 * @return パディングされる前のデータ
	 */
	private byte[] invPaddingByteWithSplit(List<byte[]> byteArray) {
		if (byteArray.isEmpty()) return null;
		// 一番最後の値を取得する
		byte[] paddingByteArray = byteArray.get(byteArray.size() - 1);
		if (paddingByteArray.length <= 0) return null;
		byte paddingByte = paddingByteArray[paddingByteArray.length - 1];
		// リストの長さがすべて同一か確認する
		for (byte[] ba : byteArray) {
			if (ba.length != byteArray.get(0).length) return null;
		}

		// パディング文字数(int変換後)
		int padCount = (int) paddingByte & 0xff;
		// 文字の長さがパディング文字数以下ならエラー
		if (byteArray.size() * byteArray.get(0).length < padCount) return null;
		// パディングにより除去されるデータがパディング文字かどうか確認を行う
		int count = padCount;
		for (int i = byteArray.size() - 1; i >= 0; i--) {
			for (int j = byteArray.get(i).length - 1; j >= 0; j--) {
				if (count <= 0) break;
				if (byteArray.get(i)[j] != paddingByte) return null;
				count--;
			}
		}

		// 結合後の配列サイズ
		int size = byteArray.size() * byteArray.get(0).length - padCount;

		byte[] ret = new byte[size];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = byteArray.get(i / byteArray.get(0).length)[i % byteArray.get(0).length];
		}

		// 結合を行う(パディング分は入らないはず)
		return ret;
	}

	/**
	 * データを分割する splitSizeで割り切れない場合はnullを返します
	 *
	 * @param byteArray 分割するデータ
	 * @param splitSize 分割サイズ
	 * @return 分割されたデータ
	 */
	private List<byte[]> split(byte[] byteArray, int splitSize) {
		// 割り切れるか計算
		if (byteArray.length % splitSize != 0) return null;
		// データ分割数を計算する
		int splitNum = byteArray.length / splitSize;

		// 戻り値データを用意する
		List<byte[]> ret = new ArrayList<byte[]>();
		for (int i = 0; i < splitNum; i++) {
			byte[] ret_b = new byte[splitSize];
			for (int j = 0; j < ret_b.length; j++) {
				// データ読み出し位置
				int pos = i * splitSize + j;
				if (pos < byteArray.length) ret_b[j] = byteArray[pos];
				else ret_b[j] = 0;

			}
			ret.add(ret_b);
		}
		return ret;
	}

	/**
	 * 16進数でデータを表示する
	 *
	 * @param headString 先頭部に表示する文字列
	 * @param dumpData   表示するバイトデータ
	 */
	private void datadump(String headString, byte[] dumpData) {
		// 表示する文字列
		StringBuilder dumpText = new StringBuilder(headString);

		// 16進数データ作成
		for (byte data : dumpData) {
			int n = ((int) data & 0xff);
			dumpText.append(Integer.toHexString(n / 16));
			dumpText.append(Integer.toHexString(n % 16));
		}
		System.out.println(dumpText);
	}

	/**
	 * xor演算する(ブロックサイズ限定)
	 *
	 * @param data   処理対象データ(書き換わります)
	 * @param vector xorデータ
	 */
	private void xorBlock(byte[] data, byte[] vector) throws FatalException {

		if (data.length != NBb) throw new FatalException("処理対象データは" + NBb + "バイトである必要があります[入力:" + data.length + "]");
		if (data.length > vector.length) throw new FatalException("xorデータは入力データより長い必要があります[入力データ:" + data.length + ",xorデータ:" + vector.length + "]");

		for (int i = 0; i < data.length; i++)
			data[i] = (byte) (data[i] ^ vector[i]);
	}

	/**
	 * ブロックの暗号化を行う
	 *
	 * @param data 暗号化するデータ(サイズは16のみ)(中身が直接変更されます)
	 * @return 成否
	 */
	private void Cipher(byte[] data) throws FatalException {

		// サイズ確認
		if (data.length != 16) throw new FatalException("暗号化するブロックのデータサイズは16バイトにする必要があります[入力:" + data.length + "byte]");

		int i = 0;
		// ラウンドキー計算
		AddRoundKey(data, i++);

		// ラウンド処理
		for (i = 1; i < mRoundNum; i++) {
			SubBytes(data);
			ShiftRows(data);
			MixColumns(data);
			AddRoundKey(data, i);
		}

		// 暗号化最終処理
		SubBytes(data);
		ShiftRows(data);
		AddRoundKey(data, i);
	}

	/**
	 * 復号を行う
	 *
	 * @param data 復号するデータ(サイズは16のみ)(中身が直接変更されます)
	 * @return 成否
	 */
	private void invCipher(byte[] data) throws FatalException {

		// サイズ確認
		if (data.length != 16) throw new FatalException("復号するブロックのデータサイズは16バイトにする必要があります[入力:" + data.length + "byte]");

		// ラウンドキー計算
		AddRoundKey(data, mRoundNum);

		for (int i = mRoundNum - 1; i > 0; i--) {
			invShiftRows(data);
			invSubBytes(data);
			AddRoundKey(data, i);
			invMixColumns(data);
		}

		invShiftRows(data);
		invSubBytes(data);
		AddRoundKey(data, 0);
	}

	/**
	 * 入力バイトごとに対応表に基いて別のバイトに変換する
	 *
	 * @param data 暗号化するデータ(サイズは16のみ)(中身が直接変更されます)
	 * @return 成否
	 */
	private void SubBytes(byte[] data) throws FatalException {
		// サイズ確認
		if (data.length != 16) throw new FatalException("暗号化するブロックのデータサイズは16バイトにする必要があります[入力:" + data.length + "byte]");

		for (int i = 0; i < NBb; i++)
			data[i] = Sbox[(int) data[i] & 0xff];

	}

	/**
	 * 入力バイトごとに対応表に基いて別のバイトに変換する(復号)
	 *
	 * @param data 復号するデータ(サイズは16のみ)(中身が直接変更されます)
	 * @return 成否
	 */
	private void invSubBytes(byte[] data) throws FatalException {
		// サイズ確認
		if (data.length != 16) throw new FatalException("復号するブロックのデータサイズは16バイトにする必要があります[入力:" + data.length + "byte]");

		for (int i = 0; i < NBb; i++)
			data[i] = invSbox[(int) data[i] & 0xff];
	}

	/**
	 * 4バイト単位でまとめた行を左に規則的にシフトして混ぜこぜにする
	 *
	 * @param data 暗号化するデータ(サイズは16のみ)(中身が直接変更されます)
	 * @return 成否
	 */
	private void ShiftRows(byte[] data) throws FatalException {
		// サイズ確認
		if (data.length != 16) throw new FatalException("暗号化するブロックのデータサイズは16バイトにする必要があります[入力:" + data.length + "byte]");
		// 配列の複製
		byte[] cw = data.clone();

		for (int i = 0; i < NBb; i += 4 * 4) {
			for (int j = 1; j < 4; j++) {
				cw[i + j + 0 * 4] = data[i + j + ((j + 0) & 3) * 4];
				cw[i + j + 1 * 4] = data[i + j + ((j + 1) & 3) * 4];
				cw[i + j + 2 * 4] = data[i + j + ((j + 2) & 3) * 4];
				cw[i + j + 3 * 4] = data[i + j + ((j + 3) & 3) * 4];
			}
		}
		System.arraycopy(cw, 0, data, 0, NBb);
	}

	/**
	 * 4バイト単位でまとめた行を左に規則的にシフトして混ぜこぜにする(復号)
	 *
	 * @param data 復号するデータ(サイズは16のみ)(中身が直接変更されます)
	 * @return 成否
	 */
	private void invShiftRows(byte[] data) throws FatalException {
		// サイズ確認
		if (data.length != 16) throw new FatalException("復号するブロックのデータサイズは16バイトにする必要があります[入力:" + data.length + "byte]");
		// 配列の複製
		byte[] cw = data.clone();
		for (int i = 0; i < NBb; i += 4 * 4) {
			for (int j = 1; j < 4; j++) {
				cw[i + j + ((j + 0) & 3) * 4] = data[i + j + 0 * 4];
				cw[i + j + ((j + 1) & 3) * 4] = data[i + j + 1 * 4];
				cw[i + j + ((j + 2) & 3) * 4] = data[i + j + 2 * 4];
				cw[i + j + ((j + 3) & 3) * 4] = data[i + j + 3 * 4];
			}
		}
		System.arraycopy(cw, 0, data, 0, NBb);
	}

	private int mul(int dt, int n) {
		int x = 0;
		for (int i = 8; i > 0; i >>= 1) {
			x <<= 1;
			if ((x & 0x100) != 0) x = (x ^ 0x1b) & 0xff;
			if (((n & i)) != 0) x ^= dt;
		}
		return x;
	}

	/**
	 * 4バイトの値をビット演算を用いて別の4バイトに変換する
	 *
	 * @param data 暗号化するデータ(サイズは16のみ)(中身が直接変更されます)
	 * @return 成否
	 */
	private void MixColumns(byte[] data) throws FatalException {
		// サイズ確認
		if (data.length != 16) throw new FatalException("暗号化するブロックのデータサイズは16バイトにする必要があります[入力:" + data.length + "byte]");
		byte[] x = new byte[16];
		for (int i = 0; i < NBb; i += 4) {
			x[i + 0] = (byte) (mul(data[i + 0], 2) ^ mul(data[i + 1], 3) ^ mul(data[i + 2], 1) ^ mul(data[i + 3], 1));
			x[i + 1] = (byte) (mul(data[i + 1], 2) ^ mul(data[i + 2], 3) ^ mul(data[i + 3], 1) ^ mul(data[i + 0], 1));
			x[i + 2] = (byte) (mul(data[i + 2], 2) ^ mul(data[i + 3], 3) ^ mul(data[i + 0], 1) ^ mul(data[i + 1], 1));
			x[i + 3] = (byte) (mul(data[i + 3], 2) ^ mul(data[i + 0], 3) ^ mul(data[i + 1], 1) ^ mul(data[i + 2], 1));
		}
		System.arraycopy(x, 0, data, 0, NBb);
	}

	/**
	 * 4バイトの値をビット演算を用いて別の4バイトに変換する(復号)
	 *
	 * @param data 復号するデータ(サイズは16のみ)(中身が直接変更されます)
	 * @return 成否
	 */
	private void invMixColumns(byte[] data) throws FatalException {
		// サイズ確認
		if (data.length != 16) throw new FatalException("復号するブロックのデータサイズは16バイトにする必要があります[入力:" + data.length + "byte]");
		byte[] x = new byte[16];
		for (int i = 0; i < NBb; i += 4) {
			x[i + 0] = (byte) (mul(data[i + 0], 14) ^ mul(data[i + 1], 11) ^ mul(data[i + 2], 13) ^ mul(data[i + 3], 9));
			x[i + 1] = (byte) (mul(data[i + 1], 14) ^ mul(data[i + 2], 11) ^ mul(data[i + 3], 13) ^ mul(data[i + 0], 9));
			x[i + 2] = (byte) (mul(data[i + 2], 14) ^ mul(data[i + 3], 11) ^ mul(data[i + 0], 13) ^ mul(data[i + 1], 9));
			x[i + 3] = (byte) (mul(data[i + 3], 14) ^ mul(data[i + 0], 11) ^ mul(data[i + 1], 13) ^ mul(data[i + 2], 9));
		}
		System.arraycopy(x, 0, data, 0, NBb);
	}

	/**
	 * 指定した4バイトのデータの排他的論理和を計算する
	 *
	 * @param data 排他的論理和を行うデータ1(書き換わります)
	 * @param pos1 排他的論理和を行う先頭のバイト位置1
	 * @param w    排他的論理和を行うデータ2
	 * @param pos2 排他的論理和を行う先頭のバイト位置2
	 */
	private void Exor(byte[] data, int pos1, byte[] w, int pos2) {
		data[pos1 + 0] = (byte) (data[pos1 + 0] ^ w[pos2 + 0]);
		data[pos1 + 1] = (byte) (data[pos1 + 1] ^ w[pos2 + 1]);
		data[pos1 + 2] = (byte) (data[pos1 + 2] ^ w[pos2 + 2]);
		data[pos1 + 3] = (byte) (data[pos1 + 3] ^ w[pos2 + 3]);
	}

	/**
	 * MixColumns の出力 (4バイト) とラウンド鍵 (4バイト) との XOR を取る
	 *
	 * @param data 暗号化するデータ(これ自体が書き換わります)
	 * @param n    不明
	 */
	private void AddRoundKey(byte[] data, int n) {
		for (int i = 0; i < NBb; i += 4) {
			Exor(data, i, mRoundKey, i + NBb * n);// data[i] ^= w[i+NB*n];
		}
	}

	private byte[] SubWord(byte[] in) {
		in[0] = Sbox[((int) in[0] & 0xff)];
		in[1] = Sbox[((int) in[1] & 0xff)];
		in[2] = Sbox[((int) in[2] & 0xff)];
		in[3] = Sbox[((int) in[3] & 0xff)];
		return in;
	}

	private byte[] RotWord(byte[] in) {
		byte x = in[0];
		in[0] = in[1];
		in[1] = in[2];
		in[2] = in[3];
		in[3] = x;
		return in;
	}

	static void Exor(byte[] w, int pos1, int pos2, byte[] temp) {
		w[pos1 + 0] = (byte) (w[pos2 + 0] ^ temp[0]);
		w[pos1 + 1] = (byte) (w[pos2 + 1] ^ temp[1]);
		w[pos1 + 2] = (byte) (w[pos2 + 2] ^ temp[2]);
		w[pos1 + 3] = (byte) (w[pos2 + 3] ^ temp[3]);
	}

	/**
	 * ラウンド鍵の準備
	 *
	 * @param key 元となるキー
	 * @return ラウンド鍵
	 */
	private byte[] KeyExpansion(byte[] key) throws FatalException {

		if (key.length != mKeyLengthByte) throw new FatalException("キーの長さは" + mKeyLengthByte + "にする必要があります．[入力:" + key.length + "]");

		byte Rcon[] = { 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, (byte) 0x80, 0x1b, 0x36 };
		byte[] temp = new byte[4];

		byte[] roundKey = new byte[NBb * (mRoundNum + 1)];
		System.arraycopy(key, 0, roundKey, 0, mKeyLengthByte);
		for (int i = mKeyLengthByte; i < roundKey.length; i += 4) {
			System.arraycopy(roundKey, i - 4, temp, 0, 4);// temp = mRoundKey[i-1];
			if (i % mKeyLengthByte == 0) {
				temp = SubWord(RotWord(temp));
				temp[0] ^= Rcon[(i / mKeyLengthByte) - 1];
			} else if (mKeyLengthByte > 6 * 4 && (i % mKeyLengthByte) == 4 * 4) temp = SubWord(temp);
			Exor(roundKey, i, i - mKeyLengthByte, temp);// mRoundKey[i] = mRoundKey[i-mKeyLengthByte] ^ temp;
		}
		return roundKey;
	}
}
