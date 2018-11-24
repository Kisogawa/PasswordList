using System;
using System.Collections.Generic;

namespace PasswordListWin
{


	/// <summary>
	/// 関数応答情報(戻り値無し)
	/// </summary>
	public class ReturnObject
	{
		// 状態(true:成功,false:失敗)
		public bool State { get; protected set; } = false;
		// 詳細
		public string Comment { get; protected set; } = null;
		// エラーオブジェクト
		public Exception CauseException { get; protected set; } = null;

		/// <summary>
		/// 成功オブジェクト生成
		/// </summary>
		public ReturnObject()
		{
			State = true;
		}

		/// <summary>
		/// コメントを設定(関数は失敗判定)
		/// </summary>
		/// <param name="oldObj">失敗原因</param>
		public ReturnObject(ReturnObject oldObj)
		{
			State = oldObj.State;
			Comment = oldObj.Comment;
			CauseException = oldObj.CauseException;
		}

		/// <summary>
		/// コメントを設定(関数は失敗判定)
		/// </summary>
		/// <param name="comment">失敗原因</param>
		public ReturnObject(string comment)
		{
			Comment = comment;
		}

		/// <summary>
		/// 例外を設定して初期化(メッセージは自動で入ります)(関数は失敗判定)
		/// </summary>
		public ReturnObject(Exception causeException)
		{
			CauseException = causeException;
			Comment = CauseException?.Message;
		}

		/// <summary>
		/// コメントと例外を設定して初期化(関数は失敗判定)
		/// </summary>
		public ReturnObject(string comment, Exception causeException)
		{
			CauseException = causeException;
			Comment = comment;
		}

		public override string ToString()
		{
			// 成功している場合
			if (State) return "成功";

			// 失敗している場合はエラーメッセージを良い感じに出す
			string text = Comment;
			if (CauseException != null)
			{
				text += "\n" + CauseException.ToString();
			}
			return text;
		}
	}

	/// <summary>
	/// 関数応答情報
	/// </summary>
	public class ReturnObject<T> : ReturnObject
	{
		/// <summary>
		/// 戻り値
		/// </summary>
		public T Value { get; private set; } = default(T);

		/// <summary>
		/// 戻り値を設定して初期化(成功判定が入ります)
		/// </summary>
		public ReturnObject(T value) : base()
		{
			Value = value;
		}

		/// <summary>
		/// コメントを設定
		/// </summary>
		/// <param name="value">新たな戻り値</param>
		/// <param name="oldObj">元オブジェクト</param>
		public ReturnObject(T value, ReturnObject oldObj) : base()
		{
			Value = value;
			State = oldObj.State;
			Comment = oldObj.Comment;
			CauseException = oldObj.CauseException;
		}

		/// <summary>
		/// コメントを設定(関数は失敗判定)
		/// </summary>
		/// <param name="comment">失敗原因</param>
		public ReturnObject(T value, string comment) : base(comment)
		{
			Value = value;
		}

		/// <summary>
		/// 戻り値と例外を設定して初期化(メッセージは自動で入ります)
		/// </summary>
		public ReturnObject(T value, Exception causeException) : base(causeException)
		{
			Value = value;
		}

		/// <summary>
		/// コメントと例外を設定して初期化(関数は失敗判定)
		/// </summary>
		public ReturnObject(T value, string comment, Exception causeException) : base(comment, causeException)
		{
			Value = value;
		}

		// 演算子のオーバーロード
		//二項+演算子をオーバーロードする
		public static bool operator ==(ReturnObject<T> c1, ReturnObject<T> c2)
		{
			if ((object)c2 == null) return false;
			return c1.Value.Equals(c2.Value);
		}

		// 演算子のオーバーロード
		//二項+演算子をオーバーロードする
		public static bool operator !=(ReturnObject<T> c1, ReturnObject<T> c2)
		{
			if ((object)c2 == null) return false;
			return !(c1 == c2);
		}

		public override bool Equals(object obj)
		{
			var @object = obj as ReturnObject<T>;
			return @object != null &&
				   EqualityComparer<T>.Default.Equals(Value, @object.Value);
		}

		public override int GetHashCode()
		{
			return -1937169414 + EqualityComparer<T>.Default.GetHashCode(Value);
		}

		public override string ToString()
		{
			// 成功している場合
			if (State) return Value.ToString();

			// 失敗している場合はエラーメッセージを良い感じに出す
			string text = Comment;
			if (CauseException != null)
			{
				text += "\n" + CauseException.ToString();
			}
			return text;
		}
	}
}
