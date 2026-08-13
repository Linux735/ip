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
            } else if (command.equals("list")) {
                for (Task item : memory) {
                    System.out.printf("%d. %s\n", memory.indexOf(item) + 1, item);
                }
            } else {
                memory.add(new Task(command));
                System.out.println("added: " + command);
                System.out.println(separator);
            }
        }
    }
}
