package app.controller;

import app.Main;
import app.Util;
import app.model.Appointment;
import app.model.Customer;
import app.model.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HomeController implements Initializable {
    @FXML
    private Label welcomeLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        welcomeLabel.setText("Welcome " + Main.getLoggedInUser());
        // Show an alert if appointment in next 15 minutes.
        String appointmentsWithin15Mins = Main.getObservables().getAppointments().stream().filter(
            appt -> {
                LocalDateTime fifteenMinutesFromNow = LocalDateTime.now().plusMinutes(15);
                return fifteenMinutesFromNow.isAfter(appt.getStart()) && fifteenMinutesFromNow.isBefore(appt.getEnd());
            }
        ).map(appt -> {
            String customer = Main.getObservables().getCustomers().stream()
                .filter(c -> c.getCustomerId() == appt.getCustomerId())
                .map(Customer::getCustomerName)
                .findFirst().orElseThrow(null);
            String user = Main.getObservables().getUsers().stream()
                .filter(u -> u.getUserId() == appt.getUserId())
                .map(User::getUserName)
                .findFirst().orElseThrow(null);
            return String.format("Meeting between %s and %s",
                customer, user);
        }).collect(Collectors.joining("\n"));
        if (!appointmentsWithin15Mins.isEmpty()) {
            Util.showInfoAlert("Appointment within 15 minutes", appointmentsWithin15Mins);
        }
    }
}
