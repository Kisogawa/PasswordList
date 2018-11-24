package android.ochawanz.passwordlistandroid.utility

import android.content.Context
import android.ochawanz.passwordlistandroid.ConnectionServerInfoItem
import android.ochawanz.passwordlistandroid.R
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

/**
 * 接続先編集(登録)ダイアログ
 */
class ConnectionEditDialog(context: Context, connectionServerInfoItem: ConnectionServerInfoItem?, onConnectionEditRegister: (ConnectionServerInfoItem) -> Unit) {

	// コンテキスト
	private val mContext = context
	// 基底ビュー
	private val mRootView: View
	// アラートダイアログ
	private val mAlertDialog: AlertDialog
	// 元情報
	private val mConnectionServerInfoItem = connectionServerInfoItem


	// 初期化処理
	init {
		//アプリ選択ダイアログ用ビューの生成
		val inflater = LayoutInflater.from(mContext)
		mRootView = inflater.inflate(R.layout.dialog_connectionedit, null)

		// 各ビューの取得
		val titleText = mRootView.findViewById<TextView>(R.id.TextView_Title)
		val name = mRootView.findViewById<View>(R.id.Include_ConnectionItemEdit).findViewById<EditText>(R.id.EditText_Name)
		val address = mRootView.findViewById<View>(R.id.Include_ConnectionItemEdit).findViewById<EditText>(R.id.EditText_Address)
		val portNoText = mRootView.findViewById<View>(R.id.Include_ConnectionItemEdit).findViewById<EditText>(R.id.EditText_PortNo)
		val key64Text = mRootView.findViewById<View>(R.id.Include_ConnectionItemEdit).findViewById<EditText>(R.id.EditText_Key64)
		val registerButton = mRootView.findViewById<Button>(R.id.Button_Register)
		val cancelButton = mRootView.findViewById<Button>(R.id.Button_Cancel)

		// タイトル・初期データの設定
		if (connectionServerInfoItem == null) titleText.text = "接続先登録"
		else {
			titleText.text = "接続先編集"
			name.setText(connectionServerInfoItem.Name)
			address.setText(connectionServerInfoItem.Address)
			portNoText.setText(connectionServerInfoItem.PortNo.toString())
			if (connectionServerInfoItem.Key64.isNotEmpty()) key64Text.hint = "パスワード設定あり"
		}

		// ダイアログ生成
		val builder = AlertDialog.Builder(mContext)
		builder.setView(mRootView)
		mAlertDialog = builder.create()
		mAlertDialog.setCancelable(false)

		// キャンセルボタンが押された時
		cancelButton.setOnClickListener {
			mAlertDialog.dismiss()
		}

		// 登録ボタンが押された時
		registerButton.setOnClickListener {
			// 各コントロールの情報を取得する
			val id32 = mConnectionServerInfoItem?.ID ?: ""
			val nameString = name.text.toString()
			val addressString = address.text.toString()
			val portNoTextString = portNoText.text.toString()
			val key64TextString = if (key64Text.text.isEmpty() && connectionServerInfoItem != null) connectionServerInfoItem.Key64 else key64Text.text.toString()

			// ポート番号の正当性確認(数字変換)
			if (portNoTextString.toIntOrNull() == null) {
				// エラーダイアログ
				AlertDialog.Builder(mContext)
						.setTitle("エラー")
						.setMessage("メインポート番号は数字を入力してください")
						.setPositiveButton("OK", null)
						.show()
				return@setOnClickListener
			}
			// 接続情報インスタンス生成
			val newConnectionServerInfoItem = ConnectionServerInfoItem.createInstance(id32, nameString, addressString, portNoTextString.toInt(), key64TextString, true)

			// 結果確認
			if (!newConnectionServerInfoItem.state) {
				// エラーダイアログ
				AlertDialog.Builder(mContext)
						.setTitle("エラー")
						.setMessage("" + newConnectionServerInfoItem.toString())
						.setPositiveButton("OK", null)
						.show()
				return@setOnClickListener
			}

			// 決定イベント
			onConnectionEditRegister(newConnectionServerInfoItem.Value!!)

			// ダイアログを閉じる
			mAlertDialog.dismiss()
		}
	}

	// ダイアログの表示
	fun showDialog() = mAlertDialog.show()
}