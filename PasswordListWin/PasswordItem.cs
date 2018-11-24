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
	/// パスワード情報エンティティ
	/// </summary>
	public class PasswordItem
	{
		// 最長の文字数
		static public readonly int MAXLENGTH_Name = 128;
		static public readonly int MAXLENGTH_UserID = 128;
		static public readonly int MAXLENGTH_Password = 128;
		static public readonly int MAXLENGTH_Comment = 512;
		static public readonly int MAXLENGTH_MailAddress = 128;


		private int ItemID_ = 0;
		/// <summary>
		/// 識別ID(0は無効値)
		/// </summary>
		[JsonProperty("ItemID")]
		public int ItemID
		{
			get
			{
				return ItemID_;
			}
			set
			{
				if (value < 0) value = 0;
				ItemID_ = value;
			}
		}

		/// <summary>
		/// 名前
		/// </summary>
		[JsonProperty("Name")]
		public string Name { get; set; } = null;

		/// <summary>
		/// 登録日
		/// </summary>
		[JsonIgnore]
		public DateTime? RegisterDate { get; set; } = null;

		/// <summary>
		/// 更新日
		/// </summary>
		[JsonIgnore]
		public DateTime? UpdateDate { get; set; } = null;

		/// <summary>
		/// ユーザーID
		/// </summary>
		[JsonProperty("UserID")]
		public string UserID { get; set; } = null;

		/// <summary>
		/// コメント
		/// </summary>
		[JsonProperty("Comment")]
		public string Comment { get; set; } = null;

		/// <summary>
		/// メールアドレス
		/// </summary>
		[JsonProperty("MailAddress")]
		public string MailAddress { get; set; } = null;

		/// <summary>
		/// パスワード
		/// </summary>
		[JsonProperty("Password")]
		public string Password { get; set; } = null;

		/// <summary>
		/// 登録日の文字列版(yyyyMMdd HHmmss)
		/// </summary>
		[JsonProperty("RegisterDateTime")]
		public string RegisterDateWithString
		{
			get
			{
				if (RegisterDate == null) return null;
				else
				{
					DateTime dateTime = (DateTime)RegisterDate;
					return dateTime.ToString("yyyyMMdd HHmmss");
				}
			}
			set
			{
				if (value == null) RegisterDate = null;
				else
				{
					try
					{
						DateTime dt2 = DateTime.ParseExact(value, "yyyyMMdd HHmmss", System.Globalization.DateTimeFormatInfo.InvariantInfo);
						RegisterDate = dt2;
					}
					catch (FormatException)
					{
						RegisterDate = null;
					}
				}
			}
		}

		/// <summary>
		/// 更新日をの文字列版(yyyyMMdd HHmmss)
		/// </summary>
		[JsonProperty("UpdateDateTime")]
		public string UpdateDateWithString
		{
			get
			{
				if (UpdateDate == null) return null;
				else
				{
					DateTime dateTime = (DateTime)UpdateDate;
					return dateTime.ToString("yyyyMMdd HHmmss");
				}
			}
			set
			{
				if (value == null) UpdateDate = null;
				else
				{
					try
					{
						DateTime dt2 = DateTime.ParseExact(value, "yyyyMMdd HHmmss", System.Globalization.DateTimeFormatInfo.InvariantInfo);
						UpdateDate = dt2;
					}
					catch (FormatException)
					{
						UpdateDate = null;
					}
				}
			}
		}


		/// <summary>
		/// 文字列化
		/// </summary>
		/// <returns></returns>
		public override string ToString()
		{
			// タイトルを返す
			return (Name ?? string.Empty) + string.Format("(#{0:0000})", ItemID);
		}

	}
}
