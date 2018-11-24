package android.ochawanz.passwordlistandroid.utility

import android.content.Context
import android.ochawanz.passwordlistandroid.ConnectionServerInfo
import android.ochawanz.passwordlistandroid.ConnectionServerInfoItem
import android.ochawanz.passwordlistandroid.R
import android.ochawanz.passwordlistandroid.interfaces.IConnectionInfo
import android.ochawanz.passwordlistandroid.layout.Separator
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class ConnectionEditListDialog(context: Context, connectionDatumItems: List<IConnectionInfo>, onConnectionEditListRegister: (List<ConnectionServerInfoItem>) -> Unit) : View.OnClickListener {

	private val mContext = context
	private val mConnectionDataKari = mutableListOf<ConnectionServerInfoItem>()
	private val inflater = LayoutInflater.from(mContext)
	private val mDialog: AlertDialog
	private val view: View?
	private val itemTable: LinearLayout

	override fun onClick(v: View?) {
		mDialog.show()
	}

	init {
		mConnectionDataKari.clear()
		view = inflater.inflate(R.layout.dialog_connectioneditlist, null)
		itemTable = view.findViewById(R.id.LinearLayout_ItemTable)
		//アプリ選択ダイアログ用ビューの生成
		val cancelButton = view.findViewById<Button>(R.id.Button_Cancel)
		val okButton = view.findViewById<Button>(R.id.Button_OK)
		val addButton = view.findViewById<Button>(R.id.Button_Add)
		// 接続先編集一覧ダイアログを開く
		val builder = AlertDialog.Builder(mContext)
		builder.setView(view)
		mDialog = builder.create()
		mDialog.setCancelable(false)

		// アイテムを追加する
		for (item in connectionDatumItems) {
			if (item is ConnectionServerInfo) addEditableView(item.toConnectionServerInfoItem())// 書き換え可能
			else addStaticView(item)
		}

		// 追加ボタンが押された時
		addButton.setOnClickListener {
			// 新規追加ダイアログの作成
			ConnectionEditDialog(mContext, null) {
				// 接続先情報をビューに追加する
				addEditableView(it)
			}.showDialog()
		}

		// OKボタンが押された時
		okButton.setOnClickListener {
			onConnectionEditListRegister(mConnectionDataKari.toList())
			mDialog.dismiss()
		}
		// キャンセルボタンが押された時
		cancelButton.setOnClickListener { mDialog.cancel() }
	}

	/**
	 * 書き換え可能ビューを追加する
	 * @param connectionServerInfoItem 書き換え基礎データ
	 */
	private fun addEditableView(connectionServerInfoItem: ConnectionServerInfoItem) {
		mConnectionDataKari.add(connectionServerInfoItem)
		// セパレーターを追加する
		val separator = Separator(mContext, 2, R.color.colorLightGray)
		itemTable.addView(separator)
		val v = inflater.inflate(R.layout.item_connectionedit, null)
		val btnSetuzokusaki = v.findViewById<Button>(R.id.Button_Setuzokusaki)
		val btnDelete = v.findViewById<Button>(R.id.Button_Delete)
		btnSetuzokusaki.text = "接続先:" + connectionServerInfoItem.Address + "\u000a" + "ポート番号:" + connectionServerInfoItem.PortNo
		btnSetuzokusaki.setOnClickListener {
			// 編集ウィンドウを開く
			ConnectionEditDialog(mContext, connectionServerInfoItem) {
				// 既存項目を書き換える
				btnSetuzokusaki.text = "接続先:" + it.Address + "\u000a" + "ポート番号:" + it.PortNo
				// 既存データを書き換える
				connectionServerInfoItem.updateAll(it)
			}.showDialog()
		}
		itemTable.addView(v)
		btnDelete.setOnClickListener {
			// 削除
			itemTable.removeView(separator)
			itemTable.removeView(v)
			mConnectionDataKari.remove(connectionServerInfoItem)
		}
	}

	/**
	 * 書き換え不可ビューを追加する
	 * @param connectionServerInfoItem 書き換え基礎データ
	 */
	private fun addStaticView(connectionServerInfoItem: IConnectionInfo) {
		// セパレーターを追加する
		val separator = Separator(mContext, 2, R.color.colorLightGray)
		itemTable.addView(separator)
		val v = inflater.inflate(R.layout.item_connectionedit_noedit, null)
		val btnSetuzokusaki = v.findViewById<TextView>(R.id.TextView_Setuzokusaki)
		btnSetuzokusaki.text = "接続先:" + connectionServerInfoItem.toString()
		itemTable.addView(v)
	}
}