package application;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

/**
 * Shows alerts to the user.
 * @author John Wood
 */
public class GenAlert {

    /**
     * Information alert.
     * @param message The message to show in the alert
     */
    public static void info(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText("Oops!");
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.show();
    }

    /**
     * Error alert.
     * @param message The message to show in the alert
     */
    public static void error(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setHeaderText("An error has occurred");
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.show();
    }

    /**
     * Warning alert.
     * @param message The message to show in the alert
     */
    public static void warning(String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setHeaderText("Warning");
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.show();
    }

    /**
     * Confirmation alert.
     * @param areYouSure The confirmation query to show in the alert
     * @param message The message to show in the alert
     * @return Whether the user clicked "Yes"
     */
    public static boolean confirmation(String areYouSure, String message) {
        //Adapted from Ali Cheaito's answer from https://stackoverflow.com/questions/8309981/how-to-create-and-show-common-dialog-error-warning-confirmation-in-javafx-2
        Alert alert = new Alert(AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(areYouSure);
        alert.setTitle("Confirmation");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES)
            return true;
        return false;
    }
}
