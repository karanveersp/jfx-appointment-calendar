package app.controller;

import app.Main;
import app.Util;
import app.View;
import app.model.Appointment;
import app.model.Customer;
import app.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;

public class AppointmentController implements Initializable {

    @FXML
    private TableView<Appointment> appointmentTable;

    private void showEditWindow() {
        try {
            Stage stage = Util.getModalStage(View.EDIT_APPOINTMENT, Modality.APPLICATION_MODAL);
            EditAppointmentController.self = stage;
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void add() {
        EditAppointmentController.isAddContext = true;
        EditAppointmentController.selected = null;
        showEditWindow();
    }

    @FXML
    public void edit() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Util.showInfoAlert("No Appointment Selected", "Select an Appointment");
            return;
        }
        EditAppointmentController.selected = selected;
        EditAppointmentController.isAddContext = false;
        showEditWindow();
    }

    @FXML
    public void delete() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Util.showInfoAlert("No Appointment Selected", "Select an Appointment");
            return;
        }
        if (Util.confirmDialog("This will delete the appointment.")) {
            Main.getDaos().getAppointmentDao().delete(selected.getAppointmentId());
            Main.getObservables().refreshAll(Main.getDaos());
        }
    }


    public static void autoResizeColumns( TableView<?> table )
    {
        //Set the right policy
        table.setColumnResizePolicy( TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.getColumns().forEach( (column) ->
        {
            //Minimal width = columnheader
            Text t = new Text( column.getText() );
            double max = t.getLayoutBounds().getWidth();
            for ( int i = 0; i < table.getItems().size(); i++ )
            {
                //cell must not be empty
                if ( column.getCellData( i ) != null )
                {
                    t = new Text( column.getCellData( i ).toString() );
                    double calcwidth = t.getLayoutBounds().getWidth();
                    //remember new max-width
                    if ( calcwidth > max )
                    {
                        max = calcwidth;
                    }
                }
            }
            //set the new max-width with some extra space
            column.setPrefWidth( max + 10.0d );
        } );
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<Appointment, Long> id = new TableColumn<>("ID");
        TableColumn<Appointment, String> name = new TableColumn<>("Customer");
        TableColumn<Appointment, String> user = new TableColumn<>("User");
        TableColumn<Appointment, String> title = new TableColumn<>("Title");
        TableColumn<Appointment, String> type = new TableColumn<>("Type");
        TableColumn<Appointment, LocalDateTime> start = new TableColumn<>("Start");
        TableColumn<Appointment, LocalDateTime> end = new TableColumn<>("End");

        id.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        name.setCellValueFactory(a -> {
            long customerId = a.getValue().getCustomerId();
            Customer cust = Main.getObservables().getCustomers().stream().filter(c -> c.getCustomerId() == customerId).findFirst().orElseThrow(null);
            return new SimpleStringProperty(cust.getCustomerName());
        });
        user.setCellValueFactory(a -> {
            long userId = a.getValue().getUserId();
            User matchedUser = Main.getObservables().getUsers().stream().filter(u -> u.getUserId() == userId).findFirst().orElseThrow(null);
            return new SimpleStringProperty(matchedUser.getUserName());
        });
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        start.setCellFactory(column -> new TableCell<Appointment, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                }
                else {
                    setText(item.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                }
            }
        });
        end.setCellFactory(column -> new TableCell<Appointment, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                }
                else {
                    setText(item.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                }
            }
        });
        start.setCellValueFactory(new PropertyValueFactory<>("start"));
        end.setCellValueFactory(new PropertyValueFactory<>("end"));
        
        appointmentTable.getColumns().clear();
        appointmentTable.getColumns().add(id);
        appointmentTable.getColumns().add(name);
        appointmentTable.getColumns().add(user);
        appointmentTable.getColumns().add(title);
        appointmentTable.getColumns().add(type);
        appointmentTable.getColumns().add(start);
        appointmentTable.getColumns().add(end);

        appointmentTable.setItems(Main.getObservables().getAppointments());
        appointmentTable.setEditable(false);
        appointmentTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        System.out.println("Initialized appointments");
    }
}
