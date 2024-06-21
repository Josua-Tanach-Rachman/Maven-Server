/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.service;

import com.mycompany.client.Room;
import com.mycompany.connection.DataBaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.Model_Data;
import model.Model_User_Account;

public class ServiceRoom {
    private static ServiceRoom instance;
    private Connection conn;
    
    private ServiceRoom() {
        this.conn = DataBaseConnection.getInstance().getConn();
    }
    
    public static ServiceRoom getInstance(){
        if(instance==null){
            instance = new ServiceRoom();
        }
        return instance;
    }
    
    public List<Room> getRoom(Model_Data data){
        String user = data.getUsername();
        
        String sql;
        Statement stmt;
        ResultSet rs;
        
        try {
            stmt = conn.createStatement();
            //Ambil Id_User
            sql = String.format("SELECT RoomList.id_Room,nama_Room,id_RM \n" +
                                "  FROM UserList\n" +
                                "  Join userToRoom ON UserList.id_User = userToRoom.id_User\n" +
                                "  JOIN roomList ON userToRoom.id_Room = roomList.id_Room\n" +
                                "  WHERE username LIKE 'd1'",user);

            rs = stmt.executeQuery(sql);
            int id_Room, id_RM;
            String nama_Room;
            
            List<Room> listRoom = new ArrayList<>();
     
         
            while(rs.next()){
                
                id_Room = rs.getInt(1);
                nama_Room = rs.getString(2);
                id_RM = rs.getInt(3);
                
                listRoom.add(new Room(id_Room,nama_Room,id_RM));
            }    

            return listRoom;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Room> getAllRoom(){

        
        String sql;
        Statement stmt;
        ResultSet rs;
        
        try {
            stmt = conn.createStatement();
           
            //Ambil Id_Room dimana user terdaftar
            sql = String.format("SELECT id_Room,nama_Room,id_RM FROM roomList");
            
            rs = stmt.executeQuery(sql);
            List<Room> listRoom = new ArrayList<>();
            
            int id_Room, id_RM;
            String nama_Room;
            while(rs.next()){
                id_Room = rs.getInt(1);
                nama_Room = rs.getString(2);
                id_RM = rs.getInt(3);
                listRoom.add(new Room(id_Room,nama_Room,id_RM));

            }
//         
            return listRoom;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Model_User_Account> getUsersInRoom(int idx){    
        String sql;
        Statement stmt;
        ResultSet rs;
        
        try {
            List<Model_User_Account> listUsers = new ArrayList<>();
            stmt = conn.createStatement();

            //Ambil daftar user dari room yang dipilih
            sql = String.format("""
                                 SELECT UserList.id_User,username 
                                  FROM UserList
                                  Join userToRoom ON UserList.id_User = userToRoom.id_User
                                  JOIN roomList ON userToRoom.id_Room = roomList.id_Room
                                  WHERE userToRoom.id_Room=%d""", idx);
            rs = stmt.executeQuery(sql);
            
            String username;
            int id_User;
            while(rs.next()){
                id_User = rs.getInt(1);
                username = rs.getString(2);
                
                listUsers.add(new Model_User_Account(id_User,username));
            }
            
//            sql = String.format("SELECT id_Room FROM userToRoom WHERE id_User = %d ",idUser);


            return listUsers;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    
}
