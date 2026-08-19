public class Alzara {
    /**
     * Starts the Alzara chatbot application.
     *
     * @param args command-line arguments supplied when the application starts
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        TaskList memory = new TaskList(Storage.load());

        while (true) {
            String command = ui.readCommand();
            ui.showLine();

            switch (CommandType.from(command)) {
            case BYE:
                ui.showGoodbye();
                return;
            case MARK:
                try {
                    int taskIndex = getTaskIndex(command, memory);
                    Task task = memory.get(taskIndex);
                    task.mark(taskIndex);
                    Storage.save(memory.getTasks());
                    ui.showTaskMarked(task);
                } catch (AlzaraException exception) {
                    ui.showError(exception.getMessage());
                }
                break;
            case UNMARK:
                try {
                    int taskIndex = getTaskIndex(command, memory);
                    Task task = memory.get(taskIndex);
                    task.unmark(taskIndex);
                    Storage.save(memory.getTasks());
                    ui.showTaskUnmarked(task);
                } catch (AlzaraException exception) {
                    ui.showError(exception.getMessage());
                }
                break;
            case TODO:
                try {
                    Task task = CommandParser.parseTodo(command);
                    memory.add(task);
                    Storage.save(memory.getTasks());
                    ui.showTaskAdded("You have something to do...", task, memory.size());
                } catch (AlzaraException exception) {
                    ui.showError(exception.getMessage());
                }
                break;
            case DEADLINE:
                try {
                    Task task = CommandParser.parseDeadline(command);
                    memory.add(task);
                    Storage.save(memory.getTasks());
                    ui.showTaskAdded("Do not miss the deadline.", task, memory.size());
                } catch (AlzaraException exception) {
                    ui.showError(exception.getMessage());
                }
                break;
            case EVENT:
                try {
                    Task task = CommandParser.parseEvent(command);
                    memory.add(task);
                    Storage.save(memory.getTasks());
                    ui.showTaskAdded("Am I invited?", task, memory.size());
                } catch (AlzaraException exception) {
                    ui.showError(exception.getMessage());
                }
                break;
            case DELETE:
                try {
                    int taskIndex = getTaskIndex(command, memory);
                    Task deletedTask = memory.delete(taskIndex);
                    Storage.save(memory.getTasks());
                    ui.showTaskDeleted(deletedTask, memory.size());
                } catch (AlzaraException exception) {
                    ui.showError(exception.getMessage());
                }
                break;
            case LIST:
                ui.showTaskList(memory.getTasks());
                break;
            case UNKNOWN:
                memory.add(new Task(command));
                Storage.save(memory.getTasks());
                ui.showRawTaskAdded(command);
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
}
