package android.ochawanz.passwordlistandroid.activity

import android.content.Intent
import android.ochawanz.passwordlistandroid.ConnectionServerInfo
import android.ochawanz.passwordlistandroid.ConnectionServerInfoItem
import android.ochawanz.passwordlistandroid.R
import android.ochawanz.passwordlistandroid.adapter.PasswordItemAdapter
import android.ochawanz.passwordlistandroid.db.SelectedItemDBHelper
import android.ochawanz.passwordlistandroid.db.ServerInfoDBHelper
import android.ochawanz.passwordlistandroid.interfaces.IConnectionInfo
import android.ochawanz.passwordlistandroid.jsonobject.ResponseJson
import android.ochawanz.passwordlistandroid.utility.*
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_progressbar.view.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.Comparator
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

	/**
	 * 接続先情報
	 */
	private var mConnectionDatumItems: List<IConnectionInfo> = listOf()

	// ハンドラ
	private val mHandler = Handler()

	// リスト取得非同期処理
	private var mTaskList: Deferred<ReturnObject<ResponseJson>>? = null

	// リスト取得非同期処理時にViewを更新するタイマー
	private var mListViewUpdateTimer: Timer? = null

	/**
	 * 読み取り専用モード(マッシュルーム起動)
	 */
	var mReadOnlyMode = false
		private set

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

//		val aes128 = AES(AES.Companion.KeyLength.AES256, "aes", "aes")
//		aes128.test("大阪")
//		val aes192 = AES(AES.Companion.KeyLength.AES192, "wRTBlurUtils: check blur style for ", "134698746")
//		aes192.test("名古屋")
//		val aes256 = AES(AES.Companion.KeyLength.AES256, "wRTBlurUtils: check blur style for HwPho", "456asd46f46adf4")
//		aes256.test("urUtils: check blur style for HwPhoneWindow, themeResId : 0x7f0c0005, context : android.ochawanz.passwordlistandroid.activity.MainActivity@cca62ad, Nhwext : 0, get ")

		// アクティビティの起動方法をよりモードを変更する
		if (intent.action == "android.intent.action.MAIN" && intent.hasCategory("android.intent.category.LAUNCHER")) {
			// 通常の起動モード
			mReadOnlyMode = false
		} else if (intent.action == "com.adamrocker.android.simeji.ACTION_INTERCEPT" &&
				(intent.hasCategory("com.adamrocker.android.simeji.REPLACE") || intent.hasCategory("android.intent.category.DEFAULT"))) {
			// 読み取り専用起動モード
			mReadOnlyMode = true
		} else {
			AlertDialog.Builder(this@MainActivity)
					.setTitle("正しい手順で起動されませんでした")
					.setMessage("起動アクション:" + intent.action)
					.setPositiveButton("OK", null)
					.setOnDismissListener { exitProcess(-1) }
					.show()
			return
		}

		// 起動モードが読み取り専用モードなら，新規登録ボタンを非表示にする
		if (mReadOnlyMode) Button_NewItem.visibility = View.GONE

		// 新規追加ボタンが押されたとき
		Button_NewItem.setOnClickListener {
			// 現在選択しているアイテムを取得する
			if (Spinner_ConnectList.selectedItem !is IConnectionInfo) return@setOnClickListener
			val connectionServerInfo = Spinner_ConnectList.selectedItem as IConnectionInfo

			EditasswordItemDialog(this, connectionServerInfo, null, { GlobalScope.launch(Dispatchers.Main) { UpdateList() } }).showDialog()
		}

		// 再取得ボタンがクリックされた時
		Button_ReloadItem.setOnClickListener {
			GlobalScope.launch(Dispatchers.Main) {
				UpdateList()
			}
		}

		// 再取得ボタンが長押しされた時(読み取り専用でないとき)
		if (!mReadOnlyMode) {
			Button_ReloadItem.setOnLongClickListener {

				val item1 = ItemSelectDialog.ItemSelectDialogItem("再取得", View.OnClickListener {
					// リスト更新
					GlobalScope.launch(Dispatchers.Main) {
						UpdateList()
					}
				})
				val item2 = ItemSelectDialog.ItemSelectDialogItem("接続先編集", ConnectionEditListDialog(this, mConnectionDatumItems) {
					updateConnectList(it)
				})
				ItemSelectDialog(this, "操作を選択してください", listOf(item1, item2)).showDailog()
				true
			}
		}

		// スピナーの選択されているのもが変更された時
		Spinner_ConnectList.onItemSelectedListener = object : OnItemSelectedListener {
			override fun onNothingSelected(parent: AdapterView<*>?) {}
			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
				// Spinner を取得
				val spinner = parent as Spinner
				// 選択されたアイテムのテキストを取得
				val connectionInfo = spinner.selectedItem as IConnectionInfo

				// タイトルの変更
				textView2.text = "一覧[" + connectionInfo.toString() + "]"
				// DBに接続する
				val mDBHelper = SelectedItemDBHelper(this@MainActivity)

				mDBHelper.SelectedCnnectionServerInfoID = connectionInfo.ID

				mDBHelper.close()

				// 空のアダプタを作成し，入れ替える(リスト要素の削除)
				ListView_PasswordItems.adapter = PasswordItemAdapter(this@MainActivity, R.layout.item_connectionlist, mutableListOf(), connectionInfo, {})
				// リスト更新
				GlobalScope.launch(Dispatchers.Main) {
					UpdateList()
				}
			}
		}

		// タイトルを変更する
		title = ""

		// 接続先一覧を初期化する
		initializeConnectList()
	}

	// 接続先情報を初期化する
	private fun initializeConnectList() {
		// DBに接続する
		val mDBHelper = ServerInfoDBHelper(this)
		// DBから接続先情報を取得する
		val connectionServerInfos: MutableList<IConnectionInfo> = mDBHelper.ConnectionServerInfos.toMutableList()
		// DBとの接続を切断する
		mDBHelper.close()

		// 接続先にローカルを追加する(先頭)
		connectionServerInfos.add(0, ConnectionLocalInfo(this))

		mConnectionDatumItems = connectionServerInfos

		// スピナーの更新
		updateConnectListSpinner()
	}

	// 接続先情報を更新する
	private fun updateConnectList(connectList: List<ConnectionServerInfoItem>) {
		// DBに接続する
		val mDBHelper = ServerInfoDBHelper(this)
		// DBに新しい情報を上書きする
		val connectionServerInfos: MutableList<IConnectionInfo> = mDBHelper.SetAllConnectionServerInfo(connectList).toMutableList()
		// DBとの接続を切断する
		mDBHelper.close()

		// 接続先にローカルを追加する(先頭)
		connectionServerInfos.add(0, ConnectionLocalInfo(this))

		mConnectionDatumItems = connectionServerInfos

		updateConnectListSpinner()
	}

	// スピナーに現在の状態を反映させる
	private fun updateConnectListSpinner() {

		// アダプターの作成
		val adapter = ArrayAdapter<IConnectionInfo>(this, R.layout.item_connectionlist)
		// DBに接続する
		val mDBHelper = SelectedItemDBHelper(this)

		// 選択状態にするアイテムのIDを取得
		val selectid = mDBHelper.SelectedCnnectionServerInfoID

		// DBとの接続を切断する
		mDBHelper.close()

		// アダプターにアイテムを追加する
		// 選択状態にするアイテムの検索
		var idx = 0
		for ((count, item) in mConnectionDatumItems.withIndex()) {
			adapter.add(item)
			if (idx == 0 && selectid == item.ID) idx = count
		}
		Spinner_ConnectList.adapter = adapter

		Spinner_ConnectList.setSelection(idx)
	}

	// 再取得
	suspend fun UpdateList() {
		// 現在選択しているアイテムを取得する
		if (Spinner_ConnectList.selectedItem !is IConnectionInfo) return
		val connectionServerInfo = Spinner_ConnectList.selectedItem as IConnectionInfo

		// 接続状態情報の作成
		val webRequestInfomation = WebRequestInfomation()

		// タイマー実行内，UI更新タスク
		val tsk = {
			// Viewの更新
			Include_ProgressBar.TextView_Status.text = webRequestInfomation.status
			Include_ProgressBar.TextView_Progress.text = webRequestInfomation.downloadStateText
			Include_ProgressBar.ProgressBar.min = 0
			Include_ProgressBar.ProgressBar.max = webRequestInfomation.totalFileSize ?: webRequestInfomation.fileSize
			Include_ProgressBar.ProgressBar.progress = webRequestInfomation.fileSize
		}

		// すでに実行されているバックグラウンド処理を中止する
		if (mTaskList != null) {
			// すでに中止要求がある場合は，無視
			if (mTaskList!!.isCancelled) return
			// キャンセル処理
			mTaskList?.cancelAndJoin()
			// タイマーのキャンセル
			mListViewUpdateTimer?.cancel()
			// ポストされた内容を削除する
			mHandler.removeCallbacks(tsk)
		}

		// タイマーの生成
		mListViewUpdateTimer = Timer("一覧取得状況更新タイマー", true)

		// タイマーを使用してビューの定期的な更新を行う
		mListViewUpdateTimer!!.schedule(object : TimerTask() {
			override fun run() {
				mHandler.post(tsk)
			}
		}, 0, 16)

		// リストの削除
		ListView_PasswordItems.adapter = null

		// バックグラウンド処理開始
		mTaskList = connectionServerInfo.Request.List(webRequestInfomation)

		// バックグラウンド処理の待機
		val ret = mTaskList!!.await()

		// バックグラウンド無し
		mTaskList = null

		// タイマーの停止
		mListViewUpdateTimer!!.cancel()
		mListViewUpdateTimer!!.purge()
		// ポストされた内容を削除する
		mHandler.removeCallbacks(tsk)

		// 最後に実行する
		tsk()

		// 結果の表示
		if (!ret.state) {
			AlertDialog.Builder(this@MainActivity)
					.setTitle("エラー(リスト取得)")
					.setMessage(ret.toString())
					.setPositiveButton("OK", null)
					.show()
			return
		}

		val sortedList = ret.Value.PasswordItems.sortedWith(Comparator { a, b ->
			// 名前がnullなら空文字として扱う
			val a_Str = a.Name ?: ""
			val b_Str = b.Name ?: ""

			// 空文字の場合は無条件で最下位
			if (a_Str.isEmpty() && b_Str.isEmpty()) return@Comparator 0
			if (a_Str.isEmpty()) return@Comparator -1
			if (b_Str.isEmpty()) return@Comparator 1

			// 小文字変換
			val a_LowStr = a_Str.toLowerCase()
			val b_LowStr = b_Str.toLowerCase()

			// 完全に文字が一致した場合は普通サイズで比較
			if (a_LowStr == b_LowStr) return@Comparator a_Str.compareTo(b_Str)

			// 文字の比較
			return@Comparator a_LowStr.compareTo(b_LowStr)
		})


		// アダプタを作成し，入れ替える
		ListView_PasswordItems.adapter = PasswordItemAdapter(this@MainActivity, R.layout.item_passwordlistitem, sortedList.toMutableList(), connectionServerInfo, { GlobalScope.launch(Dispatchers.Main) { UpdateList() } })
	}

	// インテントを返却する
	fun replace(result: String) {
		val REPLACE_KEY = "replace_key"
		val data = Intent()
		data.putExtra(REPLACE_KEY, result)
		setResult(RESULT_OK, data)
		finish()
	}

}
