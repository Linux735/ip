/**
 * Marks a task as done, persists the change, and reports it.
 */
public class MarkCommand extends Command {
    private final int taskIndex;

    public MarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList memory, Ui ui) {
        Task task = memory.get(taskIndex);
        task.mark(taskIndex);
        Storage.save(memory.getTasks());
        ui.showTaskMarked(task);
    }
}
