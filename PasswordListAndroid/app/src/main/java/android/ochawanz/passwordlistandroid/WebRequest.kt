package android.ochawanz.passwordlistandroid

import android.ochawanz.passwordlistandroid.interfaces.IRequest
import android.ochawanz.passwordlistandroid.jsonobject.PasswordItem
import android.ochawanz.passwordlistandroid.jsonobject.ResponseJson
import android.ochawanz.passwordlistandroid.utility.ReturnObject
import android.ochawanz.passwordlistandroid.utility.WebRequestInfomation
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import java.net.SocketTimeoutException
import java.net.URL
import java.net.URLEncoder
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit


/**
 * サーバーとの接続に関するクラス
 */
class WebRequest(connectionServerInfo: ConnectionServerInfo) : IRequest {

	private val mConnectionServerInfo = connectionServerInfo

	/**
	 * 接続先URL(メイン)
	 */
	private val mRequestUri: String
		get() = "http://" + mConnectionServerInfo.Address + ":" + mConnectionServerInfo.PortNo + "/"

	/**
	 * 接続ライブラリ(OKHTTP)
	 */
	private val mOkHttpClient: OkHttpClient

	init {
		// OKHttpクライアントの生成
		val okHttpClientBuilder = OkHttpClient.Builder()
		// タイムアウトの設定(30秒)
		okHttpClientBuilder.connectTimeout(30, TimeUnit.SECONDS)
		okHttpClientBuilder.readTimeout(30, TimeUnit.SECONDS)
		okHttpClientBuilder.writeTimeout(30, TimeUnit.SECONDS)

		// 設定
		mOkHttpClient = okHttpClientBuilder.build()
	}

	/**
	 * 一覧情報を取得する
	 */
	override fun List(webRequestInfomation: WebRequestInfomation) = GlobalScope.async(Dispatchers.IO) {
		try {
			val requestResult = Request("PasswordList/List", webRequestInfomation = webRequestInfomation, coroutineScope = this)

			// 失敗している場合(キャンセル等)
			if (!requestResult.state) {
				webRequestInfomation.status = "中止"
				return@async ReturnObject(ResponseJson(), requestResult)
			}

			webRequestInfomation.status = "データ解析中"

			val jsonString = mConnectionServerInfo.KeyManager.Decode(requestResult.Value)

			// Jsonオブジェクトにパースする
			val mapper = ObjectMapper()
			val hoge = mapper.readValue(jsonString.Value, ResponseJson().javaClass)

			webRequestInfomation.status = "完了"

			// パース結果の成功失敗を取り込む
			return@async if (hoge.responseInfomation.result == "Success") ReturnObject(hoge)    // 成功
			else ReturnObject(hoge, "[サーバー応答メッセージ]\n" + hoge.responseInfomation.comment)    // サーバー応答より失敗

		} catch (e: JsonProcessingException) {
			webRequestInfomation.status = "中止"
			e.printStackTrace()
			return@async ReturnObject(ResponseJson(), e, "受信データの解析に失敗しました")
		} catch (e: Exception) {
			webRequestInfomation.status = "中止"
			e.printStackTrace()
			return@async ReturnObject(ResponseJson(), e)
		}
	}

	/**
	 * 詳細情報を取得する
	 */
	override fun Show(passwordItem: PasswordItem, webRequestInfomation: WebRequestInfomation) = GlobalScope.async {
		try {

			val itemid = mConnectionServerInfo.KeyManager.Encode(passwordItem.ItemID.toString())
			if (!itemid.state) return@async ReturnObject(ResponseJson(), itemid)

			val requestResult = Request("PasswordList/Show", mapOf(Pair("ItemID", itemid.Value)), webRequestInfomation, this)

			// 失敗している場合(キャンセル等)
			if (!requestResult.state) {
				webRequestInfomation.status = "中止"
				return@async ReturnObject(ResponseJson(), requestResult)
			}

			webRequestInfomation.status = "データ解析中"

			val jsonString = mConnectionServerInfo.KeyManager.Decode(requestResult.Value)

			// Jsonオブジェクトにパースする
			val mapper = ObjectMapper()
			val hoge = mapper.readValue(jsonString.Value, ResponseJson().javaClass)

			webRequestInfomation.status = "完了"

			// パース結果の成功失敗を取り込む
			return@async if (hoge.responseInfomation.result == "Success") ReturnObject(hoge)    // 成功
			else ReturnObject(hoge, "[サーバー応答メッセージ]\n" + hoge.responseInfomation.comment)    // サーバー応答より失敗

		} catch (e: JsonProcessingException) {
			webRequestInfomation.status = "中止"
			e.printStackTrace()
			return@async ReturnObject(ResponseJson(), e, "受信データの解析に失敗しました")
		} catch (e: Exception) {
			webRequestInfomation.status = "中止"
			e.printStackTrace()
			return@async ReturnObject(ResponseJson(), e)
		}
	}

	/**
	 * 登録する
	 */
	override fun Register(passwordItem: PasswordItem, webRequestInfomation: WebRequestInfomation) = GlobalScope.async {
		try {

			val name = mConnectionServerInfo.KeyManager.Encode(passwordItem.Name!!)
			if (!name.state) return@async ReturnObject(ResponseJson(), name)
			val userid = mConnectionServerInfo.KeyManager.Encode(passwordItem.UserID!!)
			if (!userid.state) return@async ReturnObject(ResponseJson(), userid)
			val password = mConnectionServerInfo.KeyManager.Encode(passwordItem.Password!!)
			if (!password.state) return@async ReturnObject(ResponseJson(), password)
			val comment = mConnectionServerInfo.KeyManager.Encode(passwordItem.Comment!!)
			if (!comment.state) return@async ReturnObject(ResponseJson(), comment)
			val mailAddress = mConnectionServerInfo.KeyManager.Encode(passwordItem.MailAddress!!)
			if (!mailAddress.state) return@async ReturnObject(ResponseJson(), mailAddress)

			// リクエスト生成
			val query = mapOf(
					Pair("Name", name.Value),
					Pair("UserID", userid.Value),
					Pair("Password", password.Value),
					Pair("Comment", comment.Value),
					Pair("MailAddress", mailAddress.Value)
			)

			val requestResult = Request("PasswordList/Register", query, webRequestInfomation, this)

			// 失敗している場合(キャンセル等)
			if (!requestResult.state) {
				webRequestInfomation.status = "中止"
				return@async ReturnObject(ResponseJson(), requestResult)
			}

			webRequestInfomation.status = "データ解析中"

			val jsonString = mConnectionServerInfo.KeyManager.Decode(requestResult.Value)

			// Jsonオブジェクトにパースする
			val mapper = ObjectMapper()
			val hoge = mapper.readValue(jsonString.Value, ResponseJson().javaClass)

			webRequestInfomation.status = "完了"

			// パース結果の成功失敗を取り込む
			return@async if (hoge.responseInfomation.result == "Success") ReturnObject(hoge)    // 成功
			else ReturnObject(hoge, "[サーバー応答メッセージ]\n" + hoge.responseInfomation.comment)    // サーバー応答より失敗

		} catch (e: JsonProcessingException) {
			webRequestInfomation.status = "中止"
			e.printStackTrace()
			return@async ReturnObject(ResponseJson(), e, "受信データの解析に失敗しました")
		} catch (e: Exception) {
			webRequestInfomation.status = "中止"
			e.printStackTrace()
			return@async ReturnObject(ResponseJson(), e)
		}
	}

	/**
	 * 変更する
	 */
	override fun Update(passwordItem: PasswordItem, webRequestInfomation: WebRequestInfomation) = GlobalScope.async {
		try {
			if (passwordItem.ItemID == 0) return@async ReturnObject(ResponseJson(), "ItemIDは0に出来ません")

			val itemid = mConnectionServerInfo.KeyManager.Encode(passwordItem.ItemID.toString())
			if (!itemid.state) return@async ReturnObject(ResponseJson(), itemid)

			// ID要素は必須
			val query = mutableMapOf(
					Pair("ItemID", itemid.Value)
			)

			if (passwordItem.Name != null) {
				val name = mConnectionServerInfo.KeyManager.Encode(passwordItem.Name!!)
				if (!name.state) return@async ReturnObject(ResponseJson(), name)
				query["Name"] = name.Value
			}
			if (passwordItem.UserID != null) {
				val userid = mConnectionServerInfo.KeyManager.Encode(passwordItem.UserID!!)
				if (!userid.state) return@async ReturnObject(ResponseJson(), userid)
				query["UserID"] = userid.Value
			}
			if (passwordItem.Password != null) {
				val password = mConnectionServerInfo.KeyManager.Encode(passwordItem.Password!!)
				if (!password.state) return@async ReturnObject(ResponseJson(), password)
				query["Password"] = password.Value
			}
			if (passwordItem.Comment != null) {
				val comment = mConnectionServerInfo.KeyManager.Encode(passwordItem.Comment!!)
				if (!comment.state) return@async ReturnObject(ResponseJson(), comment)
				query["Comment"] = comment.Value
			}
			if (passwordItem.MailAddress != null) {
				val mailAddress = mConnectionServerInfo.KeyManager.Encode(passwordItem.MailAddress!!)
				if (!mailAddress.state) return@async ReturnObject(ResponseJson(), mailAddress)
				query["MailAddress"] = mailAddress.Value
			}

			val requestResult = Request("PasswordList/Modification", query, webRequestInfomation, this)

			// 失敗している場合(キャンセル等)
			if (!requestResult.state) {
				webRequestInfomation.status = "中止"
				return@async ReturnObject(ResponseJson(), requestResult)
			}

			webRequestInfomation.status = "データ解析中"

			val jsonString = mConnectionServerInfo.KeyManager.Decode(requestResult.Value)

			// Jsonオブジェクトにパースする
			val mapper = ObjectMapper()
			val hoge = mapper.readValue(jsonString.Value, ResponseJson().javaClass)

			webRequestInfomation.status = "完了"

			// パース結果の成功失敗を取り込む
			return@async if (hoge.responseInfomation.result == "Success") ReturnObject(hoge)    // 成功
			else ReturnObject(hoge, "[サーバー応答メッセージ]\n" + hoge.responseInfomation.comment)    // サーバー応答より失敗

		} catch (e: JsonProcessingException) {
			webRequestInfomation.status = "中止"
			e.printStackTrace()
			return@async ReturnObject(ResponseJson(), e, "受信データの解析に失敗しました")
		} catch (e: Exception) {
			webRequestInfomation.status = "中止"
			e.printStackTrace()
			return@async ReturnObject(ResponseJson(), e)
		}
	}

	/**
	 * 削除する
	 */
	override fun Remove(passwordItem: PasswordItem, webRequestInfomation: WebRequestInfomation) = GlobalScope.async {
		try {
			if (passwordItem.ItemID == 0) return@async ReturnObject(ResponseJson(), "ItemIDは0に出来ません")


			val itemid = mConnectionServerInfo.KeyManager.Encode(passwordItem.ItemID.toString())
			if (!itemid.state) return@async ReturnObject(ResponseJson(), itemid)

			// リクエスト生成
			val query = mapOf(
					Pair("ItemID", itemid.Value)
			)

			val requestResult = Request("PasswordList/Remove", query, webRequestInfomation, this)

			// 失敗している場合(キャンセル等)
			if (!requestResult.state) {
				webRequestInfomation.status = "中止"
				return@async ReturnObject(ResponseJson(), requestResult)
			}

			webRequestInfomation.status = "データ解析中"

			val jsonString = mConnectionServerInfo.KeyManager.Decode(requestResult.Value)

			// Jsonオブジェクトにパースする
			val mapper = ObjectMapper()
			val hoge = mapper.readValue(jsonString.Value, ResponseJson().javaClass)

			webRequestInfomation.status = "完了"

			// パース結果の成功失敗を取り込む
			return@async if (hoge.responseInfomation.result == "Success") ReturnObject(hoge)    // 成功
			else ReturnObject(hoge, "[サーバー応答メッセージ]\n" + hoge.responseInfomation.comment)    // サーバー応答より失敗

		} catch (e: JsonProcessingException) {
			webRequestInfomation.status = "中止"
			e.printStackTrace()
			return@async ReturnObject(ResponseJson(), e, "受信データの解析に失敗しました")
		} catch (e: Exception) {
			webRequestInfomation.status = "中止"
			e.printStackTrace()
			return@async ReturnObject(ResponseJson(), e)
		}
	}

	/**
	 * Webリクエストを行う(サブに自動接続)
	 */
	private fun Request(path: String, data: Map<String, String?> = mapOf(), webRequestInfomation: WebRequestInfomation, coroutineScope: CoroutineScope?): ReturnObject<String> {

		// クエリ生成
		val stringBuilder = StringBuilder()
		for (item in data) {
			if (item.value == null) continue
			val encItem = URLEncoder.encode(item.value, "UTF-8")
			stringBuilder.append(item.key + "=" + encItem + "&")
		}


		// 関数内関数
		fun access1(url: URL): ReturnObject<String> {
			// Webリクエスト情報設定
			webRequestInfomation.init()
			webRequestInfomation.hostName = url.host
			webRequestInfomation.portNo = url.port
			webRequestInfomation.path = url.path
			webRequestInfomation.status = "接続開始...[" + webRequestInfomation.path + "]"

			val mediaType = MediaType.parse("application/x-www-form-urlencoded")

			// リクエスト生成
			val request = okhttp3.Request.Builder()
					.url(url)
					.addHeader("Content-Type", "application/x-www-form-urlencoded")
					.post(RequestBody.create(mediaType, stringBuilder.toString()))
					.build()

			val call = mOkHttpClient.newCall(request)

			webRequestInfomation.status = "接続用タスク起動...[" + webRequestInfomation.path + "]"

			// HTTPステータスコード
			var HTTPStatusCode = -1

			// 別スレッドで実行し待機する
			val tsk = GlobalScope.async {
				try {
					webRequestInfomation.status = "サーバー接続...[" + webRequestInfomation.path + "]"
					// 接続
					val response = call.execute()
							?: return@async ReturnObject(byteArrayOf(), "接続に失敗しました")

					webRequestInfomation.status = "データ取得開始[" + webRequestInfomation.path + "]"
					// ファイルサイズのヘッダ取得
					val sizeString = response.header("Content-Length")

					// HTTPステータスコード
					HTTPStatusCode = response.code()

					// サイズの取得
					val size = sizeString?.toIntOrNull()

					// Body取得
					val responseBody = response.body()
							?: return@async ReturnObject(byteArrayOf(), "応答がありません")

					// ソースの取得
					val source = responseBody.source()
							?: return@async ReturnObject(byteArrayOf(), "データの取得に失敗しました")

					// ストリームの取得
					val byteStream = source.inputStream()
							?: return@async ReturnObject(byteArrayOf(), "ストリームの取得に失敗しました")

					// 設定
					webRequestInfomation.totalFileSize = size

					// 応答データ(Byte)
					val responseData = mutableListOf<Byte>()

					webRequestInfomation.status = "データ取得中[" + webRequestInfomation.path + "]"
					// データの取得
					while (true) {
						val d = byteStream.read()
						if (d == -1) break
//					Thread.sleep(1)
						responseData.add(d.toByte())
						webRequestInfomation.fileSize = responseData.size
						if (!isActive) {
							// キャンセル要求がある場合は，データ取得を中止する
							throw return@async ReturnObject(byteArrayOf(), "キャンセル要求")
						}
					}

					return@async ReturnObject(responseData.toByteArray())
				} catch (e: java.lang.Exception) {
					return@async ReturnObject(byteArrayOf(), e)
				}
			}

			// 終了を待機する
			while (!tsk.isCompleted) {
				Thread.sleep(10)
				if (coroutineScope?.isActive == false) {
					// キャンセル要求があった場合は接続の中止
					call.cancel()
					tsk.cancel()
					webRequestInfomation.status = "キャンセル要求"
				}
			}

			if (tsk.isCancelled) return ReturnObject("", CancellationException("ユーザー操作にタスクが中止されました"), "キャンセルされました")

			// 結果の取得
			val response = tsk.getCompleted()

			// バイト配列を文字列に変換する
			val response_string = String(response.Value)

			// HTTPステータスコードから成功，失敗を判定する
			return when (HTTPStatusCode) {
				200 -> ReturnObject(response_string)    // 成功
				400 -> ReturnObject(response_string, "リクエストが不正です[" + HTTPStatusCode + "]")
				401 -> ReturnObject(response_string, "認証が必要です[" + HTTPStatusCode + "]")
				403 -> ReturnObject(response_string, "アクセスが禁止されています[" + HTTPStatusCode + "]")
				404 -> ReturnObject(response_string, "ページが存在しません[" + HTTPStatusCode + "]")
				405 -> ReturnObject(response_string, "許可されていないメソッドです[" + HTTPStatusCode + "]")
				406 -> ReturnObject(response_string, "サーバー側が受理できませんでした[" + HTTPStatusCode + "]")
				407 -> ReturnObject(response_string, "プロキシ認証が必要です[" + HTTPStatusCode + "]")
				408 -> ReturnObject(response_string, "リクエストがタイムアウトしました(サーバー応答)[" + HTTPStatusCode + "]")
				411 -> ReturnObject(response_string, "Content-Lengthヘッダが必要です[" + HTTPStatusCode + "]")
				413 -> ReturnObject(response_string, "ペイロードが大きすぎます[" + HTTPStatusCode + "]")
				414 -> ReturnObject(response_string, "URIが長すぎます[" + HTTPStatusCode + "]")
				418 -> ReturnObject(response_string, "私はティーポット[" + HTTPStatusCode + "]")
				500 -> ReturnObject(response_string, "サーバー内部エラー[" + HTTPStatusCode + "]")
				503 -> ReturnObject(response_string, "サービス利用不可[" + HTTPStatusCode + "]")
				505 -> ReturnObject(response_string, "サポートしていないHTTpバージョンです[" + HTTPStatusCode + "]")
				else -> ReturnObject(response_string, "不明なHTTPステータスコードが返却されました[" + HTTPStatusCode + "]")
			}
		}

		try {
			// 接続先URLを設定する(メイン処理)
			val url = URL(mRequestUri + path)
			return access1(url)
		} catch (e: java.net.ConnectException) {
			// リトライ対象のエラー
			webRequestInfomation.status = "接続に失敗"
			return ReturnObject("", e, "接続に失敗しました")
		} catch (e: UnknownHostException) {
			// エラー
			webRequestInfomation.status = "接続先不明"
			return ReturnObject("", e, "ホスト名を解決できませんでした")
		} catch (e: SocketTimeoutException) {
			// タイムアウト
			webRequestInfomation.status = "タイムアウト"
			return ReturnObject("", e, "タイムアウトしました")
		} catch (e: Exception) {
			// エラー
			return ReturnObject("", e)
		}
	}
}