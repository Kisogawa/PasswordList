using System;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Documents;

namespace PasswordListWin
{
	/// <summary>
	/// ItemWindow.xaml の相互作用ロジック
	/// </summary>
	public partial class DetailWindow : Window
	{
		// バックグラウンド操作
		private Task BackgroundTask;
		private PasswordItem PasswordItem;
		private WebRequest WebRequest;

		/// <summary>
		/// 初期データを使用して詳細を表示する(ItemIDのみ必須)
		/// </summary>
		/// <param name="webRequest"></param>
		/// <param name="passwordItem"></param>
		public DetailWindow(WebRequest webRequest, PasswordItem passwordItem)
		{
			InitializeComponent();

			// ボタンイベントの設定
			Button_Reload.Click += (sender, e) => GetData();
			Button_Name.Click += (sender, e) => Clipboard_SetText(TextBox_Name.Text);
			Button_ID.Click += (sender, e) => Clipboard_SetText(TextBox_ID.Text);
			Button_Password.Click += (sender, e) => Clipboard_SetText(TextBox_Password.Text);
			Button_Mail.Click += (sender, e) => Clipboard_SetText(TextBox_Mail.Text);
			Button_Register.Click += (sender, e) => Clipboard_SetText(TextBox_Register.Text);
			Button_Update.Click += (sender, e) => Clipboard_SetText(TextBox_Update.Text);
			Button_Comment.Click += (sender, e) => Clipboard_SetText(new TextRange(TextBox_Comment.Document.ContentStart, TextBox_Comment.Document.ContentEnd).Text);
			Button_Return.Click += (sender, e) => Close();
			WebRequest = webRequest ?? throw new ArgumentNullException("WebRequestはnullに出来ません．");
			if (passwordItem == null) throw new ArgumentNullException("PasswordItemはnullに出来ません．");
			if (passwordItem.ItemID == 0) throw new ArgumentException("ItemIDは必須項目です");
			PasswordItem = passwordItem;
			Closed += (sender, e) => Owner?.Activate();

			// 再取得必要フラグ
			bool getFlag = false;

			// 表示に必要な情報を確認する
			if (passwordItem.Name == null) getFlag = true;
			if (passwordItem.UserID == null) getFlag = true;
			if (passwordItem.Password == null) getFlag = true;
			if (passwordItem.MailAddress == null) getFlag = true;
			if (passwordItem.Comment == null) getFlag = true;
			if (passwordItem.RegisterDateWithString == null) getFlag = true;
			if (passwordItem.UpdateDateWithString == null) getFlag = true;

			// 再取得フラグがある場合は再取得処理を行い初期化を後回しにする
			if (getFlag)
			{
				// 再取得処理
				GetData();
				return;
			}

			SetItemData(passwordItem);
		}

		// 再取得処理
		private void GetData()
		{
			// バックグラウンド操作実行中
			if (BackgroundTask != null && BackgroundTask.Status == TaskStatus.Running)
			{
				new MessageBox_DarkStyle(this, "バックグラウンド操作が実行中です．あとで実行してください．", "エラー", System.Drawing.SystemIcons.Exclamation, WindowStartupLocation.CenterOwner).ShowDialog();
				return;
			}
			// 詳細取得操作を行う
			Loading.Visibility = Visibility.Visible;
			BackgroundTask = new Task(() =>
			{
				// 詳細取得操作を実行する
				ReturnObject<ResponseJson> responseJson = WebRequest.Show(PasswordItem);

				// 取得した情報を表示する
				Dispatcher.Invoke(new Action(() =>
				{
					Loading.Visibility = Visibility.Collapsed;
					if (responseJson.State == false)
					{
						new MessageBox_DarkStyle(this, responseJson.Comment, "エラー", System.Drawing.SystemIcons.Error, WindowStartupLocation.CenterOwner).ShowDialog();
						return;
					}

					// 要素の設定
					SetItemData(responseJson.Value.PasswordItems[0]);
				}));
			});
			BackgroundTask.Start();
		}

		/// <summary>
		/// アイテムの情報を設定する
		/// </summary>
		private void SetItemData(PasswordItem passwordItem)
		{
			if (passwordItem == null) return;


			// 表示に必要な情報を確認する(なければ更新しない)
			if (passwordItem.ItemID == 0) return;
			if (passwordItem.Name == null) return;
			if (passwordItem.UserID == null) return;
			if (passwordItem.Password == null) return;
			if (passwordItem.MailAddress == null) return;
			if (passwordItem.Comment == null) return;
			if (passwordItem.RegisterDateWithString == null) return;
			if (passwordItem.UpdateDateWithString == null) return;

			PasswordItem = passwordItem;
			// タイトルを変更する
			Title = PasswordItem.ToString();
			TextBox_Name.Text = PasswordItem.Name;
			TextBox_ID.Text = PasswordItem.UserID;
			TextBox_Password.Text = PasswordItem.Password;
			TextBox_Mail.Text = PasswordItem.MailAddress;
			TextBox_Register.Text = PasswordItem.RegisterDate?.ToLongDateString() + " " + PasswordItem.RegisterDate?.ToLongTimeString();
			TextBox_Update.Text = PasswordItem.UpdateDate?.ToLongDateString() + " " + PasswordItem.UpdateDate?.ToLongTimeString();
			new TextRange(TextBox_Comment.Document.ContentStart, TextBox_Comment.Document.ContentEnd).Text = PasswordItem.Comment;
		}

		// クリップボード
		private void Clipboard_SetText(string str)
		{
			if (str == null) return;
			try
			{
				Clipboard.SetDataObject(str, false);
			}
			catch (System.Runtime.InteropServices.COMException)
			{
			}
		}
	}
}
