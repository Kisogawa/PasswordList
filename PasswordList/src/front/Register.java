package front;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.SQLConnection;
import entity.Setting;
import exception.InternalServerException;
import logic.RegisterLogic;

/**
 * 登録操作を行う際のURL
 */
@WebServlet("/Register")
public class Register extends HttpServlet {

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// POSTで送信されたとき，中身のデータを確認する

		// 文字コードを設定する
		request.setCharacterEncoding("UTF-8");
		// 情報を取得する
		String Name = request.getParameter("Name");
		String UserID = request.getParameter("UserID");
		String Password = request.getParameter("Password");
		String Comment = request.getParameter("Comment");
		String MailAddress = request.getParameter("MailAddress");

		// SQLコネクションを取得する
		Object object = getServletContext().getAttribute("SQLConnection");
		SQLConnection sqlConnection = (SQLConnection) object;
		// 設定情報を取得する
		Object object2 = getServletContext().getAttribute("Setting");
		Setting setting = (Setting) object2;

		// 登録操作モジュール作成
		try {
			String registerligic = RegisterLogic.Register(Name, UserID, Password, Comment, MailAddress, sqlConnection, setting);
			response.setCharacterEncoding("UTF-8");
			response.setContentLength(registerligic.getBytes().length);
			PrintWriter printWriter = response.getWriter();
			printWriter.print(registerligic);
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
