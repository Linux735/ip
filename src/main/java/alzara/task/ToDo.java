package alzara.task;

/**
 * A task with just a description and a done/not-done state - no date fields.
 */
public class ToDo extends Task{
    /**
     * Creates a to-do that starts out not done.
     *
     * @param task the task's description
     */
    public ToDo(String task) {
        super(task);
    }

    /**
     * @return this task's save-file line, prefixed with {@code "T | "}
     */
    @Override
    public String toSaveFormat() {
        return "T | " + super.toSaveFormat();
    }

    /**
     * @return this task's console display, prefixed with {@code "[T]"}
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
