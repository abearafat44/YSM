package application.object;

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
    private final ArrayList<Med> meds;
    private final HashMap<String,Integer[]> byLot;
    private final HashMap<String,ArrayList<Integer>> byName;
    private final SimpleStringProperty fname;
    private SimpleIntegerProperty maxLots;
    private final String txtName;

    /**
     * Null Constructor
     */
    public Inventory(){
        this.meds = new ArrayList<>();
        this.byLot = new HashMap<>();
        this.byName = new HashMap<>();
        this.fname = new SimpleStringProperty("");
        this.maxLots = new SimpleIntegerProperty(0);
        this.txtName = "";
    }

    /**
     * Constructor
     @param filename The name of the Excel file containing the data
     */
    public Inventory(String filename,String txtName) throws IOException{
        this.fname = new SimpleStringProperty(filename);
        this.txtName = txtName;
        FileInputStream getter = new FileInputStream(filename);
        XSSFWorkbook workbook = new XSSFWorkbook(getter);
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
        workbook.close();
    }

    public ArrayList<Med> getMeds(){
        return this.meds;
    }
    public HashMap<String,ArrayList<Integer>> getByName() {return this.byName;}
    public HashMap<String,Integer[]> getByLot() {return this.byLot;}
    public String getfName(){return this.fname.getValue();}
    public String getTxtName(){return this.txtName;}

    /**
     * Writes the inventory to the file in which it is to be stored
     */
    public void writeToSheet() throws IOException{
        int a = 1;
        XSSFWorkbook wb2 = new XSSFWorkbook();
        Sheet sh = wb2.createSheet("Medications");
        Row first = sh.createRow(0);
        first.createCell(0).setCellValue("Name");
        first.createCell(1).setCellValue("Strength");
        first.createCell(2).setCellValue("Description");
        first.createCell(3).setCellValue("Class");
        first.createCell(4).setCellValue("Total Stock");
        first.createCell(5).setCellValue("Total Packaged Stock");
        first.createCell(6).setCellValue("Total Unpackaged Stock");
        for(int b = 0; b< maxLots.get(); b++){
            first.createCell(7 + 4*b).setCellValue("Lot " + (b + 1));
            first.createCell(8 + 4*b).setCellValue("Packaged stock " + (b + 1));
            first.createCell(9 + 4*b).setCellValue("Unpackaged stock " + (b + 1));
            first.createCell(10 + 4*b).setCellValue("Expiry " + (b + 1));
        }
        for(Med md:meds){
            md.writeMed(sh.createRow(a));
            a++;
        }
        FileOutputStream outtie = new FileOutputStream(fname.getValue());
        wb2.write(outtie);
        wb2.close();
        outtie.close();
    }

    /**
     * Adds a Medication to the Inventory if the medication does not exist.
     * @param mede The Med object to be added to the inventory
     * @throws IOException if the txt file doesn't exist
     */
    public void addMed(Med mede,boolean inApp) throws IOException {
        this.meds.add(mede);
        if(mede.getLots().size()> maxLots.get()) maxLots = new SimpleIntegerProperty(mede.getLots().size()); //direct ref to lots
        int a = 0;
        for(Lot lot:mede.getLots()){
            this.byLot.put(lot.getLotNumber(),new Integer [2]);
            byLot.get(lot.getLotNumber())[0] = meds.size() - 1;
            byLot.get(lot.getLotNumber())[1] = a;
            a++;
        }
        if(byName.containsKey(mede.getName())) byName.get(mede.getName()).add(meds.size() - 1);
        else{
            ArrayList<Integer> nw = new ArrayList<>();
            nw.add(meds.size() - 1);
            byName.put(mede.getName(),nw);
        }
        if(inApp) {
            FileWriter fWrite = new FileWriter(txtName, true);
            BufferedWriter bWrite = new BufferedWriter(fWrite);
            PrintWriter pWrite = new PrintWriter(bWrite);
            pWrite.println("ADD-" + mede.getName() + "-" + mede.getDescription() + "-" + mede.getStrength() + "-" + mede.getLots().get(0).getLotNumber() + "-" + mede.getLots().get(0).getPackagedStock() + "-" +  mede.getLots().get(0).getUnpackagedStock() + "-" + mede.getLots().get(0).getExpiry() + "-" + mede.getType());
            pWrite.flush();
            fWrite.close();
            bWrite.close();
            fWrite.close();
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
     * @throws IOException if the txt file is corrupted/missing
     * @return an ArrayList of the Lots that were removed for each med
     */
    public ArrayList<Med> removeExpired() throws IOException{
        ArrayList<Med> removed = new ArrayList<>();
        Med remMed = new Med();
        for (Med med : meds) {
            for (int a = 0;a<med.getLots().size();a++) {
                Lot lot = med.getLots().get(a);
                remMed = new Med(med.getName(), med.getDescription(), med.getStrength(), new ArrayList<>(), med.getType());
                if (lot.isExpired()) {
                    med.disposeLot(lot.getLotNumber(),true,"commands.txt");
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
        try {
            for (Integer local : byName.get(name)) {
                found.add(meds.get(local));
            }
        }
        catch(NullPointerException e){System.out.println();}
        return found;
    }
}

