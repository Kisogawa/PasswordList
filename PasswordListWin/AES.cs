using System;
using System.Collections.Generic;
using System.Text;

namespace PasswordListWin
{
	/// <summary>
	/// AES暗号化クラス
	/// </summary>
	public class AES
	{
		/// <summary>
		/// 暗号キーの長さ
		/// </summary>
		public enum KeyLength
		{
			AES128,
			AES192,
			AES256
		}

		/// <summary>
		/// 別バイト置き換え用データ
		/// </summary>
		static private readonly byte[] Sbox = { 0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76, 0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0, 0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15, 0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75, 0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84, 0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf, 0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8, 0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2, 0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73, 0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb, 0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79, 0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08, 0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a, 0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e, 0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf, 0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16 };

		/// <summary>
		/// 別バイト置き換え用データ(復号)
		/// </summary>
		static private readonly byte[] invSbox = { 0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb, 0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb, 0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e, 0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25, 0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92, 0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84, 0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06, 0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b, 0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73, 0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e, 0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b, 0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4, 0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f, 0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef, 0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61, 0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d };

		/// <summary>
		/// 鍵の長さ(enum定義)
		/// </summary>
		private readonly KeyLength mKeyLengthEnum;

		/// <summary>
		/// 鍵の長さ(ビット長)
		/// </summary>
		private readonly int mKeyLengthBit;

		/// <summary>
		/// 暗号キーの長さ(byte)
		/// </summary>
		private readonly int mKeyLengthByte;

		/// <summary>
		/// 暗号化ブロックサイズ(byte)
		/// </summary>
		static private readonly int NBb = 16;

		/// <summary>
		/// ラウンド数(鍵長128bit→10,192bit→12,256bit→14)
		/// </summary>
		private readonly int mRoundNum;

		/// <summary>
		/// 暗号鍵
		/// </summary>
		private readonly byte[] mKey;

		/// <summary>
		/// ラウンド鍵(暗号インスタンスで共有)
		/// </summary>
		private readonly byte[] mRoundKey;

		/// <summary>
		/// 初期ベクトル
		/// </summary>
		private readonly byte[] mIV;

		/// <summary>
		/// AES暗号化(CBCモード)初期化
		/// </summary>
		/// <param name="keyLength">暗号鍵の長さ</param>
		/// <param name="key">暗号鍵(1文字以上)</param>
		/// <param name="iv">初期ベクトル</param>
		public AES(KeyLength keyLength, string key, string iv)
		{
			mKeyLengthEnum = keyLength;
			switch (mKeyLengthEnum)
			{
				case KeyLength.AES128: mKeyLengthBit = 128; break;
				case KeyLength.AES192: mKeyLengthBit = 192; break;
				case KeyLength.AES256: mKeyLengthBit = 256; break;
				default: throw new ArgumentException("不明な鍵長です");
			}
			mKeyLengthByte = mKeyLengthBit / 8;
			mRoundNum = mKeyLengthBit / 32 + 6;


			/* 暗号キー生成 */
			// 鍵文字列をバイト配列に変換する
			byte[] keys = Encoding.UTF8.GetBytes(key);
			// 長さ0はエラー
			if (keys.Length == 0) throw new ArgumentException("暗号鍵は1文字以上入力してください");
			// 鍵データを作成する
			byte[] keydata = new byte[mKeyLengthByte];
			for (int i = 0; i < keydata.Length; i++) keydata[i] = keys[i % keys.Length];
			// 残りの鍵情報で排他的論理和を計算する
			for (int i = mKeyLengthByte; i < keys.Length; i++) keydata[i % mKeyLengthByte] = (byte)(keydata[i % mKeyLengthByte] ^ keys[i]);
			mKey = keydata;

			/* ラウンド鍵生成 */
			ReturnObject<byte[]> ret = KeyExpansion(keydata);
			if (!ret.State) throw new ArgumentException("鍵の生成に失敗[" + ret.ToString() + "]");
			mRoundKey = ret.Value;

			/* 初期ベクトル生成 */
			// 鍵文字列をバイト配列に変換する
			byte[] ivs = new byte[0];
			if (iv.Length > 0) ivs = System.Text.Encoding.UTF8.GetBytes(iv);
			// 初期ベクトルデータを作成する
			byte[] ivdata = new byte[NBb];
			for (int i = 0; i < ivdata.Length; i++) ivdata[i] = ivs[i % ivs.Length];
			// 残りの初期ベクトル情報で排他的論理和を計算する
			for (int i = NBb; i < ivs.Length; i++) ivdata[i % NBb] = (byte)(ivdata[i % NBb] ^ ivs[i]);
			mIV = ivdata;

			// キーとIVのダンプデータ出力
			//datadump("KEY:       ", mKey);
			//datadump("IV :       ", mIV);
		}

		public void test(string data)
		{
			string d = Convert.ToBase64String(Encoding.UTF8.GetBytes(data));


			Console.WriteLine("PLAINTEXT: " + d);
			datadump("KEY:       ", mKey);
			datadump("IV:        ", mIV);
			datadump("RoundKey:  ", mRoundKey);
			datadump("Sbox:      ", Sbox);
			datadump("invSbox:   ", invSbox);

			ReturnObject<string> encData = encodeBASE64(d);
			Console.WriteLine("暗号化:    " + encData);
			ReturnObject<string> decData = decodeBASE64(encData.Value);
			Console.WriteLine("復号化:    " + decData);
			Console.WriteLine("暗号化テスト終了");
		}

		/// <summary>
		/// 暗号化を行う
		/// </summary>
		/// <param name="inDataBASE64">暗号化を行うデータ(BASE64エンコード済み)</param>
		public ReturnObject<string> encodeBASE64(string inDataBASE64)
		{
			// 入力データをバイト化(BASE64デコード)
			byte[] inDataByte = null;
			try
			{
				inDataByte = Convert.FromBase64String(inDataBASE64);
			}
			catch (FormatException e)
			{
				return new ReturnObject<string>("", "暗号化に失敗", e);
			}
			ReturnObject<byte[]> ret = encode(inDataByte);
			if (!ret.State) return new ReturnObject<string>("", ret);
			// BASE64デコードを行い，処理終了
			return new ReturnObject<string>(Convert.ToBase64String(ret.Value));
		}

		/// <summary>
		/// 暗号化を行う
		/// </summary>
		/// <param name="inData">暗号化を行うデータ</param>
		public ReturnObject<byte[]> encode(byte[] inData)
		{
			// 入力データをブロックサイズの倍数にパディングし分割する
			List<byte[]> dataPading = paddingByteWithSplit(inData, NBb);
			// 初期ベクトルをベクトルに設定
			byte[] vector = mIV;
			// 暗号化を行う
			foreach (byte[] d in dataPading)
			{
				// ベクトル演算(CBC)
				xorBlock(d, vector);
				// ブロックの暗号化処理
				Cipher(d);
				// 暗号化結果を次回に使用するベクトルとして記録
				vector = d;
			}
			// 結果データを連結する
			byte[] encodedData = new byte[dataPading.Count * NBb];
			for (int i = 0; i < encodedData.Length; i++)
			{
				encodedData[i] = dataPading[i / NBb][i % NBb];
			}
			// BASE64デコードを行い，処理終了
			return new ReturnObject<byte[]>(encodedData);
		}

		/// <summary>
		/// 復号を行う
		/// </summary>
		/// <param name="inDataBASE64">復号を行うデータ(BASE64エンコード済み)</param>
		/// <returns></returns>
		public ReturnObject<string> decodeBASE64(string inDataBASE64)
		{
			byte[] inDataByte = null;
			// 入力データをバイト化(BASE64デコード)
			try
			{
				inDataByte = Convert.FromBase64String(inDataBASE64);
			}
			catch (FormatException e)
			{
				return new ReturnObject<string>("", "復号に失敗", e);
			}
			ReturnObject<byte[]> ret = decode(inDataByte);
			if (!ret.State) return new ReturnObject<string>("", ret);
			// BASE64デコードを行い，処理終了
			return new ReturnObject<string>(Convert.ToBase64String(ret.Value));
		}

		/// <summary>
		/// 復号を行う
		/// </summary>
		/// <param name="inData">復号を行うデータ</param>
		public ReturnObject<byte[]> decode(byte[] inData)
		{
			// 入力データをブロックサイズに分割する
			List<byte[]> dataSplit = split(inData, NBb);
			if (dataSplit == null) return new ReturnObject<byte[]>(null, "復号に失敗");
			// 初期ベクトルをベクトルに設定
			var vector = mIV;
			// 復号を行う
			foreach (byte[] d in dataSplit)
			{
				// 今回使用するベクトル
				byte[] v = new byte[d.Length];
				for (int i = 0; i < v.Length; i++) v[i] = d[i];
				// ブロックの復号処理
				invCipher(d);
				// ベクトル演算(CBC)
				xorBlock(d, vector);
				// 暗号化結果を次回に使用するベクトルとして記録
				vector = v;
			}

			byte[] ret = invPaddingByteWithSplit(dataSplit);
			if (ret == null) return new ReturnObject<byte[]>(null, "復号に失敗");

			return new ReturnObject<byte[]>(ret);
		}

		/// <summary>
		/// パディングを行い，パディングサイズ毎にデータを分割する
		/// </summary>
		/// <param name="byteArray">パディングするデータ</param>
		/// <param name="maxPaddingSize">パディングされたデータ</param>
		/// <returns></returns>
		private List<byte[]> paddingByteWithSplit(byte[] byteArray, int maxPaddingSize)
		{
			if (maxPaddingSize < 1 || 256 <= maxPaddingSize) throw new ArgumentException("パディングサイズは1～255の間である必要があります");
			// パディング文字数
			int padCount = maxPaddingSize - (byteArray.Length % maxPaddingSize);
			// パディング文字(パディング文字数のバイト変換後)
			byte padCountByte = (byte)(padCount & 0xff);
			// データ分割数を計算する
			int splitNum = (byteArray.Length + padCount) / maxPaddingSize;

			List<byte[]> ret = new List<byte[]>(splitNum);

			// 戻り値データを用意する
			for (int i = 0; i < splitNum; i++)
			{
				ret.Add(new byte[maxPaddingSize]);
				for (int j = 0; j < ret[i].Length; j++)
				{
					// データ読み出し位置
					int pos = i * maxPaddingSize + j;
					if (pos < byteArray.Length) ret[i][j] = byteArray[pos];
					else ret[i][j] = padCountByte;
				}
			}
			return ret;
		}

		/// <summary>
		/// データの結合を行い，パディング文字を削除する(分割データはすべて同じ長さの必要あり)
		/// </summary>
		/// <param name="byteArray">パディングされたデータ</param>
		/// <returns>パディングされる前のデータ</returns>
		private byte[] invPaddingByteWithSplit(List<byte[]> byteArray)
		{
			if (byteArray.Count <= 0) return null;
			// 一番最後の値を取得する
			byte[] paddingByteArray = byteArray[byteArray.Count - 1];
			if (paddingByteArray.Length <= 0) return null;
			byte paddingByte = paddingByteArray[paddingByteArray.Length - 1];
			// リストの長さがすべて同一か確認する
			foreach (byte[] ba in byteArray)
			{
				if (ba.Length != byteArray[0].Length) return null;
			}

			// パディング文字数(int変換後)
			int padCount = paddingByte & 0xff;
			// 文字の長さがパディング文字数以下ならエラー
			if (byteArray.Count * byteArray[0].Length < padCount) return null;

			var count = padCount;
			// パディングにより除去されるデータがパディング文字かどうか確認を行う
			for (int i = byteArray.Count - 1; i >= 0; i--)
			{
				for (int j = byteArray[i].Length - 1; j >= 0; j--)
				{
					if (count <= 0) break;
					if (byteArray[i][j] != paddingByte) return null;
					count--;
				}
			}

			// 結合後の配列サイズ
			int size = byteArray.Count * byteArray[0].Length - padCount;

			// 結合を行う(パディング分は入らないはず)
			byte[] ret = new byte[size];
			for (int i = 0; i < ret.Length; i++)
			{
				ret[i] = byteArray[i / byteArray[0].Length][i % byteArray[0].Length];
			}
			return ret;
		}

		/// <summary>
		/// データを分割する<br/>
		/// splitSizeで割り切れない場合はnullを返します
		/// </summary>
		/// <param name="byteArray">分割するデータ</param>
		/// <param name="splitSize">分割サイズ</param>
		/// <returns>分割されたデータ</returns>
		private List<byte[]> split(byte[] byteArray, int splitSize)
		{
			// 割り切れるか計算
			if (byteArray.Length % splitSize != 0) return null;
			// データ分割数を計算する
			int splitNum = byteArray.Length / splitSize;

			// 戻り値データを用意する
			List<byte[]> ret = new List<byte[]>(splitNum);
			for (int i = 0; i < splitNum; i++)
			{
				ret.Add(new byte[splitSize]);
				for (int j = 0; j < ret[i].Length; j++)
				{
					// データ読み出し位置
					int pos = i * splitSize + j;
					ret[i][j] = byteArray[pos];
				}
			}
			return ret;
		}


		/// <summary>
		/// 16進数でデータを表示する
		/// </summary>
		/// <param name="headString">先頭部に表示する文字列</param>
		/// <param name="dumpData">表示するバイトデータ</param>
		private void datadump(string headString, byte[] dumpData)
		{
			// 表示する文字列
			StringBuilder dumpText = new StringBuilder(headString);

			// 16進数データ作成
			foreach (var data in dumpData)
			{
				int n = data & 0xff;

				dumpText.Append(string.Format("{0:X}", n / 16));
				dumpText.Append(string.Format("{0:X}", n % 16));
			}
			Console.WriteLine(dumpText.ToString());
		}

		/// <summary>
		/// xor演算する(ブロックサイズ限定)
		/// </summary>
		/// <param name="data">処理対象データ(書き換わります)</param>
		/// <param name="vector">xorデータ</param>
		private ReturnObject xorBlock(byte[] data, byte[] vector)
		{

			if (data.Length != NBb) return new ReturnObject("処理対象データは" + NBb + "バイトである必要があります[入力:" + data.Length + "]");
			if (data.Length > vector.Length) return new ReturnObject("xorデータは入力データより長い必要があります[入力データ:" + data.Length + ",xorデータ:" + vector.Length + "]");

			for (int i = 0; i < data.Length; i++) data[i] = (byte)(data[i] ^ vector[i]);

			return new ReturnObject();
		}


		/// <summary>
		/// ブロックの暗号化を行う
		/// </summary>
		/// <param name="data">暗号化するデータ(サイズは16のみ)(中身が直接変更されます)</param>
		/// <returns>成否</returns>
		private ReturnObject Cipher(byte[] data)
		{

			// サイズ確認
			if (data.Length != 16) return new ReturnObject("暗号化するブロックのデータサイズは16バイトにする必要があります[入力:" + data.Length + "byte]");

			int i = 0;
			// ラウンドキー計算
			AddRoundKey(data, i++);

			// ラウンド処理
			while (i < mRoundNum)
			{
				SubBytes(data);
				ShiftRows(data);
				MixColumns(data);
				AddRoundKey(data, i++);
			}

			// 暗号化最終処理
			SubBytes(data);
			ShiftRows(data);
			AddRoundKey(data, i);

			return new ReturnObject();
		}

		/// <summary>
		/// 復号を行う
		/// </summary>
		/// <param name="data">復号するデータ(サイズは16のみ)(中身が直接変更されます)</param>
		/// <returns>成否</returns>
		private ReturnObject invCipher(byte[] data)
		{

			// サイズ確認
			if (data.Length != 16) return new ReturnObject("復号するブロックのデータサイズは16バイトにする必要があります[入力:" + data.Length + "byte]");

			// ラウンドキー計算
			AddRoundKey(data, mRoundNum);

			int i = mRoundNum - 1;
			while (i > 0)
			{
				invShiftRows(data);
				invSubBytes(data);
				AddRoundKey(data, i);
				invMixColumns(data);
				i--;
			}

			invShiftRows(data);
			invSubBytes(data);
			AddRoundKey(data, 0);

			return new ReturnObject();
		}

		/// <summary>
		/// 入力バイトごとに対応表に基いて別のバイトに変換する
		/// </summary>
		/// <param name="data">暗号化するデータ(サイズは16のみ)(中身が直接変更されます)</param>
		/// <returns>成否</returns>
		private ReturnObject SubBytes(byte[] data)
		{
			// サイズ確認
			if (data.Length != 16) return new ReturnObject("暗号化するブロックのデータサイズは16バイトにする必要があります[入力:" + data.Length + "byte]");
			for (int i = 0; i < NBb; i++) data[i] = Sbox[data[i] & 0xff];
			return new ReturnObject();
		}

		/// <summary>
		/// 入力バイトごとに対応表に基いて別のバイトに変換する(復号)
		/// </summary>
		/// <param name="data">復号するデータ(サイズは16のみ)(中身が直接変更されます)</param>
		/// <returns>成否</returns>
		private ReturnObject invSubBytes(byte[] data)
		{
			// サイズ確認
			if (data.Length != 16) return new ReturnObject("復号するブロックのデータサイズは16バイトにする必要があります[入力:" + data.Length + "byte]");
			for (int i = 0; i < NBb; i++) data[i] = invSbox[data[i] & 0xff];
			return new ReturnObject();
		}

		/// <summary>
		/// 4バイト単位でまとめた行を左に規則的にシフトして混ぜこぜにする
		/// </summary>
		/// <param name="data">暗号化するデータ(サイズは16のみ)(中身が直接変更されます)</param>
		/// <returns>成否</returns>
		private ReturnObject ShiftRows(byte[] data)
		{
			// サイズ確認
			if (data.Length != 16) return new ReturnObject("暗号化するブロックのデータサイズは16バイトにする必要があります[入力:" + data.Length + "byte]");
			// 配列の複製
			byte[] cw = new byte[16];
			for (int i = 0; i < cw.Length; i++) cw[i] = data[i];

			for (int i = 0; i < NBb; i += 4 * 4)
			{
				for (int j = 1; j < 4; j++)
				{
					cw[i + j + 0 * 4] = data[i + j + (j + 0 & 3) * 4];
					cw[i + j + 1 * 4] = data[i + j + (j + 1 & 3) * 4];
					cw[i + j + 2 * 4] = data[i + j + (j + 2 & 3) * 4];
					cw[i + j + 3 * 4] = data[i + j + (j + 3 & 3) * 4];
				}
			}
			Array.Copy(cw, 0, data, 0, NBb);
			return new ReturnObject();
			/**
			 * 4バイト単位でまとめた行を左に規則的にシフトして混ぜこぜにする(復号)
			 * @param data 復号するデータ(サイズは16のみ)(中身が直接変更されます)
			 * @return 成否
			 */
		}

		private ReturnObject invShiftRows(byte[] data)
		{
			// サイズ確認
			if (data.Length != 16) return new ReturnObject("復号するブロックのデータサイズは16バイトにする必要があります[入力:" + data.Length + "byte]");
			// 配列の複製
			byte[] cw = new byte[16];
			for (int i = 0; i < cw.Length; i++) cw[i] = data[i];

			for (int i = 0; i < NBb; i += 4 * 4)
			{
				for (int j = 1; j < 4; j++)
				{
					cw[i + j + (j + 0 & 3) * 4] = data[i + j + 0 * 4];
					cw[i + j + (j + 1 & 3) * 4] = data[i + j + 1 * 4];
					cw[i + j + (j + 2 & 3) * 4] = data[i + j + 2 * 4];
					cw[i + j + (j + 3 & 3) * 4] = data[i + j + 3 * 4];
				}
			}
			Array.Copy(cw, 0, data, 0, NBb);
			return new ReturnObject();
		}


		private int mul(int dt, int n)
		{
			int i = 8;
			int x = 0;

			while (i > 0)
			{
				x = x << 1;
				if ((x & 0x100) != 0) x = x ^ 0x1b & 0xff;
				if ((n & i) != 0) x = x ^ dt;
				i = i >> 1;
			}
			return x;
		}

		/// <summary>
		/// 4バイトの値をビット演算を用いて別の4バイトに変換する
		/// </summary>
		/// <param name="data">暗号化するデータ(サイズは16のみ)(中身が直接変更されます)</param>
		/// <returns>成否</returns>
		private ReturnObject MixColumns(byte[] data)
		{
			// サイズ確認
			if (data.Length != 16) return new ReturnObject("暗号化するブロックのデータサイズは16バイトにする必要があります[入力:" + data.Length + "byte]");
			byte[] x = new byte[16];
			for (int i = 0; i < NBb; i += 4)
			{
				x[i + 0] = (byte)(mul(data[i + 0], 2) ^
						mul(data[i + 1], 3) ^
						mul(data[i + 2], 1) ^
						mul(data[i + 3], 1));
				x[i + 1] = (byte)(mul(data[i + 1], 2) ^
					mul(data[i + 2], 3) ^
					mul(data[i + 3], 1) ^
					mul(data[i + 0], 1));
				x[i + 2] = (byte)(mul(data[i + 2], 2) ^
					mul(data[i + 3], 3) ^
					mul(data[i + 0], 1) ^
					mul(data[i + 1], 1));
				x[i + 3] = (byte)(mul(data[i + 3], 2) ^
					mul(data[i + 0], 3) ^
					mul(data[i + 1], 1) ^
					mul(data[i + 2], 1));
			}
			Array.Copy(x, 0, data, 0, NBb);
			return new ReturnObject();
		}

		/// <summary>
		/// 4バイトの値をビット演算を用いて別の4バイトに変換する(復号)
		/// </summary>
		/// <param name="data">復号するデータ(サイズは16のみ)(中身が直接変更されます)</param>
		/// <returns>成否</returns>
		private ReturnObject invMixColumns(byte[] data)
		{
			// サイズ確認
			if (data.Length != 16) return new ReturnObject("復号するブロックのデータサイズは16バイトにする必要があります[入力:" + data.Length + "byte]");
			byte[] x = new byte[16];
			for (int i = 0; i < NBb; i += 4)
			{
				x[i + 0] = (byte)(mul(data[i + 0], 14) ^
						mul(data[i + 1], 11) ^
						mul(data[i + 2], 13) ^
						mul(data[i + 3], 9));
				x[i + 1] = (byte)(mul(data[i + 1], 14) ^
						mul(data[i + 2], 11) ^
						mul(data[i + 3], 13) ^
						mul(data[i + 0], 9));
				x[i + 2] = (byte)(mul(data[i + 2], 14) ^
						mul(data[i + 3], 11) ^
						mul(data[i + 0], 13) ^
						mul(data[i + 1], 9));
				x[i + 3] = (byte)(mul(data[i + 3], 14) ^
						mul(data[i + 0], 11) ^
						mul(data[i + 1], 13) ^
						mul(data[i + 2], 9));
			}
			Array.Copy(x, 0, data, 0, NBb);
			return new ReturnObject();
		}

		/// <summary>
		/// 指定した4バイトのデータの排他的論理和を計算する
		/// </summary>
		/// <param name="data">排他的論理和を行うデータ1(書き換わります)</param>
		/// <param name="pos1">排他的論理和を行う先頭のバイト位置1</param>
		/// <param name="w">排他的論理和を行うデータ2</param>
		/// <param name="pos2">排他的論理和を行う先頭のバイト位置2</param>
		private void Exor(byte[] data, int pos1, byte[] w, int pos2)
		{
			data[pos1 + 0] = (byte)(data[pos1 + 0] ^ w[pos2 + 0]);
			data[pos1 + 1] = (byte)(data[pos1 + 1] ^ w[pos2 + 1]);
			data[pos1 + 2] = (byte)(data[pos1 + 2] ^ w[pos2 + 2]);
			data[pos1 + 3] = (byte)(data[pos1 + 3] ^ w[pos2 + 3]);
		}

		/// <summary>
		/// MixColumns の出力 (4バイト) とラウンド鍵 (4バイト) との XOR を取る
		/// </summary>
		/// <param name="data">暗号化するデータ(これ自体が書き換わります)</param>
		/// <param name="n">不明</param>
		private void AddRoundKey(byte[] data, int n)
		{
			for (int i = 0; i < NBb; i += 4) Exor(data, i, mRoundKey, i + NBb * n);// data[i] ^= mRoundKey[i+NB*n];
		}

		private byte[] SubWord(byte[] inData)
		{
			inData[0] = Sbox[inData[0] & 0xff];
			inData[1] = Sbox[inData[1] & 0xff];
			inData[2] = Sbox[inData[2] & 0xff];
			inData[3] = Sbox[inData[3] & 0xff];
			return inData;
		}

		private byte[] RotWord(byte[] inData)
		{
			byte x = inData[0];
			inData[0] = inData[1];
			inData[1] = inData[2];
			inData[2] = inData[3];
			inData[3] = x;
			return inData;
		}

		private void Exor(byte[] w, int pos1, int pos2, byte[] temp)
		{
			w[pos1 + 0] = (byte)(w[pos2 + 0] ^ temp[0]);
			w[pos1 + 1] = (byte)(w[pos2 + 1] ^ temp[1]);
			w[pos1 + 2] = (byte)(w[pos2 + 2] ^ temp[2]);
			w[pos1 + 3] = (byte)(w[pos2 + 3] ^ temp[3]);
		}


		/// <summary>
		/// ラウンド鍵の準備
		/// </summary>
		/// <param name="key">元となるキー</param>
		/// <returns>ラウンド鍵</returns>
		private ReturnObject<byte[]> KeyExpansion(byte[] key)
		{

			if (key.Length != mKeyLengthByte) return new ReturnObject<byte[]>(null, "キーの長さは" + mKeyLengthByte + "にする必要があります．[入力:" + key.Length + "]");

			byte[] Rcon = { 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36 };
			byte[] temp = new byte[4];

			byte[] roundKey = new byte[NBb * (mRoundNum + 1)];
			Array.Copy(key, roundKey, mKeyLengthByte);
			for (int i = mKeyLengthByte; i < roundKey.Length; i += 4)
			{
				Array.Copy(roundKey, i - 4, temp, 0, 4);
				if (i % mKeyLengthByte == 0)
				{
					temp = SubWord(RotWord(temp));
					temp[0] = (byte)(temp[0] ^ Rcon[i / mKeyLengthByte - 1]);
				}
				else if (mKeyLengthByte > 6 * 4 && i % mKeyLengthByte == 4 * 4) temp = SubWord(temp);
				Exor(roundKey, i, i - mKeyLengthByte, temp);// mRoundKey[i] = mRoundKey[i-mKeyLengthByte] ^ temp;
			}
			return new ReturnObject<byte[]>(roundKey);
		}
	}
}
