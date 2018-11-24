package android.ochawanz.passwordlistandroid.utility

import android.content.Context
import android.ochawanz.passwordlistandroid.R
import android.ochawanz.passwordlistandroid.interfaces.IConnectionInfo
import android.ochawanz.passwordlistandroid.jsonobject.PasswordItem
import android.ochawanz.passwordlistandroid.jsonobject.ResponseJson
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.dialog_removepassworditem.view.*
import kotlinx.android.synthetic.main.include_progressbar.view.*
import kotlinx.coroutines.*
import java.util.*

/**
 * 削除ダイアログ
 */
class RemovePasswordItemDialog(context: Context, connectionInfo: IConnectionInfo, passwordItem: PasswordItem, updateFunc: () -> Unit) {

	// コンテキスト
	private val mContext = context
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
	private var mTaskRemove: Deferred<ReturnObject<ResponseJson>>? = null
	// 詳細取得非同期処理時にViewを更新するタイマー
	private var mViewUpdateTimer: Timer? = null
	// プログレスバー
	private val mIncludeProgressBar: View
	// プログレスバー
	private val mUpdateFunc = updateFunc


	// 初期化処理
	init {
		//アプリ選択ダイアログ用ビューの生成
		val inflater = LayoutInflater.from(mContext)
		mRootView = inflater.inflate(R.layout.dialog_removepassworditem, null)


		// ダイアログ生成
		val builder = AlertDialog.Builder(mContext)
		builder.setView(mRootView)
		mAlertDialog = builder.create()
		mAlertDialog.setCancelable(false)

		// タイトルの更新
		mRootView.TextView_Title.text = passwordItem.toString() + "の削除"
		mRootView.TextView_TaskMessage.text = "本当に削除してよろしいですか？"

		// プログレスバー
		mIncludeProgressBar = mRootView.Include_ProgressBar

		// OKボタンが押されたときは，削除処理を実行する
		mRootView.Button_OK.setOnClickListener {
			GlobalScope.launch(Dispatchers.Main) { Remove() }
		}

		// キャンセルボタンが押されたときは処理を行わず，閉じる
		mRootView.Button_Cancel.setOnClickListener {
			// 実行中の場合はキャンセルしない
			if (mTaskRemove == null) mAlertDialog.dismiss()
		}
	}

	// ダイアログの表示
	fun showDialog() = mAlertDialog.show()

	private suspend fun Remove() {

		// すでに実行されている場合は処理をしない
		if (mTaskRemove != null) return

		// コントロールの有効状態を変更する
		mRootView.Button_OK.isEnabled = false
		mRootView.Button_Cancel.isEnabled = false

		// 接続状態情報の作成
		val webRequestInfomation = WebRequestInfomation()

		// タイマーの生成
		mViewUpdateTimer = Timer("状況更新タイマー(削除)", true)

		// タイマーを使用してビューの定期的な更新を行う
		mViewUpdateTimer!!.schedule(object : TimerTask() {
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

		mRootView.TextView_TaskMessage.text = "削除中・・・"

		// バックグラウンド処理開始
		mTaskRemove = mConnectionInfo.Request.Remove(mPasswordItem, webRequestInfomation)

		// バックグラウンド処理の待機
		val ret = mTaskRemove!!.await()

		// バックグラウンド無し
		mTaskRemove = null

		// タイマーの停止
		mViewUpdateTimer!!.cancel()
		mViewUpdateTimer!!.purge()

		// コントロールの有効状態をもとにもどす
		mRootView.Button_OK.isEnabled = true
		mRootView.Button_Cancel.isEnabled = true

		// 失敗している場合は，メッセージの出力を行い，削除実行前に戻す
		if (!ret.state) {
			// メッセージの設定
			mRootView.TextView_TaskMessage.text = "本当に削除してよろしいですか？(再試行)"
			// エラーメッセージの設定
			mRootView.TextView_ErrorMessage.visibility = View.VISIBLE
			mRootView.TextView_ErrorMessage.text = ret.toString()
			// ボタンを再試行に変更する
			mRootView.Button_OK.text = "再試行"
			return
		}

		// タイトルの設定
		mRootView.TextView_TaskMessage.text = "削除完了"

		// OKボタンの非表示
		mRootView.Button_OK.visibility = View.GONE
		// キャンセルボタンの文字変更
		mRootView.Button_Cancel.text = "閉じる"

		// 項目の更新
		mUpdateFunc()
	}
}