package org.example;

import javafx.beans.property.*;
import org.apache.poi.ss.usermodel.Row;
import java.util.ArrayList;

/**
 * The med class stores the relevant information of a singular medication. This includes name, description, strength and an array of lots.
 * @author Alison Okumura
 */
public class Med{

    //Layout of one row in Spreadsheet is as follows:
    //Product	Strength    Description	    Type	Total Stock	    Total Packed	Total Unpacked	Lot 1	Packed stock 1	UnPacked stock 1	Expiry 1

    private StringProperty name;
    private StringProperty description;
    private StringProperty strength;
    private ArrayList<Lot> lots;
    private StringProperty type;

    /**
     * Null constructor.
     */
    public Med(){
        this.name = new SimpleStringProperty("");
        this.description = new SimpleStringProperty("");
        this.strength = new SimpleStringProperty("");
        this.lots = new ArrayList<>(2);//UPDATED HERE ----------------------
        this.type = new SimpleStringProperty("");
    }

    /**
     * constructor.
     * @param name Name of the Medication
     * @param description Description of Medication
     * @param strength Strength of Medication
     * @param lots ArrayList of Medication Lot objects
     * @param type class of medication
     */
    public Med(String name, String description, String strength, ArrayList<Lot> lots, String type){
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
        this.strength = new SimpleStringProperty(strength);
        this.lots = lots;
        this.type = new SimpleStringProperty(type);
    }

    /**
     * constructor.
     * @param row The Row object from the spreadsheet containing information about the medication being constructed
     */
    public Med(Row row){
        this.name = new SimpleStringProperty(row.getCell(0).getStringCellValue());
        this.description = new SimpleStringProperty(row.getCell(2).getStringCellValue());
        this.strength = new SimpleStringProperty(row.getCell(1).getStringCellValue());
        this.lots = new ArrayList<>();
        this.type = new SimpleStringProperty(row.getCell(3).getStringCellValue());
        int i = 0;
        //the formula 7+4*i gives the position of the lot number cell according to the lot's position in lots array.
        while(row.getCell(3+(4*(i+1))) != null){
            String lotNumber = row.getCell(7+4*i).getStringCellValue();
            int packedStock = (int) row.getCell(8+4*i).getNumericCellValue();
            int unpackedStock = (int) row.getCell(9+4*i).getNumericCellValue();
            String expiry = row.getCell(10+4*i).getStringCellValue();
            Lot temp = new Lot(lotNumber,packedStock ,unpackedStock,expiry);
            this.lots.add(temp);
            i++;
        }
    }

    public Med(Med m){
        this.name = new SimpleStringProperty(m.getName());
        this.name = new SimpleStringProperty(m.getDescription());
        this.strength = new SimpleStringProperty(m.getStrength());
        this.lots = m.lots;
        this.type = new SimpleStringProperty(m.getType());
    }

    //Getters and Setters
    public String getName(){return this.name.get();}
    public String getDescription(){return this.description.get();}
    public String getStrength(){return this.strength.get();}
    public String getType() {return this.type.get();}
    public ArrayList<Lot> getLots() {return this.lots;}
    public void setName(String name){this.name = new SimpleStringProperty(name);}
    public void setType(String type){this.type = new SimpleStringProperty(type);}
    public void setDescription(String description){this.description = new SimpleStringProperty(description);}
    public void setStrength(String strength){this.strength = new SimpleStringProperty(strength);}
    public void setLots(ArrayList<Lot> lots){this.lots = lots;}
    
    /**
     * Takes a Med object and parses the Lot array to find the total number in stock.
     * @return An Integer Property storing the total stock associated with that medication
     */
    public IntegerProperty totalStock(){
        int sum = 0;
        for (Lot lot : this.lots) {
            sum = sum + lot.getFullStock();
        }
        return new SimpleIntegerProperty(sum);
    }

    /**
     * This method will take a row and modify its values to reflect the Meds attributes.
     * @param row The row to which the med object should be written.
     */
    public void writeMed(Row row){
        row.createCell(0).setCellValue(this.getName());
        row.createCell(1).setCellValue(this.getStrength());
        row.createCell(2).setCellValue(this.getDescription());
        row.createCell(3).setCellValue(this.getType());
        row.createCell(4).setCellValue(this.totalStock().get());
        int p = 0;
        int up = 0;
        for(int i=0;i<this.lots.size();i++){
            int pos = 7+4*i;
            row.createCell(pos).setCellValue(this.lots.get(i).getLotNumber());
            row.createCell(pos+1).setCellValue(this.lots.get(i).getPackagedStock());
            row.createCell(pos+2).setCellValue(this.lots.get(i).getUnpackagedStock());
            row.createCell(pos+3).setCellValue(this.lots.get(i).getExpiry());
            p = p + this.lots.get(i).getPackagedStock();
            up = up + this.lots.get(i).getUnpackagedStock();
        }
        row.createCell(5).setCellValue(p);
        row.createCell(6).setCellValue(up);

    }

    /**
     * This method takes the Med object and adds the new lot to the lots array.
     * @param lotNum The lot number of the lot being added
     * @param numberPackaged The number of units in that lot which are packaged
     * @param numUnpackaged The number of units in that lot which are bottled
     * @param expiry The String representation of the expiry date in the form MM/YYYY
     */
    public void addLot(String lotNum, int numberPackaged, int numUnpackaged, String expiry){
        Lot newLot = new Lot(lotNum, numberPackaged, numUnpackaged,expiry);
        this.lots.add(newLot);
    }

    /**
     * This method removes a Lot of medication from the Med object. To be used if (1) all stock is dispensed or (2) the lot is expired.
     * @param lotNumber The Lot Number of the Lot which is to be removed
     */
    public void disposeLot(String lotNumber){
        int i =0;
        while(!this.lots.get(i).getLotNumber().equals(lotNumber)){
            i++;
        }
        this.lots.remove(i);
    }

    /**
     * iterates through the lots array and returns list of expired Lot objects.
     * @return an ArrayList of Lot objects representing the expired lots.
     */
    public ArrayList<Lot> getExpiredLots(){
        ArrayList<Lot> expired = new ArrayList<>();
        for (Lot lot : this.lots) {
            if (lot.isExpired()) {
                expired.add(lot);
            }
        }
        return expired;
    }

    //NEW METHOD
    /**
     * Returns whether the Med has an expired Lot.
     * @return A boolean representing whether the Med has an expired Lot.
     * @author John Wood
     */
    public boolean hasExpiredLot() {
        for(Lot l : this.lots) {
            if(l.isExpired())
                return true;
        }
        return false;
    }

    //NEW METHOD
    /**
     * Returns whether the Med has a lot that will be expired next month.
     * @return A boolean representing whether the Med has a lot that will be expired next month.
     * @author John Wood
     */
    public boolean hasExpiredLotNextMonth() {
        for(Lot l : this.lots) {
            if(l.willExpireNextMonth())
                return true;
        }
        return false;
    }
}

