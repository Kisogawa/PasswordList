using System;
using System.Collections.Generic;
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
	/// ProgressBoxDarkStyle.xaml の相互作用ロジック
	/// </summary>
	public partial class ProgressBoxDarkStyle : Window
	{
		/// <summary>
		/// バックグラウンドで動かすタスク
		/// </summary>
		/// <returns></returns>
		public delegate ReturnObject BackgroundTask();

		/// <summary>
		/// バックグラウンドの処理が終了した時の処理(同期的)
		/// </summary>
		/// <param name="value"></param>
		public delegate void EndOfBackgroundTask(ReturnObject value);

		private BackgroundTask BackgroundTaskFunc;
		private EndOfBackgroundTask EndOfBackgroundTaskFunc;

		private Task Task;

		/// <summary>
		/// 初期化
		/// </summary>
		/// <param name="title">タイトルに表示する文字</param>
		/// <param name="backgroundTask">バックグラウンドで動かす処理(非同期)</param>
		/// <param name="endOfBackgroundTask">バックグラウンドの処理が終了した時の処理(同期的)</param>
		public ProgressBoxDarkStyle(string title, BackgroundTask backgroundTask, EndOfBackgroundTask endOfBackgroundTask)
		{
			InitializeComponent();

			Title = title;
			BackgroundTaskFunc = backgroundTask;
			EndOfBackgroundTaskFunc = endOfBackgroundTask;

			Task = new Task(() =>
			{
				ReturnObject returnObject = BackgroundTaskFunc?.Invoke();
				// 取得した情報を表示する
				Dispatcher.Invoke(new Action(() =>
				{
					Close();
					EndOfBackgroundTaskFunc?.Invoke(returnObject);
				}));
			});


			Loaded += (sender, e) =>
			{
				//タスクを開始する
				Task.Start();
			};
		}
	}
}
