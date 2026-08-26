package alzara;

import alzara.command.Command;
import alzara.parser.CommandParser;
import alzara.storage.Storage;
import alzara.task.TaskList;
import alzara.ui.Ui;

/**
 * The Alzara chatbot: sets up its collaborators once, then either runs the
 * console read-command-execute loop until the user says {@code bye}, or (for
 * the JavaFX GUI) answers one command at a time via {@link #getResponse}.
 */
public class Alzara {
    private final Ui ui;
    private final TaskList memory;
    private boolean isExit = false;

    /**
     * Creates an {@link Alzara} for the console.
     */
    public Alzara() {
        this(false);
    }

    /**
     * Creates an {@link Alzara} for either the console or the JavaFX GUI,
     * loading the saved task list immediately either way.
     *
     * @param isGuiMode if true, {@link #getResponse} returns each reply as a
     *         string instead of the {@link Ui} printing it to the console
     */
    public Alzara(boolean isGuiMode) {
        this.ui = new Ui(isGuiMode);
        this.memory = new TaskList(Storage.load());
    }

    /**
     * Runs the read-command-execute loop until the user says {@code bye}.
     */
    public void run() {
        ui.showWelcome();

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
     * Parses and executes a single command typed into the GUI, returning
     * Alzara's reply as plain text.
     *
     * @param input the raw command text typed by the user
     * @return Alzara's reply to that command
     */
    public String getResponse(String input) {
        try {
            Command userCommand = CommandParser.parse(input);
            userCommand.execute(memory, ui);
            isExit = userCommand.isExit();
        } catch (AlzaraException exception) {
            ui.showError(exception.getMessage());
        }
        return ui.getAndClearResponse();
    }

    /**
     * Returns true if the most recent {@link #getResponse} call executed a
     * command that should end the program, e.g. {@code bye}.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns Alzara's welcome message as plain text, for the GUI to show
     * once when the window opens.
     */
    public String getWelcomeMessage() {
        ui.showWelcome();
        return ui.getAndClearResponse();
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
