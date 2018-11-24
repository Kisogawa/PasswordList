using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace PasswordListWin
{
	/// <summary>
	/// ModificationWindow.xaml の相互作用ロジック
	/// </summary>
	public partial class ModificationWindow : Window
	{
		// バックグラウンド操作
		private Task BackgroundTask;
		private PasswordItem PasswordItem;
		private WebRequest WebRequest;

		public ModificationWindow(WebRequest webRequest, PasswordItem passwordItem)
		{
			InitializeComponent();

			// 各種イベント設定
			Closed += (sender, e) => Owner.Activate();
			Button_Return.Click += (sender, e) => Close();
			WebRequest = webRequest ?? throw new ArgumentNullException("WebRequestはnullに出来ません．");
			if (passwordItem == null) throw new ArgumentNullException("PasswordItemはnullに出来ません．");
			if (passwordItem.ItemID == 0) throw new ArgumentException("ItemIDは必須項目です");
			PasswordItem = passwordItem;

			Button_AddNewItem.Click += (sender, e) =>
			{
				// 名前は必ず入力する
				if (TextBox_Name.Text == string.Empty)
				{
					new MessageBox_DarkStyle(this, "名前は必ず入力してください．", "エラー", System.Drawing.SystemIcons.Exclamation, WindowStartupLocation.CenterOwner).ShowDialog();
					return;
				}
				// バックグラウンド操作実行中
				if (BackgroundTask != null && BackgroundTask.Status == TaskStatus.Running)
				{
					new MessageBox_DarkStyle(this, "バックグラウンド操作が実行中です．あとで実行してください．", "エラー", System.Drawing.SystemIcons.Exclamation, WindowStartupLocation.CenterOwner).ShowDialog();
					return;
				}
				// 登録操作を行う
				Loading.Visibility = Visibility.Visible;
				// 登録用オブジェクトの準備
				PasswordItem item = new PasswordItem()
				{
					ItemID = PasswordItem.ItemID,   // 必須
					Name = TextBox_Name.Text,
					UserID = TextBox_ID.Text,
					Password = TextBox_Password.Text,
					MailAddress = TextBox_Mail.Text,
					Comment = new TextRange(TextBox_Comment.Document.ContentStart, TextBox_Comment.Document.ContentEnd).Text
				};
				BackgroundTask = new Task(() =>
				{
					// 変更操作を実行する
					ReturnObject<ResponseJson> responseJson = webRequest.Modification(item);

					// 取得した情報を表示する
					Dispatcher.Invoke(new Action(() =>
					{
						Loading.Visibility = Visibility.Collapsed;
						// 変更に失敗
						if (responseJson.State == false)
						{
							new MessageBox_DarkStyle(this, responseJson.Comment, "エラー", System.Drawing.SystemIcons.Error, WindowStartupLocation.CenterOwner).ShowDialog();
							return;
						}

						// 閲覧モードでダイアログを起動し，このダイアログを閉じる
						Close();
						// トップウィンドウ取得
						TopWindow topWindow = Owner as TopWindow;
						// 取得出来なければ詳細情報ウィンドウは表示しない
						if (topWindow == null)
						{
							new MessageBox_DarkStyle(this, "登録した情報を表示出来ません．[トップウィンドウが見つかりませんでした．]", "エラー", System.Drawing.SystemIcons.Error, WindowStartupLocation.CenterOwner).ShowDialog();
							return;
						}

						// 現在のリストを取得し削除する
						TopWindowListItem removeItem = null;
						foreach (var item2 in topWindow.ListBox_Items.Items)
						{
							TopWindowListItem topWindowListItem2 = item2 as TopWindowListItem;
							if (topWindowListItem2 == null) continue;
							if (topWindowListItem2.PasswordItem.ItemID == responseJson.Value.PasswordItems[0].ItemID) removeItem = topWindowListItem2;
						}
						if (removeItem != null)
						{
							topWindow.ListBox_Items.Items.Remove(removeItem);
							removeItem.CloseWindow();
						}

						// リストを新規登録
						TopWindowListItem topWindowListItem = new TopWindowListItem(webRequest, responseJson.Value.PasswordItems[0]);
						// 挿入位置を計算する
						int count = 0;
						foreach (var item2 in topWindow.ListBox_Items.Items)
						{
							TopWindowListItem topWindowListItem2 = item2 as TopWindowListItem;
							if (topWindowListItem2 == null)
							{
								count++;
								continue;
							}
							if (topWindowListItem2.PasswordItem.Name.CompareTo(topWindowListItem.PasswordItem.Name) > 0) break;
							count++;
						}
						topWindow.ListBox_Items.Items.Insert(count, topWindowListItem);
						topWindowListItem.ShowDtailWindow();
					}));
				});
				BackgroundTask.Start();
			};

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

			PasswordItem = passwordItem;
			// タイトルを変更する
			Title = PasswordItem.ToString();
			TextBox_Name.Text = PasswordItem.Name;
			TextBox_ID.Text = PasswordItem.UserID;
			TextBox_Password.Text = PasswordItem.Password;
			TextBox_Mail.Text = PasswordItem.MailAddress;
			new TextRange(TextBox_Comment.Document.ContentStart, TextBox_Comment.Document.ContentEnd).Text = PasswordItem.Comment;
		}
	}
}
