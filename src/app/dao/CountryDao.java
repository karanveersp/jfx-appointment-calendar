package app.dao;

import app.Util;
import app.db.BaseDao;
import app.db.Database;
import app.model.Country;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CountryDao extends BaseDao<Country> {

    public CountryDao(Database db, String tableName, String idColName) {
        super(db, tableName, idColName);
    }

    public static CountryDao getInstance(Database db) {
        return new CountryDao(db, "country", "countryId");
    }

    @Override
    protected Country parseResult(ResultSet rs) throws SQLException {
        return new Country(
            rs.getLong(idColName),
            rs.getString("country"),
            rs.getTimestamp("createDate").toLocalDateTime(),
            rs.getString("createdBy"),
            rs.getTimestamp("lastUpdate").toLocalDateTime(),
            rs.getString("lastUpdateBy")
        );
    }

    public Optional<Country> getIfExists(String country) {
        String sql = String.format("SELECT * FROM %s WHERE country='%s'", tableName, country);
        try {
            ResultSet rs = db.query(sql);
            if (rs.next()) {
                return Optional.of(parseResult(rs));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Country updated) {
        String colVals = String.join(",",
            "country=" + Util.withSingleQuotes(updated.getCountry()),
            "lastUpdate=" + Util.toUTCTimestamp(updated.getLastUpdate()),
            "lastUpdateBy=" + Util.withSingleQuotes(updated.getLastUpdateBy()));
        String updateSql = String.format("UPDATE %s SET (%s) WHERE %s = %d",
            tableName, colVals, idColName, updated.getCountryId());
        try {
            int i = db.update(updateSql);
            return i == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String insertValuesString(Country c) {
        return Stream.of(
            c.getCountryId(),
            Util.withSingleQuotes(c.getCountry()),
            Util.toUTCTimestamp(c.getCreateDate()),
            Util.withSingleQuotes(c.getCreatedBy()),
            Util.toUTCTimestamp(c.getLastUpdate()),
            Util.withSingleQuotes(c.getLastUpdateBy())
        ).map(String::valueOf).collect(Collectors.joining(","));
    }

    public Country findExistingOrCreate(String country, String createdBy) {
        return getIfExists(country).orElseGet(() -> insert(new Country(country, createdBy)));
    }

    @Override
    public Country insert(Country addition) {
        try {
            addition.setCountryId(Util.getNextAvailableId(db, idColName, tableName));
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
