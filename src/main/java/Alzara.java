import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.ArrayList;

public class Alzara {
    /**
     * Starts the Alzara chatbot application.
     *
     * @param args command-line arguments supplied when the application starts
     */
    public static void main(String[] args) {
        String separator = "____________________________________________________________";
        String banner = "    _    _     ______    _    ____       _    \n"
                + "   / \\  | |   |__  /   / \\  |  _ \\     / \\   \n"
                + "  / _ \\ | |     / /   / _ \\ | |_) |   / _ \\  \n"
                + " / ___ \\| |___ / /_  / ___ \\|  _ <   / ___ \\ \n"
                + "/_/   \\_\\_____/____|/_/   \\_\\_| \\_\\ /_/   \\_\\\n";
        System.out.println(separator);
        System.out.print(banner);
        System.out.println("And as it was foretold,");
        System.out.println("You find yourself face to face with the great Alzara.");
        System.out.println("What can I do for you?");
        System.out.println(separator);

        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> memory = Storage.load();

        while (true) {
            String command = scanner.nextLine();
            System.out.println(separator);

            switch (CommandType.from(command)) {
            case BYE:
                System.out.println("Our audience has ended. Until we meet again.");
                System.out.println(separator);
                return;
            case MARK:
                try {
                    int taskIndex = getTaskIndex(command, memory);
                    Task task = memory.get(taskIndex);
                    task.mark(taskIndex);
                    Storage.save(memory);
                    System.out.println("You have satisfied the great Alzara.");
                    System.out.println(task);
                    System.out.println(separator);
                } catch (AlzaraException exception) {
                    printError(exception, separator);
                }
                break;
            case UNMARK:
                try {
                    int taskIndex = getTaskIndex(command, memory);
                    Task task = memory.get(taskIndex);
                    task.unmark(taskIndex);
                    Storage.save(memory);
                    System.out.println("As I predicted...");
                    System.out.println(task);
                    System.out.println(separator);
                } catch (AlzaraException exception) {
                    printError(exception, separator);
                }
                break;
            case TODO:
                try {
                    if (command.trim().equals("todo")) {
                        throw new AlzaraException(AlzaraException.MISSING_TASK_DESC);
                    }
                    Task task = new ToDo(command.substring(5));
                    memory.add(task);
                    Storage.save(memory);
                    System.out.println("You have something to do...");
                    System.out.println(task);
                    System.out.println("You have " + memory.size() + " tasks.");
                    System.out.println(separator);
                } catch (AlzaraException exception) {
                    printError(exception, separator);
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
                    Storage.save(memory);
                    System.out.println("Do not miss the deadline.");
                    System.out.println(task);
                    System.out.println("You have " + memory.size() + " tasks.");
                    System.out.println(separator);
                } catch (AlzaraException exception) {
                    printError(exception, separator);
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
                    Storage.save(memory);
                    System.out.println("Am I invited?");
                    System.out.println(task);
                    System.out.println("You have " + memory.size() + " tasks.");
                    System.out.println(separator);
                } catch (AlzaraException exception) {
                    printError(exception, separator);
                }
                break;
            case DELETE:
                try {
                    int taskIndex = getTaskIndex(command, memory);
                    Task deletedTask = memory.remove(taskIndex);
                    Storage.save(memory);
                    System.out.println("I have removed the task " + deletedTask);
                    System.out.println("You have " + memory.size() + " tasks remaining.");
                    System.out.println(separator);
                } catch (AlzaraException exception) {
                    printError(exception, separator);
                }
                break;
            case LIST:
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < memory.size(); i++) {
                    System.out.printf("%d.%s%n", i + 1, memory.get(i));
                }
                System.out.println(separator);
                break;
            case UNKNOWN:
                memory.add(new Task(command));
                Storage.save(memory);
                System.out.println("added: " + command);
                System.out.println(separator);
                break;
            }
        }
    }

    private static int getTaskIndex(String command, ArrayList<Task> tasks) throws AlzaraException {
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

        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new AlzaraException(AlzaraException.TASK_DOES_NOT_EXIST_MESSAGE);
        }
        return taskIndex;
    }

    private static void printError(AlzaraException exception, String separator) {
        System.out.println(exception.getMessage());
        System.out.println(separator);
    }
}
