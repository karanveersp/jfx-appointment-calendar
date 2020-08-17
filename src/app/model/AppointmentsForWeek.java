package app.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public final class AppointmentsForWeek {
    private final List<Appointment> mon;
    private final List<Appointment> tue;
    private final List<Appointment> wed;
    private final List<Appointment> thu;
    private final List<Appointment> fri;
    private final String duration;
    private final LocalDate dateMon;
    private final LocalDate dateTue;
    private final LocalDate dateWed;
    private final LocalDate dateThu;
    private final LocalDate dateFri;

    public AppointmentsForWeek(LocalDate date, List<Appointment> appointments) {
        LocalDate start = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        dateMon = start;
        dateTue = start.plusDays(1);
        dateWed = start.plusDays(2);
        dateThu = start.plusDays(3);
        dateFri = start.plusDays(4);
        mon = filterByDate(appointments, dateMon);
        tue = filterByDate(appointments, dateTue);
        wed = filterByDate(appointments, dateWed);
        thu = filterByDate(appointments, dateThu);
        fri = filterByDate(appointments, dateFri);
        duration = String.format("From %s to %s",
            start.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)),
            dateFri.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
    }

    public LocalDate getDateByDay(DayOfWeek day) {
        switch (day) {
            case MONDAY:
                return dateMon;
            case TUESDAY:
                return dateTue;
            case WEDNESDAY:
                return dateWed;
            case THURSDAY:
                return dateThu;
            case FRIDAY:
                return dateFri;
            default:
                throw new NoSuchElementException("Saturday and Sunday not supported");
        }
    }

    public List<Appointment> getAppointmentsByDay(DayOfWeek day) {
        switch (day) {
            case MONDAY:
                return mon;
            case TUESDAY:
                return tue;
            case WEDNESDAY:
                return wed;
            case THURSDAY:
                return thu;
            case FRIDAY:
                return fri;
            default:
                throw new NoSuchElementException("Saturday and Sunday not supported");
        }
    }
    public List<Appointment> getMon() {
        return mon;
    }

    public List<Appointment> getTue() {
        return tue;
    }

    public List<Appointment> getWed() {
        return wed;
    }

    public List<Appointment> getThu() {
        return thu;
    }

    public List<Appointment> getFri() {
        return fri;
    }

    private List<Appointment> filterByDate(List<Appointment> appointments, LocalDate date) {
        return appointments.stream().filter(a -> a.getStart().toLocalDate().equals(date))
            .collect(Collectors.toList());
    }

    public String getDuration() {
        return duration;
    }
}
