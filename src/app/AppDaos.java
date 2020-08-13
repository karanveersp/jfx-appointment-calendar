package app;

import app.dao.*;
import app.db.Database;

public class AppDaos {
    private final CountryDao countryDao;
    private final CustomerDao customerDao;
    private final UserDao userDao;
    private final AppointmentDao appointmentDao;
    private final AddressDao addressDao;
    private final CityDao cityDao;

    public AppDaos(Database db) {
        countryDao = CountryDao.getInstance(db);
        customerDao = CustomerDao.getInstance(db);
        userDao = UserDao.getInstance(db);
        appointmentDao = AppointmentDao.getInstance(db);
        addressDao = AddressDao.getInstance(db);
        cityDao = CityDao.getInstance(db);
    }

    public CountryDao getCountryDao() {
        return countryDao;
    }

    public CustomerDao getCustomerDao() {
        return customerDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public AppointmentDao getAppointmentDao() {
        return appointmentDao;
    }

    public AddressDao getAddressDao() {
        return addressDao;
    }

    public CityDao getCityDao() {
        return cityDao;
    }
}
