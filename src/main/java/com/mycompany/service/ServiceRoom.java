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
import java.util.List;
import model.Model_Data;

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
            sql = String.format("SELECT id_User FROM UserList WHERE username LIKE '%s'",user);
            System.out.println(user);

            rs = stmt.executeQuery(sql);
            rs.next();
            int idUser = rs.getInt(1);
            
            //Ambil Id_Room dimana user terdaftar
//            sql = String.format("SELECT id_Room FROM userToRoom WHERE id_User = %d ",idUser);
            sql = String.format("SELECT id_Room,nama_Room,id_RM FROM roomList WHERE id_User = %d ",idUser);
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
    
    public List<Integer> getUsersInRoom(int idx){    
        String sql;
        Statement stmt;
        ResultSet rs;
        
        try {
            List<Integer> listRoom = new ArrayList<>();
            stmt = conn.createStatement();
//            sql = String.format("SELECT id_User FROM UserList WHERE username LIKE '%s'",user);
            
            //Ambil daftar user dari room yang dipilih
            sql = String.format("SELECT id_User FROM userToRoom WHERE id_Room = %d", idx);
            rs = stmt.executeQuery(sql);
            
            while(rs.next()){
                int idUser = rs.getInt(1);
                listRoom.add(idUser);
            }
            
//            sql = String.format("SELECT id_Room FROM userToRoom WHERE id_User = %d ",idUser);


            return listRoom;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    
}
