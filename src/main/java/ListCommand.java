/**
 * Displays every task currently in the task list.
 */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList memory, Ui ui) {
        ui.showTaskList(memory.getTasks());
    }
}
