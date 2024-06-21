/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.service;

import com.mycompany.connection.DataBaseConnection;
import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import model.Model_Client;
import model.Model_Data;
import model.Model_User_Account;

/**
 *
 * @author User
 */
public class ServiceUser {
    private static ServiceUser instance;
    private Connection conn;
    private String user;
    private String pass;
    
    private ServiceUser(){
        this.conn = DataBaseConnection.getInstance().getConn();
    }
    
    public static ServiceUser getInstance(){
        if(instance==null){
            instance= new ServiceUser();
        }
        return instance;
    }
    
    public boolean register(Model_Data data){
        user = data.getUsername();
        pass = data.getPass();
        
        String sql;
        Statement stmt;
        
        try {
            stmt = conn.createStatement();
            sql = String.format("INSERT INTO UserList(username,pass,confPass) VALUES ('%s','%s','%s')",this.user,this.pass,this.pass);
        
            stmt.execute(sql);
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Model_User_Account login(Model_Data data){
        Model_User_Account acc = null;
        user = data.getUsername();
        pass = data.getPass();
        
        String sql;
        Statement stmt;
        ResultSet rs;
        
        try {
            stmt = conn.createStatement();
            sql = String.format("SELECT id_User, username FROM UserList WHERE username LIKE '%s'",user);


            rs = stmt.executeQuery(sql);
            
            
            if(rs.next()){
                int idUser = rs.getInt(1);
                String username = rs.getString(2);
              
                acc = new Model_User_Account(idUser, username);
            }
            
            return acc; 
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
 
    
}
