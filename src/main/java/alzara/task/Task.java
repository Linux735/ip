package alzara.task;

/**
 * A task with a description and a done/not-done state.
 *
 * {@link ToDo}, {@link Deadline}, and {@link Event} are the only subtypes the
 * app actually creates; this class holds the state and formatting logic they
 * all share.
 */
public class Task {
    private String task;
    private boolean isDone;

    private Task(String task, boolean isDone) {
        this.task = task;
        this.isDone = isDone;
    }

    /**
     * Creates a task that starts out not done.
     *
     * @param task the task's description
     */
    public Task(String task) {
        this(task, false);
    }

    /**
     * Marks this task as done.
     *
     * @param index unused - kept for symmetry with {@link #unmark}, but this
     *         method's effect doesn't depend on it
     */
    public void mark(int index) {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     *
     * @param index unused - kept for symmetry with {@link #mark}, but this
     *         method's effect doesn't depend on it
     */
    public void unmark(int index) {
        this.isDone = false;
    }

    /**
     * Returns this task's save-file line.
     *
     * @return the save-file line, e.g. {@code "N | read book"}
     */
    public String toSaveFormat() {
        return (this.isDone ? "Y" : "N") + " | " + this.task;
    }

    /**
     * Returns true if this task's description contains {@code keyword}, ignoring case.
     *
     * @param keyword the text to search the description for
     * @return true if the description contains {@code keyword}
     */
    public boolean matches(String keyword) {
        String taskChecker = this.task.toLowerCase();
        return taskChecker.contains(keyword.toLowerCase());
    }

    /**
     * Returns this task's console display.
     *
     * @return the display line, e.g. {@code "[X] read book"} once done
     */
    @Override
    public String toString() {
        if (this.isDone) {
            return "[X] " + this.task;
        }
        return "[ ] " + this.task;
    }
}
