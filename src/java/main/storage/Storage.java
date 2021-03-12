package main.storage;

import main.cargo.Cargo;
import main.port.Port;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Storage {
    private Port port;
    private static int uniqieId = 1;
    private String storageId;
    private BlockingQueue<Cargo> cargoStorage;

    public Storage (Port port){
        this.port = port;
        this.storageId = "Storage ID "+uniqieId++;
        cargoStorage = new LinkedBlockingQueue<>();
    }

    public void addCargo(Cargo c){
        try {
            cargoStorage.put(c);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public Cargo removeCargo(){
        Cargo cargo = null;
        try {
            cargo = cargoStorage.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return cargo;
    }

    public BlockingQueue<Cargo> getCargoStorage() {
        return cargoStorage;
    }

    public String getStorageId() {
        return storageId;
    }
}
