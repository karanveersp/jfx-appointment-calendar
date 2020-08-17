package app;

import app.db.Database;
import app.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Properties;

public class Main extends Application  {

    // Data fields
    private static Stage stage = null;
    private static ViewSettable controller;
    private static AppObservables observables;
    private static AppDaos daos;
    private static User loggedInUser;
    private static Database db;

    public static boolean isLoggedIn() {
        return loggedInUser != null;
    }

    public static String getLoggedInUserName() {
        return loggedInUser.getUserName();
    }

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
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
            e.printStackTrace();
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

