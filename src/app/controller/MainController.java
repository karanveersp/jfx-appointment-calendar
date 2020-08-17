package app.controller;

import app.Main;
import app.ViewSettable;
import app.View;
import app.Util;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable, ViewSettable {
    @FXML
    private BorderPane mainBorderPane;


    @FXML
    private void logoutUser() {
        if (Main.isLoggedIn()) {
            Util.logSignOut();
            Main.setLoggedInUser(null);
        }
        set(View.LOGIN);
    }

    @FXML
    private void toCustomers() {
        if (!Main.isLoggedIn()) {
            Util.signInRequiredAlert();
            return;
        }
        set(View.CUSTOMERS);
    }

    @FXML
    private void toAppointments() {
        if (!Main.isLoggedIn()) {
            Util.signInRequiredAlert();
            return;
        }
        set(View.APPOINTMENTS);
    }

    @FXML
    private void toCalendar() {
        if (!Main.isLoggedIn()) {
            Util.signInRequiredAlert();
            return;
        }
        set(View.CALENDAR);
    }

    @FXML
    private void toReports() {
        if (!Main.isLoggedIn()) {
            Util.signInRequiredAlert();
            return;
        }
        set(View.REPORTS);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logoutUser();
    }

    @Override
    public void set(View view) {
        try {
            mainBorderPane.setCenter(Util.loadFxml(view));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

