package main.dock;

import main.port.Port;
import main.ships.Ship;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Dock {
    private static int uniqieId = 1;
    private String dockId;
    private BlockingQueue<Ship>waitingShips;
    private Port port;
    private int shipsUnloaded;

    public Dock(Port port){
        this.dockId = "Dock Id "+uniqieId++;
        waitingShips = new LinkedBlockingQueue<>();
        this.port = port;
    }

    public synchronized void addShip(Ship ship){
        waitingShips.add(ship);
    }

    public synchronized void removeShip(){
        try {
            waitingShips.take();
            shipsUnloaded++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public BlockingQueue<Ship> getWaitingShips() {
        return waitingShips;
    }

    public String getDockId() {
        return dockId;
    }

    public int getShipsUnloaded() {
        return shipsUnloaded;
    }
}
