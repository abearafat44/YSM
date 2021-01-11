package application.controller;

import application.Main;
import application.object.Lot;
import application.object.Med;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller for the prescription search window.
 * @author John Wood
 */
public class PrescribeWindowController {

    private Main main;
    @FXML private ImageView logo, prescribeIcon, infoIcon;
    @FXML private TextField amountField,lotField;
    @FXML private TableColumn<Lot,String> lotColumn,expiryColumn;
    @FXML private TableColumn<Lot,Integer> packedColumn;
    @FXML private TableView<Lot> lotsTable;
    @FXML private Label typeLabel,descriptionLabel;
    @FXML private Text nameStrengthText;
    /**
     * Sets the instance of the Main class.
     * @param main The instance of the Main class.
     */
    public void setMain(Main main) {
        this.main = main;
    }

    /**
     * Initialization. Sets the image views and action listener.
     */
    @FXML private void initialize() {
        //Image views
        logo.setImage(new Image("file:src/main/resources/images/ysmLogo.png"));
        prescribeIcon.setImage(new Image("file:src/main/resources/images/icons/note.png"));
        infoIcon.setImage(new Image("file:src/main/resources/images/icons/info.png"));

        //Only allow numbers in the amount field
        amountField.textProperty().addListener(((observableValue, oldValue, newValue) -> amountField.setText(newValue.replaceAll("\\D", ""))));
        lotColumn.setCellValueFactory(cellData -> cellData.getValue().getLotNumberProperty());
        packedColumn.setCellValueFactory(cellData -> cellData.getValue().getPackagedStockProperty().asObject());
        expiryColumn.setCellValueFactory(cellData -> cellData.getValue().getExpiryProperty());
        lotField.textProperty().addListener(((observableValue,oldValue,newValue) -> {
            if(checkLotNumber()){
                fillTable(newValue);
            }
        }));
    }

    
    /**
     * Returns to the main menu.
     */
    @FXML private void mainMenu() {
        main.mainMenuWindow();
    }

    /**
     * Checks to see if the content of the lot textfield constitutes a valid lot number
     * @return true if the lot number exists in the inventory, false otherwise.
     */
    @FXML private boolean checkLotNumber(){
        if(!lotField.getText().equals("")){
            try{
                Main.inventory.searchByLot(lotField.getText());
            }
            catch(NullPointerException e){
                return false;
            }
        return true;
        }
    return false;
    }

    /**
     * Fills the table with all the Med information for the name and strength of med described by the lotNumber.
     * @param lotNumber The lot number of the med being loaded into the table.
     */
    @FXML private void fillTable(String lotNumber){
        Med currentMed = Main.inventory.getMeds().get(Main.inventory.getByLot().get(lotNumber)[0]);
        lotsTable.getItems().clear();
        nameStrengthText.setText(currentMed.getName() + ", " + currentMed.getStrength());
        typeLabel.setText(currentMed.getType());
        descriptionLabel.setText(currentMed.getDescription());
        for(Lot loot:currentMed.getLots()){
            lotsTable.getItems().add(loot);
        }

    }

    @FXML private void prescribeAndExit() throws IOException {
        if(!checkLotNumber()){
            Alert fakeLot = new Alert(Alert.AlertType.ERROR);
            fakeLot.setTitle("Error");
            fakeLot.setHeaderText("Lot Not Found");
            fakeLot.setContentText("No Medication\n with that Lot Number\n was found in the Inventory");
            fakeLot.show();
        }else {
            Med nowMed = Main.inventory.getMeds().get(Main.inventory.getByLot().get(lotField.getText())[0]);
            boolean enough = Main.inventory.getMeds().get(Main.inventory.getByLot().get(lotField.getText())[0]).getLots().get(Main.inventory.getByLot().get(lotField.getText())[1]).dispenseFromLot(Integer.parseInt(amountField.getText()), Main.inventory.getTxtName(), true);
            if (!enough) {
                Alert tooFew = new Alert(Alert.AlertType.ERROR);
                tooFew.setTitle("Error");
                tooFew.setHeaderText("Too Few Packed");
                tooFew.setContentText("There are not enough Units\n in that Lot's Packaged Stock\n to complete the Operation");
                tooFew.show();
            } else {
                Alert confirmation = new Alert(Alert.AlertType.INFORMATION,"",ButtonType.OK);
                confirmation.setTitle("Operation Complete");
                confirmation.setHeaderText("The Following Medication was Dispensed:");
                confirmation.setContentText("Name: " + nowMed.getName() +"\nType: " + nowMed.getType() +
                "\nDescription: " + nowMed.getDescription() + "\nLot Number: " + lotField.getText() + "\nNumber Dispensed: "
                + amountField.getText());
                confirmation.showAndWait();
                this.mainMenu();
            }
        }
    }


}

