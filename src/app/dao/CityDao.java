package app.dao;

import app.Util;
import app.db.BaseDao;
import app.db.Dao;
import app.db.Database;
import app.model.City;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CityDao extends BaseDao<City> implements Dao<City> {

    public CityDao(Database db, String tableName, String idColName) {
        super(db, tableName, idColName);
    }

    public static CityDao getInstance(Database db) {
        return new CityDao(db, "city", "cityId");
    }

    @Override
    protected City parseResult(ResultSet rs) throws SQLException {
        return new City(rs.getLong("cityId"),
            rs.getString("city"),
            rs.getLong("countryId"),
            rs.getTimestamp("createDate").toLocalDateTime(),
            rs.getString("createdBy"),
            rs.getTimestamp("lastUpdate").toLocalDateTime(),
            rs.getString("lastUpdateBy")
        );
    }

    @Override
    public boolean update(City updated) {
        String colVals = String.join(",",
            "city=" + Util.withSingleQuotes(updated.getCity()),
            "countryId=" + updated.getCountryId(),
            "lastUpdate=" + Util.toUTCTimestamp(LocalDateTime.now()),
            "lastUpdateBy=" + Util.withSingleQuotes(updated.getLastUpdateBy())
        );
        String updateSql = String.format("UPDATE %s SET (%s) WHERE %s = %d",
            tableName, colVals, idColName, updated.getCityId());
        try {
            int i = db.update(updateSql);
            return i == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String insertValuesString(City c) {
        return Stream.of(
            c.getCityId(),
            Util.withSingleQuotes(c.getCity()),
            c.getCountryId(),
            Util.toUTCTimestamp(c.getCreateDate()),
            Util.withSingleQuotes(c.getCreatedBy()),
            Util.toUTCTimestamp(c.getLastUpdate()),
            Util.withSingleQuotes(c.getLastUpdateBy())
        ).map(String::valueOf).collect(Collectors.joining(","));
    }

    @Override
    public City insert(City addition) {
        try {
            addition.setCityId(Util.getNextAvailableId(db, idColName, tableName));
            String sql = String.format("INSERT INTO %s VALUES (%s)",
                tableName, insertValuesString(addition));
            db.update(sql);
            return addition;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Optional<City> getIfExists(String city) {
        String sql = String.format("SELECT * FROM %s WHERE city='%s'", tableName, city);
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

    public City findExistingOrCreate(String city, long countryId, String loggedInUser) {
        return getIfExists(city).orElseGet(() -> insert(new City(city, countryId, loggedInUser)));
    }
}
