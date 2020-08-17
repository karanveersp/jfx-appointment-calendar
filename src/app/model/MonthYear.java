package app.model;

import java.time.Month;

public class MonthYear {
    private final Month month;
    private final int year;
    public MonthYear(Month month, int year) {
        this.month = month;
        this.year = year;
    }

    @Override
    public String toString() {
        return month.name() + " " + year;
    }

    public Month getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
