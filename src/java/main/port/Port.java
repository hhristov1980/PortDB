package main.port;

import main.cargo.Cargo;
import main.connector.DBConnector;
import main.crane.Crane;
import main.distributor.Distributor;
import main.dock.Dock;
import main.ships.Ship;
import main.storage.Storage;
import main.util.Randomizer;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Port {
    private String name;
    private CopyOnWriteArrayList<Dock>docks;
    private CopyOnWriteArrayList<Crane>cranes;
    private CopyOnWriteArrayList<Distributor>distributors;
    private CopyOnWriteArrayList<Storage> storages;
    private ConcurrentSkipListMap<String,ConcurrentSkipListMap<String, ConcurrentSkipListMap<String, ConcurrentSkipListMap<String, LocalDateTime>>>> archive;

    public Port(String name){
        if(name.length()>0){
            this.name = name;
        }
        docks = new CopyOnWriteArrayList<>();
        cranes = new CopyOnWriteArrayList<>();
        distributors = new CopyOnWriteArrayList<>();
        storages = new CopyOnWriteArrayList<>();
        archive = new ConcurrentSkipListMap<>();

    }

    public void addDocks(Dock d){
        docks.add(d);
    }
    public void addCrane(Crane c){
        cranes.add(c);
    }
    public void addDistributor(Distributor d){
        distributors.add(d);
    }
    public void addStorage(Storage s){
        storages.add(s);
    }


    public synchronized void enterDocks(Ship ship){
        int dockChoice = Randomizer.getRandomInt(0,docks.size()-1);
        Dock dock = docks.get(dockChoice);
        dock.addShip(ship);
        notifyAll();
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void unloadCargo(Crane crane){
        boolean noShipsToUnload = true;
        Cargo cargo;
        for(Dock d: docks){
            if(d.getWaitingShips().size()>0){
                noShipsToUnload=false;
                Ship ship = d.getWaitingShips().peek();
                if(ship.getShipCargoStorage().size()>0){
                    int size = ship.getShipCargoStorage().size();
                    for(int i = 0; i<size; i++){
                        cargo = ship.getShipCargoStorage().pop();
                        int cargoStorageIndex = Randomizer.getRandomInt(0,1);
                        crane.increaseCargoNum();
                        storages.get(cargoStorageIndex).addCargo(cargo);
                        if(!archive.containsKey(d.getDockId())){
                            archive.put(d.getDockId(),new ConcurrentSkipListMap<>());
                        }
                        if(!archive.get(d.getDockId()).containsKey(crane.getCraneId())){
                            archive.get(d.getDockId()).put(crane.getCraneId(), new ConcurrentSkipListMap<>());
                        }
                        if(!archive.get(d.getDockId()).get(crane.getCraneId()).containsKey(ship.getShipName())){
                            archive.get(d.getDockId()).get(crane.getCraneId()).put(ship.getShipName(), new ConcurrentSkipListMap<>());
                        }
                        archive.get(d.getDockId()).get(crane.getCraneId()).get(ship.getShipName()).put(cargo.getCargoId(), LocalDateTime.now());
                        insertData(cargo,ship,crane,d);
                        System.out.println("Cargo with "+cargo.getCargoId()+" was unloaded from "+ship.getShipName()+" on dock "+d.getDockId()+" by Crane with "+crane.getCraneId());

                    }
                    System.out.println(ship.getShipName()+" was unloaded successfully");
                    d.removeShip();
                    notifyAll();

                }

            }
        }
        if (!noShipsToUnload){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized Cargo takeCargoFromStorage(Distributor distributor){
        boolean noCargoInStorage = true;
        Cargo cargo = null;
        for(Storage s: storages){
            if(s.getCargoStorage().size()>0){
                noCargoInStorage=false;
                cargo = s.removeCargo();
                System.out.println(distributor.getDistributorId()+" took cargo with "+cargo.getCargoId()+" from storage "+s.getStorageId());
                break;
            }
        }
        if (noCargoInStorage){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return cargo;
    }

    public ConcurrentSkipListMap<String, ConcurrentSkipListMap<String, ConcurrentSkipListMap<String, ConcurrentSkipListMap<String, LocalDateTime>>>> getArchive() {
        return archive;
    }

    public void work(){
        for(Crane c: cranes){
            c.start();
        }
        for(Distributor d: distributors){
            d.start();
        }
    }

    public CopyOnWriteArrayList<Crane> getCranes() {
        return cranes;
    }

    public CopyOnWriteArrayList<Dock> getDocks() {
        return docks;
    }

    public CopyOnWriteArrayList<Distributor> getDistributors() {
        return distributors;
    }

    private synchronized void insertData(Cargo cargo, Ship ship, Crane crane, Dock dock){
        String insertQuery = "INSERT INTO port_shipments (boat_name, dock_id, crane_id, unloading_time, package_id) VALUES (?,?,?,?,?)";
        Connection connection = DBConnector.getInstance().getConnection();
        try(PreparedStatement statement = connection.prepareStatement(insertQuery)){
            statement.setString(1, ship.getShipName());
            statement.setString(2, dock.getDockId());
            statement.setString(3, crane.getCraneId());
            statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            statement.setString(5, cargo.getCargoId());
            statement.executeUpdate();
            System.out.println("Data for ship "+ship.getShipName()+" was added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            DBConnector.getInstance().closeConnection();
        }



    }



}
