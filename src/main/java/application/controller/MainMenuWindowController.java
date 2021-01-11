package application.controller;

import application.Main;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;

import java.util.Random;

/**
 * Controller for the landing page (main menu) window.
 * @author John Wood
 */
public class MainMenuWindowController {

    private Main main;
    @FXML
    private ImageView logo, background, prescribeIcon, manageIcon, settingsIcon;
    @FXML
    private AnchorPane mainPane;

    /**
     * Sets the instance of the Main class.
     * @param main The instance of the Main class
     */
    public void setMain(Main main) {
        this.main=main;
    }

    /**
     * Initialization. Sets the image views.
     */
    @FXML
    private void initialize() {
        logo.setImage(new Image("file:src/main/resources/images/ysmLogo.png"));
        prescribeIcon.setImage(new Image("file:src/main/resources/images/icons/note.png"));
        manageIcon.setImage(new Image("file:src/main/resources/images/icons/spreadsheet.png"));
        settingsIcon.setImage(new Image("file:src/main/resources/images/icons/system.png"));

        if (Main.getPrefs().getBoolean("backImage", false)) {
            //Set a random background
            Random r = new Random();
            background.setImage(new Image("file:src/main/resources/images/toronto/toronto" + (r.nextInt(6) + 1) + ".jpeg"));
        }

    }

    /**
     * Opens the prescription window.
     */
    @FXML private void prescribe(){
        main.prescribeWindow();
    }

    /**
     * Opens the manage window.
     */
    @FXML private void manage() {
        main.manageWindow();
    }

    /**
     * opens the settings menu.
     */
    @FXML private void settings() {
        main.settingsWindow();
    }
}
