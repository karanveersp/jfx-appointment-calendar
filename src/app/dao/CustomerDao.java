package app.dao;

import app.Util;
import app.db.BaseDao;
import app.db.Dao;
import app.db.Database;
import app.model.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomerDao extends BaseDao<Customer> implements Dao<Customer> {

    public CustomerDao(Database db, String tableName, String idColName) {
        super(db, tableName, idColName);
    }

    public static CustomerDao getInstance(Database db) {
        return new CustomerDao(db, "customer", "customerId");
    }

    @Override
    protected Customer parseResult(ResultSet rs) throws SQLException {
        return new Customer(rs.getLong("customerId"),
            rs.getString("customerName"),
            rs.getLong("addressId"),
            rs.getInt("active"),
            rs.getTimestamp("createDate").toLocalDateTime(),
            rs.getString("createdBy"),
            rs.getTimestamp("lastUpdate").toLocalDateTime(),
            rs.getString("lastUpdateBy")
        );
    }

    @Override
    public boolean update(Customer updated) {
        String colVals = updateValuesString(updated);
        String updateSql = String.format("UPDATE %s SET %s WHERE %s = %d",
            tableName, colVals, idColName, updated.getCustomerId());
        try {
            int i = db.update(updateSql);
            return i == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String updateValuesString(Customer c) {
        return String.join(",",
            "customerName=" + Util.withSingleQuotes(c.getCustomerName()),
            "addressId=" + c.getAddressId(),
            "lastUpdate=" + Util.toUTCTimestamp(c.getLastUpdate()),
            "lastUpdateBy=" + Util.withSingleQuotes(c.getLastUpdateBy())
        );
    }

    private String insertValuesString(Customer c) {
        return Stream.of(
            c.getCustomerId(),
            Util.withSingleQuotes(c.getCustomerName()),
            c.getAddressId(),
            0, // for active column
            Util.toUTCTimestamp(c.getCreateDate()),
            Util.withSingleQuotes(c.getCreatedBy()),
            Util.toUTCTimestamp(c.getLastUpdate()),
            Util.withSingleQuotes(c.getLastUpdateBy())
        ).map(String::valueOf).collect(Collectors.joining(","));
    }

    @Override
    public Customer insert(Customer addition) {
        try {
            addition.setCustomerId(Util.getNextAvailableId(db, idColName, tableName));
            String sql = String.format("INSERT INTO %s VALUES (%s)",
                tableName, insertValuesString(addition));
            db.update(sql);
            return addition;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
