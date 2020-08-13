package app.db;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.*;

public class Database {
    private final DataSource dataSource;
    private Connection conn;

    public Database(String url, String user, String password) {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setURL(url);
        ds.setUser(user);
        ds.setPassword(password);
        dataSource = ds;
    }

    public void startTransaction() throws SQLException {
        getConnection().createStatement().execute("START TRANSACTION");
    }

    public void rollback() {
        try {
            getConnection().createStatement().execute("ROLLBACK");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void commit()   {
        try {
            getConnection().createStatement().execute("COMMIT");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Connection getConnection() {
        if (conn != null) {
            return conn;
        }
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public ResultSet query(String query) throws SQLException {
        Connection c = getConnection();
        Statement stmt = c.createStatement();
        return stmt.executeQuery(query);
    }

    public int update(String sql) throws SQLException {
        Connection c = getConnection();
        Statement stmt = c.createStatement();
        return stmt.executeUpdate(sql);
    }
}
