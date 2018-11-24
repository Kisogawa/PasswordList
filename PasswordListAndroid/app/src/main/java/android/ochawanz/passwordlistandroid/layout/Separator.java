package android.ochawanz.passwordlistandroid.layout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

//セパレーター
public class Separator extends LinearLayout {
	public Separator(Context context) {
		super(context);

		//レイアウトを設定する
		View view = new View(context);
		addView(view);
		setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
	}

	public Separator(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);

		//高さ情報を取得する
		//レイアウトを設定する
		View view = new View(context);
		addView(view);
	}

	public Separator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		//レイアウトを設定する
		View view = new View(context);
		addView(view);
		setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
	}

	public Separator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);

		//レイアウトを設定する
		View view = new View(context);
		setBackgroundResource(0);
		addView(view);
		setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
	}

	//ソースから使用する場合はこの初期化処理を使用すると楽
	public Separator(Context context, int pxHeght, int colorResID) {
		super(context);

		//レイアウトを設定する
		View view = new View(context);
		setBackgroundResource(colorResID);
		addView(view);
		setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pxHeght));
	}
}
