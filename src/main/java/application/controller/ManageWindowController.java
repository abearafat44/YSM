package application.controller;

import application.GenAlert;
import application.Main;
import application.object.Lot;
import application.object.Med;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Controller for the management window.
 * @author John Wood
 */
public class ManageWindowController {

    private Main main;
    private ArrayList<Med> expiredList = new ArrayList<Med>();
    private ArrayList<Med> expiredInMonthList = new ArrayList<Med>();

    @FXML private ImageView logo, infoIcon, editIcon;
    @FXML private Label medNameLabel, medTypeLabel, medDescriptionLabel, medTotalStockLabel;
    @FXML private ComboBox filterBox;
    @FXML private Button disposeButton;

    @FXML private TableView<Med> medTableView;
    @FXML private TableColumn<Med, String> medNameCol, medDescriptionCol, medStrengthCol;

    @FXML private TableView<Lot> lotTableView;
    @FXML private TableColumn<Lot, String> lotNumberCol, lotExpiryCol;
    @FXML private TableColumn<Lot, Integer> lotPackStockCol, lotUnpackStockCol;

    /**
     * Sets the instance of the Main class.
     * @param main The instance of the Main class
     */
    public void setMain(Main main) {
        this.main = main;
    }

    /**
     * Initialization. Sets image views. Set columns. Show empty values on info card. Action listeners.
     */
    @FXML private void initialize() {
        //Image views
        logo.setImage(new Image("file:src/main/resources/images/ysmLogo.png"));
        infoIcon.setImage(new Image("file:src/main/resources/images/icons/info.png"));
        editIcon.setImage(new Image("file:src/main/resources/images/icons/edit.png"));

        //Find meds with lots that are/will soon expire and add them to appropriate lists
        for(Med m : Main.inventory.getMeds()) {
            if(m.hasExpiredLot())
                expiredList.add(m);
            else if(m.hasExpiredLotNextMonth())
                expiredInMonthList.add(m);
        }

        //Set column
        medNameCol.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        medDescriptionCol.setCellValueFactory(cellData -> cellData.getValue().getDescriptionProperty());
        medStrengthCol.setCellValueFactory(cellData -> cellData.getValue().getStrengthProperty());
        lotNumberCol.setCellValueFactory(cellData -> cellData.getValue().getLotNumberProperty());
        lotExpiryCol.setCellValueFactory(cellData -> cellData.getValue().getExpiryProperty());
        lotPackStockCol.setCellValueFactory(cellData -> cellData.getValue().getPackagedStockProperty().asObject());
        lotUnpackStockCol.setCellValueFactory(cellData -> cellData.getValue().getUnpackagedStockProperty().asObject());

        //Add selection listener to med table view
        medTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            showLots(newValue);
        });

        //Changing background colours of rows mainly from Joe Almore's answer on https://stackoverflow.com/questions/30889732/javafx-tableview-change-row-color-based-on-column-value
        //With adaptation based on Pablo Insua's answer on the same discussion thread.
        lotTableView.setRowFactory(row -> new TableRow<Lot>() {
            @Override public void updateItem(Lot lot, boolean empty) {
                super.updateItem(lot, empty);
                if(lot == null || empty)
                    setStyle("");
                else if(lot.isExpired())
                    setStyle("-fx-background-color: #d45b5b;"); // red
                else if(lot.willExpireNextMonth())
                    setStyle("-fx-background-color: #e6cc25;"); // yellow
                else
                    setStyle("");
            }
        });

        //Set the filter combo box
        //http://tutorials.jenkov.com/javafx/combobox.html
        filterBox.getItems().addAll("All", "Expired in a month", "Expired");

        //Filter combo box selection action listener. The meds that are displayed and the visibility of the
        // "dispose all lots" button change
        filterBox.getSelectionModel().selectedIndexProperty().addListener(((observableValue, oldValue, newValue) -> {
            int i = newValue.intValue();

            if(i == 0) { //All meds
                medTableView.getItems().setAll(Main.inventory.getMeds());
                disposeButton.setVisible(false);
            }
            else if(i == 1) { //Expired in a month
                medTableView.getItems().setAll(expiredInMonthList);
                disposeButton.setVisible(false);
            }
            else { //Expired meds
                medTableView.getItems().setAll(expiredList);
                disposeButton.setVisible(true);
            }
        }));

        //Set initial filter which sets med table view items and disposal button visibility
        filterBox.getSelectionModel().select(0);

        //Clear info card
        showLots(null);

    }

    /**
     * Shows the lot information of a Med in the lot table view and labels. If the Med is null, the lot table view and labels will be cleared.
     * @param m The Med
     */
    private void showLots(Med m) {
        if(m!=null) {
            //Set labels
            medNameLabel.setText(m.getName());
            medTypeLabel.setText(m.getType());
            medDescriptionLabel.setText(m.getDescription());
            medTotalStockLabel.setText(String.valueOf(m.totalStock().get()));

            //Table view
            lotTableView.setItems(FXCollections.observableArrayList(m.getLots()));
        }

        else {
            medNameLabel.setText("Select a medication");
            medTypeLabel.setText("---");
            medDescriptionLabel.setText("---");
            medTotalStockLabel.setText("---");

            lotTableView.setItems(null);
        }
    }

    /**
     * Returns to the main menu.
     */
    @FXML private void mainMenu() {
        main.mainMenuWindow();
    }
    @FXML private void add() {main.addMedicationWindow();}

    /**
     * Opens the edit window.
     */
    @FXML private void edit() {
        //Get selected item
        Med m = medTableView.getSelectionModel().getSelectedItem();
        if(m == null)
            GenAlert.info("You must select a medication to edit.");
        else
            main.editWindow(m);
    }

    /**
     * Disposes all expired lots with confirmation.
     */
    @FXML private void disposeAllExpired() {
        if(GenAlert.confirmation("Are you sure?", "All expired lots will be deleted from " + expiredList.size() + " medications. This action is irreversible.")) {
            try {
                Main.inventory.removeExpired();
                //clear expired list
                expiredList.clear();
                //clear table view
                medTableView.getItems().clear();
            } catch(IOException e) {
                GenAlert.error("An error occurred while attempting to dispose all expired lots. (TXT file is missing or corrupted)");
            }
        }
    }

    /**
     * Disposes a lot.
     */
    @FXML private void disposeLot() {
        //Get selected lot
        Lot selectedLot = lotTableView.getSelectionModel().getSelectedItem();
        if(selectedLot == null)
            GenAlert.info("You must select a lot to dispose.");
        else if(GenAlert.confirmation("Are you sure?", "Lot " + selectedLot.getLotNumber() + " will be disposed. This action is irreversible.")) {
            try {
                medTableView.getSelectionModel().getSelectedItem().disposeLot(selectedLot.getLotNumber(), true, "commands.txt");
                //Update table view
                lotTableView.getItems().remove(selectedLot);
            } catch(IOException e) {
                GenAlert.error("An error occurred while attempting to dispose the lot. (TXT file is missing or corrupted)");
            }
        }
    }

}
