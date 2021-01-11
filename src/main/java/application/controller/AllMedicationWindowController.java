package application.controller;

import application.Main;
import application.object.Lot;
import application.object.Med;
import javafx.beans.binding.ObjectExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller for the all medication viewing window.
 * @author John Wood
 */
public class AllMedicationWindowController {

    //TODO bind comparator to table view

    private Main main;
    @FXML private ImageView logo;
    @FXML private Label medNameLabel, medTypeLabel, medDescriptionLabel, medTotalStockLabel;
    private ObservableList<Med> medList = FXCollections.observableArrayList(Main.inventory.getMeds());
    private ObservableList<Lot> lotList = FXCollections.observableArrayList();

    @FXML private TableView<Med> medTableView;
    @FXML private TableColumn<Med, String> medNameCol, medDescriptionCol;
    @FXML private TableColumn<Med, String> medStrengthCol;

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
     * Initialization. Set table view, logo, labels and items.
     */
    @FXML private void initialize() {
        //Logo
        logo.setImage(new Image("file:src/main/resources/images/ysmLogo.png"));

        //Set labels
        showLots(null);

        //Set column
        medNameCol.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        medDescriptionCol.setCellValueFactory(cellData -> cellData.getValue().getDescriptionProperty());
        medStrengthCol.setCellValueFactory(cellData -> cellData.getValue().getStrengthProperty());
        lotNumberCol.setCellValueFactory(cellData -> cellData.getValue().getLotNumberProperty());
        lotExpiryCol.setCellValueFactory(cellData -> cellData.getValue().getExpiryProperty());
        lotPackStockCol.setCellValueFactory(cellData -> cellData.getValue().getPackagedStockProperty().asObject());
        lotUnpackStockCol.setCellValueFactory(cellData -> cellData.getValue().getUnpackagedStockProperty().asObject());

        //Changing background colours of rows mainly from Joe Almore's answer on https://stackoverflow.com/questions/30889732/javafx-tableview-change-row-color-based-on-column-value
        //With adaptation based on Pablo Insua's answer on the same discussion thread.
        medTableView.setRowFactory(row -> new TableRow<Med>() {
            @Override public void updateItem(Med med, boolean empty) {
                super.updateItem(med, empty);
                if(med == null || empty)
                    setStyle("");
                //Red before yellow so if there's both, red shows
                else if(med.hasExpiredLot())
                    setStyle("-fx-background-color: #d45b5b;"); // red
                else if(med.hasExpiredLotNextMonth())
                    setStyle("-fx-background-color: #e6cc25;"); // yellow
                else
                    setStyle("");

            }
        });

        //Changing background colours of rows mainly from Joe Almore's answer on https://stackoverflow.com/questions/30889732/javafx-tableview-change-row-color-based-on-column-value
        //With adaptation based on Pablo Insua's answer on the same discussion thread.
        lotTableView.setRowFactory(row -> new TableRow<Lot>() {
            @Override public void updateItem(Lot lot, boolean empty) {
                super.updateItem(lot, empty);
                if(lot == null || empty)
                    setStyle("");
                    //Red before yellow so if there's both, red shows
                else if(lot.isExpired())
                    setStyle("-fx-background-color: #d45b5b;"); // red
                else if(lot.willExpireNextMonth())
                    setStyle("-fx-background-color: #e6cc25;"); // yellow
                else
                    setStyle("");
            }
        });


        //Sort expired meds first
        SortedList<Med> sortedMedList = new SortedList<Med>(medList, (Med med1, Med med2) -> {
            boolean oneExp = med1.hasExpiredLot();
            boolean twoExp = med2.hasExpiredLot();
            boolean oneExpMonth = true;
            boolean twoExpMonth = true;

            if(!oneExp)
                oneExpMonth = med1.hasExpiredLotNextMonth();
            if(!twoExp)
                twoExpMonth = med2.hasExpiredLotNextMonth();

            if(oneExp && !twoExp)
                return -1;
            else if(twoExp && !oneExp)
                return 1;
            else if (oneExpMonth && !twoExpMonth)
                return -1;
            else if(twoExpMonth && !oneExpMonth)
                return 1;
            else
                return 0;
        });


        //Sort expired lots first
        SortedList<Lot> sortedLotList = new SortedList<Lot>(lotList, (Lot lot1, Lot lot2) -> {
            boolean oneExp = lot1.isExpired();
            boolean twoExp = lot2.isExpired();
            boolean oneExpMonth = true;
            boolean twoExpMonth = true;

            if(!oneExp)
                oneExpMonth = lot1.willExpireNextMonth();
            if(!twoExp)
                twoExpMonth = lot2.willExpireNextMonth();

            if(oneExp && !twoExp)
                return -1;
            else if(twoExp && !oneExp)
                return 1;
            else if (oneExpMonth && !twoExpMonth)
                return -1;
            else if(twoExpMonth && !oneExpMonth)
                return 1;
            else
                return 0;
        });


        //Set items
        medTableView.setItems(sortedMedList);
        lotTableView.setItems(sortedLotList);

        //Add selection listener to med table view
       medTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            showLots(newValue);
       });
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
            lotList.setAll(m.getLots());
        }

        else {
            medNameLabel.setText("Select a medication...");
            medTypeLabel.setText("---");
            medDescriptionLabel.setText("---");
            medTotalStockLabel.setText("---");

           lotList.clear();
        }
    }
}
