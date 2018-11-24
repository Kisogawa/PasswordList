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
	/// 接続先情報
	/// </summary>
	class ConnectionData
	{
		/// <summary>
		/// 接続先アドレス
		/// </summary>
		[JsonProperty("ServerInfo")]
		public List<ServerInfo> ServerInfos = new List<ServerInfo>();
	}
}
