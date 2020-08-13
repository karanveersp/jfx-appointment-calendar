package app.controller;

import app.Main;
import app.Util;
import app.model.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class EditCustomerController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(EditCustomerController.class);

    public static CustomerWithAddress selectedCustomer;
    public static Stage self;
    public static boolean isAddContext;

    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField address2Field;

    @FXML
    private TextField cityField;

    @FXML
    private TextField zipField;

    @FXML
    private TextField countryField;

    @FXML
    private TextField phoneField;

    private List<TextField> fields;

    @FXML
    public void saveCustomer() {
        if (!isInputValid()) {
            Util.showInfoAlert("Form invalid", "All fields are required");
            return;
        }
        try {
            Main.getDb().startTransaction();
            if (isAddContext) {
                addCustomer();
            } else {
                updateCustomer();
            }
        } catch (Exception e) {
            Util.showInfoAlert("An error occurred", e.getMessage());
            logger.error(e.getMessage(), e);
            Main.getDb().rollback();
            return;
        }
        Main.getDb().commit();
        Main.getObservables().refreshAll(Main.getDaos());
        logger.debug("Completed saving customer");
        self.close();
    }

    private void addCustomer() {
        Country country = Main.getDaos().getCountryDao().findExistingOrCreate(countryField.getText(), Main.getLoggedInUser());
        City city = Main.getDaos().getCityDao().findExistingOrCreate(cityField.getText(), country.getCountryId(), Main.getLoggedInUser());
        // For address, we create an address object, and depending on add/edit context
        // set certain fields from existing address

        Address address = new Address(
            addressField.getText(),
            address2Field.getText(),
            city.getCityId(),
            zipField.getText(),
            phoneField.getText(),
            Main.getLoggedInUser()
        );
        address = Main.getDaos().getAddressDao().insert(address);
        Customer customer = new Customer(nameField.getText(), address.getAddressId(), Main.getLoggedInUser());
        Main.getDaos().getCustomerDao().insert(customer);
    }

    private void updateCustomer() throws Exception {
        Country country = Main.getDaos().getCountryDao().findExistingOrCreate(countryField.getText(), Main.getLoggedInUser());
        City city = Main.getDaos().getCityDao().findExistingOrCreate(cityField.getText(), country.getCountryId(), Main.getLoggedInUser());
        if (city.getCountryId() != country.getCountryId()) {
            // Create a duplicate named city in updated country
            city = Main.getDaos().getCityDao().insert(new City(city.getCity(), country.getCountryId(), Main.getLoggedInUser()));
        }
        Address address = new Address(
            addressField.getText(),
            address2Field.getText(),
            city.getCityId(),
            zipField.getText(),
            phoneField.getText(),
            Main.getLoggedInUser()
        );
        // update address
        Address existing = selectedCustomer.getAddress();
        // reset some fields to existing
        address.setAddressId(existing.getAddressId());
        address.setCreateDate(existing.getCreateDate());
        address.setCreatedBy(existing.getCreatedBy());
        boolean isOk = Main.getDaos().getAddressDao().update(address);
        if (!isOk) {
            throw new Exception("Error while updating address");
        }

        Customer customer = new Customer(nameField.getText(), address.getAddressId(), Main.getLoggedInUser());
        // update customer
        Customer existingCustomer = selectedCustomer.getCustomer();
        // reset some fields
        customer.setCustomerId(existingCustomer.getCustomerId());
        customer.setCreateDate(existingCustomer.getCreateDate());
        customer.setCreatedBy(existingCustomer.getCreatedBy());

        isOk = Main.getDaos().getCustomerDao().update(customer);
        if (!isOk) {
            throw new Exception("Error while updating customer");
        }
    }


    private boolean isInputValid() {
        for (TextField field : fields) {
            if (field.getPromptText().equalsIgnoreCase("address 2")) {
                continue;
            }
            if (field.getText().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fields = Arrays.asList(
            nameField,
            addressField,
            address2Field,
            cityField,
            zipField,
            countryField,
            phoneField
        );
        if (!isAddContext) {
            // editing existing customer
            // pre-populate fields
            nameField.setText(selectedCustomer.getCustomer().getCustomerName());
            addressField.setText(selectedCustomer.getAddress().getAddress());
            address2Field.setText(selectedCustomer.getAddress().getAddress2());
            cityField.setText(selectedCustomer.getCity().getCity());
            phoneField.setText(selectedCustomer.getAddress().getPhone());
            zipField.setText(selectedCustomer.getAddress().getPostalCode());
            countryField.setText(selectedCustomer.getCountry().getCountry());
        }
    }
}
