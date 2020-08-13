## Competencies

#### app.db.Database and File Server Applications
The graduate produces database and file server applications using advanced Java programming language constructs to meet business requirements.

#### Lambda
The graduate incorporates lambda expressions in application development to meet business requirements more efficiently.

#### Collections (Streams and Filters)
The graduate incorporates streams and filters in application development to manipulate data more efficiently.

#### Localization API & Date/Time API
The graduate applies the localization API and date/time API in application development to support end-users in various geographical regions.

#### Advanced Exception Control
The graduate incorporates advanced exception control mechanisms in application development for improving user experience and application stability.

## Intro

Throughout your career in software design and development, you will be asked to create applications with various features and criteria based on a variety of business requirements. For this assessment, you will create your own Java application with requirements that mirror those you will encounter in a real-world job assignment.

The skills you will showcase in this assessment are also directly relevant to technical interview questions for future employment. This application should become a portfolio piece for you to show to future employers.

You are working for a software company that has been contracted to develop a scheduling desktop user interface application. The contract is with a global consulting organization that conducts business in multiple languages and has main offices in various locations. The consulting organization has provided a MySQL database that your application must pull data from. The database is used for other systems and therefore its structure cannot be modified.

The organization outlined specific business requirements that must be included as part of the application. From these requirements, a system analyst at your company created solution statements for you to implement in developing the application.

## Requirements

A. Create a log-in form that can determine the user's location and translate log-in and error control messages into two languages.

B. Provide the ability to add, update, and delete customer records in the database, including name, address (however you model it), and phone number.

C. Provide the ability to add, update and delete appointments, capturing the type of appointment, and a link to the specific customer record in the database.

D. Provide ability to view the calendar by the month and by week.

E. Provide ability to automatically adjust appointment times based on user time zones and daylight saving time.

F. Write exception controls to prevent _each_ of the following. You may use the same mechanism of exception control more than once, but you must incorporate _at least_ two different mechanism of exception control. 
    * Scheduling an appointment outside business hours
    * Scheduling overlapping appointments
    * Entering nonexistent or invalid customer data
    * Entering incorrect username and password

G. Write two or more lambda expressions to make your program more efficient, justifying the use of each lambda expression with an in-line comment.

H. Write code to provide an alert if there is an appointment within 15 minutes of the user's log-in.

I. Provide the ability to generate _each_ of the following reports:
    * Number of appointment types by month
    * The schedule for each consultant
    * One additional report of your choice

J. Provide the ability to track user activity by recording timestamps for user log-ins in a .txt file. Each new record should be appended to the log file, if the file already exists.




## Requirement A - Log-in Form

Create a log-in form that can determine the user's location and translate log-in and error control messages (e.g., "The username and password did not match.") into _two_ languages. 

- The form must accept a username and password.
- These credentials must match an entry in the database.
- Error message should be displayed if login is invalid.
- Support two languages (English + 1) triggered by the System Default Locale for all strings on this one form.

- The language support must be automatic, you must go by the System Default. (Java can do this easily)

- The evaluator will bring down your app, change the language on their machine, bring up your app, and expect to see the language changed.

- Only login form should have alternative language support.

- User test, and password test.

## Requirement B - Customer Data

Provide the ability to add, update, and delete customer records in the database, including name, address, and phone number.

You must be able to create/update/delete customers in the application. The minimum data for input should be name, address, phone number.

## Requirement C - Appointment Data

Provide the ability to add, update, and delete appointments, capturing the type of appointment and a link to the specific customer record in the database.

You must be able to create/update/delete appointments in the application.

"a link to the specific customer record in the database" is a fancy way of saying customer id.

The minimum data for user input should be type, customer, schedule time.

## Requirement D - The Calendar

Provide the ability to view the calendar by month and by week.

A list of appointment in a table. Display current month and current week.

Also have a way to display all appointments.