package org.example;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import java.time.LocalDate;

/**
 * The Lot object stores the lot number, stock and expiry of a lot. Med objects have an array of lots.
 * @author John Wood
 */
public class Lot {
    private StringProperty lotNumberProperty;
    private IntegerProperty packagedStockProperty;
    private IntegerProperty unpackagedStockProperty;
    private StringProperty expiryProperty;

    /**
     * Null constructor.
     */
    public Lot() {
        this.lotNumberProperty = new SimpleStringProperty("");
        this.packagedStockProperty = new SimpleIntegerProperty(0);
        this.unpackagedStockProperty = new SimpleIntegerProperty(0);
        this.expiryProperty = new SimpleStringProperty("");
    }

    /**
     * Constructor.
     * @param lotNumber The lot number
     * @param packaged The packaged stock
     * @param unpackaged The unpackaged stock
     * @param expiry The expiry date formatted as (mm/yyyy)
     */
    public Lot(String lotNumber, int packaged, int unpackaged, String expiry) {
    	this.lotNumberProperty = new SimpleStringProperty(lotNumber);
    	this.packagedStockProperty = new SimpleIntegerProperty(packaged);
    	this.unpackagedStockProperty = new SimpleIntegerProperty(unpackaged);
    	this.expiryProperty = new SimpleStringProperty(expiry);
    }

    //LOT NUMBER GET+SET
    public String getLotNumber() {
        return this.lotNumberProperty.get();
    }
    public void setLotNumber(String lotNumber) { //here we use .set()
        this.lotNumberProperty.set(lotNumber);
    }
    public StringProperty getLotNumberProperty() {
        return this.lotNumberProperty;
    }
    public void setLotNumberProperty(StringProperty lotNumberProperty) { //but here we use = maybe something to look into...
        this.lotNumberProperty = lotNumberProperty;
    }

    //STOCK GET+SET
    public int getFullStock() {
        return (this.packagedStockProperty.get()+this.unpackagedStockProperty.get());
    }
    public int getPackagedStock() {
    	return this.packagedStockProperty.get();
    }
    public int getUnpackagedStock(){
    	return this.unpackagedStockProperty.get();
    }
    public void setPackagedStock(int stock) {
        this.packagedStockProperty.set(stock);
    }
    public void setUnpackagedStock(int stock) {
    	this.unpackagedStockProperty.set(stock);
    }
    public IntegerProperty getPackagedStockProperty() {
        return this.packagedStockProperty;
    }
    public IntegerProperty getUnpackagedStockProperty(){ 
    	return unpackagedStockProperty;
    }
    public void setPackagedStockProperty(IntegerProperty stockProperty) { 
    	this.packagedStockProperty = stockProperty;
    }
    public void setUnpackagedStockProperty(IntegerProperty stockProperty) { 
    	this.unpackagedStockProperty = stockProperty;
    }

    //EXPIRY GET+SET
    public String getExpiry() {
        return this.expiryProperty.get();
    }
    public void setExpiry(String expiry) {
        this.expiryProperty.set(expiry);
    }
    public StringProperty getExpiryProperty() {
        return this.expiryProperty;
    }
    public void setExpiryProperty(StringProperty expiryProperty) {
        this.expiryProperty = expiryProperty;
    }

    /**
     * Returns the month number of the lot expiry date.
     * @return The month number of the lot expiry date
     */
    public int getExpiryMonth() {
        //Parsing difference: https://www.geeksforgeeks.org/integer-valueof-vs-integer-parseint-with-examples/
        return Integer.parseInt(expiryProperty.get().substring(0, expiryProperty.get().indexOf('/')));
    }

    /**
     * Returns the year of the lot expiry date.
     * @return The year of the lot expiry date.
     */
    public int getExpiryYear() {
        return Integer.parseInt(expiryProperty.get().substring(expiryProperty.get().indexOf('/')+1));
    }

    //I have added this method to clean things up a bit and to facilitate any changes to how we determine whether something is expired
    /**
     * Returns whether or not the lot is expired relative to the inputted month and year.
     * IMPORTANT: If the current month is equal to the expiry month, the lot is considered NOT expired.
     * @param month The month to consider as an int
     * @param year The year to consider as an int
     * @return A boolean representing whether or not the lot is expired
     */
    private boolean checkExpiry(int month, int year) {
        int expiryMonth = getExpiryMonth();
        int expiryYear = getExpiryYear();

        if(year > expiryYear)
            return true;
        else if(expiryYear > year)
            return false;
        else return month > expiryMonth;
    }

    /**
     * Returns whether or not the lot is expired relative to today's month and year as determined by "the system clock in the default time-zone" -Java 8 Documentation for LocalDate.now().
     * IMPORTANT: If the current month is equal to the expiry month, the lot is considered NOT expired.
     * @return A boolean representing whether or not the lot is expired
     */
    public boolean isExpired() {
        return checkExpiry(LocalDate.now().getMonthValue(), LocalDate.now().getYear());
    }

    /**
     * Returns whether, next month, the lot will be expired. The current month and year is as determined by "the system clock in the default time-zone" -Java 8 Documentation for LocalDate.now().
     * IMPORTANT: If the month is equal to the expiry month, the lot is considered NOT expired.
     * @return A boolean representing whether or not the lot is expired next month.
     */
    public boolean willExpireNextMonth() {
        //Get current month and year
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();

        //Increase month. If it's December, the year must increase and the month becomes 1.
        if(month == 12) {
            year++;
            month = 1;
        }
        else
            month++;

        //Return expiry status
        return checkExpiry(month, year);
    }

    //MIGRATED FROM MED
    /**
     * The Package method will alter the stock of packaged and unpackaged medication to reflect the physical packaging of unpackaged medication.
     * @param quantity The number of pills being packaged
     */
    public boolean packageFromLot(int quantity){
        int pStock = this.getPackagedStock();
        int uStock = this.getUnpackagedStock();
        if(uStock - quantity<0){
            return false;
        }else {
            this.setPackagedStock(pStock + quantity);
            this.setUnpackagedStock(uStock - quantity);
            return true;
        }
    }

    //MIGRATED FROM MED
    /**
     * Decreases total stock, total packaged stock and lot packaged stock by quantity only if this does not result in negative stock.
     * @param quantity The number of units being dispensed
     * @return whether the operation was succesful
     */
    public boolean dispenseFromLot(int quantity){
        if(this.getPackagedStock() - quantity<0){
            return false;
        }else {
            this.setPackagedStock(this.getPackagedStock() - quantity);
            return true;
        }
    }

}
