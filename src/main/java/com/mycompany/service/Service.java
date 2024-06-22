/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.service;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.protocol.Packet;
import com.mycompany.client.Room;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JTextArea;
import model.Model_Client;
import model.Model_Data;
import model.Model_Receive_Message;
import model.Model_Room_Setter;
import model.Model_Send_Message;
import model.Model_User_Account;

/**
 *
 * @author User
 */
public class Service {
    private static Service instance;
    private SocketIOServer server;
    private static final int PORT_NUMBER = 5556;
    private JTextArea txtArea;
    private JTextArea clientArea;
    private Map<String,Integer> currentRoom;
   
    
    private List<Room> listRoom;
    private int curRoomIdx;
    
    
    public static Service getInstance(JTextArea txtArea, JTextArea clientArea) {
        if(instance == null) {
            
            instance = new Service(txtArea, clientArea);
        }
        return instance;
    }

    private Service(JTextArea txtArea, JTextArea clientArea) {
            this.txtArea = txtArea;
            this.clientArea = clientArea;
            this.curRoomIdx=-1;
//            clients = new ArrayList<>();
            this.currentRoom = new HashMap<>();
            initRoom();
    }
        
    private void initRoom(){
        ServiceRoom sr = ServiceRoom.getInstance();
        this.listRoom = sr.getAllRoom();
        
      
    }
     
    public void startServer(){
         Configuration config = new Configuration();
         config.setPort(PORT_NUMBER);
         
         server = new SocketIOServer(config);
        
         
         server.addConnectListener(new ConnectListener(){
             @Override
             public void onConnect(SocketIOClient sioc) {
                 txtArea.append("Client has connected succesfully\n");
                 clientArea.append(sioc.getRemoteAddress() +"\n");
             }
             
         });
         
         server.addEventListener("register", Model_Data.class, new DataListener<Model_Data>() {
             @Override
             public void onData(SocketIOClient sioc, Model_Data t, AckRequest ar) throws Exception {
                    ServiceUser sU = ServiceUser.getInstance();
                    boolean successRegister = sU.register(t);
                    ar.sendAckData(successRegister);
                    txtArea.append("User Registered, Username : "+ t.getUsername() +", Password : "+ t.getPass()+"\n");
             }
         });
         
         server.addEventListener("login",  Model_Data.class, new DataListener<Model_Data>() {
             @Override
             public void onData(SocketIOClient sioc, Model_Data t, AckRequest ar) throws Exception {
                   
                    ServiceUser sU = ServiceUser.getInstance();
                    Model_User_Account acc = sU.login(t);
                    
                    if(acc!=null){
                        ar.sendAckData(true, acc);
                        addClient(sioc, acc);
                       
                    }else{
                        ar.sendAckData(false);
                    }
                    
                    txtArea.append("User Login, Username : "+ t.getUsername() +", Password : "+ t.getPass()+"\n");
             }
         });
         
         //Server add listener buat room
         
         server.addEventListener("getRoomInServer", Model_Data.class, new DataListener<Model_Data>() {
            @Override
            public synchronized void onData(SocketIOClient sioc, Model_Data t, AckRequest ar) throws Exception {
                
                ServiceRoom sR = ServiceRoom.getInstance();

                List<Room> result = sR.getRoom(t);
                sioc.sendEvent("getRoom", result.toArray());

            }
         });
         
         //Server listener untuk pengambilan user dalam suatu room
         //Perlu ditentukan apakah privilege lewat event listener atau 
         server.addEventListener("getUsersInRoom", Integer.class, new DataListener<Integer>() {
             @Override
             public synchronized void onData(SocketIOClient sioc, Integer idx, AckRequest ar) throws Exception {
                 //Idx mulai dari 1
                 
                 //Ambil ke SQL perlu dari 1
                List<Model_User_Account> result = ServiceRoom.getInstance().getUsersInRoom(idx);
                sioc.sendEvent("getUsersInRoomFromServer", result.toArray());
                
                //Ambil room dari 0
                Room selectedRoom = listRoom.get(idx-1);
                sioc.sendEvent("changeTitle", selectedRoom.getNama());  
                
                //Idx perlu dikurangi untuk menyesuaikan dari 0
                idx--;
                Model_Client client = selectedRoom.getClientBySocket(sioc);
                setCurrentRoomForClient(client, idx);
                
             }
         });
         
        server.addEventListener("send_to_users", Model_Send_Message.class, new DataListener<Model_Send_Message>(){
            @Override
            public void onData(SocketIOClient sioc, Model_Send_Message message, AckRequest ar) throws Exception {
                broadcastToRoom(message);
            }
        });
        

         
         server.start();
         
         txtArea.append("Connected to Server Port :"+ PORT_NUMBER +"\n");
         
    }
    public void addClient(SocketIOClient socket, Model_User_Account acc){
        //TODO : CEK APAKAH SEMUA ADDED CLIENTNYA NYA BENER ATAU TIDAK
        
        //User Id mulai dari 1
        int user_Id = acc.getUserId();
        
        //Sql perlu dari 1
        List<Integer> roomList = ServiceRoom.getInstance().locateRoomForUser(user_Id);

        //Isi keluaran dari SQL mulai dari 0 sehingga perlu dikurangi 1
        for(int idxRoom: roomList){
            listRoom.get(idxRoom-1).addClient(new Model_Client(socket,acc));
        }
    }
    
    public void removeClientFromRoom(int idxRoom, Model_Client client){
//        int user_Id = acc.getUserId();    
        int user_Id = client.getUser().getUserId();
        List<Integer> roomList = ServiceRoom.getInstance().locateRoomForUser(user_Id);
        
        for(int idx: roomList){
            if(idx!=idxRoom){
               
                listRoom.get(idx).removeClient(client);
            }
            
        }
    }
    
    public void addClientToRoom(int idxRoom, Model_Client client){
//        int user_Id = acc.getUserId();    
        int user_Id = client.getUser().getUserId();
        List<Integer> roomList = ServiceRoom.getInstance().locateRoomForUser(user_Id);
        
        for(int idx: roomList){
            if(idx==idxRoom){       
                listRoom.get(idx).addClient(client);
            }
            
        }
    }
    public void broadcastToRoom(Model_Send_Message message){
        //Ambil id room dari pengirim , mulai dari 1
        //Perlu dikurangi 1 untuk menyesuaikan java
        int roomIdx = message.getId_Room()-1;
        
        
        List<Model_Client> clients = listRoom.get(roomIdx).getClients();
        
        
         for(Model_Client client : clients){
//                System.out.println("Room Id pengirim: "+ roomIdx+" ,room posisi client terkini: "+currentRoom.get(client.getUser().getNama()));
                
                if(client.getUser().getUserId()!=message.getFromIdUser() && (currentRoom.get(client.getUser().getNama())!=null &&currentRoom.get(client.getUser().getNama()) == roomIdx) ){
                    client.getClient().sendEvent("broadcast", new Model_Receive_Message(message.getFromIdUser(), message.getId_Room(), message.getText()));
            }
        }  
    }
    
    public void setCurrentRoomForClient(Model_Client client, Integer room) {
        
        this.currentRoom.put(client.getUser().getNama(),room);

        
    }
}
