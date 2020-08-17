package app.controller;

import app.Main;
import app.Util;
import app.model.Appointment;
import app.model.AppointmentsForWeek;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CalendarController implements Initializable {
    private final LinkedHashMap<String, List<Appointment>> monthlyAppts = new LinkedHashMap<>();
    private final LinkedHashMap<String, AppointmentsForWeek> weeklyApptsMap = new LinkedHashMap<>();
    private final ArrayList<String> weeklyApptsIndexToKey = new ArrayList<>();
    private final ArrayList<String> monthlyApptsIndexToKey = new ArrayList<>();
    private int monthYearIndex = 0;
    private int weekIndex = 0;

    @FXML
    private Label headerLabel;

    @FXML
    private RadioButton weeklyRadio;

    @FXML
    private RadioButton monthlyRadio;

    @FXML
    private TreeView<String> weeklyTreeView;

    @FXML
    private ListView<Appointment> monthlyListView;

    @FXML
    private Button nextBtn;

    @FXML
    private Button prevBtn;

    @FXML
    private void onPrev() {
        if (weeklyRadio.isSelected()) {
            // prev week
            if (!prevBtn.isDisabled() && weekIndex > 0) {
                weekIndex--;
                updateWeeklyTreeView(weekIndex);
            }
        } else {
            // prev month
            if (!prevBtn.isDisabled() && monthYearIndex > 0) {
                monthYearIndex--;
                updateMonthlyList(monthYearIndex);
            }
        }
    }

    private void updateMonthlyList(int index) {
        String key = monthlyApptsIndexToKey.get(index);
        headerLabel.setText(key);
        monthlyListView.getItems().setAll(monthlyAppts.get(key));
        prevBtn.setDisable(index - 1 < 0);
        nextBtn.setDisable(index + 1 == monthlyApptsIndexToKey.size());
    }


    private void updateWeeklyTreeView(int index) {
        String key = weeklyApptsIndexToKey.get(index);
        headerLabel.setText(key);
        TreeItem<String> rootItem = new TreeItem<>("Weekly Appointments");
        AppointmentsForWeek thisWeek = weeklyApptsMap.get(key);
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                continue;
            }
            rootItem.getChildren().add(getTreeForDay(day, thisWeek));
        }
        weeklyTreeView.setRoot(rootItem);
        weeklyTreeView.setShowRoot(false);
        prevBtn.setDisable(index - 1 < 0);
        nextBtn.setDisable(index + 1 == weeklyApptsIndexToKey.size());
    }

    @FXML
    private void onNext() {
        if (weeklyRadio.isSelected()) {
            // advance next week
            if (!nextBtn.isDisabled() && weekIndex + 1 != weeklyApptsIndexToKey.size()) {
                weekIndex++;
                updateWeeklyTreeView(weekIndex);
            }
        } else {
            // advance next monthly
            if (!nextBtn.isDisabled() && monthYearIndex + 1 != monthlyApptsIndexToKey.size()) {
                monthYearIndex++;
                updateMonthlyList(monthYearIndex);
            }
        }
    }

    private void initializeWeeklyAppts(List<Appointment> sortedAppts) {
        LocalDateTime min = sortedAppts.get(0).getStart();
        LocalDateTime max = sortedAppts.get(sortedAppts.size()-1).getEnd();
        List<AppointmentsForWeek> weeksBetween = new ArrayList<>();
        LocalDate temp = min.toLocalDate();
        while(!temp.isAfter(max.toLocalDate())) {
            // end of week
            AppointmentsForWeek thisWeeksAppts = new AppointmentsForWeek(temp, sortedAppts);
            weeksBetween.add(thisWeeksAppts);
            // next monday
            temp = temp.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusDays(7);
        }

        for (AppointmentsForWeek appts : weeksBetween) {
            weeklyApptsMap.put(appts.getDuration(), appts);
            weeklyApptsIndexToKey.add(appts.getDuration());
        }
    }

    private void initializeMonthlyAppts(List<Appointment> sortedAppts) {
        // Building a list of month/year combinations from
        // start of first appointment to end of last appointment.
        // And then building an ordered map from m/y combination
        // to appointments filtered for that month.
        LocalDateTime min = sortedAppts.get(0).getStart();
        LocalDateTime max = sortedAppts.get(sortedAppts.size()-1).getEnd();
        List<LocalDate> monthsBetween = new ArrayList<>();
        LocalDate temp = LocalDate.of(min.getYear(), min.getMonth(), 1);
        while (!temp.isAfter(max.toLocalDate())) {
            monthsBetween.add(temp);
            temp = temp.plusMonths(1);
        }
        List<String> yearMonthValues = monthsBetween.stream().map(t -> t.getMonth().name() + " " + t.getYear())
            .collect(Collectors.toList());

        for (String yearMonVal : yearMonthValues) {
            monthlyApptsIndexToKey.add(yearMonVal);
            List<Appointment> filtered = sortedAppts.stream().filter(a -> {
                Month mon = Month.valueOf(yearMonVal.split(" ")[0]);
                int year = Integer.parseInt(yearMonVal.split(" ")[1]);
                return a.getStart().getMonth().equals(mon) && a.getStart().getYear() == year;
            })
                .collect(Collectors.toList());
            monthlyAppts.put(yearMonVal, filtered);
        }
    }

    private TreeItem<String> appointmentAsTreeItem(Appointment appt) {
        String customer = Main.getObservables().getCustomerById(appt.getCustomerId()).getCustomerName();
        String user  = Main.getObservables().getUserById(appt.getUserId()).getUserName();
        String duration = Util.formatStartTimeWithDuration(appt.getStart(), appt.getEnd(), false);
        String asStr = String.format("%s - %s meeting with %s", duration, customer, user);
        return new TreeItem<>(asStr);
    }

    private TreeItem<String> getTreeForDay (DayOfWeek day, AppointmentsForWeek weeklyAppts) {
        TreeItem<String> givenDay = new TreeItem<>(day.name() + " - " + Util.getFormattedDate(weeklyAppts.getDateByDay(day)));
        for (Appointment appt : weeklyAppts.getAppointmentsByDay(day)) {
            givenDay.getChildren().add(appointmentAsTreeItem(appt));
        }
        return givenDay;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Appointment> sortedAppts = Main.getObservables().getAppointments()
            .stream()
            .sorted(Comparator.comparing(Appointment::getStart))
            .collect(Collectors.toList());

        initializeMonthlyAppts(sortedAppts);
        initializeWeeklyAppts(sortedAppts);

        monthlyListView.setCellFactory(lview -> new ListCell<Appointment>() {
            @Override
            protected void updateItem(Appointment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String customer = Main.getObservables().getCustomerById(item.getCustomerId()).getCustomerName();
                    String user =Main.getObservables().getUserById(item.getUserId()).getUserName();
                    setText(String.format("%s - %s meeting with %s ",
                        Util.formatStartTimeWithDuration(item.getStart(), item.getEnd(), true),
                        customer, user));
                }
            }
        });

        weeklyRadio.setOnAction(event -> {
            if (weeklyRadio.isSelected()) {
                updateWeeklyTreeView(weekIndex);
                weeklyTreeView.setVisible(true);
                monthlyListView.setVisible(false);
            }
        });

        monthlyRadio.setOnAction(event -> {
            if (monthlyRadio.isSelected()) {
                updateMonthlyList(monthYearIndex);
                weeklyTreeView.setVisible(false);
                monthlyListView.setVisible(true);
            }
        });


        weeklyRadio.selectedProperty().setValue(true);
        weeklyRadio.fireEvent(new ActionEvent());
    }
}
