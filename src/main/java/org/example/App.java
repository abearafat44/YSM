package org.example;


import java.util.ArrayList;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
/**
 * JavaFX App
 */
public class App {
    /*@Override
    public void start(Stage stage) {
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();
        Apple gala = new Apple("Red",7);
        var label = new Label("colour: "+gala.getColour()+ " size: "+gala.getSize());
        var scene = new Scene(new StackPane(label), 640, 480);


        stage.setScene(scene);
        stage.show();
    }*/

    public static void main( String[] args ) throws Exception {
        Inventory myInventory = new Inventory("drug_inventory.xlsx");
        /*Code used to generate sheet used for testing
        Lot lot1 = new Lot("lot1num49",0,100,"10/2020");
        Lot lot2 = new Lot("lot2num67",76,0,"08/2020");
        Lot lot3 = new Lot("lot3num25",23,50,"05/2020");

        ArrayList<Lot> med1Lots = new ArrayList<>();

        med1Lots.add(lot1);
        med1Lots.add(lot2);
        med1Lots.add(lot3);

        Med med1 = new Med("My Med 1","Pill",500,med1Lots,"Homeopathic");
        myInventory.addMed(med1);
        myInventory.writeToSheet();
        */
        Med firstMed = new Med(myInventory.getMeds().get(0));
        System.out.println(firstMed.hasExpiredLot());

    }

}