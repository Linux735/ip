package alzara;

import alzara.command.Command;
import alzara.parser.CommandParser;
import alzara.storage.Storage;
import alzara.task.TaskList;
import alzara.ui.Ui;

/**
 * The Alzara chatbot: sets up its collaborators once, then runs the
 * read-command-execute loop until the user says {@code bye}.
 */
public class Alzara {
    private final Ui ui;
    private TaskList memory;

    public Alzara() {
        this.ui = new Ui();
    }

    /**
     * Runs the read-command-execute loop until the user says {@code bye}.
     */
    public void run() {
        ui.showWelcome();
        memory = new TaskList(Storage.load());

        while (true) {
            String command = ui.readCommand();
            ui.showLine();

            try {
                Command userCommand = CommandParser.parse(command);
                userCommand.execute(memory, ui);
                if (userCommand.isExit()) {
                    return;
                }
            } catch (AlzaraException exception) {
                ui.showError(exception.getMessage());
            }
        }
    }

    /**
     * Starts the Alzara chatbot application.
     *
     * @param args command-line arguments supplied when the application starts
     */
    public static void main(String[] args) {
        new Alzara().run();
    }
}
