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
 * 登録変更操作クラス
 */
public class ModificationLogic {

	/**
	 * 登録変更操作モジュール作成
	 *
	 * @param ItemID_str  変更するアイテムID（必須）
	 * @param name        新たに登録する名前(null可)
	 * @param userID      新たに登録するユーザーID(null可)
	 * @param password    新たに登録するパスワード(null可)
	 * @param comment     新たに登録するコメント(null可)
	 * @param mailAddress 新たに登録するメールアドレス(null可)
	 * @param connection  SQLデータベースとの接続情報（必須）
	 * @param setting 設定情報
	 * @throws InternalServerException サーバー内部エラー
	 */
	public static String Modification(String ItemID_str, String name, String userID, String password, String comment, String mailAddress, SQLConnection connection, Setting setting) throws InternalServerException {
		// 返却用Entity
		PasswordItem passwordItem = null;
		// 状態クラス
		ResponseInfomation responseInfomation;
		// 暗号鍵
		String key = "";

		// 文字列を数字に変換する
		try {
			// 暗号化キーを取得する
			key = Angou.getAESKey(setting);

			// 引数の要素を復号する
			ItemID_str = Angou.decode(ItemID_str, key);
			name = Angou.decode(name, key);
			userID = Angou.decode(userID, key);
			password = Angou.decode(password, key);
			comment = Angou.decode(comment, key);
			mailAddress = Angou.decode(mailAddress, key);

			int ItemID = Integer.parseInt(ItemID_str);
			// 登録変更用Entity作成
			PasswordItem passwordItem_Modification = new PasswordItem();
			// 情報の登録
			passwordItem_Modification.setItemID(ItemID);
			passwordItem_Modification.setName(name);
			passwordItem_Modification.setUserID(userID);
			passwordItem_Modification.setPassword(password);
			passwordItem_Modification.setComment(comment);
			passwordItem_Modification.setMailAddress(mailAddress);
			passwordItem = PassListDAO.Modification(passwordItem_Modification, connection);
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