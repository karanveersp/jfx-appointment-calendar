package app.dao;

import app.db.BaseDao;
import app.db.Dao;
import app.db.Database;
import app.model.User;
import app.Util;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserDao extends BaseDao<User> implements Dao<User> {

    public UserDao(Database db, String tableName, String idColName) {
        super(db, tableName, idColName);
    }

    public static UserDao getInstance(Database db) {
        return new UserDao(db, "user", "userId");
    }

    @Override
    public boolean update(User updated) {
        String colVals = updateValuesString(updated);

        String updateSql = String.format("UPDATE %s SET (%s) WHERE %s = %d",
            tableName, colVals, idColName, updated.getUserId());

        try {
            int i = db.update(updateSql);
            return i == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public User insert(User addition) {
        try {
            addition.setUserId(Util.getNextAvailableId(db, idColName, tableName));
            String sql = String.format("INSERT INTO %s VALUES (%s)",
                tableName, insertValuesString(addition)
            );
            db.update(sql);
            return addition;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Implementation of result set parser from superclass
     * @param rs ResultSet
     * @return User
     * @throws SQLException
     */
    protected User parseResult(ResultSet rs) throws SQLException {
        return new User(rs.getLong("userId"),
            rs.getString("userName"),
            rs.getString("password"),
            rs.getTimestamp("createDate").toLocalDateTime(),
            rs.getString("createdBy"),
            rs.getTimestamp("lastUpdate").toLocalDateTime(),
            rs.getString("lastUpdateBy")
        );
    }

    /**
     * Helper method to get the string with col=val format
     * for update
     * @param u updated User
     * @return String
     */
    private String updateValuesString(User u) {
        return String.join(",", "userName" + "=" + Util.withSingleQuotes(u.getUserName()),
            "password" + "=" + Util.withSingleQuotes(u.getPassword()),
            // Set the local system date time to timestamp
            "lastUpdate" + "=" + Util.toUTCTimestamp(LocalDateTime.now()),
            "lastUpdateBy" + "=" + Util.withSingleQuotes(u.getLastUpdateBy())
            // We don't update the create time or creator
        );
    }

    /**
     * Returns string in format field1,field2,... for field values of given user
     * @param u new User
     * @return comma separated string of field values
     */
    private String insertValuesString(User u){
        return Stream.of(
            u.getUserId(),
            Util.withSingleQuotes(u.getUserName()),
            Util.withSingleQuotes(u.getPassword()),
            0,  // for active column
            Util.toUTCTimestamp(u.getCreateDate()),
            Util.withSingleQuotes(u.getCreatedBy()),
            Util.toUTCTimestamp(u.getLastUpdate()),
            Util.withSingleQuotes(u.getLastUpdateBy())
        ).map(String::valueOf).collect(Collectors.joining(","));
    }


}
