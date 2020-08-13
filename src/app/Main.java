package app;

import app.db.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class Main extends Application  {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    // Data fields
    private static Stage stage = null;
    private static ViewSettable controller;
    private static AppObservables observables;
    private static AppDaos daos;
    private static String userName;
    private static Database db;

    public static boolean isLoggedIn() {
        return userName != null && !userName.isEmpty();
    }

    public static String getLoggedInUser() {
        return userName;
    }

    public static void setLoggedInUser(String user) {
        userName = user;
    }

    public static Stage getStage() {
        return stage;
    }

    public static Database getDb() {
        return db;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + View.MAIN.getName()));
        loader.load();
        controller = loader.getController();
        Parent root = loader.getRoot();
        primaryStage.setTitle("JFX Appointment Calendar");
        primaryStage.setScene(new Scene(root));
        Main.stage = primaryStage;
        primaryStage.show();
    }

    @Override
    public void init() {
        try {
            db = initializeDatabase();
            daos = new AppDaos(db);
            observables = AppObservables.initializeFromDaos(daos);
        } catch (IOException e) {
            logger.error("While initializing database or observables", e);
        }
    }

    public static AppDaos getDaos() {
        return daos;
    }

    private Database initializeDatabase() throws IOException {
        // Initialize database
        try {
            Properties p = Util.loadDBProps();
            return new Database(
                p.getProperty("DB_URL"), p.getProperty("DB_USER"), p.getProperty("DB_PASS")
            );
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static ViewSettable getViewSetter() { return controller; }
    public static AppObservables getObservables() {
        return observables;
    }
}

