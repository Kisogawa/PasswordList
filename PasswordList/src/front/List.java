package front;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.SQLConnection;
import entity.Setting;
import exception.InternalServerException;
import logic.ListLogic;

/**
 * Servlet implementation class List
 */
@WebServlet("/List")
public class List extends HttpServlet {

	/**
	 * 登録IDと名前をすべて取得する
	 *
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 文字コードを設定する
		request.setCharacterEncoding("UTF-8");

		// 引数無し

		// SQLコネクションを取得する
		Object object = getServletContext().getAttribute("SQLConnection");
		SQLConnection sqlConnection = (SQLConnection) object;
		// 設定情報を取得する
		Object object2 = getServletContext().getAttribute("Setting");
		Setting setting = (Setting) object2;

		// リストモジュール作成
		try {
			String listLogic = ListLogic.List(sqlConnection, setting);
			response.setCharacterEncoding("UTF-8");
			response.setContentLength(listLogic.getBytes().length);
			PrintWriter printWriter = response.getWriter();
			printWriter.print(listLogic);
			printWriter.flush();
			printWriter.close();
		} catch (InternalServerException e) {
			e.printStackTrace();
			// 500番エラー
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}
}
