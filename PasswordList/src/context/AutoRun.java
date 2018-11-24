package context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import dao.SQLConnection;
import entity.Setting;

public class AutoRun implements ServletContextListener {

	/**
	 * Tomcat起動時処理
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {

		// 実際のパス取得
		String realPath = sce.getServletContext().getRealPath("/settings/Setting.yml");
		System.out.println(realPath);

		// 設定インスタンス
		Setting setting = null;

		// 設定ファイルの存在確認を行う
		File file = new File(realPath);
		if (file.exists()) {
			// ファイルが存在すれば，パース
			Yaml y = new Yaml();
			try (final InputStream in = Files.newInputStream(Paths.get(file.getPath()))) {
				setting = y.loadAs(in, Setting.class);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (org.yaml.snakeyaml.error.YAMLException e) {
				e.printStackTrace();
			}
		}

		if (setting == null) {
			setting = new Setting();
		}

		// 出力用ストリーム
		try (final OutputStream out = Files.newOutputStream(Paths.get(file.getPath()))) {
			final OutputStreamWriter outw = new OutputStreamWriter(out, Charset.forName("UTF-8"));

			// LocalDateTimeの変換設定
			Representer representer = new Representer();
			representer.addClassTag(Setting.class, Tag.MAP);// 完全修飾名を表示しない

			// 書式の設定
			DumperOptions options = new DumperOptions();
			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
			options.setIndicatorIndent(2);
			options.setIndent(8);

			// YAMLに書き出し
			final Yaml y = new Yaml(representer, options);
			y.dump(setting, outw);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (org.yaml.snakeyaml.error.YAMLException e) {
			e.printStackTrace();
		}

		// コンテキストに記録
		sce.getServletContext().setAttribute("Setting", setting);

		// DBに接続する
		sce.getServletContext().setAttribute("SQLConnection", new SQLConnection(setting));
	}

	/**
	 * Tomcatシャットダウン時処理
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Object object = sce.getServletContext().getAttribute("SQLConnection");
		if (object instanceof SQLConnection) {
			SQLConnection sqlConnection = (SQLConnection) object;
			sqlConnection.close();
		}
	}

}
