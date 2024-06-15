/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class DataBaseConnection {
        private static DataBaseConnection instance;
        private Connection conn;
        
        public static  DataBaseConnection getInstance(){
            if(instance == null ){
                instance = new DataBaseConnection();
            }
            return instance;
        }

    public DataBaseConnection() {
    }
        
    public void connectToDatabase(){
        
        
        String dbURL = "jdbc:sqlserver://Acer\\SQLEXPRESS;encrypt=true;trustServerCertificate=true;databaseName=GUITest;IntegratedSecurity=true";
        String username = "sa";
        String password = "pass";
        
            try {
                conn = DriverManager.getConnection(dbURL, username, password);
                
                
            } catch (SQLException ex) {
                Logger.getLogger(DataBaseConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }
    
    
}
