using Newtonsoft.Json;

namespace PasswordListWin
{
	/// <summary>
	/// 接続先情報
	/// </summary>
	public class ServerInfo
	{
		/// <summary>
		/// デフォルトコンストラクタ
		/// </summary>
		public ServerInfo()
		{
			WebRequest = new WebRequest(this);
		}

		/// <summary>
		/// デフォルトコンストラクタ
		/// </summary>
		public ServerInfo(string address, ushort portNo, string key64)
		{
			Address = address;
			PortNo = portNo;
			Key64 = key64;

			WebRequest = new WebRequest(this);
		}


		private string Name_ = string.Empty;
		/// <summary>
		/// 接続先名アドレス
		/// </summary>
		[JsonProperty("Name")]
		public string Name
		{
			get { return Name_; }
			set
			{
				if (value == null) Name_ = string.Empty;
				else Name_ = value;
			}
		}

		private string Address_ = string.Empty;
		/// <summary>
		/// 接続先アドレス
		/// </summary>
		[JsonProperty("Address")]
		public string Address
		{
			get { return Address_; }
			set
			{
				if (value == null) Address_ = string.Empty;
				else Address_ = value;
			}
		}
		/// <summary>
		/// 接続先ポート番号
		/// </summary>
		[JsonProperty("PortNo")]
		public ushort PortNo { get; set; } = 80;


		private string Key64_ = string.Empty;
		/// <summary>
		/// 暗号キー
		/// </summary>
		[JsonProperty("Key64")]
		public string Key64
		{
			get { return Key64_; }
			set
			{
				if (value == null) Key64_ = string.Empty;
				else Key64_ = value;
			}
		}

		[JsonIgnore]
		public readonly WebRequest WebRequest;

		override public string ToString()
		{
			if (Name != string.Empty) return Name;
			else return Address + ":" + PortNo;
		}

	}
}
