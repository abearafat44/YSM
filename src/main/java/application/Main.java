package application;

import application.controller.*;
import application.object.Inventory;
import application.object.Med;
import application.object.Lot;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.prefs.Preferences;

/**
 * Main class of the application.
 * @author John Wood
 */
//That fxml files should be in the resources folder from John Farrelly's answer on https://stackoverflow.com/questions/50902170/maven-javafx-project-unable-to-find-fxml-file-using-getresource
public class Main extends Application {
    private Stage primaryStage;
    public static Inventory inventory;
    private static final Preferences prefs = Preferences.userNodeForPackage(SettingsWindowController.class);

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage=primaryStage;
        //this.primaryStage.getIcons().add(new Image(Main.class.getClassLoader().getResourceAsStream("images/ysmLogo.png")));
        this.primaryStage.setTitle("YSM Medication");

        //Initialize the inventory
        boolean success = true;
        try {
          inventory = new Inventory(prefs.get("excelPath",""), prefs.get("commandsPath",""));
        } catch (Exception e) {
           e.printStackTrace();
           success = false;
        }

        landingWindow();

        //Display applicable error message
        if(!success)
            GenAlert.error("An error occurred while getting data from the spreadsheet.");

    }

    /**
     * The landing window.
     */
    public void landingWindow() {
        try {
            //File path from Zavael's comment on Evaldas Ilginis's answer on https://stackoverflow.com/questions/17228487/javafx-location-is-not-set-error-message
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/LandingWindowView.fxml"));
            AnchorPane pane = loader.load();
            LandingWindowController landingWindowController = loader.getController();
            landingWindowController.setMain(this);
            Scene scene = new Scene(pane);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The main menu.
     */
    public void mainMenuWindow(){
    try {
        //File path from Zavael's comment on Evaldas Ilginis's answer on https://stackoverflow.com/questions/17228487/javafx-location-is-not-set-error-message
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainMenuWindowView.fxml"));
        AnchorPane pane = loader.load();
        MainMenuWindowController mainMenuWindowController = loader.getController();
        mainMenuWindowController.setMain(this);
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    catch (Exception e) {
       e.printStackTrace();
        }
    }

    /**
     * The prescription window.
     */
    public void prescribeWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/PrescribeWindowView.fxml"));
            AnchorPane pane = loader.load();
            PrescribeWindowController prescribeWindowController = loader.getController();
           prescribeWindowController.setMain(this);
            Scene scene = new Scene(pane);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (Exception e){
           e.printStackTrace();
        }
    }

    /**
     * The manage window.
     */
    public void manageWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/ManageWindowView.fxml"));
            AnchorPane pane = loader.load();
            ManageWindowController manageWindowController = loader.getController();
            manageWindowController.setMain(this);
            Scene scene = new Scene(pane);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * The settings window.
     */
    public void settingsWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/SettingsWindowView.fxml"));
            AnchorPane pane = loader.load();
            SettingsWindowController settingsWindowController = loader.getController();
            settingsWindowController.setMain(this);
            Scene scene = new Scene(pane);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * The edit window.
     */
    public void editWindow(Med m) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/EditWindowView.fxml"));
            AnchorPane pane = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.setTitle("Edit");
            Scene scene = new Scene(pane);
            EditWindowController editWindowController = loader.getController();
            editWindowController.setMain(this, dialogStage);
            editWindowController.setMed(m);
            dialogStage.setScene(scene);
            dialogStage.show();
        } catch (IOException e) {
            //Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * The medication adding window.
     */
    public void addMedicationWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/AddMedicationWindowView.fxml"));
            AnchorPane pane = loader.load();
            AddMedicationWindowController addMedicationWindowController = loader.getController();
            addMedicationWindowController.setMain(this);
            Scene scene = new Scene(pane);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * All medication window.
     */
    public void allMedicationWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/AllMedicationWindowView.fxml"));
            AnchorPane pane = loader.load();
            AllMedicationWindowController allMedicationWindowController = loader.getController();
            allMedicationWindowController.setMain(this);
            Scene scene = new Scene(pane);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Preferences getPrefs() {
        return prefs;
    }

    public static void main(String[] args) {
        launch(args);
    }
}