import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Makes sense of the raw command text typed by the user and builds the
 * {@link Command} it describes, or reports why the command is malformed.
 */
public class CommandParser {
    /**
     * Parses a full command line into the {@link Command} it describes.
     */
    public static Command parse(String command) throws AlzaraException {
        switch (CommandType.from(command)) {
        case BYE:
            return new ExitCommand();
        case MARK:
            return new MarkCommand(parseTaskIndex(command));
        case UNMARK:
            return new UnmarkCommand(parseTaskIndex(command));
        case TODO:
            return new AddCommand(parseTodo(command), "You have something to do...");
        case DEADLINE:
            return new AddCommand(parseDeadline(command), "Do not miss the deadline.");
        case EVENT:
            return new AddCommand(parseEvent(command), "Am I invited?");
        case DELETE:
            return new DeleteCommand(parseTaskIndex(command));
        case LIST:
            return new ListCommand();
        case UNKNOWN:
        default:
            throw new AlzaraException(AlzaraException.UNRECOGNISED_COMMAND_MESSAGE);
        }
    }

    /**
     * Parses the 0-based task index out of a {@code mark}/{@code unmark}/{@code delete}
     * command. Does not check the index against the task list's bounds, since that
     * depends on the list's current size rather than the command text itself - each
     * command's {@code execute} checks that itself.
     */
    private static int parseTaskIndex(String command) throws AlzaraException {
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

    private static Task parseTodo(String command) throws AlzaraException {
        if (command.trim().equals("todo")) {
            throw new AlzaraException(AlzaraException.MISSING_TASK_DESC);
        }
        return new ToDo(command.substring(5));
    }

    private static Task parseDeadline(String command) throws AlzaraException {
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

    private static Task parseEvent(String command) throws AlzaraException {
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
