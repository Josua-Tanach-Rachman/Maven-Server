package com.mycompany.client;

import java.util.ArrayList;
import java.util.List;

public class Room {
    List<Integer> Users;
    int id_Room;
    String nama;
    int id_RM;
    
    public Room(int id_Room, String nama, int id_RM) {
        this.id_Room = id_Room;
        this.nama = nama;
        this.id_RM = id_RM;
        this.Users= new ArrayList<>();
        
    }

    public synchronized void addUser(int id_User){
        Users.add(id_User);
    }
    
    public synchronized void  removeUser(int id_User){
        Users.remove(id_User);
    }
    
    public synchronized List<Integer> getUsers(){
        return Users;
    }

    public int getId_RM() {
        return id_RM;
    }

    public int getId_Room() {
        return id_Room;
    }

    public String getNama() {
        return nama;
    }

    public void setId_RM(int id_RM) {
        this.id_RM = id_RM;
    }

    public void setId_Room(int id_Room) {
        this.id_Room = id_Room;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
    
    
}
