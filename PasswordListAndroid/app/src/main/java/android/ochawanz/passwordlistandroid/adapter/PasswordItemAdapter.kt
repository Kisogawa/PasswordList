package android.ochawanz.passwordlistandroid.adapter

import android.ochawanz.passwordlistandroid.ConnectionServerInfo
import android.ochawanz.passwordlistandroid.activity.MainActivity
import android.ochawanz.passwordlistandroid.interfaces.IConnectionInfo
import android.ochawanz.passwordlistandroid.jsonobject.PasswordItem
import android.ochawanz.passwordlistandroid.utility.EditasswordItemDialog
import android.ochawanz.passwordlistandroid.utility.ItemSelectDialog
import android.ochawanz.passwordlistandroid.utility.PasswordItemDialog
import android.ochawanz.passwordlistandroid.utility.RemovePasswordItemDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.item_passwordlistitem.view.*


class PasswordItemAdapter(mainActivity: MainActivity, resource: Int, objects: MutableList<PasswordItem>, connectionInfo: IConnectionInfo, updateFunc: () -> Unit) : ArrayAdapter<PasswordItem>(mainActivity, resource, objects) {
	private val mResource = resource
	private val mItems = objects
	private val mInflater = LayoutInflater.from(mainActivity)
	private val mConnectionInfo = connectionInfo
	private val mUpdateFunc = updateFunc
	private val mMainActivity = mainActivity

	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
		val view = convertView ?: mInflater.inflate(mResource, null)

		val item = mItems[position]

		// 項目が選択された時
		view.TextView_PasswordListItem.setOnClickListener {
			PasswordItemDialog(mMainActivity, mConnectionInfo, item).showDialog()
		}

		// 項目が長押しされた時(読み取り専用でないとき)
		if (!mMainActivity.mReadOnlyMode) {
			view.TextView_PasswordListItem.setOnLongClickListener {
				// ダイアログ表示(表示，編集，削除)
				val item1 = ItemSelectDialog.ItemSelectDialogItem("表示", View.OnClickListener {
					PasswordItemDialog(mMainActivity, mConnectionInfo, item).showDialog()
				})
				val item2 = ItemSelectDialog.ItemSelectDialogItem("編集", View.OnClickListener {
					EditasswordItemDialog(mMainActivity, mConnectionInfo, item, mUpdateFunc).showDialog()
				})
				val item3 = ItemSelectDialog.ItemSelectDialogItem("削除", View.OnClickListener {
					RemovePasswordItemDialog(mMainActivity, mConnectionInfo, item, mUpdateFunc).showDialog()
				})
				ItemSelectDialog(mMainActivity, item.toString(), listOf(item1, item2, item3)).showDailog()
				return@setOnLongClickListener true
			}
		}


		view.TextView_PasswordListItem.text = item.Name

		return view
	}
}