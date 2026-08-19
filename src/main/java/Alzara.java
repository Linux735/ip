import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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
                    if (command.trim().equals("todo")) {
                        throw new AlzaraException(AlzaraException.MISSING_TASK_DESC);
                    }
                    Task task = new ToDo(command.substring(5));
                    memory.add(task);
                    Storage.save(memory.getTasks());
                    ui.showTaskAdded("You have something to do...", task, memory.size());
                } catch (AlzaraException exception) {
                    ui.showError(exception.getMessage());
                }
                break;
            case DEADLINE:
                try {
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
                    Task task = new Deadline(description, deadline);
                    memory.add(task);
                    Storage.save(memory.getTasks());
                    ui.showTaskAdded("Do not miss the deadline.", task, memory.size());
                } catch (AlzaraException exception) {
                    ui.showError(exception.getMessage());
                }
                break;
            case EVENT:
                try {
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
                    Task task = new Event(description, start, end);
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

    private static int getTaskIndex(String command, TaskList memory) throws AlzaraException {
        String[] parts = command.trim().split("\\s+");
        if (parts.length < 2) {
            throw new AlzaraException(AlzaraException.MISSING_TASK_NUMBER_MESSAGE);
        }

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(parts[1]) - 1;
        } catch (NumberFormatException exception) {
            throw new AlzaraException(AlzaraException.NON_NUMERIC_TASK_NUMBER_MESSAGE);
        }

        if (taskIndex < 0 || taskIndex >= memory.size()) {
            throw new AlzaraException(AlzaraException.TASK_DOES_NOT_EXIST_MESSAGE);
        }
        return taskIndex;
    }
}
