- Log file

User logins will be logged in the file app.log
in the project root directory.

- Lambda examples

The following lambda usages have comments detailing their usage.

- AppObservables.java
  ```java
  public Customer getCustomerById(long id) {
      // Lambda example: Predicate to match the given id
      // to filter the matching customer.
      return customers.stream()
          .filter(c -> c.getCustomerId() == id)
          .findFirst().orElseThrow(null);
  }
  ```

- EditAppointmentController.java
  ```java
  // Lambda example: Passing in a lambda callback which results in a new DateCell
  // allowing only dates from today and future.
  // Used to disallow creation of appointments on past days.
  // Without using the lambda, the argument has to be a new instance of a class
  // with a long name and type parameters which makes the code less readable.
  startDatePicker.setDayCellFactory(param -> new DateCell() {
      @Override
      public void updateItem(LocalDate date, boolean empty) {
          super.updateItem(date, empty);
          setDisable(empty || date.compareTo(LocalDate.now()) < 0);
      }
  });
  ```