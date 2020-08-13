package app;

import app.dao.AddressDao;
import app.dao.CityDao;
import app.dao.CountryDao;
import app.dao.CustomerDao;
import app.db.Database;
import app.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class AppObservables {
    private final ObservableList<Customer> customers;
    private final ObservableList<Address> addresses;
    private final ObservableList<City> cities;
    private final ObservableList<Appointment> appointments;
    private final ObservableList<Country> countries;
    private final ObservableList<User> users;

    public AppObservables(ObservableList<Customer> customers, ObservableList<Address> addresses, ObservableList<City> cities, ObservableList<Appointment> appointments, ObservableList<Country> countries, ObservableList<User> users) {
        this.customers = customers;
        this.addresses = addresses;
        this.cities = cities;
        this.appointments = appointments;
        this.countries = countries;
        this.users = users;
    }

    public ObservableList<Customer> getCustomers() {
        return customers;
    }

    public ObservableList<Address> getAddresses() {
        return addresses;
    }

    public ObservableList<City> getCities() {
        return cities;
    }

    public ObservableList<Appointment> getAppointments() {
        return appointments;
    }

    public ObservableList<Country> getCountries() {
        return countries;
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    /**
     * Resets the observable lists by fetching
     * the rows from the database
     * @param daos AppDaos object
     */
    public void refreshAll(AppDaos daos) {
        customers.setAll(daos.getCustomerDao().getAll());
        addresses.setAll(daos.getAddressDao().getAll());
        appointments.setAll(daos.getAppointmentDao().getAll());
        countries.setAll(daos.getCountryDao().getAll());
        cities.setAll(daos.getCityDao().getAll());
        users.setAll(daos.getUserDao().getAll());
    }


    public static AppObservables initializeFromDaos(AppDaos daos) {
        List<Customer> customerSet = daos.getCustomerDao().getAll();
        List<City> citySet = daos.getCityDao().getAll();
        List<Address> addressSet = daos.getAddressDao().getAll();
        List<Appointment> appointmentSet = daos.getAppointmentDao().getAll();
        List<Country> countrySet = daos.getCountryDao().getAll();
        List<User> userSet = daos.getUserDao().getAll();
        return new AppObservables(
            FXCollections.observableArrayList(customerSet),
            FXCollections.observableArrayList(addressSet),
            FXCollections.observableArrayList(citySet),
            FXCollections.observableArrayList(appointmentSet),
            FXCollections.observableArrayList(countrySet),
            FXCollections.observableArrayList(userSet)
        );
    }
}
