package alzara.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

import alzara.AlzaraException;
import alzara.task.Deadline;
import alzara.task.Event;
import alzara.task.Task;
import alzara.task.ToDo;

/**
 * Loads tasks from, and saves tasks to, the save file at {@code data/alzara.txt}
 * (relative to the working directory the program is run from).
 */
public class Storage {
    private static final String DATA_DIR = "data";
    private static final String FILE_NAME = "alzara.txt";
    private static final File FILE_PATH = new File(DATA_DIR, FILE_NAME);
    private static final String FIELD_SEPARATOR = " \\| ";

    /**
     * Overwrites the save file with every task in {@code memory}, one per line
     * in each task's {@link Task#toSaveFormat()}.
     *
     * <p>Creates the {@code data} folder first if it doesn't exist yet. Prints
     * a message and returns without throwing if the folder can't be created
     * or the file can't be written - a failed save shouldn't crash the program.
     *
     * @param memory the current task list to persist; does nothing if {@code null}
     */
    public static void save(ArrayList<Task> memory) {
        if (memory == null) {
            return;
        }

        File parentDir = FILE_PATH.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean wasCreated = parentDir.mkdirs();
            if (!wasCreated && !parentDir.exists()) {
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

    /**
     * Reads every task from the save file, skipping (and reporting, with its
     * line number and reason) any line that can't be parsed rather than
     * failing the whole load.
     *
     * @return the loaded tasks, or an empty list if the save file doesn't
     *         exist yet, can't be read, or couldn't be loaded at all
     */
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

    /**
     * Parses one save-file line (e.g. {@code "T | N | read book"}) into the
     * matching {@link Task} subtype, and applies its done flag.
     *
     * @throws AlzaraException if the line has too few fields, an invalid done
     *         flag, an empty description, an unrecognised type letter, or a
     *         missing/malformed date field for that type
     */
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
        boolean isDone = doneFlag.equals("Y");

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

        if (isDone) {
            task.mark(0);
        }
        return task;
    }
}
