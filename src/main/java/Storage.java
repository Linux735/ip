import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class Storage {
    private static final String DATA_DIR = "data";
    private static final String FILE_NAME = "alzara.txt";
    private static final File FILE_PATH = new File(DATA_DIR, FILE_NAME);
    private static final String FIELD_SEPARATOR = " \\| ";

    public static void save(ArrayList<Task> memory) {
        if (memory == null) {
            return;
        }

        File parentDir = FILE_PATH.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created && !parentDir.exists()) {
                System.out.println("Could not create the data folder. Your tasks were not saved.");
                return;
            }
        }

        try {
            FileWriter writer = new FileWriter(FILE_PATH);
            for (Task task : memory) {
                writer.write(task.toSaveFormat() + System.lineSeparator());
            }
            writer.close();
        } catch (IOException exception) {
            System.out.println("Something went wrong while saving your tasks.");
        }
    }

    public static ArrayList<Task> load() {
        ArrayList<Task> memory = new ArrayList<>();
        if (!FILE_PATH.exists()) {
            return memory;
        }
        if (!FILE_PATH.canRead()) {
            System.out.println("Could not read the save file. Starting with an empty task list.");
            return memory;
        }

        try {
            Scanner scanner = new Scanner(FILE_PATH);
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    Task task = loadTask(line);
                    memory.add(task);
                } catch (AlzaraException exception) {
                    System.out.println("Skipping corrupted entry on line " + lineNumber
                            + " of the save file: " + exception.getMessage());
                }
            }
            scanner.close();
        } catch (IOException exception) {
            System.out.println("Something went wrong while loading your tasks. "
                    + "Starting with an empty task list.");
            return new ArrayList<>();
        }
        return memory;
    }

    private static Task loadTask(String line) throws AlzaraException {
        String[] parts = line.split(FIELD_SEPARATOR);
        if (parts.length < 3) {
            throw new AlzaraException("not enough fields");
        }

        String type = parts[0].trim();
        String doneFlag = parts[1].trim();
        String description = parts[2];

        if (!doneFlag.equals("N") && !doneFlag.equals("Y")) {
            throw new AlzaraException("invalid done flag");
        }
        if (description.trim().isEmpty()) {
            throw new AlzaraException("missing description");
        }
        boolean done = doneFlag.equals("Y");

        Task task;
        switch (type) {
            case "T":
                task = new ToDo(description);
                break;
            case "D":
                if (parts.length < 4 || parts[3].trim().isEmpty()) {
                    throw new AlzaraException("missing deadline field");
                }
                LocalDate deadlineDate;
                try {
                    deadlineDate = LocalDate.parse(parts[3].trim());
                } catch (DateTimeParseException exception) {
                    throw new AlzaraException("invalid deadline date");
                }
                task = new Deadline(description, deadlineDate);
                break;
            case "E":
                if (parts.length < 5 || parts[3].trim().isEmpty() || parts[4].trim().isEmpty()) {
                    throw new AlzaraException("missing event start/end field");
                }
                LocalDate eventStart;
                LocalDate eventEnd;
                try {
                    eventStart = LocalDate.parse(parts[3].trim());
                    eventEnd = LocalDate.parse(parts[4].trim());
                } catch (DateTimeParseException exception) {
                    throw new AlzaraException("invalid event date");
                }
                task = new Event(description, eventStart, eventEnd);
                break;
            default:
                throw new AlzaraException("unrecognised task type '" + type + "'");
        }

        if (done) {
            task.mark(0);
        }
        return task;
    }
}
