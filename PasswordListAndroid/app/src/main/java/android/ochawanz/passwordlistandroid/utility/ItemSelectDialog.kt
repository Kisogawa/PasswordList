package android.ochawanz.passwordlistandroid.utility

import android.content.Context
import android.ochawanz.passwordlistandroid.R
import android.ochawanz.passwordlistandroid.layout.Separator
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView


class ItemSelectDialog(context: Context, title: String, itemSelectDialogItem: List<ItemSelectDialogItem>) {

	// コンテキスト
	private val mContext = context
	// 基底ビュー
	private val mRootView: View
	// アラートダイアログ
	private val mAlertDialog: AlertDialog


	// 初期化処理
	init {
		//アプリ選択ダイアログ用ビューの生成
		val inflater = LayoutInflater.from(mContext)
		mRootView = inflater.inflate(R.layout.dialog_selectitem, null)
		val closeButton = mRootView.findViewById<Button>(R.id.Button_Close)
		val titleText = mRootView.findViewById<TextView>(R.id.TextView_Title)
		val selectItemTable = mRootView.findViewById<LinearLayout>(R.id.LinearLayout_SelectItemTable)
		// タイトルの設定
		titleText.text = title

		// アニメーション設定
		// ダイアログ生成
		val builder = AlertDialog.Builder(mContext)
		builder.setView(mRootView)
		mAlertDialog = builder.create()

		// 要素を追加する
		for (item in itemSelectDialogItem) {
			// ボタンを生成する
			val btn = Button(mContext, null, 0, R.style.ButtonAccent)
			btn.text = item.Text
			btn.height = (45 * context.resources.displayMetrics.density).toInt()
			btn.setOnClickListener { v ->
				item.OnClickListener?.onClick(v)
				mAlertDialog.dismiss()
			}
			selectItemTable.addView(btn)
			// 最後の要素でなければセパレーターを追加する
			if (itemSelectDialogItem.last() != item) {
				val sp = Separator(mContext, 2, R.color.colorLightGray)
				selectItemTable.addView(sp)
			}
		}

		// 閉じるボタンが押された時
		closeButton.setOnClickListener {
			mAlertDialog.dismiss()
		}
	}

	// ダイアログの表示
	fun showDailog() = mAlertDialog.show()

	// ダイアログアイテムクラス
	class ItemSelectDialogItem(text: String, onClickListener: View.OnClickListener?) {

		private var _Text = text
		private var _OnClickListener = onClickListener
		/**
		 * 表示する文字列
		 */
		var Text: String
			get() = _Text
			private set(value) {
				_Text = value
			}

		/**
		 * 項目が選択された時のイベント
		 */
		var OnClickListener: View.OnClickListener?
			get() = _OnClickListener
			private set(value) {
				_OnClickListener = value
			}
	}
}