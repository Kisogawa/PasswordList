using System;
using System.IO;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;

namespace PasswordListWin
{
	/// <summary>
	/// TopWindow.xaml の相互作用ロジック
	/// </summary>
	public partial class TopWindow : Window
	{
		/// <summary>
		/// 接続先情報
		/// </summary>
		private ConnectionData ConnectionData;

		/// <summary>
		/// 現在選択している接続情報クラス
		/// </summary>
		private ServerInfo CurrantServerInfo;

		/// <summary>
		/// 一覧のロードタスク
		/// </summary>
		private Task Task_LoadAccess;

		/// <summary>
		/// 新しいアイテム
		/// </summary>
		private RegisterWindow NewItemWindow;

		public TopWindow()
		{
			InitializeComponent();

			try
			{
				using (StreamReader reader = new StreamReader("ConnectionData.json", Encoding.UTF8))
				{
					// 接続先情報を格納したファイルの文字を取得する
					string connectionDataStr = reader.ReadToEnd();
					// 接続先情報を取得する
					ConnectionData = Parser.Deserialize<ConnectionData>(connectionDataStr);
				}
			}
			catch (System.IO.FileNotFoundException e)
			{

			}
			catch (System.IO.DirectoryNotFoundException e)
			{

			}
			catch (System.NotSupportedException e)
			{

			}

			// 読み込みに失敗した場合は，規定値を設定
			if (ConnectionData == null)
			{
				ConnectionData = new ConnectionData();
				ConnectionData.ServerInfos.Add(new ServerInfo("localhost", 8080, "<<暗号キーを入力>>"));
				ConnectionData.ServerInfos.Add(new ServerInfo("127.0.0.1", 8080, "<<暗号キーを入力>>"));
			}

			// ファイルへ書き出す
			try
			{
				using (StreamWriter writer = new StreamWriter("ConnectionData.json", false, Encoding.UTF8))
				{
					// Jsonへパース
					string connectionDataStr = Parser.Serialize(ConnectionData);

					// 書き込み
					writer.WriteLine(connectionDataStr);
				}
			}
			catch (System.UnauthorizedAccessException e)
			{

			}
			catch (System.IO.DirectoryNotFoundException e)
			{

			}
			catch (System.IO.PathTooLongException e)
			{

			}
			catch (System.IO.IOException e)
			{

			}
			catch (System.Security.SecurityException e)
			{

			}

			// コンボボックスの選択が変更されたときのイベントを設定
			ComboBox_ConnectList.SelectionChanged += (sender, e) =>
			{
				// リストボックスを空にする
				foreach (var item in ListBox_Items.Items) (item as TopWindowListItem)?.CloseWindow();
				ListBox_Items.Items.Clear();
				// 全新規登録ウィンドウを閉じる
				NewItemWindow?.Close();
				ServerInfo serverInfo = ComboBox_ConnectList.SelectedValue as ServerInfo;
				if (serverInfo == null)
				{
					CurrantServerInfo = null;
					// タイトルを変更する
					Title = "一覧[接続無し]";
					return;
				}
				// 新たなWebリクエスト生成
				CurrantServerInfo = serverInfo;
				// タイトルを変更する
				Title = "一覧[" + serverInfo.ToString() + "]";

				// 取得
				ShowList();
			};

			// 新規登録ボタンが押されたときのイベント
			Button_NewItem.Click += (sender, e) =>
			{
				if (NewItemWindow != null)
				{
					NewItemWindow.Activate();
					return;
				}
				if (CurrantServerInfo == null) return;
				// 新規生成ウィンドウ表示
				NewItemWindow = new RegisterWindow(CurrantServerInfo.WebRequest);
				NewItemWindow.Owner = this;
				NewItemWindow.Closed += (sender2, e2) => NewItemWindow = null;
				NewItemWindow.Show();
			};


			// コンボボックスに表示
			foreach (var item in ConnectionData.ServerInfos)
			{
				ComboBox_ConnectList.Items.Add(item);
			}
			// 最初の要素を選択する
			if (ComboBox_ConnectList.Items.Count > 0) ComboBox_ConnectList.SelectedIndex = 0;
		}

		/// <summary>
		/// リストを表示する
		/// </summary>
		private void ShowList()
		{
			if (CurrantServerInfo == null) return;

			// ロード画面を表示する
			Loading.Visibility = Visibility.Visible;
			// アイテム削除
			foreach (var item in ListBox_Items.Items) (item as TopWindowListItem)?.CloseWindow();
			ListBox_Items.Items.Clear();
			// 現在のオブジェクトを取得
			Task Task_Access = null;
			// 非同期処理を行う
			Task_LoadAccess = new Task(() =>
			{
				// 鯖から情報を取得する
				ReturnObject<ResponseJson> responseJson = CurrantServerInfo.WebRequest.List();

				// 取得した情報を表示する
				Dispatcher.Invoke(new Action(() =>
				{
					// オブジェクトが違う場合は，更新操作を行わない
					if (Task_Access != Task_LoadAccess) return;

					// ロード画面を終了する
					Loading.Visibility = Visibility.Collapsed;

					if (responseJson.State == false)
					{
						// エラーを表示
						new MessageBox_DarkStyle(this, "一覧取得に失敗しました[" + responseJson.Comment + "]", "エラー", System.Drawing.SystemIcons.Error, WindowStartupLocation.CenterOwner).ShowDialog();
						return;
					}

					foreach (var item in ListBox_Items.Items) (item as TopWindowListItem)?.CloseWindow();
					ListBox_Items.Items.Clear();
					// 並び替えを行う
					Array.Sort(responseJson.Value.PasswordItems, (arg1, arg2) =>
					{
						return arg1.Name.CompareTo(arg2.Name);
					});


					// 取得した情報を表示する
					foreach (var item in responseJson.Value.PasswordItems)
					{
						TopWindowListItem topWindowListItem = new TopWindowListItem(CurrantServerInfo.WebRequest, item);
						ListBox_Items.Items.Add(topWindowListItem);
					}
				}));
			});
			Task_Access = Task_LoadAccess;
			Task_Access.Start();
		}

		/// <summary>
		/// 再取得を行う
		/// </summary>
		private void Update() => ShowList();

		private void Button_Update_Click(object sender, RoutedEventArgs e)
		{
			Update();
		}
	}
}