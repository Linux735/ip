package alzara.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A task with a description, a done/not-done state, and a due date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private LocalDate deadline;

    /**
     * Creates a deadline that starts out not done.
     *
     * @param task the task's description
     * @param deadline the date the task is due by
     */
    public Deadline(String task, LocalDate deadline) {
        super(task);
        this.deadline = deadline;
    }

    /**
     * Returns this task's save-file line, prefixed with the type letter and
     * suffixed with the due date.
     *
     * @return the save-file line, e.g. {@code "D | N | return book | 2019-10-15"}
     */
    @Override
    public String toSaveFormat() {
        return "D | " + super.toSaveFormat() + " | " + this.deadline;
    }

    /**
     * Returns this task's console display, prefixed with the type letter and
     * suffixed with the due date.
     *
     * @return the display line, with the due date as {@code "(by: MMM dd yyyy)"}
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.deadline.format(DISPLAY_FORMAT) + ")";
    }
}
