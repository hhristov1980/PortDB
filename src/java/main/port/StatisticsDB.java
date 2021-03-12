package main.port;

import main.connector.DBConnector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class StatisticsDB extends Thread{

    private Port port;

    public StatisticsDB(Port port){
        this.port = port;
    }

    @Override
    public void run() {

        while (true){
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            File file = new File(LocalDateTime.now()+"DBreport.txt");
            try (PrintStream ps = new PrintStream(file)){
                shipWithMostCargo(ps);
                printTotalCargoPerCrane(ps);
                printTotalShipsByDocks(ps);
                printTotalStatistics(ps);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private void printTotalShipsByDocks(PrintStream ps){
        Connection connection = DBConnector.getInstance().getConnection();
        try (Statement st = connection.createStatement()
        ) {
            ResultSet result = st.executeQuery("SELECT dock_id, COUNT(boat_name) AS ships FROM port_shipments GROUP BY dock_id ORDER BY dock_id;");
            ps.println("Ships per dock:");
            while(result.next()) {
                ps.println(result.getString("dock_id")+" "+result.getString("ships"));
            }
        } catch (SQLException e) {
            System.out.println("Error creating statement. " + e.getMessage());
        }
        finally {
            DBConnector.getInstance().closeConnection();
        }
    }

    private void printTotalCargoPerCrane(PrintStream ps){
        Connection connection = DBConnector.getInstance().getConnection();
        try (Statement st = connection.createStatement()) {
            ResultSet result = st.executeQuery("SELECT crane_id, COUNT(package_id) AS packages FROM port_shipments GROUP BY crane_id ORDER BY crane_id;");
            ps.println("Packages per crane:");
            while(result.next()) {
                ps.println(result.getString("crane_id")+" "+result.getString("packages"));
            }
        } catch (SQLException e) {
            System.out.println("Error creating statement. " + e.getMessage());
        } finally {
            DBConnector.getInstance().closeConnection();
        }
    }

    private void shipWithMostCargo(PrintStream ps){
        Connection connection = DBConnector.getInstance().getConnection();
        try (Statement st = connection.createStatement()) {
            ResultSet result = st.executeQuery("SELECT boat_name, COUNT(boat_name) AS number_of_cargo FROM port_shipments GROUP BY boat_name LIMIT 1;");
            while(result.next()) {
                ps.println("The ship with most cargo is "+result.getString("boat_name")+" with "+result.getString("number_of_cargo")+" shipments!");
            }
        } catch (SQLException e) {
            System.out.println("Error creating statement. " + e.getMessage());
        } finally {
            DBConnector.getInstance().closeConnection();
        }

    }

    private void printTotalStatistics(PrintStream ps){
        Connection connection = DBConnector.getInstance().getConnection();
        try (Statement st = connection.createStatement()
        ) {
            ResultSet result = st.executeQuery("SELECT * FROM port_shipments ORDER BY dock_id,unloading_time;");
            ps.println("Total statistics:");
            while(result.next()) {
                ps.print(result.getInt("entry_id")+" | ");
                ps.print(result.getString("dock_id")+" | ");
                ps.print(result.getString("boat_name")+" | ");
                ps.print(result.getString("crane_id")+" | ");
                ps.print(result.getTimestamp("unloading_time")+" | ");
                ps.println(result.getString("package_id"));
            }
        } catch (SQLException e) {
            System.out.println("Error creating statement. " + e.getMessage());
        } finally {
            DBConnector.getInstance().closeConnection();
        }
    }

}
