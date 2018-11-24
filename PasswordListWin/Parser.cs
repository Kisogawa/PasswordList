using System;
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using Newtonsoft.Json.Linq;

namespace PasswordListWin
{
	/// <summary>
	/// Jsonのパースを行うクラス
	/// </summary>
	public class Parser
	{

		/// <summary>
		/// Jsonの文字列から解析を行いデータクラスに格納する(Json⇒オブジェクト)
		/// </summary>
		/// <typeparam name="T">Json解析データを格納するクラス</typeparam>
		/// <param name="jsonString">Json文字列</param>
		/// <returns>Json解析データクラス(エラーの場合はデフォルト値)</returns>
		public static T Deserialize<T>(string jsonString)
		{
			return JsonConvert.DeserializeObject<T>(jsonString);
		}

		/// <summary>
		/// 任意のオブジェクトを JSON メッセージへシリアライズします。
		/// </summary>
		public static string Serialize<T>(T graph)
		{
			return JsonConvert.SerializeObject(graph);
		}

	}
}
