package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.*;
import java.io.*;
import javafx.beans.property.SimpleStringProperty;

/**
 * The Inventory class stores all the relevant information for the inventory.
 * @author Jason Hayhoe
 */
public class Inventory {
    private ArrayList<Med> meds;
    private HashMap<String,Integer[]> byLot;
    private HashMap<String,ArrayList<Integer>> byName;
    private XSSFWorkbook workbook;
    private SimpleStringProperty fname;
    private SimpleIntegerProperty maxLots;

    /**
     * Null Constructor
     */
    public Inventory(){
        this.meds = new ArrayList<>();
        this.byLot = new HashMap<>();
        this.byName = new HashMap<>();
        this.workbook = new XSSFWorkbook();
        this.fname = new SimpleStringProperty("");
        this.maxLots = new SimpleIntegerProperty(0);
    }

    /**
     * Constructor
     @param filename The name of the Excel file containing the data
     */
    public Inventory(String filename) throws Exception{
        this.fname = new SimpleStringProperty(filename);
        FileInputStream getter = new FileInputStream(filename);
        this.workbook = new XSSFWorkbook(getter);
        getter.close();
        Sheet sh = workbook.getSheet("Medications");
        int last = sh.getLastRowNum();
        this.meds = new ArrayList<>();
        this.byLot = new HashMap<>();
        this.byName = new HashMap<>();
        int ml = 0;
        for(int a = 1;a<=last;a++){
            Med new_med = new Med(sh.getRow(a));
            this.meds.add(new_med);
            for(int k = 0;k<new_med.getLots().size();k++){
                Integer [] locations = {meds.size() - 1,k};
                byLot.put(new_med.getLots().get(k).getLotNumber(),locations);
            }
            if(byName.containsKey(new_med.getName())) byName.get(new_med.getName()).add(meds.size() - 1);
            else{
                ArrayList<Integer> nw = new ArrayList<>();
                nw.add(meds.size() - 1);
                byName.put(new_med.getName(),nw);
            }
            if(((sh.getRow(a).getLastCellNum() -6)/4 > ml)) ml = (sh.getRow(a).getLastCellNum() - 6)/4;
        }
        maxLots = new SimpleIntegerProperty(ml);
    }

    public ArrayList<Med> getMeds(){
        return this.meds;
    }

    /**
     * Writes the inventory to the file in which it is to be stored
     */
    public void writeToSheet() throws Exception{
        int a = 1;
        Sheet sh = workbook.getSheet("Medications");
        Row first = sh.getRow(0);
        for(int b = 0; b< maxLots.get(); b++){
            first.createCell(7 + 4*b).setCellValue("Lot " + (b + 1));
            first.createCell(8 + 4*b).setCellValue("Unpackaged stock " + (b + 1));
            first.createCell(9 + 4*b).setCellValue("Packaged stock " + (b + 1));
            first.createCell(10 + 4*b).setCellValue("Expiry " + (b + 1));
        }
        for(Med md:meds){
            md.writeMed(sh.createRow(a));
            a++;
        }
        FileOutputStream outtie = new FileOutputStream(fname.getValue());
        this.workbook.write(outtie);
        this.workbook.close();
        outtie.close();
    }

    /**
     * Adds a Medication to the Inventory if the medication does not exist.
     * @param mede The Med object to be added to the inventory
     */
    public void addMed(Med mede) {
        this.meds.add(mede);
        if(mede.getLots().size()> maxLots.get()) maxLots = new SimpleIntegerProperty(mede.getLots().size()); //direct ref to lots
        for(Lot lot:mede.getLots()){
            
        }
    }

    /**
     * Searches the Inventory and finds all the expired Lots for each medication
     * @return  An ArrayList of Expired Meds
     */
    public ArrayList<Med> listExpired(){
        ArrayList<Med> exp = new ArrayList<>();
        for(Med md:meds){
            exp.add(new Med(md.getName(),md.getDescription(),md.getStrength(),md.getExpiredLots(),md.getType()));
        }
        return exp;
    }

    /**
     * Searches the Inventory and Removes the Expired Lots for each Medication
     * @return an ArrayList of the Lots that were removed for each med
     */
    public ArrayList<Med> removeExpired(){
        ArrayList<Med> removed = new ArrayList<>();
        Med remMed = new Med();
        for (Med med : meds) {
            for (int a = 0;a<med.getLots().size();a++) {
                Lot lot = med.getLots().get(a);
                remMed = new Med(med.getName(), med.getDescription(), med.getStrength(), new ArrayList<Lot>(), med.getType());
                if (lot.isExpired()) {
                    med.getLots().remove(lot);
                    remMed.getLots().add(lot);
                }
            }
            if (remMed.getLots().size() != 0) removed.add(remMed);
        }
        return removed;
    }

    /**
     * @param lot_number The lot number being searched for
     * @return The Lot Object corresponding with the given lot number
     */
    public Lot searchByLot(String lot_number){
        return meds.get(byLot.get(lot_number)[0]).getLots().get(byLot.get(lot_number)[1]);
    }

    public ArrayList<Med> searchByName(String name){
        ArrayList<Med> found = new ArrayList<>();
        for(Integer local:byName.get(name)){
            found.add(meds.get(local));
        }
        return found;
    }
}

