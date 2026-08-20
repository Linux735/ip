package alzara.ui;

import java.util.ArrayList;
import java.util.Scanner;

import alzara.task.Task;

/**
 * Handles all interaction with the user: reading raw input from the console
 * and printing Alzara's responses. Keeping this in one place means the rest
 * of the program never calls {@code System.out}/{@code Scanner} directly.
 */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";

    private final Scanner scanner;

    /**
     * Opens a {@link Scanner} on {@link System#in} for {@link #readCommand()}.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads the next full line of input typed by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints the horizontal divider used to separate one interaction from the next.
     */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /**
     * Prints the startup banner and welcome message.
     */
    public void showWelcome() {
        String banner = "    _    _     ______    _    ____       _    \n"
                + "   / \\  | |   |__  /   / \\  |  _ \\     / \\   \n"
                + "  / _ \\ | |     / /   / _ \\ | |_) |   / _ \\  \n"
                + " / ___ \\| |___ / /_  / ___ \\|  _ <   / ___ \\ \n"
                + "/_/   \\_\\_____/____|/_/   \\_\\_| \\_\\ /_/   \\_\\\n";
        showLine();
        System.out.print(banner);
        System.out.println("And as it was foretold,");
        System.out.println("You find yourself face to face with the great Alzara.");
        System.out.println("What can I do for you?");
        showLine();
    }

    /**
     * Prints the exit message shown by {@code bye}.
     */
    public void showGoodbye() {
        System.out.println("Our audience has ended. Until we meet again.");
        showLine();
    }

    /**
     * Reports that a task was marked done, showing the task itself.
     *
     * @param task the task that was marked done
     */
    public void showTaskMarked(Task task) {
        System.out.println("You have satisfied the great Alzara.");
        System.out.println(task);
        showLine();
    }

    /**
     * Reports that a task was marked not done, showing the task itself.
     *
     * @param task the task that was marked not done
     */
    public void showTaskUnmarked(Task task) {
        System.out.println("As I predicted...");
        System.out.println(task);
        showLine();
    }

    /**
     * Reports that a new task was added, e.g. after a {@code todo}, {@code deadline}
     * or {@code event} command. {@code flavourText} is the command-specific line
     * shown above the task (e.g. "Do not miss the deadline.").
     */
    public void showTaskAdded(String flavourText, Task task, int taskCount) {
        System.out.println(flavourText);
        System.out.println(task);
        System.out.println("You have " + taskCount + " tasks.");
        showLine();
    }

    /**
     * Reports that a task was removed, showing the removed task and how many
     * tasks remain.
     *
     * @param task the task that was removed
     * @param remainingCount the number of tasks left after the removal
     */
    public void showTaskDeleted(Task task, int remainingCount) {
        System.out.println("I have removed the task " + task);
        System.out.println("You have " + remainingCount + " tasks remaining.");
        showLine();
    }

    /**
     * Prints every task in {@code tasks}, numbered from 1, for the {@code list} command.
     *
     * @param tasks the tasks to display
     */
    public void showTaskList(ArrayList<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.printf("%d.%s%n", i + 1, tasks.get(i));
        }
        showLine();
    }

    /**
     * Prints an error message, e.g. an {@link alzara.AlzaraException}'s
     * {@code getMessage()}.
     *
     * @param message the error message to display
     */
    public void showError(String message) {
        System.out.println(message);
        showLine();
    }

    public void showMatchingTasks(ArrayList<Task> matches) {
        System.out.println("Here are the matching tasks in your list:");
        for (int i = 0; i < matches.size(); i++) {
            System.out.printf("%d.%s%n", i + 1, matches.get(i));
        }
        showLine();
    }
}

