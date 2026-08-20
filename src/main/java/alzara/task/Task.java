package alzara.task;

/**
 * A task with a description and a done/not-done state. {@link ToDo},
 * {@link Deadline}, and {@link Event} are the only subtypes the app actually
 * creates; this class holds the state and formatting logic they all share.
 */
public class Task {
    private String task;
    private boolean done;

    private Task(String task , boolean done) {
        this.task = task;
        this.done = done;
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
        this.done = true;
    }

    /**
     * Marks this task as not done.
     *
     * @param index unused - kept for symmetry with {@link #mark}, but this
     *         method's effect doesn't depend on it
     */
    public void unmark(int index) {
        this.done = false;
    }

    /**
     * @return this task's save-file line, e.g. {@code "N | read book"}
     */
    public String toSaveFormat() {
        return (this.done ? "Y" : "N") + " | " + this.task;
    }

    /**
     * @return this task's console display, e.g. {@code "[X] read book"} once done
     */
    @Override
    public String toString() {
        if (this.done) {
            return "[X] " + this.task;
        }
        return "[ ] " + this.task;
    }
}
