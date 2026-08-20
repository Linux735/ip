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
     * @param task the task's description
     * @param start the date the event starts
     * @param end the date the event ends
     */
    public Event(String task, LocalDate start, LocalDate end) {
        super(task);
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
}
