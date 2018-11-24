using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace PasswordListWin
{
	/// <summary>
	/// Json返却データクラス
	/// </summary>
	public class ResponseJson
	{
		/// <summary>
		/// 応答データクラス
		/// </summary>
		[JsonProperty("responseInfomation")]
		public ResponseInfomation ResponseInfomation { get; set; } = new ResponseInfomation();

		private PasswordItem[] PasswordItems_ = new PasswordItem[0];
		/// <summary>
		/// パスワードデータ格納配列
		/// </summary>
		[JsonProperty("passwordItems")]
		public PasswordItem[] PasswordItems
		{
			get { return PasswordItems_; }
			set
			{
				if (value == null) PasswordItems_ = new PasswordItem[0];
				else PasswordItems_ = value;
			}
		}

		private AESKey[] aeskeys_ = new AESKey[0];
		/// <summary>
		/// AESキーデータ格納配列
		/// </summary>
		[JsonProperty("aeskeys")]
		public AESKey[] aeskeys
		{
			get { return aeskeys_; }
			set
			{
				if (value == null) aeskeys_ = new AESKey[0];
				else aeskeys_ = value;
			}
		}
	}
}
