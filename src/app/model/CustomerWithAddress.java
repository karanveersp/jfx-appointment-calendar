package app.model;

import app.AppObservables;

public class CustomerWithAddress {
    private final Customer customer;
    private final Address address;
    private final City city;
    private final Country country;

    public CustomerWithAddress(Customer customer, Address address, City city, Country country) {
        this.customer = customer;
        this.address = address;
        this.city = city;
        this.country = country;
    }

    public Customer getCustomer() {
        return customer;
    }


    public Address getAddress() {
        return address;
    }



    public City getCity() {
        return city;
    }


    public Country getCountry() {
        return country;
    }


    public static CustomerWithAddress fromCustomerId(long customerId, AppObservables observables) {
        Customer cust = observables.getCustomers().stream().filter(c -> c.getCustomerId() == customerId)
            .findFirst().orElseThrow(null);
        Address addr = observables.getAddresses().stream().filter(a -> a.getAddressId() == cust.getAddressId())
            .findFirst().orElseThrow(null);
        City cty = observables.getCities().stream().filter(c -> c.getCityId() == addr.getCityId())
            .findFirst().orElseThrow(null);
        Country ctry = observables.getCountries().stream().filter(c -> c.getCountryId() == cty.getCountryId())
            .findFirst().orElseThrow(null);
        return new CustomerWithAddress(cust, addr, cty, ctry);
    }
}
