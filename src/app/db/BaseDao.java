package app.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public abstract class BaseDao<T> implements Dao<T> {
    protected final Database db;
    protected final String tableName;
    protected final String idColName;

    protected BaseDao(Database db, String tableName, String idColName) {
        this.db = db;
        this.tableName = tableName;
        this.idColName = idColName;
    }

    protected abstract T parseResult(ResultSet rs) throws SQLException;

    @Override
    public List<T> getAll() {
        List<T> result = new ArrayList<>();
        try {
            ResultSet rs = db.query(String.format("SELECT * FROM %s", tableName));
            while (rs.next()) {
                T a = parseResult(rs);
                result.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Optional<T> getById(long id) {
        Optional<T> res = Optional.empty();
        try {
            String sql = String.format("SELECT * FROM %s WHERE %s = %d", tableName, idColName, id);
            ResultSet rs = db.query(sql);
            if (rs.next()) {
                res = Optional.of(parseResult(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }


    @Override
    public boolean delete(long id) {
        String sql = String.format("DELETE FROM %s WHERE %s = %d", tableName, idColName, id);
        System.out.println("DELETE SQL: " + sql);
        try {
            int i = db.update(sql);
            return i == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }




}
