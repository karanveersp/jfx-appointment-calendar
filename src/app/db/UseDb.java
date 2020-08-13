package app.db;

import app.Util;
import app.dao.AppointmentDao;
import app.model.Appointment;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Properties;

public class UseDb {


    public static void main(String[] args) throws SQLException, IOException {
        Properties p = Util.loadDBProps();
        Database db = new Database(
            p.getProperty("DB_URL"), p.getProperty("DB_USER"), p.getProperty("DB_PASS")
        );
        AppointmentDao dao = AppointmentDao.getInstance(db);
        List<Appointment> appts = dao.getAll();

        Appointment newman = appts.get(1);
        System.out.println(newman.getStart() + "\t" + newman.getEnd());
        System.out.println(newman.getStart());
        System.out.println(Util.toUTCTimestamp(newman.getStart()));
//
//        UserDao userDao = new UserDao(db, "user", "userId");
//        System.out.println(userDao.authenticate("test", "test"));

//        ResultSet rs = db.query("SELECT * FROM address");
//        System.out.println(rs.getFetchSize());
//
//        System.out.println(db.update("INSERT INTO address VALUES (NULL, \"adsf\", \"adsf\", 1, \"1234\", \"1234\", CURRENT_TIMESTAMP, \"me\", CURRENT_TIMESTAMP, \"me\");"));
    }
}
