package main.connector;

import com.google.gson.Gson;
import main.util.Randomizer;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

    private static DBConnector instance;
    private Connection connection;
    private DBCredentials credentials;

    public static DBConnector getInstance() {
        if (instance == null) {
            instance = new DBConnector();
        }
        return instance;
    }

    private DBConnector() {
        loadCredentials();
        if(credentials != null) {
            initConnection();
        }
    }

    private void initConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + credentials.host + ":" + credentials.port + credentials.dbScheme, credentials.username, credentials.password);
        } catch (ClassNotFoundException e) {
            System.out.println("Unable to use mysql! " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error in communication with MYDatabase! " + e.getMessage());
        }
    }

    private class DBCredentials{
        private String host;
        private int port;
        private String dbScheme;
        private String username;
        private String password;

    }

    private void loadCredentials() {
        Gson gson = new Gson();
        try {
            DBCredentials credentials = gson.fromJson(new FileReader("db_settings.json"), DBCredentials.class);
            if(credentials == null){
                System.out.println("Credentials missing in configuration file");
                return;
            }
            this.credentials = credentials;
        } catch (FileNotFoundException e) {
            System.out.println("Error reading credentials. " + e.getMessage() );
        }
    }

    public Connection getConnection() {
        if(connection == null){
            initConnection();
        }
        return connection;
    }
    public void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Closing connection failed - " + e.getMessage());
        }
        connection = null;

    }
}
