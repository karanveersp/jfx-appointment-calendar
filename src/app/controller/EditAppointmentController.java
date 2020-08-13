package app.controller;

import app.Main;
import app.Util;
import app.model.Appointment;
import app.model.Customer;
import app.model.User;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class EditAppointmentController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(EditAppointmentController.class);

    public static Appointment selected;
    public static Stage self;
    public static boolean isAddContext;

    @FXML
    private ComboBox<Customer> customerDropdown;

    @FXML
    private ComboBox<User> userDropdown;

    @FXML
    private TextField typeField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TextField startTimeField;

    @FXML
    private TextField endTimeField;

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
            LocalDateTime.of(startDatePicker.getValue(), LocalTime.parse(startTimeField.getText())),
            LocalDateTime.of(endDatePicker.getValue(), LocalTime.parse(endTimeField.getText())),
            Main.getLoggedInUser()
        );
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
        if (isValidTime(startTimeField.getText()) && isValidTime(endTimeField.getText())) {
            LocalTime endTime = LocalTime.parse(endTimeField.getText());
            LocalTime startTime = LocalTime.parse(startTimeField.getText());
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            if (LocalDateTime.of(startDate, startTime).isAfter(LocalDateTime.of(endDate, endTime))) {
                timeLabel.setText("Start must be before end date/time");
                return false;
            } else {
                timeLabel.setText("");
                return true;
            }
        }
        timeLabel.setText("");
        return false;
    }

    private boolean isValidTime(String s) {
        boolean isProperFormat = s.matches("^(\\d\\d:\\d\\d:\\d\\d)$");
        if (!isProperFormat) {
            return false;
        }
        try {
            LocalTime.parse(s);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    private void validateAndSetMessage(TextField field, Label label) {
        String value = field.getText();
        if (value.isEmpty()) {
            label.setText("Required");
        } else if (!isValidTime(value)) {
            label.setText("Invalid Time");
        } else {
            label.setText("");
        }
    }

    private boolean allFieldsValid() {
        boolean customerSelected = customerDropdown.getSelectionModel().getSelectedItem() != null;
        boolean userSelected = userDropdown.getSelectionModel().getSelectedItem() != null;
        boolean typeFilled = validateType();
        boolean titleFilled = validateTitle();
        validateAndSetMessage(startTimeField, startTimeLabel);
        validateAndSetMessage(endTimeField, endTimeLabel);
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
            startTimeField.setText(selected.getStart().toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME));
            endTimeField.setText(selected.getEnd().toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME));
        } else {
            customerDropdown.getSelectionModel().selectFirst();
            userDropdown.getSelectionModel().selectFirst();
        }
        checkAllFields();
    }
}
