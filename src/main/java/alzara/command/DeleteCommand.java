package alzara.command;

import alzara.AlzaraException;
import alzara.storage.Storage;
import alzara.task.Task;
import alzara.task.TaskList;
import alzara.ui.Ui;

/**
 * Removes a task from the task list, persists the change, and reports it.
 */
public class DeleteCommand extends Command {
    private final int taskIndex;

    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public void execute(TaskList memory, Ui ui) throws AlzaraException {
        if (taskIndex < 0 || taskIndex >= memory.size()) {
            throw new AlzaraException(AlzaraException.TASK_DOES_NOT_EXIST_MESSAGE);
        }
        Task deletedTask = memory.delete(taskIndex);
        Storage.save(memory.getTasks());
        ui.showTaskDeleted(deletedTask, memory.size());
    }
}
