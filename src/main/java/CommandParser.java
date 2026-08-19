import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Makes sense of the raw command text typed by the user: extracts the
 * fields each command needs (description, dates, task number) and builds
 * the corresponding {@link Task}, or reports why the command is malformed.
 */
public class CommandParser {
    /**
     * Parses the 0-based task index out of a {@code mark}/{@code unmark}/{@code delete}
     * command. Does not check the index against the task list's bounds, since that
     * depends on the list's current size rather than the command text itself.
     */
    public static int parseTaskIndex(String command) throws AlzaraException {
        String[] parts = command.trim().split("\\s+");
        if (parts.length < 2) {
            throw new AlzaraException(AlzaraException.MISSING_TASK_NUMBER_MESSAGE);
        }

        try {
            return Integer.parseInt(parts[1]) - 1;
        } catch (NumberFormatException exception) {
            throw new AlzaraException(AlzaraException.NON_NUMERIC_TASK_NUMBER_MESSAGE);
        }
    }

    public static Task parseTodo(String command) throws AlzaraException {
        if (command.trim().equals("todo")) {
            throw new AlzaraException(AlzaraException.MISSING_TASK_DESC);
        }
        return new ToDo(command.substring(5));
    }

    public static Task parseDeadline(String command) throws AlzaraException {
        if (command.trim().equals("deadline")) {
            throw new AlzaraException(AlzaraException.MISSING_TASK_DESC);
        }

        int deadlineMarker = command.indexOf(" /by ");
        if (deadlineMarker == -1) {
            throw new AlzaraException(AlzaraException.MISSING_DEADLINE_MARKER_MESSAGE);
        }

        String description = command.substring(9, deadlineMarker);
        if (description.trim().isEmpty()) {
            throw new AlzaraException(AlzaraException.MISSING_TASK_DESC);
        }

        String deadlineText = command.substring(deadlineMarker + 5);
        LocalDate deadline;
        try {
            deadline = LocalDate.parse(deadlineText.trim());
        } catch (DateTimeParseException exception) {
            throw new AlzaraException(AlzaraException.INVALID_DEADLINE_DATE_MESSAGE);
        }

        return new Deadline(description, deadline);
    }

    public static Task parseEvent(String command) throws AlzaraException {
        if (command.trim().equals("event")) {
            throw new AlzaraException(AlzaraException.MISSING_TASK_DESC);
        }

        int startMarker = command.indexOf(" /from ");
        int endMarker = command.indexOf(" /to ");
        if (startMarker == -1 || endMarker == -1 || endMarker < startMarker) {
            throw new AlzaraException(AlzaraException.MISSING_EVENT_MARKER_MESSAGE);
        }

        String description = command.substring(6, startMarker);
        if (description.trim().isEmpty()) {
            throw new AlzaraException(AlzaraException.MISSING_TASK_DESC);
        }

        String startText = command.substring(startMarker + 7, endMarker);
        String endText = command.substring(endMarker + 5);
        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(startText.trim());
            end = LocalDate.parse(endText.trim());
        } catch (DateTimeParseException exception) {
            throw new AlzaraException(AlzaraException.INVALID_DEADLINE_DATE_MESSAGE);
        }

        return new Event(description, start, end);
    }
}
