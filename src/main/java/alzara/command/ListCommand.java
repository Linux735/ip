package alzara.command;

import alzara.task.TaskList;
import alzara.ui.Ui;

/**
 * Displays every task currently in the task list.
 */
public class ListCommand extends Command {
    /**
     * Prints every task currently in {@code memory}.
     */
    @Override
    public void execute(TaskList memory, Ui ui) {
        ui.showTaskList(memory.getTasks());
    }
}
