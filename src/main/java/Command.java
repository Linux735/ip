/**
 * Represents a single user command that knows how to carry out its own
 * effect on the task list and UI. Concrete subclasses implement one kind of
 * command each (e.g. exiting the program, adding a task).
 */
public abstract class Command {
    /**
     * Carries out this command's effect, e.g. adding a task or printing the
     * task list.
     */
    public abstract void execute(TaskList memory, Ui ui) throws AlzaraException;

    /**
     * Returns true if this command should end the program's main loop.
     * Only {@link ExitCommand} overrides this to return true.
     */
    public boolean isExit() {
        return false;
    }
}
