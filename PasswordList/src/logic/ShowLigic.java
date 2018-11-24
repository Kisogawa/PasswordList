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
 * 登録情報取得クラス
 */
public class ShowLigic {

	/**
	 * 詳細情報の取得
	 * @param ItemID_str アイテムID
	 * @param connection 接続情報
	 * @param setting 設定情報
	 * @return 詳細情報
	 * @throws InternalServerException サーバー内部エラー
	 */
	public static String Show(String ItemID_str, SQLConnection connection, Setting setting) throws InternalServerException {
		// 返却用Entity
		PasswordItem passwordItem = null;
		// 状態クラス
		ResponseInfomation responseInfomation;
		// 暗号鍵
		String key = "";
		// 文字列を数字に変換する
		try {
			// 暗号鍵を取得する
			key = Angou.getAESKey(setting);
			// 引数の要素を復号する
			ItemID_str = Angou.decode(ItemID_str, key);

			int ItemID = Integer.parseInt(ItemID_str);
			passwordItem = PassListDAO.Show(ItemID, connection);
			responseInfomation = ResponseInfomation.Success();
		} catch (NumberFormatException e) {
			// 数値に変換出来なかった
			responseInfomation = new ResponseInfomation();
			responseInfomation.setResult("Failed");
			responseInfomation.setComment("正しいIDを設定してください");
		} catch (FatalException | NormalException e) {
			e.printStackTrace();
			responseInfomation = new ResponseInfomation();
			responseInfomation.setResult("Failed");
			responseInfomation.setComment(e.getMessage());
		}

		// Json化クラス生成
		ResponseJson responseJson = new ResponseJson(responseInfomation, CommonLogic.CreateList(passwordItem));
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
