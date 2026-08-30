package alzara.task;

/**
 * A task with a description and a done/not-done state.
 *
 * {@link ToDo}, {@link Deadline}, and {@link Event} are the only subtypes the
 * app actually creates; this class holds the state and formatting logic they
 * all share.
 */
public class Task {
    private String description;
    private boolean isDone;

    private Task(String description, boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }

    /**
     * Creates a task that starts out not done.
     *
     * @param description the task's description
     */
    public Task(String description) {
        this(description, false);
    }

    /**
     * Marks this task as done.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns this task's save-file line.
     *
     * @return the save-file line, e.g. {@code "N | read book"}
     */
    public String toSaveFormat() {
        return (this.isDone ? "Y" : "N") + " | " + this.description;
    }

    /**
     * Returns true if this task's description contains every one of
     * {@code keywords}, ignoring case.
     *
     * @param keywords the keywords to search the description for
     * @return true if the description contains all of {@code keywords}
     */
    public boolean matches(String... keywords) {
        String descriptionChecker = this.description.toLowerCase();
        for (String keyword : keywords) {
            if (!descriptionChecker.contains(keyword.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns this task's console display.
     *
     * @return the display line, e.g. {@code "[X] read book"} once done
     */
    @Override
    public String toString() {
        if (this.isDone) {
            return "[X] " + this.description;
        }
        return "[ ] " + this.description;
    }
}
