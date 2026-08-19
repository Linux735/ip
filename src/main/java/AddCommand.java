/**
 * Adds a task to the task list, persists it, and reports it was added.
 * Used by the {@code todo}, {@code deadline}, and {@code event} commands,
 * which only differ in the already-parsed {@link Task} and the flavour
 * text shown above it (e.g. "Do not miss the deadline.").
 */
public class AddCommand extends Command {
    private final Task task;
    private final String flavourText;

    public AddCommand(Task task, String flavourText) {
        this.task = task;
        this.flavourText = flavourText;
    }

    @Override
    public void execute(TaskList memory, Ui ui) {
        memory.add(task);
        Storage.save(memory.getTasks());
        ui.showTaskAdded(flavourText, task, memory.size());
    }
}
