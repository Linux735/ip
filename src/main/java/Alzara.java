/**
 * The Alzara chatbot: sets up its collaborators once, then runs the
 * read-command-execute loop until the user says {@code bye}.
 */
public class Alzara {
    private final Ui ui;
    private TaskList memory;

    public Alzara() {
        this.ui = new Ui();
    }

    /**
     * Runs the read-command-execute loop until the user says {@code bye}.
     */
    public void run() {
        ui.showWelcome();
        memory = new TaskList(Storage.load());

        while (true) {
            String command = ui.readCommand();
            ui.showLine();

            switch (CommandType.from(command)) {
            case BYE:
                try {
                    Command exitCommand = new ExitCommand();
                    exitCommand.execute(memory, ui);
                    return;
                } catch (AlzaraException exception) {
                    ui.showError(exception.getMessage());
                }
                break;
            case MARK:
                try {
                    int taskIndex = getTaskIndex(command, memory);
                    Command markCommand = new MarkCommand(taskIndex);
                    markCommand.execute(memory, ui);
                } catch (AlzaraException exception) {
                    ui.showError(exception.getMessage());
                }
                break;
            case UNMARK:
                try {
                    int taskIndex = getTaskIndex(command, memory);
                    Command unmarkCommand = new UnmarkCommand(taskIndex);
                    unmarkCommand.execute(memory, ui);
                } catch (AlzaraException exception) {
                    ui.showError(exception.getMessage());
                }
                break;
            case TODO:
                try {
                    Task task = CommandParser.parseTodo(command);
                    Command addTodoCommand = new AddCommand(task, "You have something to do...");
                    addTodoCommand.execute(memory, ui);
                } catch (AlzaraException exception) {
                    ui.showError(exception.getMessage());
                }
                break;
            case DEADLINE:
                try {
                    Task task = CommandParser.parseDeadline(command);
                    Command addDeadlineCommand = new AddCommand(task, "Do not miss the deadline.");
                    addDeadlineCommand.execute(memory, ui);
                } catch (AlzaraException exception) {
                    ui.showError(exception.getMessage());
                }
                break;
            case EVENT:
                try {
                    Task task = CommandParser.parseEvent(command);
                    Command addEventCommand = new AddCommand(task, "Am I invited?");
                    addEventCommand.execute(memory, ui);
                } catch (AlzaraException exception) {
                    ui.showError(exception.getMessage());
                }
                break;
            case DELETE:
                try {
                    int taskIndex = getTaskIndex(command, memory);
                    Command deleteCommand = new DeleteCommand(taskIndex);
                    deleteCommand.execute(memory, ui);
                } catch (AlzaraException exception) {
                    ui.showError(exception.getMessage());
                }
                break;
            case LIST:
                try {
                    Command listCommand = new ListCommand();
                    listCommand.execute(memory, ui);
                } catch (AlzaraException exception) {
                    ui.showError(exception.getMessage());
                }
                break;
            case UNKNOWN:
                try {
                    Command unknownCommand = new UnknownCommand(command);
                    unknownCommand.execute(memory, ui);
                } catch (AlzaraException exception) {
                    ui.showError(exception.getMessage());
                }
                break;
            }
        }
    }

    /**
     * Parses the task number out of a {@code mark}/{@code unmark}/{@code delete} command
     * and checks it refers to an existing task in {@code memory}.
     */
    private static int getTaskIndex(String command, TaskList memory) throws AlzaraException {
        int taskIndex = CommandParser.parseTaskIndex(command);
        if (taskIndex < 0 || taskIndex >= memory.size()) {
            throw new AlzaraException(AlzaraException.TASK_DOES_NOT_EXIST_MESSAGE);
        }
        return taskIndex;
    }

    /**
     * Starts the Alzara chatbot application.
     *
     * @param args command-line arguments supplied when the application starts
     */
    public static void main(String[] args) {
        new Alzara().run();
    }
}
