import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Storage {
    private static final String DATA_DIR = "data";
    private static final String FILE_NAME = "alzara.txt";
    private static final File FILE_PATH = new File(DATA_DIR, FILE_NAME);

    public static void save(ArrayList<Task> memory) {
        try {
            File parentDir = FILE_PATH.getParentFile();
            if (parentDir != null) {
                parentDir.mkdirs();
            }
            FileWriter writer = new FileWriter(FILE_PATH);
            for (Task task : memory) {
                writer.write(task.toSaveFormat() + System.lineSeparator());
            }
            writer.close();
        } catch (IOException exception) {
            System.out.println("Something went wrong while saving your tasks.");
        }
    }
}
