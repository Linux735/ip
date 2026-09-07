package alzara.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import alzara.task.Deadline;
import alzara.task.Event;
import alzara.task.Task;
import alzara.task.ToDo;

/**
 * Tests for {@link Storage}. Each test points {@link Storage} at a fresh JUnit
 * {@code @TempDir}, so none of them ever touch the real {@code data/alzara.txt}
 * save file. Corrupted-line tests capture {@code System.out} to check the
 * "Skipping corrupted entry..." report text, since {@link Storage} prints
 * those directly rather than returning them.
 */
class StorageTest {
    private static final String SAVE_FILE_NAME = "alzara.txt";

    /**
     * Runs {@code action} with {@code System.out} redirected into a buffer,
     * restores the original {@code System.out} afterwards, and returns
     * everything that was printed.
     */
    private String captureSystemOut(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    /**
     * Writes {@code lines} (already in save-file format) directly into
     * {@code dataDir}'s {@code alzara.txt}, simulating a save file that
     * already existed before {@link Storage} touched it.
     */
    private void writeSaveFile(File dataDir, String... lines) throws IOException {
        File saveFile = new File(dataDir, SAVE_FILE_NAME);
        Files.write(saveFile.toPath(), List.of(lines), StandardCharsets.UTF_8);
    }

    // --- save() / load() round trip ---

    // A freshly created data directory has no save file yet, so load() should
    // return an empty list without printing anything.
    @Test
    void load_noSaveFileYet_returnsEmptyList(@TempDir File dataDir) {
        Storage storage = new Storage(dataDir);

        ArrayList<Task> loaded = storage.load();

        assertTrue(loaded.isEmpty());
    }

    // save() followed by load() should reconstruct an equivalent ToDo,
    // including its done flag.
    @Test
    void saveThenLoad_singleToDo_roundTrips(@TempDir File dataDir) {
        Storage storage = new Storage(dataDir);
        ArrayList<Task> memory = new ArrayList<>();
        memory.add(new ToDo("read book"));

        storage.save(memory);
        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("T | N | read book", loaded.get(0).toSaveFormat());
    }

    // save() should persist every task type (ToDo/Deadline/Event), their
    // dates, and their done flags, and load() should reconstruct them in the
    // same order.
    @Test
    void saveThenLoad_allTaskTypesAndDoneStates_preservesOrderAndDetails(@TempDir File dataDir) {
        Storage storage = new Storage(dataDir);
        ArrayList<Task> memory = new ArrayList<>();
        ToDo toDo = new ToDo("read book");
        toDo.mark();
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        Event event = new Event("trip", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 16));
        memory.add(toDo);
        memory.add(deadline);
        memory.add(event);

        storage.save(memory);
        ArrayList<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals("T | Y | read book", loaded.get(0).toSaveFormat());
        assertEquals("D | N | return book | 2019-10-15", loaded.get(1).toSaveFormat());
        assertEquals("E | N | trip | 2019-10-15 | 2019-10-16", loaded.get(2).toSaveFormat());
    }

    // save(null) should do nothing - no file, no exception.
    @Test
    void save_nullMemory_doesNothing(@TempDir File dataDir) {
        Storage storage = new Storage(dataDir);

        storage.save(null);

        assertFalse(new File(dataDir, SAVE_FILE_NAME).exists());
    }

    // save() should create the data directory itself if it doesn't exist yet.
    @Test
    void save_dataDirectoryDoesNotExistYet_isCreated(@TempDir File tempDir) {
        File dataDir = new File(tempDir, "nested/data/dir");
        Storage storage = new Storage(dataDir);
        ArrayList<Task> memory = new ArrayList<>();
        memory.add(new ToDo("read book"));

        storage.save(memory);

        assertTrue(new File(dataDir, SAVE_FILE_NAME).exists());
    }

    // A second save() should fully replace the first save's content, not append to it.
    @Test
    void save_calledTwice_secondSaveReplacesFirst(@TempDir File dataDir) {
        Storage storage = new Storage(dataDir);
        ArrayList<Task> firstMemory = new ArrayList<>();
        firstMemory.add(new ToDo("read book"));
        firstMemory.add(new ToDo("borrow book"));
        storage.save(firstMemory);

        ArrayList<Task> secondMemory = new ArrayList<>();
        secondMemory.add(new ToDo("walk dog"));
        storage.save(secondMemory);
        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("T | N | walk dog", loaded.get(0).toSaveFormat());
    }

    // save() writes to a temp file before moving it into place - after a
    // successful save, that temp file should not be left behind.
    @Test
    void save_afterSuccess_leavesNoLeftoverTempFile(@TempDir File dataDir) {
        Storage storage = new Storage(dataDir);
        ArrayList<Task> memory = new ArrayList<>();
        memory.add(new ToDo("read book"));

        storage.save(memory);

        assertFalse(new File(dataDir, SAVE_FILE_NAME + ".tmp").exists());
    }

    // --- load() corrupted-entry handling ---

    // Blank lines in the save file should be silently skipped - no task, no report.
    @Test
    void load_blankLinesInFile_areSkippedSilently(@TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir, "T | N | read book", "", "   ", "T | N | borrow book");
        Storage storage = new Storage(dataDir);

        ArrayList<Task> loaded = new ArrayList<>();
        String output = captureSystemOut(() -> loaded.addAll(storage.load()));

        assertEquals(2, loaded.size());
        assertFalse(output.contains("Skipping"));
    }

    // A line with too few "|"-separated fields should be reported and skipped.
    @Test
    void load_notEnoughFields_reportsAndSkipsLine(@TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir, "T | N");
        Storage storage = new Storage(dataDir);

        ArrayList<Task> loaded = new ArrayList<>();
        String output = captureSystemOut(() -> loaded.addAll(storage.load()));

        assertTrue(loaded.isEmpty());
        assertTrue(output.contains("Skipping corrupted entry on line 1 of the save file: not enough fields"));
    }

    // A done flag that isn't "N" or "Y" should be reported and skipped.
    @Test
    void load_invalidDoneFlag_reportsAndSkipsLine(@TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir, "T | Z | read book");
        Storage storage = new Storage(dataDir);

        ArrayList<Task> loaded = new ArrayList<>();
        String output = captureSystemOut(() -> loaded.addAll(storage.load()));

        assertTrue(loaded.isEmpty());
        assertTrue(output.contains("Skipping corrupted entry on line 1 of the save file: invalid done flag"));
    }

    // A blank description should be reported and skipped.
    @Test
    void load_missingDescription_reportsAndSkipsLine(@TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir, "T | N |   ");
        Storage storage = new Storage(dataDir);

        ArrayList<Task> loaded = new ArrayList<>();
        String output = captureSystemOut(() -> loaded.addAll(storage.load()));

        assertTrue(loaded.isEmpty());
        assertTrue(output.contains("Skipping corrupted entry on line 1 of the save file: missing description"));
    }

    // An unrecognised type letter should be reported (including the offending
    // letter) and skipped.
    @Test
    void load_unrecognisedTaskType_reportsAndSkipsLine(@TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir, "X | N | mystery task");
        Storage storage = new Storage(dataDir);

        ArrayList<Task> loaded = new ArrayList<>();
        String output = captureSystemOut(() -> loaded.addAll(storage.load()));

        assertTrue(loaded.isEmpty());
        assertTrue(output.contains(
                "Skipping corrupted entry on line 1 of the save file: unrecognised task type 'X'"));
    }

    // A "D" line with no date field at all should be reported and skipped.
    @Test
    void load_deadlineMissingDateField_reportsAndSkipsLine(@TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir, "D | N | return book");
        Storage storage = new Storage(dataDir);

        ArrayList<Task> loaded = new ArrayList<>();
        String output = captureSystemOut(() -> loaded.addAll(storage.load()));

        assertTrue(loaded.isEmpty());
        assertTrue(output.contains(
                "Skipping corrupted entry on line 1 of the save file: missing deadline field"));
    }

    // A "D" line whose date field doesn't parse as yyyy-MM-dd should be reported and skipped.
    @Test
    void load_deadlineInvalidDate_reportsAndSkipsLine(@TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir, "D | N | return book | Sunday");
        Storage storage = new Storage(dataDir);

        ArrayList<Task> loaded = new ArrayList<>();
        String output = captureSystemOut(() -> loaded.addAll(storage.load()));

        assertTrue(loaded.isEmpty());
        assertTrue(output.contains(
                "Skipping corrupted entry on line 1 of the save file: invalid deadline date"));
    }

    // An "E" line missing its start/end fields should be reported and skipped.
    @Test
    void load_eventMissingDateFields_reportsAndSkipsLine(@TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir, "E | N | trip");
        Storage storage = new Storage(dataDir);

        ArrayList<Task> loaded = new ArrayList<>();
        String output = captureSystemOut(() -> loaded.addAll(storage.load()));

        assertTrue(loaded.isEmpty());
        assertTrue(output.contains(
                "Skipping corrupted entry on line 1 of the save file: missing event start/end field"));
    }

    // An "E" line whose start or end date doesn't parse should be reported and skipped.
    @Test
    void load_eventInvalidDate_reportsAndSkipsLine(@TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir, "E | N | trip | 2019-10-15 | Sunday");
        Storage storage = new Storage(dataDir);

        ArrayList<Task> loaded = new ArrayList<>();
        String output = captureSystemOut(() -> loaded.addAll(storage.load()));

        assertTrue(loaded.isEmpty());
        assertTrue(output.contains(
                "Skipping corrupted entry on line 1 of the save file: invalid event date"));
    }

    // An "E" line whose start date is after its end date should be reported and skipped,
    // even though both dates parse fine individually.
    @Test
    void load_eventStartDateAfterEndDate_reportsAndSkipsLine(@TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir, "E | N | trip | 2019-10-20 | 2019-10-15");
        Storage storage = new Storage(dataDir);

        ArrayList<Task> loaded = new ArrayList<>();
        String output = captureSystemOut(() -> loaded.addAll(storage.load()));

        assertTrue(loaded.isEmpty());
        assertTrue(output.contains(
                "Skipping corrupted entry on line 1 of the save file: event start date after end date"));
    }

    // A save file mixing valid and corrupted lines should keep every valid
    // task, in order, and report every corrupted line by its own line number.
    @Test
    void load_mixOfValidAndCorruptedLines_keepsValidTasksAndReportsEachCorruptedLine(
            @TempDir File dataDir) throws IOException {
        writeSaveFile(dataDir,
                "T | N | read book",
                "X | N | mystery task",
                "D | N | return book",
                "T | Y | walk dog");
        Storage storage = new Storage(dataDir);

        ArrayList<Task> loaded = new ArrayList<>();
        String output = captureSystemOut(() -> loaded.addAll(storage.load()));

        assertEquals(2, loaded.size());
        assertEquals("T | N | read book", loaded.get(0).toSaveFormat());
        assertEquals("T | Y | walk dog", loaded.get(1).toSaveFormat());
        assertTrue(output.contains(
                "Skipping corrupted entry on line 2 of the save file: unrecognised task type 'X'"));
        assertTrue(output.contains(
                "Skipping corrupted entry on line 3 of the save file: missing deadline field"));
    }
}
