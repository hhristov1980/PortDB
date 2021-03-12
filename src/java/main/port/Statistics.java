package main.port;

import main.crane.Crane;
import main.distributor.Distributor;
import main.dock.Dock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class Statistics extends Thread {
    private Port port;

    public Statistics(Port port){
        this.port = port;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            File f = new File(LocalDateTime.now()+" archive.txt");
            try(PrintStream ps = new PrintStream(f)){
                ps.println("============== STATISTICS ==============");
                for (Map.Entry<String, ConcurrentSkipListMap<String, ConcurrentSkipListMap<String, ConcurrentSkipListMap<String, LocalDateTime>>>> docks : port.getArchive().entrySet()) {
                    ps.println(docks.getKey()+": ");
                    for (Map.Entry<String, ConcurrentSkipListMap<String, ConcurrentSkipListMap<String, LocalDateTime>>>crane:docks.getValue().entrySet()){
                        ps.println("\t"+crane.getKey()+": ");
                        for(Map.Entry<String, ConcurrentSkipListMap<String, LocalDateTime>> ships:crane.getValue().entrySet()){
                            ps.println("\t\t"+ships.getKey()+": ");
                            for(Map.Entry<String, LocalDateTime> cargo:ships.getValue().entrySet()){
                                ps.println("\t\t\t"+cargo.getKey()+" Date and time of entering at the cargo storage: "+cargo.getValue());
                            }
                        }
                    }
                }
                int totalShips = 0;
                ps.println();
                ps.println("-------------------------------------------");
                for(Dock d: port.getDocks()){
                    ps.println(d.getDockId()+": "+d.getShipsUnloaded()+" unloaded ships;");
                    totalShips+=d.getShipsUnloaded();
                }
                ps.println("Total ships unloaded: "+totalShips);
                ps.println("-------------------------------------------");

                int totalCargo = 0;
                for (Crane c: port.getCranes()){
                    ps.println(c.getCraneId()+": "+c.getCargoUnloaded()+" cargo unloaded;");
                    totalCargo+=c.getCargoUnloaded();
                }
                ps.println("Total cargo unloaded: "+totalCargo);
                ps.println("-------------------------------------------");

                int totalCargoDis = 0;
                for(Distributor d: port.getDistributors()){
                    ps.println(d.getDistributorId()+" took "+d.getCargoToDistribute().size()+" cargo from storage");
                    totalCargoDis+=d.getCargoToDistribute().size();
                }
                ps.println("Total cargo taken from storage by distributors: "+totalCargoDis);
                ps.println("-------------------------------------------");


                ps.println("============== END OF STATISTICS ==============");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }
    }
}
