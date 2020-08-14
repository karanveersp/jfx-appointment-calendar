package app.controller;

import app.Main;
import app.Util;
import app.model.Appointment;
import app.model.Customer;
import app.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EditAppointmentController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(EditAppointmentController.class);

    public static Appointment selected;
    public static Stage self;
    public static boolean isAddContext;

    private final ObservableList<String> hours = FXCollections.observableArrayList(
        IntStream.range(0, 24)
            .boxed()
            .map(i -> {
                if (i <= 9) {
                    return "0" + i;
                }
                return String.valueOf(i);
            }).collect(Collectors.toList()));
    private final ObservableList<String> minutes = FXCollections.observableArrayList("00", "15", "30", "45");

    @FXML
    private ComboBox<Customer> customerDropdown;

    @FXML
    private ComboBox<User> userDropdown;

    @FXML
    private TextField typeField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private ComboBox<String> startHour;

    @FXML
    private ComboBox<String> endHour;

    @FXML
    private ComboBox<String> startMinute;

    @FXML
    private ComboBox<String> endMinute;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Label startTimeLabel;

    @FXML
    private Label endTimeLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Button saveBtn;

    @FXML
    private Label typeLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField titleField;


    @FXML
    public void save() {

        try {
            Main.getDb().startTransaction();
            if (isAddContext) {
                add();
            } else {
                update();
            }
        } catch (Exception e) {
            Util.showInfoAlert("An error occurred", e.getMessage());
            logger.error(e.getMessage(), e);
            Main.getDb().rollback();
            return;
        }
        Main.getDb().commit();
        Main.getObservables().refreshAll(Main.getDaos());
        logger.debug("Completed saving appointment");
        self.close();
    }

    private void add() {
        Appointment appt = getAppointmentFromFields();
        Main.getDaos().getAppointmentDao().insert(appt);
    }

    private Appointment getAppointmentFromFields() {
        long customerId = customerDropdown.getSelectionModel().getSelectedItem().getCustomerId();
        long userId = userDropdown.getSelectionModel().getSelectedItem().getUserId();
        return new Appointment(
            customerId,
            userId,
            titleField.getText(),
            typeField.getText(),
            LocalDateTime.of(
                startDatePicker.getValue(),
                parseHourMinute(startHour.getValue(), startMinute.getValue())),
            LocalDateTime.of(endDatePicker.getValue(),
                parseHourMinute(endHour.getValue(), endMinute.getValue())),
            Main.getLoggedInUser()
        );
    }

    private LocalTime parseHourMinute(String hour, String minute) {
        return LocalTime.of(Integer.parseInt(hour), Integer.parseInt(minute));
    }

    private void update() throws Exception {
        Appointment appt = getAppointmentFromFields();
        // Reset certain fields to existing appointment
        appt.setAppointmentId(selected.getAppointmentId());
        appt.setCreateDate(selected.getCreateDate());
        appt.setCreatedBy(selected.getCreatedBy());
        boolean isOk = Main.getDaos().getAppointmentDao().update(appt);
        if (!isOk) {
            throw new Exception("While trying to update appointment");
        }
    }

    private boolean validateStartBeforeEnd() {
        if (endHour.getValue() != null && endMinute.getValue() != null && startHour.getValue() != null && startMinute.getValue() != null) {
            LocalTime endTime = parseHourMinute(endHour.getValue(), endMinute.getValue());
            LocalTime startTime = parseHourMinute(startHour.getValue(), startMinute.getValue());
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            if (LocalDateTime.of(startDate, startTime).isAfter(LocalDateTime.of(endDate, endTime))) {
                timeLabel.setText("Start must be before end date/time");
                return false;
            } else if (!withinBusinessHours(startTime, endTime)) {
                timeLabel.setText("Start and end must be within 09:00 to 15:00");
                return false;
            } else {
                timeLabel.setText("");
                return true;
            }
        }
        timeLabel.setText("");
        return false;
    }

    private boolean withinBusinessHours(LocalTime start, LocalTime end) {
        LocalTime nine = LocalTime.of(8, 59);
        LocalTime five = LocalTime.of(17, 1);
        return start.isAfter(nine) && end.isBefore(five);
    }

//    private boolean isValidTime(String s) {
//        boolean isProperFormat = s.matches("^(\\d\\d:\\d\\d:\\d\\d)$");
//        if (!isProperFormat) {
//            return false;
//        }
//        try {
//            LocalTime.parse(s);
//        } catch (DateTimeParseException e) {
//            return false;
//        }
//        return true;
//    }

    private void validateAndSetMessage(ComboBox<String> hour, ComboBox<String> minute, Label label) {
        if (hour.getSelectionModel().getSelectedItem() == null || minute.getSelectionModel().getSelectedItem() == null) {
            label.setText("Required");
        } else {
            label.setText("");
        }
    }

    private boolean allFieldsValid() {
        boolean customerSelected = customerDropdown.getSelectionModel().getSelectedItem() != null;
        boolean userSelected = userDropdown.getSelectionModel().getSelectedItem() != null;
        boolean typeFilled = validateType();
        boolean titleFilled = validateTitle();
        validateAndSetMessage(startHour, startMinute, startTimeLabel);
        validateAndSetMessage(endHour, endMinute, endTimeLabel);
        return customerSelected && userSelected && validateStartBeforeEnd() && typeFilled && titleFilled;
    }

    @FXML
    private void checkAllFields() {
        saveBtn.setDisable(!allFieldsValid());
    }

    private boolean validateTitle() {
        if (!titleField.getText().isEmpty()) {
            titleLabel.setText("");
            return true;
        }
        titleLabel.setText("Required");
        return false;
    }

    private boolean validateType() {
        if (!typeField.getText().isEmpty()) {
            typeLabel.setText("");
            return true;
        }
        typeLabel.setText("Required");
        return false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startHour.setItems(hours);
        endHour.setItems(hours);
        startMinute.setItems(minutes);
        endMinute.setItems(minutes);

        customerDropdown.setButtonCell(new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText("");
                } else {
                    setText(item.getCustomerName());
                }
            }
        });
        customerDropdown.setCellFactory(listView -> new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText("");
                } else {
                    setText(item.getCustomerName());
                }
            }
        });
        customerDropdown.setItems(Main.getObservables().getCustomers());
        userDropdown.setButtonCell(new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText("");
                } else {
                    setText(item.getUserName());
                }
            }
        });
        userDropdown.setCellFactory(listView -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText("");
                } else {
                    setText(item.getUserName());
                }
            }
        });
        userDropdown.setItems(Main.getObservables().getUsers());
        if (!isAddContext) {
            typeField.setText(selected.getType());
            titleField.setText(selected.getTitle());
            Customer existingCustomer = customerDropdown.getItems().stream().filter(c -> c.getCustomerId() == selected.getCustomerId()).findFirst().orElseThrow(null);
            customerDropdown.getSelectionModel().select(existingCustomer);
            User existingUser = userDropdown.getItems().stream().filter(u -> u.getUserId() == selected.getUserId()).findFirst().orElseThrow(null);
            userDropdown.getSelectionModel().select(existingUser);

            startDatePicker.setValue(selected.getStart().toLocalDate());
            endDatePicker.setValue(selected.getEnd().toLocalDate());

            LocalTime selectedStart = selected.getStart().toLocalTime();
            LocalTime selectedEnd = selected.getEnd().toLocalTime();
            String startMin = getNearestQuarterHour(selectedStart.getMinute());
            String endMin = getNearestQuarterHour(selectedEnd.getMinute());

            startHour.getSelectionModel().select(selectedStart.getHour());
            startMinute.getSelectionModel().select(startMin);
            endHour.getSelectionModel().select(selectedEnd.getHour());
            endMinute.getSelectionModel().select(endMin);
        } else {
            customerDropdown.getSelectionModel().selectFirst();
            userDropdown.getSelectionModel().selectFirst();
        }
        checkAllFields();
    }

    public String getNearestQuarterHour(int minute) {
        if (minute >= 0 && minute <= 7) {
            return "00";
        } else if (minute > 7 && minute <= 22) {
            return "15";
        } else if (minute > 22 && minute <= 37) {
            return "30";
        } else if (minute > 37 && minute <= 52) {
            return "45";
        } else {
            return "00";
        }
    }
}
