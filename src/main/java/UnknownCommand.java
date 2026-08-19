/**
 * Handles input that isn't a recognised command: Alzara stores the raw text
 * as a task anyway, rather than rejecting it.
 */
public class UnknownCommand extends Command {
    private final String rawCommand;

    public UnknownCommand(String rawCommand) {
        this.rawCommand = rawCommand;
    }

    @Override
    public void execute(TaskList memory, Ui ui) {
        memory.add(new Task(rawCommand));
        Storage.save(memory.getTasks());
        ui.showRawTaskAdded(rawCommand);
    }
}
