using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Interop;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace PasswordListWin
{
	/// <summary>
	/// MessageBox_DarkStyle.xaml の相互作用ロジック
	/// </summary>
	public partial class MessageBox_DarkStyle : Window
	{
		/// <summary>
		/// OKされた場合はtrue,それ以外はfalse
		/// </summary>
		private bool? Modorichi = null;

		private Label TextLabel;

		/// <summary>
		/// コンソトラクター(ボタン無し)
		/// </summary>
		public MessageBox_DarkStyle(Window parent, string messageBoxText, Icon icon, WindowStartupLocation place)
		{
			Init(parent, messageBoxText, "", icon, place);
			WindowStyle = WindowStyle.None;
			AllowsTransparency = true;
			grid1.Children.Remove(Grid_Button);
			grid1.RowDefinitions.Remove(RowDefinition1);
		}

		/// <summary>
		/// コンソトラクター(OK)
		/// </summary>
		public MessageBox_DarkStyle(Window parent, string messageBoxText, string caption, Icon icon, WindowStartupLocation place)
		{
			Init(parent, messageBoxText, caption, icon, place);

			//ボタンを追加する(OKボタンのみ)
			Button b = new Button();
			b.Style = (Style)FindResource("ButtonDarkStyle");
			b.Content = "OK";
			b.Width = 100;
			b.HorizontalAlignment = HorizontalAlignment.Center;
			b.FontSize = 14;
			b.FontWeight = FontWeights.Bold;
			b.Click += OK_Click;
			Grid_Button.Children.Add(b);
			FocusManager.SetFocusedElement(FocusManager.GetFocusScope(b), b);
		}

		/// <summary>
		/// コンソトラクター(YesNo)
		/// </summary>
		/// <param name="parent"></param>
		/// <param name="messageBoxText"></param>
		/// <param name="caption"></param>
		/// <param name="icon"></param>
		/// <param name="place"></param>
		/// <param name="YesNo">この引数は何を入れても一緒</param>
		public MessageBox_DarkStyle(Window parent, string messageBoxText, string caption, System.Drawing.Icon icon, WindowStartupLocation place, bool YesNo)
		{
			Init(parent, messageBoxText, caption, icon, place);

			//Gridの仕切りを追加する
			ColumnDefinition tmp = new ColumnDefinition();
			tmp.Width = new GridLength(1, GridUnitType.Star);
			ColumnDefinition tmp2 = new ColumnDefinition();
			tmp2.Width = new GridLength(1, GridUnitType.Star);
			Grid_Button.ColumnDefinitions.Add(tmp);
			Grid_Button.ColumnDefinitions.Add(tmp2);

			//ボタンを追加する(Yesボタン)
			Button yes = new Button();
			yes.Style = (Style)FindResource("ButtonDarkStyle");
			yes.Content = "YES";
			yes.Width = 100;
			yes.HorizontalAlignment = HorizontalAlignment.Center;
			yes.Margin = new Thickness(0, 0, 5, 0);
			yes.FontSize = 14;
			yes.FontWeight = FontWeights.Bold;
			yes.SetValue(Grid.ColumnProperty, 0);
			yes.Click += YES_Click;
			
			Grid_Button.Children.Add(yes);

			//ボタンを追加する(Noボタン)
			Button No = new Button();
			No.Style = (Style)FindResource("ButtonDarkStyle");
			No.Content = "NO";
			No.Width = 100;
			No.HorizontalAlignment = HorizontalAlignment.Center;
			No.Margin = new Thickness(5, 0, 0, 0);
			No.FontSize = 14;
			No.FontWeight = FontWeights.Bold;
			No.SetValue(Grid.ColumnProperty, 1);
			No.Click += NO_Click;
			Grid_Button.Children.Add(No);
			FocusManager.SetFocusedElement(FocusManager.GetFocusScope(yes), yes);
		}

		/// <summary>
		/// 初期化共通処理
		/// </summary>
		private void Init(Window parent, string messageBoxText, string caption, System.Drawing.Icon icon, WindowStartupLocation place)
		{
			InitializeComponent();
			Title = caption;
			if(parent != null && parent.IsVisible)
			{
				Owner = parent;
			}
			else
			{
				Owner = null;
			}

			//ラベルを追加し、メッセージを記入する
			TextLabel = new Label()
			{
				Content = messageBoxText,
				HorizontalAlignment = HorizontalAlignment.Center,
				VerticalAlignment = VerticalAlignment.Center,
				Foreground = (System.Windows.Media.Brush)FindResource("TextBox.Foreground"),
				FontFamily = new System.Windows.Media.FontFamily("MS UI Gothic"),
				FontSize = 18,
				Padding = new Thickness(30),
			};
			TextLabel.SetValue(Grid.ColumnProperty, 1);
			grid1.Children.Add(TextLabel);
			var ic = icon;
			image1.Source = Imaging.CreateBitmapSourceFromHIcon(ic.Handle, Int32Rect.Empty, BitmapSizeOptions.FromEmptyOptions());
			WindowStartupLocation = place;
		}

		/// <summary>
		/// OKボタンが押されたときに実行される
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void OK_Click(object sender, RoutedEventArgs e)
		{
			Modorichi = true;
			Close();
		}

		/// <summary>
		/// YESボタンが押されたときに実行される
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void YES_Click(object sender, RoutedEventArgs e)
		{
			Modorichi = true;
			Close();
		}

		/// <summary>
		/// NOボタンが押されたときに実行される
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void NO_Click(object sender, RoutedEventArgs e)
		{
			Modorichi = false;
			Close();
		}

		public new bool ShowDialog()
		{
			return ShowDialog(false);
		}

		public bool ShowDialog(bool showInTaskbar)
		{
			ShowInTaskbar = showInTaskbar;
			base.ShowDialog();
			return (Modorichi == null) ? false : (bool)Modorichi;
		}

		/// <summary>
		/// 文字列を変更する
		/// </summary>
		public void ChangeText(string text)
		{
			TextLabel.Content = text;
		}
	}
}
