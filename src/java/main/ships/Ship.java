package main.ships;

import main.cargo.Cargo;
import main.port.Port;
import main.util.Randomizer;
import java.util.Stack;

public class Ship extends Thread{
    private String shipName;
    private Stack<Cargo> shipCargoStorage;
    private Port port;

    public Ship(String shipName, Port port){
        if(shipName.length()>0){
            this.shipName = shipName;
        }
        this.port = port;
        shipCargoStorage = new Stack<>();
        int numberOfCargo = Randomizer.getRandomInt(1,4);
        for(int i = 0; i<numberOfCargo;i++){
            shipCargoStorage.push(new Cargo());
        }
    }


    @Override
    public void run() {
        port.enterDocks(this);
        while (!shipCargoStorage.isEmpty()){
        }
        System.out.println("Ship "+shipName+" left the port!");

    }

    public Stack<Cargo> getShipCargoStorage() {
        return shipCargoStorage;
    }

    public String getShipName() {
        return shipName;
    }
}
