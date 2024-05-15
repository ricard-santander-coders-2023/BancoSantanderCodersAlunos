package infra.factory.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionFactory {
    public static Connection getConexao(String url, String usuario, String senha) throws SQLException {
       return DriverManager.getConnection(url,usuario,senha);
    }
}
