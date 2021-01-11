package application.controller;

import application.Main;
import application.object.Lot;
import application.object.Med;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Controller for the edit window.
 * @author John Wood
 */
public class EditWindowController {

    private Main main;
    private Stage secondaryStage;
    public Med m;
    //private Inventory I;
    @FXML private ImageView medicineIcon;
    @FXML private TextField nameField, strengthField, descriptionField, typeField, lotField, packedField, unpackedField, yearField;
    @FXML private ComboBox<String> monthBox, sourceBox, lotBox;
    @FXML private Button Done, newLot, deleteLot;

    /**
     * Sets the instance of the Main class and the secondary stage.
     * @param main The instance of the Main class
     * @param secondaryStage The secondary stage
     */
    public void setMain(Main main, Stage secondaryStage) {
        this.main = main;
        this.secondaryStage = secondaryStage;
    }

    /**
     * Initialization. Sets the image view. Sets action listeners.
     */
    @FXML
    private void initialize() {
        medicineIcon.setImage(new Image("file:src/main/resources/images/icons/medicine.png"));
        packedField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            if(newValue.matches("\\D"))
                packedField.setText(newValue.replaceAll("\\D", ""));
        }));
        unpackedField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            if(newValue.matches("\\D"))
                unpackedField.setText(newValue.replaceAll("\\D", ""));
        }));
        yearField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
            if(newValue.matches("\\D"))
                yearField.setText(newValue.replaceAll("\\D", ""));
        }));
    }

    public void setMed(Med m) {
        this.m = m;
        nameField.setText(m.getName());
        strengthField.setText(m.getStrength());
        descriptionField.setText(m.getDescription());
        typeField.setText(m.getType());
        sourceBox.getItems().addAll(Main.getPrefs().get("distributors", "").split(","));
        monthBox.getItems().addAll("01","02","03","04","05","06","07","08","09","10","11","12");

        //Setting lot elements here
        lotBox.getItems().removeAll();
        for(int i=0; i<m.getLots().size();i++){ lotBox.getItems().add(m.getLots().get(i).getLotNumber()); }

        lotBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                setLot(t1, m);
            }
        });

    }
    private void setLot(String t1, Med m){
        this.m = m;
        String input = t1;
        for(int i=0;i< this.m.getLots().size();i++){
            if(m.getLots().get(i).getLotNumber().equals(input)){
                lotField.setText(m.getLots().get(i).getLotNumber());
                packedField.setText(Integer.toString(m.getLots().get(i).getPackagedStock()));
                unpackedField.setText(Integer.toString(m.getLots().get(i).getUnpackagedStock()));
                String expiry = m.getLots().get(i).getExpiry();
                String[] expiryDate = expiry.split("/");
                yearField.setText(expiryDate[1]);
                monthBox.setValue(expiryDate[0]);
            }
        }
    }

    @FXML private void Done() throws Exception {
        m.setName(nameField.getText());
        m.setStrength(strengthField.getText());
        m.setDescription(descriptionField.getText());
        m.setType(typeField.getText());
        ArrayList<Lot> lots = m.getLots();
        for(int i=0;i<lots.size();i++){
            if(lots.get(i).getLotNumber() == lotField.getText()){
                int packedInt, unpackedInt;
                try {
                    packedInt = Integer.parseInt(packedField.getText());
                    unpackedInt = Integer.parseInt(unpackedField.getText());
                }
                catch (NumberFormatException e) {
                    packedInt = 0;
                    unpackedInt = 0;
                }
                lots.get(i).setPackagedStock(packedInt);
                lots.get(i).setUnpackagedStock(unpackedInt);
                lots.get(i).setExpiry(String.join("/",monthBox.getValue(),yearField.getText()));
            }
        }
        Stage stage = (Stage) Done.getScene().getWindow();
        stage.close();
    }
    @FXML private void newlot() {
        int packedInt, unpackedInt;
        try {
            packedInt = Integer.parseInt(packedField.getText());
            unpackedInt = Integer.parseInt(unpackedField.getText());
        }
        catch (NumberFormatException e) {
            packedInt = 0;
            unpackedInt = 0;
        }
        String expiry = String.join("/",monthBox.getValue(),yearField.getText());
        m.addLot(lotField.getText(),packedInt, unpackedInt, expiry);
        lotBox.getItems().add(lotField.getText());
        lotField.clear();
    }

    @FXML private void deletelot() {
        for (int i=0;i<m.getLots().size();i++) {
            if (m.getLots().get(i).getLotNumber().equals(lotField.getText())) { m.getLots().remove(i);}
        }
        Stage stage = (Stage) deleteLot.getScene().getWindow();
        stage.close();
        main.editWindow(m);
    }

}
