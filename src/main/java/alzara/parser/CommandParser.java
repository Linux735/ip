package alzara.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import alzara.AlzaraException;
import alzara.command.AddCommand;
import alzara.command.Command;
import alzara.command.DeleteCommand;
import alzara.command.ExitCommand;
import alzara.command.FindCommand;
import alzara.command.ListCommand;
import alzara.command.MarkCommand;
import alzara.command.UnmarkCommand;
import alzara.task.Deadline;
import alzara.task.Event;
import alzara.task.Task;
import alzara.task.ToDo;

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
        case FIND:
            return new FindCommand(parseKeyword(command));
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

    /**
     * Parses a {@code todo} command into a {@link ToDo}.
     *
     * @throws AlzaraException if the command has no description
     */
    private static Task parseTodo(String command) throws AlzaraException {
        if (command.trim().equals("todo")) {
            throw new AlzaraException(AlzaraException.MISSING_TASK_DESC);
        }
        return new ToDo(command.substring(5));
    }

    /**
     * Parses a {@code deadline} command into a {@link Deadline}, extracting the
     * description before the {@code /by} marker and the date after it.
     *
     * @throws AlzaraException if the description or {@code /by} date is missing
     *         or malformed
     */
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

    /**
     * Parses an {@code event} command into an {@link Event}, extracting the
     * description before the {@code /from} marker and the two dates between
     * the {@code /from} and {@code /to} markers.
     *
     * @throws AlzaraException if the description or either date is missing,
     *         out of order, or malformed
     */
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

    private static String parseKeyword(String command) throws AlzaraException {
        if (command.trim().equals("find")) {
            throw new AlzaraException(AlzaraException.MISSING_KEYWORD_MESSAGE);
        }
        return command.substring(5);
    }
}
