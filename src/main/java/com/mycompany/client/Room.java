package com.mycompany.client;

import com.corundumstudio.socketio.SocketIOClient;
import java.util.ArrayList;
import java.util.List;
import model.Model_Client;


public class Room {
  
    private List<Model_Client> clients;
    int id_Room;
    String nama;
    int id_RM;
    
    
    public Room(int id_Room, String nama, int id_RM) {
        this.id_Room = id_Room;
        this.nama = nama;
        this.id_RM = id_RM;
        this.clients = new ArrayList<>();
         
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
    public List<Model_Client> getClients() {
        return clients;
    }

    
    public void addClient(Model_Client client) {
        clients.add(client);
    }

    public void removeClient(Model_Client client) {
        clients.remove(client);
    }
    
    public Model_Client getClientBySocket(SocketIOClient sioc){
        Model_Client client=null;
        for(Model_Client cl : clients){
            if(cl.getClient().equals(sioc)){
                client = cl;
            }
        }
        return client;
    }
}
