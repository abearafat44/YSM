package application.controller;

import application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;
import java.util.prefs.*;

/**
 * Controller for the settings window
 */
public class SettingsWindowController {

    private Main main;
    private Preferences prefs = Main.getPrefs();


    @FXML
    private ImageView logo;
    @FXML
    private TextField excelTextField, commandsTextField,distributors;
    @FXML
    private CheckBox backgrounds;


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
        excelTextField.setText(prefs.get("excelPath",""));
        commandsTextField.setText(prefs.get("commandsPath",""));
        distributors.setText(prefs.get("distributors",""));
        backgrounds.setSelected(prefs.getBoolean("backImage", false));

    }

    /**
     * Returns to the main menu.
     */
    @FXML private void mainMenu() {
        main.mainMenuWindow();
    }
    /**
     * Displays an alert with attributions.
     */
    @FXML private void viewAttributions() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Attributions");
        alert.setTitle("Attributions");
        alert.setContentText("Prescribe icon\nIcon made by Freepik from www.flaticon.com\nManage icon\nIcon made by " +
                "Freepik from www.flaticon.com\nSettings icon\nIcon made by Freepik from www.flaticon.com\nInfo icon\n" +
                "Icon made by Freepik from www.flaticon.com\nEdit icon\nIcon made by Freepik from www.flaticon.com\nMed" +
                "icine icon\nIcon made by Freepik from www.flaticon.com\nPharmacy icon\nIcon made by Freepik from www.f" +
                "laticon.com");
        alert.show();
    }

    /**
     * Allows the user to select the excel file from file Explorer.
     */
    @FXML private void excelSelect(){
        FileChooser selector = new FileChooser();
        Stage secondary = new Stage();
        File selected = selector.showOpenDialog(secondary);
        if(selected!=null && !selected.getPath().contains(".xlsx")) {
            Alert invalidAlert = new Alert(Alert.AlertType.WARNING);
            invalidAlert.setHeaderText("Invalid File Type");
            invalidAlert.setContentText("The Excel Filename must end in .xlsx");
            invalidAlert.show();
        }
        else{
            if(selected!=null){
                excelTextField.setText(selected.getPath());
            }
        }
    }

    /**
     * Allows the user to select the commands txt file from file explorer
     */
    @FXML private void commandsSelect(){
        FileChooser selector = new FileChooser();
        Stage secondary = new Stage();
        File selected = selector.showOpenDialog(secondary);
        if(selected!=null && !selected.getPath().contains(".txt")) {
            Alert invalidAlert = new Alert(Alert.AlertType.WARNING);
            invalidAlert.setHeaderText("Invalid File Type");
            invalidAlert.setContentText("The commands Filename must end in .txt");
            invalidAlert.show();
        }
        else{
            if(selected!=null){
                commandsTextField.setText(selected.getPath());
            }
        }
    }

    /**
     * Checks to see if the distributors field is empty.
     * @return an Alert warning that the field is empty, or null if it is not.
     */
    @FXML private Alert distributorCheck(){
        Alert distributorAlert = new Alert(Alert.AlertType.ERROR);
        if(distributors.getText().isEmpty()){
            distributorAlert.setTitle("Empty TextBox Warning");
            distributorAlert.setContentText("At least one Distributor is required");
            return distributorAlert;
        }
        return null;
    }


    /**
     *
     * @return true if the settings window has been modified, false otherwise.
     */
    @FXML private boolean modificationCheck(){
        if(backgrounds.isSelected() != prefs.getBoolean("backImage",backgrounds.isSelected())) return true;
        if(!excelTextField.getText().equals(prefs.get("excelPath",excelTextField.getText()))) return true;
        if(!commandsTextField.getText().equals(prefs.get("commandsPath",commandsTextField.getText()))) return true;
        return !distributors.getText().equals(prefs.get("distributors", distributors.getText()));
    }

    @FXML private void returnToHome(){
        if(modificationCheck()){
            Alert goHome = new Alert(Alert.AlertType.WARNING,"",ButtonType.YES,ButtonType.NO,ButtonType.CANCEL);
            goHome.setTitle("Warning");
            goHome.setHeaderText("Unsaved Changes");
            goHome.setContentText("There are unsaved changes in the application settings.\n Do you want to save these changes?");
            Optional<ButtonType> choice = goHome.showAndWait();
            if(choice.get() == ButtonType.YES) saveAndClose();
            else{
                if(choice.get() == ButtonType.NO) mainMenu();
            }
        }else{
            mainMenu();
        }
    }

    @FXML private void saveAndClose(){
        boolean goodCommands = true;
        boolean goodExcel = true;
        boolean goodDistributors = true;
        if(!commandsTextField.getText().isEmpty()){
            if(!commandsTextField.getText().contains(".txt")){
                Alert invalidAlert = new Alert(Alert.AlertType.WARNING);
                invalidAlert.setHeaderText("Invalid File Type");
                invalidAlert.setContentText("The commands Filename must end in .txt");
                invalidAlert.show();
                goodCommands = false;
            }else{
                prefs.put("commandsPath",commandsTextField.getText());
            }
        }

        if(!excelTextField.getText().isEmpty()){
            if(!excelTextField.getText().contains(".xlsx")){
                Alert invalidAlert = new Alert(Alert.AlertType.WARNING);
                invalidAlert.setHeaderText("Invalid File Type");
                invalidAlert.setContentText("The Excel Filename must end in .xlsx");
                invalidAlert.show();
                goodExcel = false;
            }else{
                prefs.put("excelPath",excelTextField.getText());
            }
        }
        Alert distCheck = distributorCheck();
        if(distCheck != null){
            distCheck.show();
            goodDistributors = false;
        }
        else prefs.put("distributors",distributors.getText());
        prefs.putBoolean("backImage",backgrounds.isSelected());
        if(goodCommands && goodExcel && goodDistributors) this.mainMenu();
    }
}
