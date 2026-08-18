import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Deadline extends Task{
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private LocalDate deadline;

    public Deadline(String task, LocalDate deadline) {
        super(task);
        this.deadline = deadline;
    }

    @Override
    public String toSaveFormat() {
        return "D | " + super.toSaveFormat() + " | " + this.deadline;
    }


    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.deadline.format(DISPLAY_FORMAT) + ")";
    }
}
