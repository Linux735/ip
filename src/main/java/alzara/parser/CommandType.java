package alzara.parser;

/**
 * Represents the commands Alzara recognises.
 */
public enum CommandType {
    BYE,
    LIST,
    MARK,
    UNMARK,
    TODO,
    DEADLINE,
    EVENT,
    DELETE,
    UNKNOWN;

    /**
     * Identifies a command using Alzara's existing command-recognition rules.
     *
     * @param command command entered by the user
     * @return type of the command, or {@code UNKNOWN} when it is not recognised
     */
    public static CommandType from(String command) {
        if (command.equalsIgnoreCase("bye")) {
            return BYE;
        } else if (command.equals("mark") || command.startsWith("mark ")) {
            return MARK;
        } else if (command.equals("unmark") || command.startsWith("unmark ")) {
            return UNMARK;
        } else if (command.equals("todo") || command.startsWith("todo ")) {
            return TODO;
        } else if (command.equals("deadline") || command.startsWith("deadline ")) {
            return DEADLINE;
        } else if (command.equals("event") || command.startsWith("event ")) {
            return EVENT;
        } else if (command.equals("delete") || command.startsWith("delete ")) {
            return DELETE;
        } else if (command.equals("list")) {
            return LIST;
        }
        return UNKNOWN;
    }
}
