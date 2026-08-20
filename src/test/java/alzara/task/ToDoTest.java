package alzara.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ToDo}. ToDo only overrides toString()/toSaveFormat() to add
 * a "[T]"/"T |" prefix - the underlying done/not-done state (mark/unmark) is
 * inherited from Task unchanged, so this class is also where that inherited
 * behaviour gets exercised, since Task itself is no longer instantiated
 * directly anywhere in the app.
 */
class ToDoTest {

    // A freshly constructed ToDo starts out not done.
    @Test
    void toString_taskNotDone_showsEmptyCheckboxWithTypePrefix() {
        ToDo toDo = new ToDo("read book");

        assertEquals("[T][ ] read book", toDo.toString());
    }

    // mark() (inherited from Task) should flip the task to done.
    @Test
    void toString_taskMarkedDone_showsXCheckboxWithTypePrefix() {
        ToDo toDo = new ToDo("read book");

        toDo.mark(0);

        assertEquals("[T][X] read book", toDo.toString());
    }

    // unmark() (inherited from Task) should flip a done task back to not done.
    @Test
    void toString_markedThenUnmarked_showsEmptyCheckboxAgain() {
        ToDo toDo = new ToDo("read book");
        toDo.mark(0);

        toDo.unmark(0);

        assertEquals("[T][ ] read book", toDo.toString());
    }

    // toSaveFormat() should start with "T | N |" (type + not done) for a fresh ToDo.
    @Test
    void toSaveFormat_taskNotDone_hasTypeAndNFlag() {
        ToDo toDo = new ToDo("read book");

        assertEquals("T | N | read book", toDo.toSaveFormat());
    }

    // toSaveFormat() should switch to "T | Y |" once the task is marked done.
    @Test
    void toSaveFormat_taskMarkedDone_hasTypeAndYFlag() {
        ToDo toDo = new ToDo("read book");

        toDo.mark(0);

        assertEquals("T | Y | read book", toDo.toSaveFormat());
    }

    // mark()/unmark() take an int index that Task never actually uses - this
    // documents that the done-state change happens regardless of what index is
    // passed in.
    @Test
    void mark_indexArgumentIgnored_taskMarkedDoneRegardless() {
        ToDo toDo = new ToDo("read book");

        toDo.mark(999);

        assertEquals("[T][X] read book", toDo.toString());
    }
}
