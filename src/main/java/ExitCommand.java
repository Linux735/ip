/**
 * Ends the program: shows the goodbye message and signals {@link Command#isExit}.
 */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList memory, Ui ui) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
