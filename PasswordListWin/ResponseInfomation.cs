using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;
using System.Threading.Tasks;

namespace PasswordListWin
{
	public class ResponseInfomation
	{

		private string Result_ = "";
		/// <summary>
		/// 結果情報
		/// </summary>
		[JsonProperty("Result")]
		public string Result
		{
			get { return Result_; }
			set
			{
				if (value == null) Result_ = string.Empty;
				else Result_ = value;
			}
		}

		private string Comment_ = "";
		/// <summary>
		/// コメント
		/// </summary>
		[JsonProperty("Comment")]
		public string Comment
		{
			get { return Comment_; }
			set
			{
				if (value == null) Comment_ = string.Empty;
				else Comment_ = value;
			}
		}
	}
}
