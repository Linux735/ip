package alzara.command;

import java.util.ArrayList;

import alzara.task.Task;
import alzara.task.TaskList;
import alzara.ui.Ui;

/**
 * Finds every task whose description contains a keyword and displays them.
 */
public class FindCommand extends Command {
    private final String keyword;

    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList memory, Ui ui) {
        ArrayList<Task> matches = new ArrayList<>();
        for (Task task : memory.getTasks()) {
            if (task.matches(keyword)) {
                matches.add(task);
            }
        }
        ui.showMatchingTasks(matches);
    }
}
