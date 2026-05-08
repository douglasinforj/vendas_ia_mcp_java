package br.com.vendasia.infra;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoMySQL {

    private static final Dotenv ENV = Dotenv.load();

    public static Connection obter() throws SQLException {
        String url = ENV.get("DB_URL");
        String user = ENV.get("DB_USER");
        String pass = ENV.get("DB_PASS");

        return DriverManager.getConnection(url, user, pass);
    }

}
