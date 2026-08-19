/**
 * Marks a task as not done, persists the change, and reports it.
 */
public class UnmarkCommand extends Command {
    private final int taskIndex;

    public UnmarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList memory, Ui ui) {
        Task task = memory.get(taskIndex);
        task.unmark(taskIndex);
        Storage.save(memory.getTasks());
        ui.showTaskUnmarked(task);
    }
}
