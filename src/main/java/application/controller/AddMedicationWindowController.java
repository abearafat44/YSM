package application.controller;

import application.Main;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller for the medication addition window.
 * @author John Wood
 */
public class AddMedicationWindowController {

    private Main main;
    @FXML private ImageView logo;

    /**
     * Sets the instance of the Main class.
     * @param main The instance of the Main class.
     */
    public void setMain(Main main)  {
        this.main = main;
    }

    /**
     * Initialization. Set logo.
     */
    @FXML private void initialize() {
        logo.setImage(new Image("file:src/main/resources/images/ysmLogo.png"));
    }

    /**
     * Opens the management menu.
     */
    @FXML private void manageMenu() {
        main.manageWindow();
    }

    /**
     * Opens the main menu.
     */
    @FXML private void mainMenu() {
        main.mainMenuWindow();
    }
}
