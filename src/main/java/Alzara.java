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
        System.out.println("And as it was foretold,\nI am awakened by the sound of tears.");
        System.out.println("You find yourself face to face with the great Alzara.");
        System.out.println("What can I do for you?");
        System.out.println(separator);

        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> memory = new ArrayList<>();

        while (true) {
            String command = scanner.nextLine();
            System.out.println(separator);

            if (command.equalsIgnoreCase("bye")) {
                System.out.println("Our audience has ended. Until we meet again.");
                System.out.println(separator);
                break;
            }

            if (command.startsWith("mark ")) {
                int taskIndex = Integer.parseInt(command.split(" ")[1]) - 1;
                if (taskIndex >= memory.size() | taskIndex < 0) {
                    System.out.println("Task does not exist.");
                } else {
                    Task task = memory.get(taskIndex);
                    task.mark(taskIndex);
                    System.out.println("You have satisfied the great Alzara.");
                    System.out.println(task);
                    System.out.println(separator);
                }
            } else if (command.startsWith("unmark ")) {
                int taskIndex = Integer.parseInt(command.split(" ")[1]) - 1;
                if (taskIndex >= memory.size() | taskIndex < 0) {
                    System.out.println("Task does not exist.");
                } else {
                    Task task = memory.get(taskIndex);
                    task.unmark(taskIndex);
                    System.out.println("As I predicted...");
                    System.out.println(task);
                    System.out.println(separator);
                }
            } else if (command.startsWith("todo ")) {
                Task task = new ToDo(command.substring(5));
                memory.add(task);
                System.out.println("You have something to do...");
                System.out.println(task);
                System.out.println("You have " + memory.size() + " tasks.");
                System.out.println(separator);
            } else if (command.startsWith("deadline ")) {
                int deadlineMarker = command.indexOf(" /by ");
                String description = command.substring(9, deadlineMarker);
                String deadline = command.substring(deadlineMarker + 5);
                Task task = new Deadline(description, deadline);
                memory.add(task);
                System.out.println("Do not miss the deadline.");
                System.out.println(task);
                System.out.println("You have " + memory.size() + " tasks.");
                System.out.println(separator);
            } else if (command.startsWith("event ")) {
                int startMarker = command.indexOf(" /from ");
                int endMarker = command.indexOf(" /to ");
                String description = command.substring(6, startMarker);
                String start = command.substring(startMarker + 7, endMarker);
                String end = command.substring(endMarker + 5);
                Task task = new Event(description, start, end);
                memory.add(task);
                System.out.println("Am I invited?");
                System.out.println(task);
                System.out.println("You have " + memory.size() + " tasks.");
                System.out.println(separator);
            } else if (command.equals("list")) {
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < memory.size(); i++) {
                    System.out.printf("%d.%s%n", i + 1, memory.get(i));
                }
                System.out.println(separator);
            } else {
                memory.add(new Task(command));
                System.out.println("added: " + command);
                System.out.println(separator);
            }
        }
    }
}
