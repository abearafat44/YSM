package application.controller;

import application.Main;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller for the landing window.
 * @author John Wood
 */
public class LandingWindowController {
    private Main main;
    @FXML private ImageView logo, pharmacyIcon;

    public void setMain(Main main) {
        this.main = main;
    }

    /**
     * Initialization. Sets the image views.
     */
    @FXML private void initialize() {
        logo.setImage(new Image("file:src/main/resources/images/ysmLogo.png"));
        pharmacyIcon.setImage(new Image("file:src/main/resources/images/icons/pharmacy.png"));
    }

    /**
     * Opens the main menu.
     */
    @FXML private void mainMenu() {
        main.mainMenuWindow();
    }
}
