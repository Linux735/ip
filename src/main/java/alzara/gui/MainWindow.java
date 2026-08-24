package alzara.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import alzara.Alzara;

/**
 * Controller for the main GUI window: a scrollable dialog history plus a text
 * field and send button for typing commands.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Alzara alzara;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image alzaraImage = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the {@link Alzara} instance this window sends commands to.
     *
     * @param alzara the Alzara instance to inject
     */
    public void setAlzara(Alzara alzara) {
        this.alzara = alzara;
    }

    /**
     * Creates two dialog boxes, one echoing the user's input and the other containing
     * Alzara's reply, and appends both to the dialog container. Clears the input field
     * afterwards.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = alzara.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getAlzaraDialog(response, alzaraImage)
        );
        userInput.clear();
    }
}
