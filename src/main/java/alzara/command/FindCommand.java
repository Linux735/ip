package alzara.command;

import java.util.ArrayList;

import alzara.task.Task;
import alzara.task.TaskList;
import alzara.ui.Ui;

/**
 * Finds every task whose description contains all of the given keywords and
 * displays them.
 */
public class FindCommand extends Command {
    private final String[] keywords;

    /**
     * Creates a command that finds every task matching all of {@code keywords}.
     *
     * @param keywords the keywords to search task descriptions for
     */
    public FindCommand(String... keywords) {
        this.keywords = keywords;
    }

    @Override
    public void execute(TaskList memory, Ui ui) {
        ArrayList<Task> matches = new ArrayList<>();
        for (Task task : memory.getTasks()) {
            if (task.matches(keywords)) {
                matches.add(task);
            }
        }
        ui.showMatchingTasks(matches);
    }
}
