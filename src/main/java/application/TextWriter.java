package application;

import application.object.Inventory;
import application.object.Lot;
import application.object.Med;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The TextWriter Class provides the functionality required to process text file updates
 * @author Jason Hayhoe
 * The structure of the commands is as follows:
 * Dispense from lot:DFL-lotNumber-quantity
 * Add med: ADD-name-description-strength-lot-#packaged-#unpackaged-expirydate-class/type
 * Package med: PKG-lot-#packaged
 * Dispose: DSP-lotNumber
 * Update: UPT-lotNumber-totalPackaged-totalUnpackaged
 */
public class TextWriter {
    private final Inventory TempInventory;
    private final String TxtName;

    /**
     * Null Constructor
     */
    public TextWriter() {
        this.TempInventory = new Inventory();
        this.TxtName = "";
    }
    /**
     * Constructor
     * @param XlsxName The name of the Excel File Storing the Inventory
     * @param TxtName Then name of the txt file that is to be processed
     * @throws IOException when either of the files cannot be found
     */
    public TextWriter(String XlsxName,String TxtName) throws IOException {
        this.TempInventory = new Inventory(XlsxName,TxtName);
        this.TxtName = TxtName;

    }

    /**
     * Process the changes found in the txt and updates the inventory accordingly
     * @throws IOException If the file name does not exist
     */
    public void TxtProcessor() throws IOException {
        boolean strengthFound = false;
        Med adder;
        int locator = 0;
        List<String> Lines = Files.readAllLines(Paths.get(this.TxtName));
        for(String line:Lines){
            String[] commands = line.split("-");
            switch(commands[0]){
                case "DFL":
                    this.TempInventory.searchByLot(commands[1]).dispenseFromLot(Integer.parseInt(commands[2]),"",false);
                    break;
                case "ADD":
                    if(this.TempInventory.searchByName(commands[1]).size() == 0){
                        Med mede = new Med(commands[1],commands[2],commands[3],new ArrayList<>(),commands[8]);
                        mede.getLots().add(new Lot(commands[4],Integer.parseInt(commands[5]),Integer.parseInt(commands[6]),commands[7]));
                        this.TempInventory.addMed(mede,false);
                    }else{
                        for(Med nameMatch: this.TempInventory.searchByName(commands[1])){
                            if(nameMatch.getStrength().compareTo(commands[3]) == 0){
                                strengthFound = true;
                                break;
                            }
                            locator++;
                        }
                        if(!strengthFound){
                            adder = new Med(commands[1],commands[2],commands[3],new ArrayList<>(),commands[8]);
                            adder.getLots().add(new Lot(commands[4],Integer.parseInt(commands[5]),Integer.parseInt(commands[6]),commands[7]));
                            this.TempInventory.addMed(adder,false);
                        }else{
                            if(this.TempInventory.getByLot().containsKey(commands[4])){
                                Integer[] locale = this.TempInventory.getByLot().get(commands[4]);
                                this.TempInventory.getMeds().get(locale[0]).getLots().get(locale[1]).setUnpackagedStock(this.TempInventory.getMeds().get(locale[0]).getLots().get(locale[1]).getUnpackagedStock() + Integer.parseInt(commands[6]));
                            }else{
                                this.TempInventory.getMeds().get(locator).addLot(commands[4],Integer.parseInt(commands[5]),Integer.parseInt(commands[6]),commands[7]);
                            }
                        }

                    }
                    break;
                case "PKG":
                    Integer[] finder = this.TempInventory.getByLot().get(commands[1]);
                    this.TempInventory.getMeds().get(finder[0]).getLots().get(finder[1]).packageFromLot(Integer.parseInt(commands[2]),false,this.TxtName);
                    break;
                case "DSP":
                    Integer[] kinder = this.TempInventory.getByLot().get(commands[1]);
                    this.TempInventory.getMeds().get(kinder[0]).disposeLot(commands[1],false,"");
                    break;
                case "UPT":
                    Integer[] hinder = this.TempInventory.getByLot().get(commands[1]);
                    this.TempInventory.getMeds().get(hinder[0]).getLots().get(hinder[1]).setPackagedStock(Integer.parseInt(commands[2]));
                    this.TempInventory.getMeds().get(hinder[0]).getLots().get(hinder[1]).setUnpackagedStock(Integer.parseInt(commands[3]));
                    break;
            }
            this.TempInventory.writeToSheet();
            FileWriter clearing = new FileWriter(this.TxtName);
            BufferedWriter buffClean = new BufferedWriter(clearing);
            buffClean.write("");
            buffClean.flush();
            clearing.flush();
            buffClean.close();
            clearing.close();

        }
    }
}