package android.ochawanz.passwordlistandroid.utility

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.ochawanz.passwordlistandroid.R
import android.ochawanz.passwordlistandroid.activity.MainActivity
import android.ochawanz.passwordlistandroid.interfaces.IConnectionInfo
import android.ochawanz.passwordlistandroid.jsonobject.PasswordItem
import android.ochawanz.passwordlistandroid.jsonobject.ResponseJson
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.common.util.Strings
import kotlinx.android.synthetic.main.dialog_passworditem.view.*
import kotlinx.android.synthetic.main.include_progressbar.view.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 詳細表示ダイアログ
 */
class PasswordItemDialog(mainActivity: MainActivity, connectionInfo: IConnectionInfo, passwordItem: PasswordItem) {

	// コンテキスト
	private val mMainActivity = mainActivity
	// 基底ビュー
	private val mRootView: View
	// アラートダイアログ
	private val mAlertDialog: AlertDialog
	// ハンドラ
	private val mHandler = Handler()
	// 接続情報
	private val mConnectionInfo = connectionInfo
	// 表示データ(書き換え有り)
	private var mPasswordItem = passwordItem
	// 詳細非同期処理
	private var mTaskShow: Deferred<ReturnObject<ResponseJson>>? = null
	// 詳細取得非同期処理時にViewを更新するタイマー
	private var mViewUpdateTimer: Timer? = null
	// クリップボードマネジャー
	private var mClipboardManager: ClipboardManager

	private val mTextViewName: TextView
	private val mTextViewID: TextView
	private val mTextViewPassword: TextView
	private val mTextViewMail: TextView
	private val mTextViewRegisterDateTime: TextView
	private val mTextViewUpdateDateTime: TextView
	private val mTextViewComment: TextView
	private val mIncludeProgressBar: View


	// 初期化処理
	init {
		//アプリ選択ダイアログ用ビューの生成
		val inflater = LayoutInflater.from(mMainActivity)
		mRootView = inflater.inflate(R.layout.dialog_passworditem, null)


		// ダイアログ生成
		val builder = AlertDialog.Builder(mMainActivity)
		builder.setView(mRootView)
		mAlertDialog = builder.create()
		mAlertDialog.setCancelable(false)

		// ビューの固定項目の更新
		mRootView.Include_ConnectionItemName.findViewById<TextView>(R.id.TextView_Title).text = "名前"
		mRootView.Include_ConnectionItemID.findViewById<TextView>(R.id.TextView_Title).text = "ID"
		mRootView.Include_ConnectionItemPassword.findViewById<TextView>(R.id.TextView_Title).text = "Password"
		mRootView.Include_ConnectionItemMail.findViewById<TextView>(R.id.TextView_Title).text = "メール"
		mRootView.Include_ConnectionItemRegisterDateTime.findViewById<TextView>(R.id.TextView_Title).text = "登録時間"
		mRootView.Include_ConnectionItemUpdateDateTime.findViewById<TextView>(R.id.TextView_Title).text = "更新時間"

		// 変更対象ビュー取得
		mTextViewName = mRootView.Include_ConnectionItemName.findViewById(R.id.TextView_Item)
		mTextViewID = mRootView.Include_ConnectionItemID.findViewById(R.id.TextView_Item)
		mTextViewPassword = mRootView.Include_ConnectionItemPassword.findViewById(R.id.TextView_Item)
		mTextViewMail = mRootView.Include_ConnectionItemMail.findViewById(R.id.TextView_Item)
		mTextViewRegisterDateTime = mRootView.Include_ConnectionItemRegisterDateTime.findViewById(R.id.TextView_Item)
		mTextViewUpdateDateTime = mRootView.Include_ConnectionItemUpdateDateTime.findViewById(R.id.TextView_Item)
		mTextViewComment = mRootView.TextView_Comment
		mIncludeProgressBar = mRootView.Include_ProgressBar

		// コピーボタン取得
		val buttonNameCopy = mRootView.Include_ConnectionItemName.findViewById<Button>(R.id.Button_Copy)
		val buttonIDCopy = mRootView.Include_ConnectionItemID.findViewById<Button>(R.id.Button_Copy)
		val buttonPasswordCopy = mRootView.Include_ConnectionItemPassword.findViewById<Button>(R.id.Button_Copy)
		val buttonMailCopy = mRootView.Include_ConnectionItemMail.findViewById<Button>(R.id.Button_Copy)
		val buttonRegisterDateTimeCopy = mRootView.Include_ConnectionItemRegisterDateTime.findViewById<Button>(R.id.Button_Copy)
		val buttonUpdateDateTimeCopy = mRootView.Include_ConnectionItemUpdateDateTime.findViewById<Button>(R.id.Button_Copy)
		val buttonCommentCopy = mRootView.Button_CommentCopy

		mClipboardManager = mainActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

		// 読み取り専用モードの場合はコピーボタンを入力ボタンに変更する
		if (mMainActivity.mReadOnlyMode) {
			buttonNameCopy.text = "入力"
			buttonIDCopy.text = "入力"
			buttonPasswordCopy.text = "入力"
			buttonMailCopy.text = "入力"
			buttonRegisterDateTimeCopy.text = "入力"
			buttonUpdateDateTimeCopy.text = "入力"
			buttonCommentCopy.text = "入力"
		}

		// 各コピー(入力)ボタンが押されたときのイベント
		buttonNameCopy.setOnClickListener {
			if (mMainActivity.mReadOnlyMode) mMainActivity.replace(mPasswordItem.Name ?: "")
			else mClipboardManager.primaryClip = ClipData.newPlainText("Name", mPasswordItem.Name
					?: "")
		}
		buttonIDCopy.setOnClickListener {
			if (mMainActivity.mReadOnlyMode) mMainActivity.replace(mPasswordItem.UserID ?: "")
			else mClipboardManager.primaryClip = ClipData.newPlainText("ID", mPasswordItem.UserID
					?: "")
		}
		buttonPasswordCopy.setOnClickListener {
			if (mMainActivity.mReadOnlyMode) mMainActivity.replace(mPasswordItem.Password ?: "")
			else mClipboardManager.primaryClip = ClipData.newPlainText("ID", mPasswordItem.Password
					?: "")
		}
		buttonMailCopy.setOnClickListener {
			if (mMainActivity.mReadOnlyMode) mMainActivity.replace(mPasswordItem.MailAddress ?: "")
			else mClipboardManager.primaryClip = ClipData.newPlainText("ID", mPasswordItem.MailAddress
					?: "")
		}
		buttonRegisterDateTimeCopy.setOnClickListener {
			if (mMainActivity.mReadOnlyMode) mMainActivity.replace(if (mPasswordItem.RegisterDate == null) "" else SimpleDateFormat("yyyy年MM月dd日HH:mm:ss").format(mPasswordItem.RegisterDate))
			else mClipboardManager.primaryClip = ClipData.newPlainText("ID", if (mPasswordItem.RegisterDate == null) "" else SimpleDateFormat("yyyy年MM月dd日HH:mm:ss").format(mPasswordItem.RegisterDate))
		}
		buttonUpdateDateTimeCopy.setOnClickListener {
			if (mMainActivity.mReadOnlyMode) mMainActivity.replace(if (mPasswordItem.UpdateDate == null) "" else SimpleDateFormat("yyyy年MM月dd日HH:mm:ss").format(mPasswordItem.UpdateDate))
			else mClipboardManager.primaryClip = ClipData.newPlainText("ID", if (mPasswordItem.UpdateDate == null) "" else SimpleDateFormat("yyyy年MM月dd日HH:mm:ss").format(mPasswordItem.UpdateDate))
		}
		buttonCommentCopy.setOnClickListener {
			if (mMainActivity.mReadOnlyMode) mMainActivity.replace(mPasswordItem.Comment ?: "")
			else mClipboardManager.primaryClip = ClipData.newPlainText("ID", mPasswordItem.Comment
					?: "")
		}


		// 再取得ボタンイベント
		mRootView.Button_Update.setOnClickListener {
			GlobalScope.launch(Dispatchers.Main) { Update() }
		}


		// 閉じるボタンが押された時
		mRootView.Button_Close.setOnClickListener {
			mAlertDialog.dismiss()
			if (mTaskShow != null && mTaskShow!!.isCancelled) {
				// キャンセル処理
				mTaskShow?.cancel()
				// タイマーのキャンセル
				mViewUpdateTimer?.cancel()
			}
		}

		ClearView()

		GlobalScope.launch(Dispatchers.Main) { Update() }
	}

	// ダイアログの表示
	fun showDialog() = mAlertDialog.show()


	private suspend fun Update() {

		// 接続状態情報の作成
		val webRequestInfomation = WebRequestInfomation()

		val tsk = {
			// Viewの更新
			mIncludeProgressBar.TextView_Status.text = webRequestInfomation.status
			mIncludeProgressBar.TextView_Progress.text = webRequestInfomation.downloadStateText
			mIncludeProgressBar.ProgressBar.min = 0
			mIncludeProgressBar.ProgressBar.max = webRequestInfomation.totalFileSize ?: webRequestInfomation.fileSize
			mIncludeProgressBar.ProgressBar.progress = webRequestInfomation.fileSize
		}

		// すでに実行されているバックグラウンド処理を中止する
		if (mTaskShow != null) {
			// すでに中止要求がある場合は，無視
			if (mTaskShow!!.isCancelled) return
			// キャンセル処理
			mTaskShow?.cancelAndJoin()
			// タイマーのキャンセル
			mViewUpdateTimer?.cancel()
			mHandler.removeCallbacks(tsk)
		}

		// タイマーの生成
		mViewUpdateTimer = Timer("詳細取得状況更新タイマー", true)

		// タイマーを使用してビューの定期的な更新を行う
		mViewUpdateTimer!!.schedule(object : TimerTask() {
			override fun run() {
				mHandler.post(tsk)
			}
		}, 0, 16)

		// ビュー内容の初期化
		ClearView()

		mRootView.TextView_Title.text = "取得中・・・"

		// バックグラウンド処理開始
		mTaskShow = mConnectionInfo.Request.Show(mPasswordItem, webRequestInfomation)

		// バックグラウンド処理の待機
		val ret = mTaskShow!!.await()

		// バックグラウンド無し
		mTaskShow = null

		// タイマーの停止
		mViewUpdateTimer!!.cancel()
		mViewUpdateTimer!!.purge()
		// Viewを不可視にする
//		mIncludeProgressBar.visibility = View.GONE

		// ビューの初期化
		ClearView()

		// タイトルの設定
		mRootView.TextView_Title.text = if (ret.Value.PasswordItems.isEmpty()){
			Log.d("PasswordListAndroid", "該当データ無し")
			"該当データ無し"
		} else{
			ret.Value.PasswordItems[0].toString()
		}

		// 結果の表示
		if (!ret.state) {
			AlertDialog.Builder(mMainActivity)
					.setTitle("エラー(詳細取得)")
					.setMessage(ret.toString())
					.setPositiveButton("OK", null)
					.show()
			return
		}

		if (ret.Value.PasswordItems.isEmpty()) return

		// 共有データ更新
		mPasswordItem = ret.Value.PasswordItems[0]

		// ビューの更新
		UpdateView()
	}

	/**
	 * 現在のデータとビューの内容を一致させる
	 */
	private fun UpdateView() {
		mTextViewName.text = mPasswordItem.Name
		mTextViewID.text = mPasswordItem.UserID
		// 読み取り専用モードならパスワードはマスクする
		if (mMainActivity.mReadOnlyMode) mTextViewPassword.text = if (Strings.isEmptyOrWhitespace(mPasswordItem.Password)) "" else "***************"
		else mTextViewPassword.text = mPasswordItem.Password
		mTextViewMail.text = mPasswordItem.MailAddress
		mTextViewRegisterDateTime.text = if (mPasswordItem.RegisterDate == null) "" else SimpleDateFormat("yyyy年MM月dd日HH:mm:ss").format(mPasswordItem.RegisterDate)
		mTextViewUpdateDateTime.text = if (mPasswordItem.UpdateDate == null) "" else SimpleDateFormat("yyyy年MM月dd日HH:mm:ss").format(mPasswordItem.UpdateDate)
		mTextViewComment.text = mPasswordItem.Comment
	}

	/**
	 * ビューの初期化を行う
	 */
	private fun ClearView() {
		// 項目を空にする
		mTextViewName.text = ""
		mTextViewID.text = ""
		mTextViewPassword.text = ""
		mTextViewMail.text = ""
		mTextViewRegisterDateTime.text = ""
		mTextViewUpdateDateTime.text = ""
		mTextViewComment.text = ""
	}
}