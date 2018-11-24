package angou;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

import entity.Setting;
import exception.FatalException;
import exception.NormalException;

/**
 * AESキークラス
 */
public class Angou {
	// ブロックサイズ(バイト)
	private final static int BLOCKSIZE = 16;
	// IVヘッダ部サイズ(1バイト毎に乱数が入る)(バイト)
	private final static int IVHEADSIZE = BLOCKSIZE * 2;

	/**
	 * 暗号キーを新規作成する(16バイト)
	 *
	 * @return 生成した暗号キー（Base64エンコード済み）
	 */
	public static String generateKey() {
		// 128ビット(16バイト)の文字列を生成
		Random random = new Random();
		byte[] strByte = new byte[BLOCKSIZE];
		for (int i = 0; i < strByte.length; i++) {
			strByte[i] = (byte) random.nextInt(255);
		}
		return Base64.getEncoder().encodeToString(strByte);
	}

	/**
	 * 初期ベクトルの生成(16バイト)
	 *
	 * @return 生成した初期ベクトル（Base64エンコード済み）
	 */
	private static String generateIV() {
		// 128ビット(16バイト)の文字列を生成
		Random random = new Random();
		byte[] strByte = new byte[BLOCKSIZE];
		for (int i = 0; i < strByte.length; i++) {
			strByte[i] = (byte) (random.nextInt(127));
		}
		return Base64.getEncoder().encodeToString(strByte);
	}

	/**
	 * 暗号化を行う
	 *
	 * @param srcText 暗号化を行う文字列
	 * @param key     暗号キー
	 * @return 暗号文字(先頭にIVあり)
	 * @exception FatalException 致命的エラー
	 */
	static public String encode(String srcText, String key) throws FatalException {
		if (key == null) throw new NullPointerException("keyがnullです");
		// キーを取得する
		byte[] key_bin;
		try {
			key_bin = key.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			throw new FatalException("");
		}

		// IV(初期ベクトル)を生成
		byte[] iv_bin = Base64.getDecoder().decode(generateIV());

		// 文字列のバイナリを取得する
		byte[] srcText_bin = null;
		try {
			srcText_bin = srcText.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new FatalException("文字列のバイト化に失敗しました．", e);
		}

		AES aes = new AES(AES.KeyLength.AES256, new String(key_bin), new String(iv_bin));
		byte[] enc = aes.encode(srcText_bin);

		// IVを記録場所を確保(1バイトおき32バイト分)
		byte[] vi_byte = new byte[IVHEADSIZE];
		Random random = new Random();
		for (int i = 0; i < vi_byte.length; i++) {
			vi_byte[i] = (byte) random.nextInt(255);
		}
		// IV記録(1バイトおき)
		for (int i = 0; i < vi_byte.length; i += 2) {
			vi_byte[i] = iv_bin[i / 2];
		}

		// 一列の配列に戻す(IVを先頭に付加)
		byte[] encoded_byte = new byte[vi_byte.length + enc.length];
		System.arraycopy(vi_byte, 0, encoded_byte, 0, vi_byte.length);
		System.arraycopy(enc, 0, encoded_byte, vi_byte.length, enc.length);

		// BASE64エンコード
		String encoded_text = Base64.getEncoder().encodeToString(encoded_byte);

		return encoded_text;
	}

	/**
	 * 復号を行う
	 *
	 * @param srcBASE64Text 暗号化文字列(BASE64)
	 * @param key           暗号キー
	 * @return 復号化した文字列
	 * @throws FatalException 致命的エラー
	 */
	static public String decode(String srcBASE64Text, String key) throws FatalException {
		if (key == null) throw new NullPointerException("keyがnullです");
		if (srcBASE64Text == null) throw new NullPointerException("keyがnullです");

		// 暗号文をバイト文字列に戻す
		try {
			Base64.getDecoder().decode(srcBASE64Text);
		}catch (Exception e) {
			e.printStackTrace();
		}
		byte[] encodedByteWirhIV = Base64.getDecoder().decode(srcBASE64Text);
		// キーを取得する
		byte[] key_bin;
		try {
			key_bin = key.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			throw new FatalException("");
		}

		/* IV(初期ベクトル)を取得 */
		// 32バイト以下ならIVが無いことになるので，エラー
		if (encodedByteWirhIV.length < IVHEADSIZE) throw new FatalException("復号に失敗しました．");

		// IV取得
		byte[] iv_bin = new byte[BLOCKSIZE];
		for (int i = 0; i < iv_bin.length; i++) {
			iv_bin[i] = encodedByteWirhIV[i * 2];
		}

		// IVを除いたデータを取得
		byte[] encodedByte = Arrays.copyOfRange(encodedByteWirhIV, IVHEADSIZE, encodedByteWirhIV.length);

		// 暗号化インスタンス生成
		AES aes = new AES(AES.KeyLength.AES256, new String(key_bin), new String(iv_bin));

		// デコード
		byte[] enc = aes.decode(encodedByte);

		// 文字列に戻す
		String srcText = new String(enc, Charset.forName("UTF-8"));

		return srcText;
	}

	/**
	 * AESキーを取得する
	 *
	 * @param setting 設定情報
	 * @return AESキー
	 * @throws NormalException 通常エラー
	 * @throws FatalException  致命的エラー
	 */
	public static String getAESKey(Setting setting) throws FatalException, NormalException {

		// キーを取得する
		String password = setting.getPassword();

		if (password == null) throw new FatalException("暗号キーを取得出来ませんでした");
		if (password == "") throw new FatalException("暗号キーは必ず設定してください(設定ファイル参照)");

		return password;
	}
}
