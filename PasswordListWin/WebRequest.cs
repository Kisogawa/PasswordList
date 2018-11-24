using System;
using System.Text;

namespace PasswordListWin
{
	/// <summary>
	/// リクエストを送るクラス
	/// </summary>
	public class WebRequest
	{
		// 接続先情報
		private readonly ServerInfo ServerInfo;

		/// <summary>
		/// データを取得するURL先頭部分
		/// </summary>
		private string RequestUri
		{
			get => "http://" + ServerInfo.Address + ":" + ServerInfo.PortNo + "/";
		}


		/// <summary>
		/// 接続情報の初期化
		/// </summary>
		/// <param name="serverInfo"></param>
		public WebRequest(ServerInfo serverInfo)
		{
			ServerInfo = serverInfo ?? throw new ArgumentNullException("URL名にnullを指定できません．");
		}

		/// <summary>
		/// 確認
		/// </summary>
		/// <param name="responseJson"></param>
		private ReturnObject Check(ResponseJson responseJson)
		{
			if (responseJson == null) return new ReturnObject("nullです．");

			if (responseJson.ResponseInfomation.Result == "Failed") return new ReturnObject("応答がありましたが操作に失敗してます．[詳細:" + responseJson.ResponseInfomation.Comment + "]");
			if (responseJson.ResponseInfomation.Result != "Success") return new ReturnObject("応答がありましたが不明なコードです．[コード:" + responseJson.ResponseInfomation.Result + "][詳細:" + responseJson.ResponseInfomation.Comment + "]");

			return new ReturnObject();
		}


		/// <summary>
		/// 登録操作を行う
		/// </summary>
		/// <param name="item">登録する情報</param>
		/// <param name="key64">暗号キー</param>
		/// <returns>null失敗</returns>
		public ReturnObject<ResponseJson> Register(PasswordItem item)
		{
			lock (this)
			{
				string url = RequestUri + "PasswordList/Register";

				System.Net.WebClient wc = new System.Net.WebClient();
				wc.Encoding = Encoding.UTF8;
				//NameValueCollectionの作成
				System.Collections.Specialized.NameValueCollection ps = new System.Collections.Specialized.NameValueCollection();
				//送信するデータ（フィールド名と値の組み合わせ）を追加
				if (item.Name == null) return new ReturnObject<ResponseJson>(null, "Name要素をnullに出来ません");
				else
				{
					ReturnObject<string> n = Angou.Encode(item.Name, ServerInfo.Key64);
					if (n.State) ps.Add("Name", n.Value);
					else return new ReturnObject<ResponseJson>(null, n);
				}
				if (item.UserID == null) return new ReturnObject<ResponseJson>(null, "UserID要素をnullに出来ません");
				else
				{
					ReturnObject<string> n = Angou.Encode(item.UserID, ServerInfo.Key64);
					if (n.State) ps.Add("UserID", n.Value);
					else return new ReturnObject<ResponseJson>(null, n);
				}
				if (item.Password == null) return new ReturnObject<ResponseJson>(null, "Password要素をnullに出来ません");
				else
				{
					ReturnObject<string> n = Angou.Encode(item.Password, ServerInfo.Key64);
					if (n.State) ps.Add("Password", n.Value);
					else return new ReturnObject<ResponseJson>(null, n);
				}
				if (item.Comment == null) return new ReturnObject<ResponseJson>(null, "Comment要素をnullに出来ません");
				else
				{
					ReturnObject<string> n = Angou.Encode(item.Comment, ServerInfo.Key64);
					if (n.State) ps.Add("Comment", n.Value);
					else return new ReturnObject<ResponseJson>(null, n);
				}
				if (item.MailAddress == null) return new ReturnObject<ResponseJson>(null, "MailAddress要素をnullに出来ません");
				else
				{
					ReturnObject<string> n = Angou.Encode(item.MailAddress, ServerInfo.Key64);
					if (n.State) ps.Add("MailAddress", n.Value);
					else return new ReturnObject<ResponseJson>(null, n);
				}
				try
				{
					//データを送信し、また受信する
					byte[] resData = wc.UploadValues(url, "POST", ps);

					//受信したデータを表示する
					string resText = Encoding.UTF8.GetString(resData);

					// 復号を行う
					ReturnObject<string> json = Angou.Decode(resText, ServerInfo.Key64);
					// 復号に失敗
					if (json == null) return new ReturnObject<ResponseJson>(null, json);
					ResponseJson responseJson = Parser.Deserialize<ResponseJson>(json.Value);

					// 確認
					ReturnObject req = Check(responseJson);
					if (req.State == false) return new ReturnObject<ResponseJson>(null, req);
					// 特有確認
					if (responseJson.PasswordItems.Length != 1) return new ReturnObject<ResponseJson>(null, "登録に失敗している可能性があります．応答データが壊れています．");

					return new ReturnObject<ResponseJson>(responseJson);
				}
				catch (System.Net.WebException e)
				{
					return new ReturnObject<ResponseJson>(null, e);
				}
				finally
				{
					wc.Dispose();
				}
			}
		}

		/// <summary>
		/// 登録IDと名前のリスト取得処理
		/// </summary>
		/// <param name="key64">暗号キー</param>
		/// <returns>null失敗</returns>
		public ReturnObject<ResponseJson> List()
		{
			lock (this)
			{
				// URL設定
				string url = RequestUri + "PasswordList/List";

				System.Net.WebClient wc = new System.Net.WebClient();
				wc.Encoding = Encoding.UTF8;
				//NameValueCollectionの作成
				System.Collections.Specialized.NameValueCollection ps = new System.Collections.Specialized.NameValueCollection();
				//データを送信し、また受信する
				try
				{

					byte[] resData = wc.UploadValues(url, "POST", ps);

					//受信したデータを表示する
					string resText = Encoding.UTF8.GetString(resData);

					// 復号を行う
					ReturnObject<string> json = Angou.Decode(resText, ServerInfo.Key64);
					// 復号に失敗
					if (json.State == false) return new ReturnObject<ResponseJson>(null, json);
					ResponseJson responseJson = Parser.Deserialize<ResponseJson>(json.Value);

					// 確認
					ReturnObject req = Check(responseJson);
					if (req.State == false) return new ReturnObject<ResponseJson>(null, req);

					return new ReturnObject<ResponseJson>(responseJson);
				}
				catch (System.Net.WebException e)
				{
					return new ReturnObject<ResponseJson>(null, e);
				}
				finally
				{
					wc.Dispose();
				}
			}
		}

		/// <summary>
		/// 詳細情報を取得する
		/// </summary>
		/// <param name="item">取得する詳細情報(ItemIDのみ使用)</param>
		/// <param name="key64">暗号キー</param>
		/// <returns>null失敗</returns>
		public ReturnObject<ResponseJson> Show(PasswordItem item)
		{
			lock (this)
			{
				string url = RequestUri + "PasswordList/Show";

				System.Net.WebClient wc = new System.Net.WebClient();
				wc.Encoding = Encoding.UTF8;
				//NameValueCollectionの作成
				System.Collections.Specialized.NameValueCollection ps = new System.Collections.Specialized.NameValueCollection();
				if (item.ItemID != 0)
				{
					ReturnObject<string> n = Angou.Encode(item.ItemID.ToString(), ServerInfo.Key64);
					if (n.State) ps.Add("ItemID", n.Value);
					else return new ReturnObject<ResponseJson>(null, n);
				}
				else return new ReturnObject<ResponseJson>(null, "ItemID要素を0に出来ません");

				try
				{
					//データを送信し、また受信する
					byte[] resData = wc.UploadValues(url, "POST", ps);
					string resText = Encoding.UTF8.GetString(resData);

					// 復号を行う
					ReturnObject<string> json = Angou.Decode(resText, ServerInfo.Key64);
					// 復号に失敗
					if (json == null) return new ReturnObject<ResponseJson>(null, json);
					ResponseJson responseJson = Parser.Deserialize<ResponseJson>(json.Value);

					// 確認
					ReturnObject req = Check(responseJson);
					if (req.State == false) return new ReturnObject<ResponseJson>(null, req);
					if (responseJson.PasswordItems.Length != 1) return new ReturnObject<ResponseJson>(null, "詳細取得に失敗している可能性があります．応答データが壊れています．");
					return new ReturnObject<ResponseJson>(responseJson);
				}
				catch (System.Net.WebException e)
				{
					return new ReturnObject<ResponseJson>(null, e);
				}
				finally
				{
					wc.Dispose();
				}
			}
		}

		/// <summary>
		/// アイテムの修正を行う
		/// </summary>
		/// <param name="item">修正後のアイテム情報</param>
		/// <returns>null失敗</returns>
		public ReturnObject<ResponseJson> Modification(PasswordItem item)
		{

			string url = RequestUri + "PasswordList/Modification";

			System.Net.WebClient wc = new System.Net.WebClient();
			wc.Encoding = Encoding.UTF8;
			//NameValueCollectionの作成
			System.Collections.Specialized.NameValueCollection ps = new System.Collections.Specialized.NameValueCollection();
			//送信するデータ（フィールド名と値の組み合わせ）を追加
			if (item.ItemID == 0) return new ReturnObject<ResponseJson>(null, "ItemIDはnullに出来ません．");
			else
			{
				ReturnObject<string> n = Angou.Encode(item.ItemID.ToString(), ServerInfo.Key64);
				if (n.State) ps.Add("ItemID", n.Value);
				else return new ReturnObject<ResponseJson>(null, n);
			}
			if(item.Name != null)
			{
				ReturnObject<string> n = Angou.Encode(item.Name, ServerInfo.Key64);
				if (n.State) ps.Add("Name", n.Value);
				else return new ReturnObject<ResponseJson>(null, n);
			}
			if(item.UserID != null)
			{
				ReturnObject<string> n = Angou.Encode(item.UserID, ServerInfo.Key64);
				if (n.State) ps.Add("UserID", n.Value);
				else return new ReturnObject<ResponseJson>(null, n);
			}
			if(item.Password != null)
			{
				ReturnObject<string> n = Angou.Encode(item.Password, ServerInfo.Key64);
				if (n.State) ps.Add("Password", n.Value);
				else return new ReturnObject<ResponseJson>(null, n);
			}
			if (item.Comment != null)
			{
				ReturnObject<string> n = Angou.Encode(item.Comment, ServerInfo.Key64);
				if (n.State) ps.Add("Comment", n.Value);
				else return new ReturnObject<ResponseJson>(null, n);
			}
			if (item.Comment != null)
			{
				ReturnObject<string> n = Angou.Encode(item.MailAddress, ServerInfo.Key64);
				if (n.State) ps.Add("MailAddress", n.Value);
				else return new ReturnObject<ResponseJson>(null, n);
			}
			try
			{
				//データを送信し、また受信する
				byte[] resData = wc.UploadValues(url, "POST", ps);

				//受信したデータを表示する
				string resText = Encoding.UTF8.GetString(resData);

				// 復号を行う
				ReturnObject<string> json = Angou.Decode(resText, ServerInfo.Key64);
				// 復号に失敗
				if (json == null) return new ReturnObject<ResponseJson>(null, json);
				ResponseJson responseJson = Parser.Deserialize<ResponseJson>(json.Value);

				// 確認
				ReturnObject req = Check(responseJson);
				if (req.State == false) return new ReturnObject<ResponseJson>(null, req);
				// 特有確認
				if (responseJson.PasswordItems.Length != 1) return new ReturnObject<ResponseJson>(null, "変更に失敗している可能性があります．応答データが壊れています．");

				return new ReturnObject<ResponseJson>(responseJson);
			}
			catch (System.Net.WebException e)
			{
				return new ReturnObject<ResponseJson>(null, e);
			}
			finally
			{
				wc.Dispose();
			}

		}

		/// <summary>
		/// アイテムを削除する
		/// </summary>
		/// <param name="item">削除する情報(ItemIDのみ使用)</param>
		/// <returns>null失敗</returns>
		public ReturnObject<ResponseJson> Remove(PasswordItem item)
		{
			lock (this)
			{
				string url = RequestUri + "PasswordList/Remove";

				System.Net.WebClient wc = new System.Net.WebClient();
				wc.Encoding = Encoding.UTF8;
				//NameValueCollectionの作成
				System.Collections.Specialized.NameValueCollection ps = new System.Collections.Specialized.NameValueCollection();
				//送信するデータ（フィールド名と値の組み合わせ）を追加
				if (item.ItemID == 0) return new ReturnObject<ResponseJson>(null, "ItemIDはnullに出来ません．");
				else
				{
					ReturnObject<string> n = Angou.Encode(item.ItemID.ToString(), ServerInfo.Key64);
					if (n.State) ps.Add("ItemID", n.Value);
					else return new ReturnObject<ResponseJson>(null, n);
				}
				try
				{
					//データを送信し、また受信する
					byte[] resData = wc.UploadValues(url, "POST", ps);

					//受信したデータを表示する
					string resText = Encoding.UTF8.GetString(resData);

					// 復号を行う
					ReturnObject<string> json = Angou.Decode(resText, ServerInfo.Key64);
					// 復号に失敗
					if (json == null) return new ReturnObject<ResponseJson>(null, json);
					ResponseJson responseJson = Parser.Deserialize<ResponseJson>(json.Value);

					// 確認
					ReturnObject req = Check(responseJson);
					if (req.State == false) return new ReturnObject<ResponseJson>(null, req);
					if (responseJson.PasswordItems.Length != 1) return new ReturnObject<ResponseJson>(null, "削除に失敗している可能性があります．応答データが壊れています．");

					return new ReturnObject<ResponseJson>(responseJson);
				}
				catch (System.Net.WebException e)
				{
					return new ReturnObject<ResponseJson>(null, e);
				}
				finally
				{
					wc.Dispose();
				}
			}
		}

		public override string ToString() => RequestUri;
	}
}
