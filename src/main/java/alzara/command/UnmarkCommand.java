package alzara.command;

import alzara.AlzaraException;
import alzara.storage.Storage;
import alzara.task.Task;
import alzara.task.TaskList;
import alzara.ui.Ui;

/**
 * Marks a task as not done, persists the change, and reports it.
 */
public class UnmarkCommand extends Command {
    private final int taskIndex;

    public UnmarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList memory, Ui ui) throws AlzaraException {
        if (taskIndex < 0 || taskIndex >= memory.size()) {
            throw new AlzaraException(AlzaraException.TASK_DOES_NOT_EXIST_MESSAGE);
        }
        Task task = memory.get(taskIndex);
        task.unmark(taskIndex);
        Storage.save(memory.getTasks());
        ui.showTaskUnmarked(task);
    }
}
