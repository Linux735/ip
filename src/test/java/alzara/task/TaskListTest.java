package alzara.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TaskList}: the thin wrapper around an ArrayList<Task> that
 * every command operates on. Task has no equals()/hashCode() override, so
 * "is this the task I added?" checks below use assertSame (reference
 * identity) rather than assertEquals.
 */
class TaskListTest {

    // A TaskList built from an empty ArrayList starts out empty.
    @Test
    void size_emptyList_returnsZero() {
        TaskList taskList = new TaskList(new ArrayList<>());

        assertEquals(0, taskList.size());
    }

    // add() should increase size() by one.
    @Test
    void add_singleTask_sizeIncreases() {
        TaskList taskList = new TaskList(new ArrayList<>());

        taskList.add(new Task("read book"));

        assertEquals(1, taskList.size());
    }

    // get() should return the exact Task instance that was added at that index.
    @Test
    void get_afterAdd_returnsSameTaskInstance() {
        TaskList taskList = new TaskList(new ArrayList<>());
        Task task = new Task("read book");

        taskList.add(task);

        assertSame(task, taskList.get(0));
    }

    // Tasks should come back out in the order they were added.
    @Test
    void add_multipleTasks_maintainsInsertionOrder() {
        TaskList taskList = new TaskList(new ArrayList<>());
        Task first = new Task("read book");
        Task second = new Task("borrow book");

        taskList.add(first);
        taskList.add(second);

        assertSame(first, taskList.get(0));
        assertSame(second, taskList.get(1));
    }

    // delete() should return the removed task and shrink the list, shifting
    // later tasks down by one index.
    @Test
    void delete_existingIndex_returnsRemovedTaskAndShiftsRemaining() {
        TaskList taskList = new TaskList(new ArrayList<>());
        Task first = new Task("read book");
        Task second = new Task("borrow book");
        taskList.add(first);
        taskList.add(second);

        Task removed = taskList.delete(0);

        assertSame(first, removed);
        assertEquals(1, taskList.size());
        assertSame(second, taskList.get(0));
    }

    // get() on an empty (or otherwise out-of-range) list isn't bounds-checked by
    // TaskList itself - it should propagate ArrayList's own exception. Callers
    // (see Command subclasses) are responsible for validating the index first.
    @Test
    void get_indexOutOfBounds_indexOutOfBoundsExceptionThrown() {
        TaskList taskList = new TaskList(new ArrayList<>());

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(0));
    }

    // Same as above, but for delete().
    @Test
    void delete_indexOutOfBounds_indexOutOfBoundsExceptionThrown() {
        TaskList taskList = new TaskList(new ArrayList<>());

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(0));
    }

    // getTasks() exposes the live underlying list, not a defensive copy - a
    // reference obtained before an add() should still reflect that add()
    // afterwards. Storage.save() and Ui.showTaskList() both depend on this.
    @Test
    void getTasks_afterSubsequentAdd_reflectsTheAddedTask() {
        TaskList taskList = new TaskList(new ArrayList<>());
        ArrayList<Task> tasksView = taskList.getTasks();
        Task task = new Task("read book");

        taskList.add(task);

        assertEquals(1, tasksView.size());
        assertSame(task, tasksView.get(0));
    }
}
