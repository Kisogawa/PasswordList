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
 * 登録IDと名前のリスト取得クラス
 */
public class ListLogic {

	/**
	 * 登録IDと名前のリスト取得処理
	 *
	 * @param connection 接続情報
	 * @param setting 設定情報
	 * @throws InternalServerException サーバー内部エラー
	 */
	public static String List(SQLConnection connection, Setting setting) throws InternalServerException {
		// Entity
		PasswordItem[] passwordItems = new PasswordItem[0];
		// 状態クラス
		ResponseInfomation responseInfomation;
		// 暗号鍵
		String key = "";

		// 登録操作(DAO取得)
		try {
			// 暗号化キーを取得する
			key = Angou.getAESKey(setting);
			passwordItems = PassListDAO.List(connection);
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
		ResponseJson responseJson = new ResponseJson(responseInfomation, passwordItems);
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
