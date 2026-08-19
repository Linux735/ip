/**
 * Removes a task from the task list, persists the change, and reports it.
 */
public class DeleteCommand extends Command {
    private final int taskIndex;

    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList memory, Ui ui) {
        Task deletedTask = memory.delete(taskIndex);
        Storage.save(memory.getTasks());
        ui.showTaskDeleted(deletedTask, memory.size());
    }
}
