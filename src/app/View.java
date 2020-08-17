package app;

public enum View {
    CUSTOMERS("customers.fxml"),
    APPOINTMENTS("appointments.fxml"),
    EDIT_CUSTOMER("editCustomer.fxml"),
    EDIT_APPOINTMENT("editAppointment.fxml"),
    REPORTS("reports.fxml"),
    CALENDAR("calendar.fxml"),
    MAIN("main.fxml"),
    LOGIN("login.fxml"),
    HOME("home.fxml");
    private final String name;
    View(String name) {
        this.name = name;
    }
    public String getName() { return name; }
}
