package com.mycompany.client;

import com.corundumstudio.socketio.SocketIOClient;
import java.util.ArrayList;
import java.util.List;
import model.Model_Client;
import org.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Room {
  
    @JsonProperty("id_Room")
    private int id_Room;
    @JsonProperty("nama")
    private String nama;
    @JsonProperty("id_RM")
    private int id_RM;
    @JsonProperty("clients")
    private List<Model_Client> clients;
    
    public Room(){}
    
    public Room(@JsonProperty("id_Room") int id_Room, @JsonProperty("nama") String nama, @JsonProperty("id_RM") int id_RM) {
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
    
    public JSONObject toJSONObject(){
        try {
            JSONObject json = new JSONObject();
            json.put("id_Room", id_Room);
            json.put("nama", nama);
            json.put("id_RM",id_RM);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
