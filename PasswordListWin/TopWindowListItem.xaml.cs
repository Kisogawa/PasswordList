using System.Windows;
using System.Windows.Controls;

namespace PasswordListWin
{
	/// <summary>
	/// TopWindowListItem.xaml の相互作用ロジック
	/// </summary>
	public partial class TopWindowListItem : UserControl
	{
		/// <summary>
		/// パスワード情報
		/// </summary>
		public PasswordItem PasswordItem { get; private set; }

		/// <summary>
		/// アクセス情報
		/// </summary>
		private WebRequest WebRequest;

		/// <summary>
		/// このオブジェクトを開いているウィンドウ(なければnull)
		/// </summary>
		private Window Window;


		public TopWindowListItem(WebRequest webRequest, PasswordItem passwordItem)
		{
			PasswordItem = passwordItem;
			WebRequest = webRequest;

			InitializeComponent();

			Title.Text = (passwordItem.Name ?? "無名");

			// ダブルクリックイベント
			MouseDoubleClick += (sender, e) => ClickDtailEventFunc();

			// 詳細表示
			MenuItem DtailMenuItem = new MenuItem() { Header = "詳細表示", Style = (Style)FindResource("MenuItemDarkStyle") };
			DtailMenuItem.Click += (sender, e) => ClickDtailEventFunc();
			// 編集
			MenuItem ModificationMenuItem = new MenuItem() { Header = "編集", Style = (Style)FindResource("MenuItemDarkStyle") };
			ModificationMenuItem.Click += (sender, e) => ClickModificationEventFunc();
			// 削除
			MenuItem DeleteMenuItem = new MenuItem() { Header = "削除", Style = (Style)FindResource("MenuItemDarkStyle") };
			DeleteMenuItem.Click += (sender, e) => DeleteEventFunc();

			// コンテキストメニュー追加


			ContextMenu = new ContextMenu();
			ContextMenu.Style = (Style)FindResource("ContextMenuDarkStyle");
			ContextMenu.Items.Add(new MenuItem() { Header = passwordItem.ToString(), IsEnabled = false, Style = (Style)FindResource("MenuItemDarkStyle") });
			ContextMenu.Items.Add(new Separator());
			ContextMenu.Items.Add(DtailMenuItem);
			ContextMenu.Items.Add(ModificationMenuItem);
			ContextMenu.Items.Add(DeleteMenuItem);

			//Unloaded += (sender, e) => 
		}

		/// <summary>
		/// 詳細情報の表示
		/// </summary>
		private void ClickDtailEventFunc()
		{
			if (Window is DetailWindow)
			{
				// インスタンスが同じ場合はアクティブにする
				Window.Activate();
				return;
			}
			else
			{
				// 違うインスタンスの場合は閉じる
				Window?.Close();
			}

			Window = new DetailWindow(WebRequest, PasswordItem);
			Window.Owner = Application.Current.MainWindow;
			Window.Closed += (sender, e) => Window = null;
			Window.Show();
			Window.Activate();
		}

		/// <summary>
		/// 詳細情報の表示
		/// </summary>
		public void ShowDtailWindow() => ClickDtailEventFunc();


		/// <summary>
		/// 編集の表示
		/// </summary>
		private void ClickModificationEventFunc()
		{
			if (Window is ModificationWindow)
			{
				// インスタンスが同じ場合はアクティブにする
				Window.Activate();
				return;
			}
			else
			{
				// 違うインスタンスの場合は閉じる
				Window?.Close();
			}

			Window = new ModificationWindow(WebRequest, PasswordItem);
			Window.Owner = Application.Current.MainWindow;
			Window.Closed += (sender, e) => Window = null;
			Window.Show();
			Window.Activate();
		}

		/// <summary>
		/// 編集の表示
		/// </summary>
		public void ShowModificationWindow() => ClickModificationEventFunc();

		/// <summary>
		/// 削除ボタンイベント
		/// </summary>
		private void DeleteEventFunc()
		{
			// 確認ダイアログ表示
			MessageBox_DarkStyle messageBox_DarkStyle = new MessageBox_DarkStyle(Application.Current.MainWindow, "本当に削除してもよろしいですか？[" + PasswordItem.ToString() + "]", "確認", System.Drawing.SystemIcons.Warning, WindowStartupLocation.CenterOwner, true);
			// ダイアログの表示とキャンセル処理
			if (!messageBox_DarkStyle.ShowDialog()) return;

			// 進捗ダイアログ表示
			ProgressBoxDarkStyle progressBoxDarkStyle = new ProgressBoxDarkStyle(PasswordItem.ToString() + "の削除",
				() => { return WebRequest.Remove(PasswordItem); },// 鯖から情報を取得する
				(returnObjectValue) =>
				{
					// トップリストから自身を削除する
					TopWindow topWindow = Application.Current.MainWindow as TopWindow;
					if (topWindow == null) return;
					if (returnObjectValue.State == false)
					{
						// 失敗している場合は，エラー表示
						new MessageBox_DarkStyle(topWindow, returnObjectValue.Comment, "エラー", System.Drawing.SystemIcons.Error, WindowStartupLocation.CenterOwner).ShowDialog();
						return;
					}
					// 自身の削除
					topWindow.ListBox_Items.Items.Remove(this);
					CloseWindow();
				}
				);
			progressBoxDarkStyle.Owner = Application.Current.MainWindow;
			progressBoxDarkStyle.Show();
		}

		/// <summary>
		/// 開いているウィンドウを閉じる(ウィンドウが開いていなければ何も起きない)
		/// </summary>
		public void CloseWindow() => Window?.Close();
	}
}
