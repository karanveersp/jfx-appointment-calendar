package app.controller;

import app.Main;
import app.Util;
import app.View;
import app.dao.AppointmentDao;
import app.dao.CustomerDao;
import app.model.Address;
import app.model.Customer;
import app.model.CustomerWithAddress;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

    @FXML
    private TableView<Customer> customerTable;

    private void showEditCustomerWindow() {
        try {
            Stage stage = Util.getModalStage(View.EDIT_CUSTOMER, Modality.APPLICATION_MODAL);
            EditCustomerController.self = stage;
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addCustomer() {
        EditCustomerController.isAddContext = true;
        EditCustomerController.selectedCustomer = null;
        showEditCustomerWindow();
    }

    @FXML
    public void editCustomer() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Util.showInfoAlert("No Customer Selected", "Select a customer");
            return;
        }
        EditCustomerController.selectedCustomer = CustomerWithAddress.fromCustomerId(selected.getCustomerId(), Main.getObservables());
        EditCustomerController.isAddContext = false;
        showEditCustomerWindow();
    }

    @FXML
    public void deleteCustomer() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Util.showInfoAlert("No Customer Selected", "Select a customer");
            return;
        }
        if (Util.confirmDialog("This will delete the customer along with their appointments.")) {
            // Have to delete the appointment rows before deleting customer because
            // of foreign key constraint.
            AppointmentDao.getInstance(Main.getDb()).deleteByCustomerId(selected.getCustomerId());
            CustomerDao.getInstance(Main.getDb()).delete(selected.getCustomerId());
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
        TableColumn<Customer, Long> id = new TableColumn<>("ID");
        TableColumn<Customer, String> name = new TableColumn<>("Name");
        TableColumn<Customer, String> number = new TableColumn<>("Number");
        TableColumn<Customer, String> address = new TableColumn<>("Address");


        id.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        name.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        number.setCellValueFactory(c -> {
            long customerId = c.getValue().getCustomerId();
            CustomerWithAddress withAddress = CustomerWithAddress.fromCustomerId(customerId, Main.getObservables());
            return new SimpleStringProperty(withAddress.getAddress().getPhone());
        });
        address.setCellValueFactory(c -> {
            long customerId = c.getValue().getCustomerId();
            CustomerWithAddress withAddress = CustomerWithAddress.fromCustomerId(customerId, Main.getObservables());
            Address matchingAddr = withAddress.getAddress();
            if (!matchingAddr.getAddress2().isEmpty()) {
                return new SimpleStringProperty(String.format("%s, %s, %s, %s",
                    matchingAddr.getAddress(),
                    matchingAddr.getAddress2(),
                    withAddress.getCity().getCity(),
                    matchingAddr.getPostalCode()));
            }
            return new SimpleStringProperty(String.format("%s, %s, %s",
                matchingAddr.getAddress(),
                withAddress.getCity().getCity(),
                matchingAddr.getPostalCode()));
        });

        customerTable.getColumns().clear();
        customerTable.getColumns().add(id);
        customerTable.getColumns().add(name);
        customerTable.getColumns().add(number);
        customerTable.getColumns().add(address);

        customerTable.setItems(Main.getObservables().getCustomers());
        customerTable.refresh();
        customerTable.setEditable(false);
        customerTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        System.out.println("Initialized customers");
    }
}
