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
import logic.RemoveLogic;

/**
 * Servlet implementation class Remove
 */
@WebServlet("/Remove")
public class Remove extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * 登録情報の削除
	 *
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 文字コードを設定する
		request.setCharacterEncoding("UTF-8");
		// 情報を取得する
		String ItemID_str = request.getParameter("ItemID");

		// SQLコネクションを取得する
		Object object = getServletContext().getAttribute("SQLConnection");
		SQLConnection sqlConnection = (SQLConnection) object;
		// 設定情報を取得する
		Object object2 = getServletContext().getAttribute("Setting");
		Setting setting = (Setting) object2;

		try {
			// 登録操作モジュール作成
			String removeligic = RemoveLogic.Remove(ItemID_str, sqlConnection, setting);
			response.setCharacterEncoding("UTF-8");
			response.setContentLength(removeligic.getBytes().length);
			PrintWriter printWriter = response.getWriter();
			printWriter.print(removeligic);
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
