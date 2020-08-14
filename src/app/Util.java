package app;

import app.db.Database;
import app.db.UseDb;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Properties;

public class Util {
    private static final Logger logger = LoggerFactory.getLogger(Util.class);

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
            logger.error("While loading fxml " + view.getName(), ex);
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
}
