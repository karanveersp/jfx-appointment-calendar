package app;

import app.db.Database;
import app.db.UseDb;
import app.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Util {

    public static Properties loadDBProps() throws IOException {
        InputStream is = UseDb.class.getResourceAsStream("/app.properties");
        Properties p = new Properties();
        p.load(is);
        return p;
    }


    public static void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static boolean confirmDialog(String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        StringBuilder sb = new StringBuilder(content);
        for (int i = 0; i < content.length(); i += 200) {
            sb.insert(i, "\n");
        }

        Label t = new Label(sb.toString());
        alert.getDialogPane().setContent(t);
        alert.setTitle("Confirm");
        alert.setHeaderText("Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static String withSingleQuotes(String in) {
        return "'" + in + "'";
    }

    public static String toUTCTimestamp(LocalDateTime dateTime) {
        Instant i = dateTime.atZone(ZoneId.systemDefault()).toInstant();
        Timestamp ts =Timestamp.valueOf(LocalDateTime.ofInstant(i, ZoneId.of("Z")));
        return withSingleQuotes(ts.toString());
    }

    public static Parent loadFxml(View view) throws IOException {
        final String viewPath = "/view/" + view.getName();
        Parent parent;
        try {
            parent = FXMLLoader.load(Util.class.getResource(viewPath));
            return parent;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public static Stage getModalStage(View view, Modality modality) throws IOException {
        Parent root = Util.loadFxml(view);
        Stage stage = new Stage();
        stage.initOwner(Main.getStage());
        stage.initModality(modality);
        stage.setScene(new Scene(root));
        return stage;
    }

    public static void signInRequiredAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Login Required");
        alert.showAndWait();
    }

    public static String getFormattedDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }

    public static long getNextAvailableId(Database db, String idColName, String tableName) throws SQLException {
        PreparedStatement stat;
        ResultSet rs;
        long result = 0;
        String sql = String.format("SELECT MAX(%s) AS max_id FROM %s", idColName, tableName);
        stat = db.getConnection().prepareStatement(sql);
        rs = stat.executeQuery();
        if (rs.next()) {
            result = rs.getInt("max_id") + 1;
        }
        return result;
    }

    public static void saveText(File file, String text) {
        try {
            PrintWriter writer= new PrintWriter(file);
            writer.println(text);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String formatStartTimeWithDuration(LocalDateTime start, LocalDateTime end, boolean withDate) {
        String date = start.toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
        if (withDate) {
            return date + " - At " + start.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " for " + getDuration(start, end);
        }
        return "At " + start.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " for " + getDuration(start, end);
    }

    public static String getDuration(LocalDateTime start, LocalDateTime end) {
        Duration dur = Duration.between(start, end);
        long millis = dur.toMillis();
        return String.format("%02dH %02dM",
            TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))
        );
    }

    public static void logSignIn() {
        try {
            FileWriter writer = new FileWriter("app.log", true);
            String now = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
            writer.append(String.format("%s : %s logged in\n", now, Main.getLoggedInUserName()));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logSignOut() {
        try {
            FileWriter writer = new FileWriter("app.log", true);
            String now = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
            writer.append(String.format("%s : %s logged out\n", now, Main.getLoggedInUserName()));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
