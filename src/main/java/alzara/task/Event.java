package alzara.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A task with a description, a done/not-done state, and a start/end date range.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private LocalDate start;
    private LocalDate end;

    /**
     * Creates an event that starts out not done.
     *
     * @param description the task's description
     * @param start the date the event starts
     * @param end the date the event ends
     */
    public Event(String description, LocalDate start, LocalDate end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    /**
     * Returns this task's save-file line, prefixed with the type letter and
     * suffixed with both dates.
     *
     * @return the save-file line, e.g. {@code "E | N | trip | 2019-10-15 | 2019-10-16"}
     */
    @Override
    public String toSaveFormat() {
        return "E | " + super.toSaveFormat() + " | " + this.start + " | " + this.end;
    }

    /**
     * Returns this task's console display, prefixed with the type letter and
     * suffixed with both dates.
     *
     * @return the display line, with both dates as {@code "(from: MMM dd yyyy to: MMM dd yyyy)"}
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.start.format(DISPLAY_FORMAT)
                + " to: " + this.end.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns true if {@code date} falls within {@code [start, end]} inclusive.
     *
     * <p><b>Known edge case (accepted, not fixed here):</b> {@link
     * alzara.parser.CommandParser#parse} never validates that an event's
     * {@code /from} date is chronologically before its {@code /to} date - it
     * only checks that the markers appear in that order in the command text.
     * If {@code start} ends up after {@code end} (e.g. a save-file entry
     * edited by hand, or a future relaxation of that parser check), the
     * range below is empty and this method silently returns false for every
     * date, including {@code start} and {@code end} themselves - such an
     * event would never appear under {@code view} for any query, with no
     * error raised anywhere.
     *
     * @param date the queried date
     * @return true if this event is happening on {@code date}
     */
    @Override
    public boolean isScheduledOn(LocalDate date) {
        return !date.isBefore(this.start) && !date.isAfter(this.end);
    }

    @Override
    public LocalDate getSortDate() {
        return this.start;
    }
}
