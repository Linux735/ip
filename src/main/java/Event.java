import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Event extends Task{
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private LocalDate start;
    private LocalDate end;

    public Event(String task, LocalDate start, LocalDate end) {
        super(task);
        this.start = start;
        this.end = end;
    }

    @Override
    public String toSaveFormat() {
        return "E | " + super.toSaveFormat() + " | " + this.start + " | " + this.end;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.start.format(DISPLAY_FORMAT)
                + " to: " + this.end.format(DISPLAY_FORMAT) + ")";
    }
}
