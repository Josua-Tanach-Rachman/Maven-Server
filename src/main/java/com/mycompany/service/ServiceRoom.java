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
import model.Model_Message;
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
                                "  WHERE username LIKE '%s'",user);

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
    
    public List<Integer> locateRoomForUser(int user_Id){    
        String sql;
        Statement stmt;
        ResultSet rs;
        
        try {
            List<Integer> listUsers = new ArrayList<>();
            stmt = conn.createStatement();

            //Ambil daftar user dari room yang dipilih
            sql = String.format("""
                                 SELECT userToRoom.id_Room
                                  FROM UserList
                                  JOIN userToRoom ON UserList.id_User = userToRoom.id_Room
                                  WHERE userToRoom.id_User = %d""", user_Id);
            rs = stmt.executeQuery(sql);
            
     
            int id_Room;
            while(rs.next()){
                id_Room = rs.getInt(1);
                listUsers.add(id_Room);
            }



            return listUsers;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Model_Message createRoom(Room room){
        String nama = room.getNama();
        int id_RM = room.getId_RM();
        
        String sql;
        Statement stmt;
        ResultSet rs;
        Model_Message ms=null;
        try {
            stmt = conn.createStatement();
            
            if(!checkRoomAvailability(nama)){
                
                sql = String.format("INSERT INTO roomList (nama_Room,id_RM) VALUES('%s',%d)", nama,id_RM);
                boolean result = stmt.execute(sql);

                sql = String.format("SELECT id_Room\n" +
                                    "  FROM roomList\n" +
                                    "  where nama_Room LIKE '%s'", nama);
                rs = stmt.executeQuery(sql);
                rs.next();
                int idRoom = rs.getInt(1);


                sql = String.format("INSERT INTO userToRoom (id_Room,id_User) VALUES(%d,%d)", idRoom,id_RM);
                stmt.execute(sql);
                
                ms = new Model_Message("Room berhasil dibuat",true);
            }else {
                ms = new Model_Message("Room sudah ada",false);
            }
            
            System.out.println(ms.getMessage());
            return ms;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public int getRoomByName(String nama){
        String sql;
        Statement stmt;
        ResultSet rs;
        Model_Message ms;
        try {
            stmt = conn.createStatement();
            
            sql = String.format("SELECT id_Room\n" +
                                "  FROM roomList\n" +
                                "  where nama_Room LIKE '%s'", nama);
            rs = stmt.executeQuery(sql);
            rs.next();
            int idRoom = rs.getInt(1);
            
           
            
            
            
            return idRoom;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    private boolean checkRoomAvailability(String nama){
        String sql;
        Statement stmt;
        ResultSet rs;
        Model_Message ms;
        String namaRoom = "";
        try {
            stmt = conn.createStatement();
            
            sql = String.format("SELECT nama_Room\n" +
                                "  FROM roomList\n" +
                                "  where nama_Room LIKE '%s'", nama);
            rs = stmt.executeQuery(sql);
            
            if(rs.next()){
                namaRoom = rs.getString(1);
            }
            
            if(!namaRoom.equals("")){
                return true;
            }
            return false;
            
       } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean joinUserToRoom(int id_Room, int id_User){
        String sql;
        Statement stmt;
        ResultSet rs;
        
        int idx = -1;
        try {
            stmt = conn.createStatement();
            
            sql = String.format("SELECT id_Room\n" +
                                "from userToRoom\n" +
                                "WHERE id_Room=%d AND id_User=%d", id_Room,id_User);
            rs = stmt.executeQuery(sql);
            
            if(rs.next()){
                idx = rs.getInt(1);
            }
            
            //Belum ada di room
            if(idx==-1){
                sql = String.format("insert userToRoom (id_Room,id_User) VALUES (%d,%d)", id_Room,id_User);
                stmt.execute(sql);
                
                return true;
            }else{      //Sudah ada di room ,jangan di join ke room
                
                return false;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
