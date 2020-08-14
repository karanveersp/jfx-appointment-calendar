package app.dao;

import app.Util;
import app.db.BaseDao;
import app.db.Database;
import app.model.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AppointmentDao extends BaseDao<Appointment> {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentDao.class);

    public AppointmentDao(Database db, String tableName, String idColName) {
        super(db, tableName, idColName);
    }

    public static AppointmentDao getInstance(Database db) {
        return new AppointmentDao(db, "appointment", "appointmentId");
    }

    @Override
    protected Appointment parseResult(ResultSet rs) throws SQLException {
        return new Appointment(
            rs.getLong(idColName),
            rs.getLong("customerId"),
            rs.getLong("userId"),
            rs.getString("title"),
            rs.getString("type"),
            rs.getTimestamp("start").toLocalDateTime(),
            rs.getTimestamp("end").toLocalDateTime(),
            rs.getTimestamp("createDate").toLocalDateTime(),
            rs.getString("createdBy"),
            rs.getTimestamp("lastUpdate").toLocalDateTime(),
            rs.getString("lastUpdateBy")
        );
    }

    @Override
    public boolean update(Appointment updated) {
        String colVals = String.join(",",
            "customerId=" + updated.getCustomerId(),
            "userId=" + updated.getUserId(),
            "title=" + Util.withSingleQuotes(updated.getTitle()),
            "type=" + Util.withSingleQuotes(updated.getType()),
            "start=" + Util.toUTCTimestamp(updated.getStart()),
            "end=" + Util.toUTCTimestamp(updated.getEnd()),
            "lastUpdate=" + Util.toUTCTimestamp(updated.getLastUpdate()),
            "lastUpdateBy=" + Util.withSingleQuotes(updated.getLastUpdateBy())
            );

        String updateSql = String.format("UPDATE %s SET %s WHERE %s = %d",
            tableName, colVals, idColName, updated.getAppointmentId());

        logger.debug("Update appointment sql: " + updateSql);
        try {
            int i = db.update(updateSql);
            return i == 1;
        } catch (SQLException e) {
            logger.error("While updating appointment", e);
        }
        return false;
    }
    private String insertValuesString(Appointment a) {
        return Stream.of(
            a.getAppointmentId(),
            a.getCustomerId(),
            a.getUserId(),
            Util.withSingleQuotes(a.getTitle()),
            Util.withSingleQuotes("not needed"),
            Util.withSingleQuotes("not needed"),
            Util.withSingleQuotes("not needed"),
            Util.withSingleQuotes(a.getType()),
            Util.withSingleQuotes("not needed"),
            Util.toUTCTimestamp(a.getStart()),
            Util.toUTCTimestamp(a.getEnd()),
            Util.toUTCTimestamp(a.getCreateDate()),
            Util.withSingleQuotes(a.getCreatedBy()),
            Util.toUTCTimestamp(a.getLastUpdate()),
            Util.withSingleQuotes(a.getLastUpdateBy())
        ).map(String::valueOf).collect(Collectors.joining(","));
    }

    public boolean deleteByCustomerId(long customerId) {
        try {
            String sql = String.format("DELETE FROM %s WHERE customerId=%d", tableName, customerId);
            int i = db.update(sql);
            return i != 0;
        } catch (SQLException e) {
            logger.error("While deleting appointment by customer id: {}", customerId, e);
        }
        return false;
    }

    @Override
    public Appointment insert(Appointment addition) {
        try {
            addition.setAppointmentId(Util.getNextAvailableId(db, idColName, tableName));
            String sql = String.format("INSERT INTO %s VALUES (%s)",
                tableName, insertValuesString(addition)
            );
            logger.debug("Inserting appointment SQL: {}", sql);
            db.update(sql);
            return addition;
        } catch (SQLException e) {
            logger.error("While inserting appointment", e);
        }
        return null;
    }
}
