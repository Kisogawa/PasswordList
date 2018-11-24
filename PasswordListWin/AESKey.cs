using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PasswordListWin
{
	public class AESKey
	{
		/// <summary>
		/// 有効な日
		/// </summary>
		[JsonIgnore]
		public DateTime? KeyDate { get; set; } = null;

		/// <summary>
		/// キー
		/// </summary>
		[JsonProperty("Key64")]
		public string Key64 { get; set; } = null;

		/// <summary>
		/// 有効な日の文字列版(yyyyMMdd)
		/// </summary>
		[JsonProperty("KeyDate")]
		public string KeyDateWithString
		{
			get
			{
				if (KeyDate == null) return null;
				else
				{
					DateTime dateTime = (DateTime)KeyDate;
					return dateTime.ToString("yyyyMMdd");
				}
			}
			set
			{
				if (value == null) KeyDate = null;
				else
				{
					try
					{
						DateTime dt2 = DateTime.ParseExact(value, "yyyyMMdd", System.Globalization.DateTimeFormatInfo.InvariantInfo);
						KeyDate = dt2;
					}
					catch (FormatException)
					{
						KeyDate = null;
					}
				}
			}
		}
	}
}
