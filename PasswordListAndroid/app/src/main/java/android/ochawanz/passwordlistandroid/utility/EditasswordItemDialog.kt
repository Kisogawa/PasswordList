package android.ochawanz.passwordlistandroid.utility

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
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.dialog_editpassworditem.view.*
import kotlinx.android.synthetic.main.include_progressbar.view.*
import kotlinx.coroutines.*
import java.util.*

/**
 * 詳細表示ダイアログ
 */
class EditasswordItemDialog(mainActivity: MainActivity, connectionInfo: IConnectionInfo, passwordItem: PasswordItem?, updateFunc: () -> Unit) {

	// 新規追加フラグ
	private val mNewItem = (passwordItem == null)
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
	private var mPasswordItem = passwordItem ?: PasswordItem()
	// 詳細非同期処理
	private var mTaskShow: Deferred<ReturnObject<ResponseJson>>? = null
	// 非同期処理(登録)
	private var mTaskRegister: Deferred<ReturnObject<ResponseJson>>? = null
	// 非同期処理(変更)
	private var mTaskUpdate: Deferred<ReturnObject<ResponseJson>>? = null
	// 詳細取得非同期処理時にViewを更新するタイマー
	private var mViewUpdateTimer: Timer? = null
	// 非同期処理時にViewを更新するタイマー(登録)
	private var mViewUpdateTimerOnTaskRegister: Timer? = null
	// 非同期処理時にViewを更新するタイマー(変更)
	private var mViewUpdateTimerOnTaskUpdate: Timer? = null

	private val mTextViewName: EditText
	private val mTextViewID: EditText
	private val mTextViewPassword: EditText
	private val mTextViewMail: EditText
	private val mEditTextViewComment: TextView
	private val mIncludeProgressBar: View


	// 初期化処理
	init {
		//アプリ選択ダイアログ用ビューの生成
		val inflater = LayoutInflater.from(mMainActivity)
		mRootView = inflater.inflate(R.layout.dialog_editpassworditem, null)


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

		// 変更対象ビュー取得
		mTextViewName = mRootView.Include_ConnectionItemName.findViewById(R.id.EditText_Item)
		mTextViewID = mRootView.Include_ConnectionItemID.findViewById(R.id.EditText_Item)
		mTextViewPassword = mRootView.Include_ConnectionItemPassword.findViewById(R.id.EditText_Item)
		mTextViewMail = mRootView.Include_ConnectionItemMail.findViewById(R.id.EditText_Item)
		mEditTextViewComment = mRootView.EditText_Comment
		mIncludeProgressBar = mRootView.Include_ProgressBar

		// 閉じるボタンが押された時
		mRootView.Button_Close.setOnClickListener {
			mAlertDialog.dismiss()
			if (mTaskShow != null && mTaskShow!!.isCancelled) {
				// キャンセル処理
				mTaskShow?.cancel()
				mTaskRegister?.cancel()
				// タイマーのキャンセル
				mViewUpdateTimer?.cancel()
				mViewUpdateTimerOnTaskRegister?.cancel()
			}
		}

		// 新規登録ボタンが押されたとき
		mRootView.Button_Register.setOnClickListener {
			GlobalScope.launch(Dispatchers.Main) {
				// 新規作成のみ動作する
				if (!mNewItem) return@launch

				// すでに実行されている場合はこの処理を行わない
				if (mTaskRegister != null) {
					return@launch
				}

				// ビューの状態を設定
				mTextViewName.isEnabled = false
				mTextViewID.isEnabled = false
				mTextViewPassword.isEnabled = false
				mTextViewMail.isEnabled = false
				mEditTextViewComment.isEnabled = false
				mRootView.Button_Register.isEnabled = false
				mRootView.Button_Close.isEnabled = false

				// 現在の登録内容の送信
				// 接続状態情報の作成
				val webRequestInfomation = WebRequestInfomation()
				// タイマーの生成
				mViewUpdateTimerOnTaskRegister = Timer("状況更新タイマー(登録)", true)
				// タイマーを使用してビューの定期的な更新を行う
				mViewUpdateTimerOnTaskRegister!!.schedule(object : TimerTask() {
					override fun run() {
						mHandler.post {
							// Viewの更新
							mIncludeProgressBar.TextView_Status.text = webRequestInfomation.status
							mIncludeProgressBar.TextView_Progress.text = webRequestInfomation.downloadStateText
							mIncludeProgressBar.ProgressBar.min = 0
							mIncludeProgressBar.ProgressBar.max = webRequestInfomation.totalFileSize ?: webRequestInfomation.fileSize
							mIncludeProgressBar.ProgressBar.progress = webRequestInfomation.fileSize
						}
					}
				}, 0, 16)

				// データの設定
				mPasswordItem.Name = mTextViewName.text.toString()
				mPasswordItem.UserID = mTextViewID.text.toString()
				mPasswordItem.Password = mTextViewPassword.text.toString()
				mPasswordItem.MailAddress = mTextViewMail.text.toString()
				mPasswordItem.Comment = mEditTextViewComment.text.toString()


				// バックグラウンド処理開始
				mTaskRegister = mConnectionInfo.Request.Register(mPasswordItem, webRequestInfomation)

				// バックグラウンド処理の待機
				val ret = mTaskRegister!!.await()

				// バックグラウンド無し
				mTaskRegister = null

				// タイマーの停止
				mViewUpdateTimerOnTaskRegister!!.cancel()
				mViewUpdateTimerOnTaskRegister!!.purge()

				// ビューの状態を戻す
				mTextViewName.isEnabled = true
				mTextViewID.isEnabled = true
				mTextViewPassword.isEnabled = true
				mTextViewMail.isEnabled = true
				mEditTextViewComment.isEnabled = true
				mRootView.Button_Register.isEnabled = true
				mRootView.Button_Close.isEnabled = true

				// 操作に失敗している場合はここで処理を終了する
				if (!ret.state) {
					if (!ret.state) {
						AlertDialog.Builder(mMainActivity)
								.setTitle("エラー(登録)")
								.setMessage(ret.toString())
								.setPositiveButton("OK", null)
								.show()
						return@launch
					}
				}

				// 処理に成功している場合はダイアログを閉じる
				mAlertDialog.dismiss()

				// 更新処理
				updateFunc()

				// ダイアログ表示
				PasswordItemDialog(mMainActivity, mConnectionInfo, ret.Value.PasswordItems[0]).showDialog()
			}

		}

		// 変更ボタンが押されたとき
		mRootView.Button_Update.setOnClickListener {
			GlobalScope.launch(Dispatchers.Main) {
				// 変更のみ動作する
				if (mNewItem) return@launch

				// すでに実行されている場合はこの処理を行わない
				if (mTaskUpdate != null) {
					return@launch
				}

				// ビューの状態を設定
				mTextViewName.isEnabled = false
				mTextViewID.isEnabled = false
				mTextViewPassword.isEnabled = false
				mTextViewMail.isEnabled = false
				mEditTextViewComment.isEnabled = false
				mRootView.Button_Update.isEnabled = false
				mRootView.Button_Close.isEnabled = false

				// 現在の登録内容の送信
				// 接続状態情報の作成
				val webRequestInfomation = WebRequestInfomation()
				// タイマーの生成
				mViewUpdateTimerOnTaskUpdate = Timer("状況更新タイマー(変更)", true)
				// タイマーを使用してビューの定期的な更新を行う
				mViewUpdateTimerOnTaskUpdate!!.schedule(object : TimerTask() {
					override fun run() {
						mHandler.post {
							// Viewの更新
							mIncludeProgressBar.TextView_Status.text = webRequestInfomation.status
							mIncludeProgressBar.TextView_Progress.text = webRequestInfomation.downloadStateText
							mIncludeProgressBar.ProgressBar.min = 0
							mIncludeProgressBar.ProgressBar.max = webRequestInfomation.totalFileSize ?: webRequestInfomation.fileSize
							mIncludeProgressBar.ProgressBar.progress = webRequestInfomation.fileSize
						}
					}
				}, 0, 16)

				// データの設定
				mPasswordItem.Name = mTextViewName.text.toString()
				mPasswordItem.UserID = mTextViewID.text.toString()
				mPasswordItem.Password = mTextViewPassword.text.toString()
				mPasswordItem.MailAddress = mTextViewMail.text.toString()
				mPasswordItem.Comment = mEditTextViewComment.text.toString()


				// バックグラウンド処理開始
				mTaskUpdate = mConnectionInfo.Request.Update(mPasswordItem, webRequestInfomation)

				// バックグラウンド処理の待機
				val ret = mTaskUpdate!!.await()

				// バックグラウンド無し
				mTaskUpdate = null

				// タイマーの停止
				mViewUpdateTimerOnTaskUpdate!!.cancel()
				mViewUpdateTimerOnTaskUpdate!!.purge()

				// ビューの状態を戻す
				mTextViewName.isEnabled = true
				mTextViewID.isEnabled = true
				mTextViewPassword.isEnabled = true
				mTextViewMail.isEnabled = true
				mEditTextViewComment.isEnabled = true
				mRootView.Button_Update.isEnabled = true
				mRootView.Button_Close.isEnabled = true

				// 操作に失敗している場合はここで処理を終了する
				if (!ret.state) {
					if (!ret.state) {
						AlertDialog.Builder(mMainActivity)
								.setTitle("エラー(変更)")
								.setMessage(ret.toString())
								.setPositiveButton("OK", null)
								.show()
						return@launch
					}
				}

				// 処理に成功している場合はダイアログを閉じる
				mAlertDialog.dismiss()

				// 更新処理
				updateFunc()

				// ダイアログ表示
				PasswordItemDialog(mMainActivity, mConnectionInfo, mPasswordItem).showDialog()
			}

		}

		GlobalScope.launch(Dispatchers.Main){ Update() }
	}

	// ダイアログの表示
	fun showDialog() = mAlertDialog.show()

	// 情報の取得(最初の一度のみの呼び出し想定)
	private suspend fun Update() {

		if (mNewItem) {
			// 新規作成の場合
			mRootView.TextView_Title.text = "新規追加"
			// 新規登録ボタンの表示
			mRootView.Button_Register.visibility = View.VISIBLE
			return
		}

		// すでに実行されている場合はこの処理を行わない
		if (mTaskShow != null) {
			return
		}

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

		// タイマーの生成
		mViewUpdateTimer = Timer("詳細取得状況更新タイマー", true)

		// タイマーを使用してビューの定期的な更新を行う
		mViewUpdateTimer!!.schedule(object : TimerTask() {
			override fun run() {
				mHandler.post(tsk)
			}
		}, 0, 16)

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

		tsk()

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

		// 変更ボタン表示
		mRootView.Button_Update.visibility = View.VISIBLE

		// 共有データ更新
		mPasswordItem = ret.Value.PasswordItems[0]

		// ビューの更新
		UpdateView()
	}

	/**
	 * 現在のデータとビューの内容を一致させる
	 */
	private fun UpdateView() {
		mTextViewName.setText(mPasswordItem.Name, TextView.BufferType.NORMAL)
		mTextViewID.setText(mPasswordItem.UserID, TextView.BufferType.NORMAL)
		mTextViewPassword.setText(mPasswordItem.Password, TextView.BufferType.NORMAL)
		mTextViewMail.setText(mPasswordItem.MailAddress, TextView.BufferType.NORMAL)
		mEditTextViewComment.text = mPasswordItem.Comment
	}
}