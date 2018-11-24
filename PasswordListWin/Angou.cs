using System;
using System.Text;

namespace PasswordListWin
{
	class Angou
	{
		// ブロックサイズ(バイト)
		private static readonly int BLOCKSIZE = 16;
		// IVヘッダ部サイズ(1バイト毎に乱数が入る)(バイト)
		private static readonly int IVHEADSIZE = BLOCKSIZE * 2;

		/**
		 * 初期ベクトルの生成(16バイト)
		 *
		 * @return 生成した初期ベクトル（Base64エンコード済み）
		 */
		private static string generateIV()
		{
			// 128ビット(16バイト)の文字列を生成
			Random random = new Random();
			byte[] strByte = new byte[BLOCKSIZE];
			for (int i = 0; i < strByte.Length; i++)
			{
				strByte[i] = (byte)(random.Next(127));
			}
			return Convert.ToBase64String(strByte);
		}

		/// <summary>
		/// 復号を行う
		/// </summary>
		/// <param name="srcBASE64Text">暗号化された文字(BASE64エンコード済み)</param>
		/// <param name="key">暗号キー</param>
		/// <returns>復号化した文字列</returns>
		static public ReturnObject<string> Decode(string srcBASE64Text, string key)
		{
			if (key == null) return new ReturnObject<string>(null, "復号keyが不明です。");
			byte[] encoded_byte;
			// 配列に戻す
			try
			{
				encoded_byte = Convert.FromBase64String(srcBASE64Text);
			}
			catch (FormatException e)
			{
				return new ReturnObject<string>(null, "復号に失敗しました．", e);
			}
			// 32バイト以下ならIVが無いことになるので，エラー
			if (encoded_byte.Length < IVHEADSIZE) return new ReturnObject<string>(null, "復号に失敗しました．");

			// IV取得
			byte[] iv_bin = new byte[BLOCKSIZE];
			for (int i = 0; i < iv_bin.Length; i++)
			{
				iv_bin[i] = encoded_byte[i * 2];
			}

			// 復号対象データ
			byte[] dec_bin = new byte[encoded_byte.Length - BLOCKSIZE * 2];
			Array.Copy(encoded_byte, BLOCKSIZE * 2, dec_bin, 0, dec_bin.Length);

			// 復号処理
			AES aes = new AES(AES.KeyLength.AES256, key, Encoding.UTF8.GetString(iv_bin));
			ReturnObject<byte[]> decodedData = aes.decode(dec_bin);

			if (!decodedData.State) return new ReturnObject<string>(null, decodedData);

			// 文字列に戻す
			string srcText = Encoding.UTF8.GetString(decodedData.Value);

			return new ReturnObject<string>(srcText);
		}

		/// <summary>
		/// 暗号化を行う
		/// </summary>
		/// <param name="srcBASE64Text">暗号化する文字列</param>
		/// <param name="key">暗号キー</param>
		/// <returns>暗号化した文字列</returns>
		static public ReturnObject<string> Encode(string srcText, string key)
		{
			if (key == null) return new ReturnObject<string>(null, "暗号keyが不明です。");

			// IV(初期ベクトル)を生成
			byte[] iv_bin = Convert.FromBase64String(generateIV());

			// 文字列のバイナリを取得する
			byte[] srcText_bin;
			try
			{
				srcText_bin = Encoding.UTF8.GetBytes(srcText);
			}
			catch (FormatException e)
			{
				return new ReturnObject<string>(null, "暗号化に失敗しました．", e);
			}

			AES aes = new AES(AES.KeyLength.AES256, key, Encoding.UTF8.GetString(iv_bin));
			ReturnObject<byte[]> enc = aes.encode(srcText_bin);

			if(!enc.State)	return new ReturnObject<string>(null, "暗号化に失敗しました．", enc.CauseException);

			// IVを記録場所を確保(1バイトおき32バイト分)
			byte[] vi_byte = new byte[IVHEADSIZE];
			Random random = new Random();
			for (int i = 0; i < vi_byte.Length; i++)
			{
				vi_byte[i] = (byte)random.Next(255);
			}
			// IV記録(1バイトおき)
			for (int i = 0; i < vi_byte.Length; i += 2)
			{
				vi_byte[i] = iv_bin[i / 2];
			}

			// 一列の配列に戻す(IVを先頭に付加)
			byte[] encoded_byte = new byte[vi_byte.Length + enc.Value.Length];
			Array.Copy(vi_byte, 0, encoded_byte, 0, vi_byte.Length);
			Array.Copy(enc.Value, 0, encoded_byte, vi_byte.Length, enc.Value.Length);

			// 文字列に戻す
			string encodedText = Convert.ToBase64String(encoded_byte);

			return new ReturnObject<string>(encodedText);
		}
	}
}
