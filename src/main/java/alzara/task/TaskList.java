package alzara.task;

import java.util.ArrayList;

/**
 * Wraps the in-memory task list and exposes the operations Alzara performs
 * on it, so callers manipulate tasks through named operations instead of
 * reaching into a raw {@code ArrayList} directly.
 */
public class TaskList {
    private final ArrayList<Task> memory;

    public TaskList(ArrayList<Task> tasks) {
        this.memory = tasks;
    }

    public void add(Task task) {
        this.memory.add(task);
    }

    public Task get(int index) {
        return this.memory.get(index);
    }

    public Task delete(int index) {
        return this.memory.remove(index);
    }

    public int size() {
        return this.memory.size();
    }

    /**
     * Exposes the underlying list for callers that need to read or persist
     * every task at once, e.g. {@link alzara.storage.Storage#save} and
     * {@link alzara.ui.Ui#showTaskList}.
     */
    public ArrayList<Task> getTasks() {
        return this.memory;
    }
}
