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
	/// ItemWindow.xaml の相互作用ロジック
	/// </summary>
	public partial class RegisterWindow : Window
	{
		// バックグラウンド操作
		private Task BackgroundTask;

		public RegisterWindow(WebRequest webRequest)
		{
			InitializeComponent();
			Title = "新規生成";
			Closed += (sender, e) => {
				Owner.Activate();
			};
			Button_Return.Click += (sender, e) =>
			{
				// ウィンドウを閉じる
				Close();
				Owner?.Activate();
			};
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
					Name = TextBox_Name.Text,
					UserID = TextBox_ID.Text,
					Password = TextBox_Password.Text,
					MailAddress = TextBox_Mail.Text,
					Comment = new TextRange(TextBox_Comment.Document.ContentStart, TextBox_Comment.Document.ContentEnd).Text
				};
				BackgroundTask = new Task(() =>
				{
					// 登録操作を実行する
					ReturnObject<ResponseJson> responseJson = webRequest.Register(item);

					// 取得した情報を表示する
					Dispatcher.Invoke(new Action(() =>
					{
						Loading.Visibility = Visibility.Collapsed;
						// 登録に失敗
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

						// リストに追加する
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
		}
	}
}
