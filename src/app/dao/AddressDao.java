package app.dao;

import app.Util;
import app.db.BaseDao;
import app.db.Dao;
import app.db.Database;
import app.model.Address;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AddressDao extends BaseDao<Address> implements Dao<Address> {
    public AddressDao(Database db, String tableName, String idColName) {
        super(db, tableName, idColName);
    }

    public static AddressDao getInstance(Database db) {
        return new AddressDao(db, "address", "addressId");
    }

    @Override
    public boolean update(Address updated) {
        String colVals = updateValuesString(updated);

        String updateSql = String.format("UPDATE %s SET %s WHERE %s = %d",
            tableName, colVals, idColName, updated.getAddressId());

        System.out.println("UPDATE SQL: " + updateSql);
        try {
            int i = db.update(updateSql);
            return i == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Address insert(Address newAddress) {
        try {
            newAddress.setAddressId(Util.getNextAvailableId(db, "addressId", "address"));
            String sql = String.format("INSERT INTO %s VALUES (%s)",
                tableName, insertValuesString(newAddress)
            );
            db.update(sql);
            return newAddress;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected Address parseResult(ResultSet rs) throws SQLException {
        return new Address(rs.getLong("addressId"),
            rs.getString("address"),
            rs.getString("address2"),
            rs.getLong("cityId"),
            rs.getString("postalCode"),
            rs.getString("phone"),
            rs.getTimestamp("createDate").toLocalDateTime(),
            rs.getString("createdBy"),
            rs.getTimestamp("lastUpdate").toLocalDateTime(),
            rs.getString("lastUpdateBy")
        );
    }


    private String updateValuesString(Address a) {
        return String.join(",", "address" + "=" + Util.withSingleQuotes(a.getAddress()),
            "address2=" + Util.withSingleQuotes(a.getAddress2()),
            "cityId=" + a.getCityId(),
            "postalCode=" + Util.withSingleQuotes(a.getPostalCode()),
            "phone=" + Util.withSingleQuotes(a.getPhone()),
            // Set the local system date time to timestamp
            "lastUpdate=" + Util.toUTCTimestamp(a.getLastUpdate()),
            "lastUpdateBy=" + Util.withSingleQuotes(a.getLastUpdateBy())
            // We don't update the create time or creator
        );
    }

    private String insertValuesString(Address a) {
        return Stream.of(
            a.getAddressId(),
            Util.withSingleQuotes(a.getAddress()),
            Util.withSingleQuotes(a.getAddress2()),
            a.getCityId(),
            Util.withSingleQuotes(a.getPostalCode()),
            Util.withSingleQuotes(a.getPhone()),
            Util.toUTCTimestamp(a.getCreateDate()),
            Util.withSingleQuotes(a.getCreatedBy()),
            Util.toUTCTimestamp(a.getLastUpdate()),
            Util.withSingleQuotes(a.getLastUpdateBy())
        ).map(String::valueOf).collect(Collectors.joining(","));
    }

}
