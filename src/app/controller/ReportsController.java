package app.controller;

import app.Main;
import app.Util;
import app.model.Appointment;
import app.model.Customer;
import app.model.MonthYear;
import app.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class ReportsController implements Initializable {

    final String typesByMonth = "Appointment types by month";
    final String userSchedule = "Schedule for each consultant";
    final String customerSchedule = "Schedule for each customer";

    private final ObservableList<String> reportTypes = FXCollections.observableArrayList(
        typesByMonth,
        userSchedule,
        customerSchedule);

    @FXML
    private ComboBox<String> reportSelector;


    @FXML
    private void exportReport() {
        String contentAsString = "";

        if (reportSelector.getValue() == null) {
            Util.showInfoAlert("Selection required", "Please select a report type");
            return;
        }
        switch (reportSelector.getValue()) {
            case typesByMonth:
                contentAsString = generateTypesByMonthReport();
                break;
            case userSchedule:
                contentAsString = generateAllUserSchedulesReport();
                break;
            case customerSchedule:
                contentAsString = generateAllCustomerSchedulesReport();
                break;
            default:
                throw new NoSuchElementException();
        }

        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter f = new FileChooser.ExtensionFilter("Txt formats", "*.txt");
        chooser.getExtensionFilters().add(f);
        File file = chooser.showSaveDialog(Main.getStage());
        if (file != null && !contentAsString.isEmpty()) {
            Util.saveText(file, contentAsString);
        }
    }

    private String generateTypesByMonthReport() {
        List<Appointment> sortedAppts = Main.getObservables().getSortedAppointments();
        List<MonthYear> monthsAndYears = sortedAppts.stream().map(a -> new MonthYear(a.getStart().getMonth(), a.getStart().getYear())).collect(Collectors.toList());
        LinkedHashMap<String, List<String>> typesByMonth = new LinkedHashMap<>();
        for (MonthYear monthYear : monthsAndYears) {
            List<String> types = sortedAppts.stream().filter(a -> a.getStart().getMonth().equals(monthYear.getMonth()) &&
                a.getStart().getYear() == monthYear.getYear())
                .map(Appointment::getType).distinct().collect(Collectors.toList());
            typesByMonth.put(monthYear.toString(), types);
        }
        StringBuilder content = new StringBuilder();
        typesByMonth.forEach((monthYear, types) -> {
            content.append(monthYear);
            content.append("\n");
            content.append(String.join(", ", types));
            content.append("\n");
        });
        return content.toString();
    }

    private String generateAllCustomerSchedulesReport() {
        List<Appointment> sortedAppts = Main.getObservables().getSortedAppointments();
        List<Customer> cs = sortedAppts.stream().map(a -> Main.getObservables().getCustomerById(a.getCustomerId())).collect(Collectors.toList());
        LinkedHashMap<String, List<Appointment>> apptsByCustomer = new LinkedHashMap<>();
        for (Customer c : cs) {
            List<Appointment> appts = sortedAppts.stream().filter(a -> a.getCustomerId() == c.getCustomerId()).collect(Collectors.toList());
            apptsByCustomer.put(c.getCustomerName(), appts);
        }
        StringBuilder content = new StringBuilder();
        apptsByCustomer.forEach((cust, appts) -> {
            content.append(cust);
            content.append("\n");
            appts.forEach(a -> {
                User u = Main.getObservables().getUserById(a.getUserId());
                String timeAndDuration = Util.formatStartTimeWithDuration(a.getStart(), a.getEnd(), true);
                content.append(String.format("%s - Meeting with %s", timeAndDuration, u.getUserName()));
                content.append("\n");
            });
            content.append("\n");
        });
        return content.toString();
    }

    private String generateAllUserSchedulesReport() {
        List<Appointment> sortedAppts = Main.getObservables().getSortedAppointments();
        List<User> users = sortedAppts.stream().map(a -> Main.getObservables().getUserById(a.getUserId())).collect(Collectors.toList());
        LinkedHashMap<String, List<Appointment>> apptsByUser = new LinkedHashMap<>();
        for (User user : users) {
            List<Appointment> appts = sortedAppts.stream().filter(a -> a.getUserId() == user.getUserId()).collect(Collectors.toList());
            apptsByUser.put(user.getUserName(), appts);
        }
        StringBuilder content = new StringBuilder();
        apptsByUser.forEach((user, appts) -> {
            content.append(user);
            content.append("\n");
            appts.forEach(a -> {
                Customer c = Main.getObservables().getCustomerById(a.getCustomerId());
                String timeAndDuration = Util.formatStartTimeWithDuration(a.getStart(), a.getEnd(), true);
                content.append(String.format("%s - Meeting with %s", timeAndDuration, c.getCustomerName()));
                content.append("\n");
            });
            content.append("\n");
        });
        return content.toString();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reportSelector.setItems(reportTypes);
    }
}

