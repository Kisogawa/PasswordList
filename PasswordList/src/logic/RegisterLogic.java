package logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import angou.Angou;
import dao.PassListDAO;
import dao.SQLConnection;
import entity.PasswordItem;
import entity.Setting;
import exception.FatalException;
import exception.InternalServerException;
import exception.NormalException;
import json.ResponseJson;
import response.ResponseInfomation;

/**
 * 登録操作クラス
 */
public class RegisterLogic {

	/**
	 * 登録操作モジュール作成
	 *
	 * @param name        新たに登録する名前
	 * @param userID      新たに登録するユーザーID
	 * @param password    新たに登録するパスワード
	 * @param comment     新たに登録するコメント
	 * @param mailAddress 新たに登録するメールアドレス
	 * @param setting 設定情報
	 * @throws InternalServerException サーバー内部エラー
	 */
	public static String Register(String name, String userID, String password, String comment, String mailAddress, SQLConnection connection, Setting setting) throws InternalServerException {
		// 登録用Entity作成
		PasswordItem passwordItem = new PasswordItem();

		// 登録Entity
		PasswordItem RegisterdPasswordItem = null;

		// 状態クラス
		ResponseInfomation responseInfomation;
		// 暗号鍵
		String key = "";

		// 登録操作(DAO取得)
		try {
			// 暗号化キーを取得する
			key = Angou.getAESKey(setting);

			// 引数の要素を復号する
			name = Angou.decode(name, key);
			userID = Angou.decode(userID, key);
			password = Angou.decode(password, key);
			comment = Angou.decode(comment, key);
			mailAddress = Angou.decode(mailAddress, key);

			passwordItem.setName(name);
			passwordItem.setUserID(userID);
			passwordItem.setPassword(password);
			passwordItem.setComment(comment);
			passwordItem.setMailAddress(mailAddress);

			RegisterdPasswordItem = PassListDAO.Register(passwordItem, connection);
			responseInfomation = ResponseInfomation.Success();
		} catch (FatalException e) {
			e.printStackTrace();
			responseInfomation = new ResponseInfomation();
			responseInfomation.setResult("Failed");
			responseInfomation.setComment(e.getMessage());
		} catch (NormalException e) {
			e.printStackTrace();
			responseInfomation = new ResponseInfomation();
			responseInfomation.setResult("Failed");
			responseInfomation.setComment(e.getMessage());
		}

		// Json化クラス生成
		ResponseJson responseJson = new ResponseJson(responseInfomation, CommonLogic.CreateList(RegisterdPasswordItem));
		ObjectMapper mapper = new ObjectMapper();
		// Jsonの整形を行う
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		String json = null;
		try {
			json = mapper.writeValueAsString(responseJson);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		// 暗号化を行う
		try {
			return Angou.encode(json, key);
		} catch (FatalException e) {
			throw new InternalServerException("暗号化失敗", e);
		}
	}
}
