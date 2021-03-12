package main.distributor;

import main.cargo.Cargo;
import main.port.Port;

import java.util.ArrayList;

public class Distributor extends Thread {
    private static int uniqieId = 1;
    private String distributorId;
    private Port port;
    private ArrayList<Cargo> cargoToDistribute;
    private int cargoTakenFromStorage;

    public Distributor(Port port){
        this.port = port;
        this.distributorId = "Distributor ID "+uniqieId++;
        cargoToDistribute = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true){
            Cargo cargo = port.takeCargoFromStorage(this);
            if(cargo!=null){
                cargoToDistribute.add(cargo);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getDistributorId() {
        return distributorId;
    }

    public ArrayList<Cargo> getCargoToDistribute() {
        return cargoToDistribute;
    }

    public int getCargoTakenFromStorage() {
        return cargoTakenFromStorage;
    }
}
