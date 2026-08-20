package alzara.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Event}: the "[E]"/"E |" prefix plus the "from"/"to" dates.
 * Mark/unmark state is already covered via ToDoTest, since that behaviour is
 * inherited from Task unchanged; the "MMM dd yyyy" padding behaviour is
 * already covered via DeadlineTest, since both classes share the same
 * DateTimeFormatter pattern.
 */
class EventTest {

    // A freshly constructed Event starts out not done, with both dates
    // reformatted from ISO (constructor input) to "MMM dd yyyy" (display).
    @Test
    void toString_taskNotDone_showsEmptyCheckboxWithFromToDates() {
        Event event = new Event("project meeting", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 16));

        assertEquals("[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)", event.toString());
    }

    // mark() (inherited from Task) should flip the task to done.
    @Test
    void toString_taskMarkedDone_showsXCheckboxWithFromToDates() {
        Event event = new Event("project meeting", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 16));

        event.mark(0);

        assertEquals("[E][X] project meeting (from: Oct 15 2019 to: Oct 16 2019)", event.toString());
    }

    // toSaveFormat() should store both dates in their raw ISO form
    // (yyyy-MM-dd), not the "MMM dd yyyy" display form.
    @Test
    void toSaveFormat_taskNotDone_hasTypeAndBothIsoDates() {
        Event event = new Event("project meeting", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 16));

        assertEquals("E | N | project meeting | 2019-10-15 | 2019-10-16", event.toSaveFormat());
    }

    // toSaveFormat() should switch to "E | Y |" once the task is marked done.
    @Test
    void toSaveFormat_taskMarkedDone_hasTypeAndYFlag() {
        Event event = new Event("project meeting", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 16));

        event.mark(0);

        assertEquals("E | Y | project meeting | 2019-10-15 | 2019-10-16", event.toSaveFormat());
    }
}
