package alzara;

/**
 * Signals that a user command could not be understood or carried out, e.g. a
 * malformed {@code deadline} command or a task index that doesn't exist.
 * {@link #getMessage()} holds the exact line Alzara should show the user.
 */
public class AlzaraException extends Exception {
    public static final String MISSING_TASK_DESC =
            "You want to do nothing?";
    public static final String MISSING_TASK_NUMBER_MESSAGE =
            "Which task are you referring to?";
    public static final String NON_NUMERIC_TASK_NUMBER_MESSAGE =
            "Nonsense.";
    public static final String TASK_DOES_NOT_EXIST_MESSAGE =
            "Do you know how to count?";
    public static final String MISSING_DEADLINE_MARKER_MESSAGE =
            "Add a deadline.";
    public static final String MISSING_EVENT_MARKER_MESSAGE =
            "All things must have a start and an end...";
    public static final String INVALID_DEADLINE_DATE_MESSAGE =
            "I cannot read that date. Use yyyy-mm-dd";
    public static final String UNRECOGNISED_COMMAND_MESSAGE =
            "Speak sense.";
    public static final String MISSING_KEYWORD_MESSAGE =
            "I cannot find nothing.";

    /**
     * Creates an exception carrying the exact message Alzara should show the user.
     *
     * @param message the user-facing error message
     */
    public AlzaraException(String message) {
        super(message);
    }
}
