package main;

import main.crane.Crane;
import main.distributor.Distributor;
import main.dock.Dock;
import main.port.Port;
import main.port.Statistics;
import main.port.StatisticsDB;
import main.ships.Ship;
import main.storage.Storage;

public class Demo {
    public static void main(String[] args) {
        Port port = new Port("IT Port");
        for(int i = 0; i<2; i++){
            port.addCrane(new Crane(port));
            port.addDistributor(new Distributor(port));
            port.addStorage(new Storage(port));
        }
        for(int i = 0; i<5; i++){
            port.addDocks(new Dock(port));
        }
        port.work();
        for(int i = 0; i<10; i++){
            Ship ship = new Ship("Talents "+(i+1), port);
            ship.start();
        }
        Statistics stat = new Statistics(port);
        stat.setDaemon(true);
        stat.start();
        StatisticsDB statDB = new StatisticsDB(port);
        statDB.setDaemon(true);
        statDB.start();

    }
}
