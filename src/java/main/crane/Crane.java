package main.crane;

import main.port.Port;

public class Crane extends Thread{
    private Port port;
    private static int uniqieId = 1;
    private String craneId;
    private int cargoUnloaded;


    public Crane(Port port){
        this.port = port;
        this.craneId = "Crane ID "+uniqieId++;
    }

    public String getCraneId() {
        return craneId;
    }

    @Override
    public void run() {
        while (true){
            port.unloadCargo(this);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void increaseCargoNum(){
        cargoUnloaded++;
    }

    public int getCargoUnloaded() {
        return cargoUnloaded;
    }
}
