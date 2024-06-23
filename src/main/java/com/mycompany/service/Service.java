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
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.client.Room;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTextArea;
import model.Model_Client;
import model.Model_Data;
import model.Model_Message;
import model.Model_Receive_Message;
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
         
         server.addEventListener("getAllRoom", Integer.class, new DataListener<Integer>() {
             @Override
             public void onData(SocketIOClient sioc, Integer t, AckRequest ar) throws Exception {
                 List<Room> rooms = ServiceRoom.getInstance().getAllRoom();
                 sioc.sendEvent("receiveAllRoom", rooms.toArray());
             }
         });
         
         
         //Server listener untuk pengambilan user dalam suatu room
         //Perlu ditentukan apakah privilege lewat event listener atau 
         server.addEventListener("getUsersInRoom", Integer.class, new DataListener<Integer>() {
             @Override
             public synchronized void onData(SocketIOClient sioc, Integer idx, AckRequest ar) throws Exception {
               
              
                 //0
                List<Model_User_Account> result = ServiceRoom.getInstance().getUsersInRoom(idx); //0
                sioc.sendEvent("getUsersInRoomFromServer", result.toArray());
                
                //Ambil room dari 0
                Room selectedRoom = listRoom.get(idx);  //0
                sioc.sendEvent("changeTitle", selectedRoom.getNama());  
                
                //Idx perlu dikurangi untuk menyesuaikan dari 0
              
                //0
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
        
        server.addEventListener("createRoom", String.class, new DataListener<String>(){
            
             @Override
             public synchronized void onData(SocketIOClient sioc, String jsonRoom, AckRequest ar) throws Exception {
                 System.out.println("Received createRoom event with JSON: " + jsonRoom);
                 
                 System.out.println("awal server: "+System.nanoTime()*1000);

                 Room room=null;
                 ObjectMapper mapper = new ObjectMapper();
                 mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                 room = mapper.readValue(jsonRoom, Room.class);
                 
                 
                 System.out.println("Check sebelum panggil sql:"+room.getNama());
                 Model_Message ms = ServiceRoom.getInstance().createRoom(room);
                 System.out.println("Create room result: " + ms.getMessage());
                 
                 //Index room sql mulai dari 1, nanti harus dikurangin
                 
                 //Ga jadi, mulai dari 0
                 int idxRoom = ServiceRoom.getInstance().getRoomByName(room.getNama());
                 List<Model_Client> listOfClient = listRoom.get(idxRoom).getClients();
//                 System.out.println();
                 Model_Client cl = null;
                 for(Model_Client client: listOfClient){
                     System.out.print(client.getUser().getNama()+" ");
                     if(client.getUser().getUserId()==room.getId_RM()){
                         System.out.println("TES MASUK CLIENT DAPET");
                         cl = client;
                     }
                 }
                 System.out.println(cl.getUser().getNama());
                 
                 
                 addClientToRoom(idxRoom, cl);
                 System.out.println("Added client to room");
                 System.out.println("sebelum send ack: "+System.nanoTime()*1000);

                 ar.sendAckData(ms);
                 System.out.println("Sent acknowledgement data to client");
                 refreshForAll();
                 
                 sioc.notifyAll();
                 System.out.println("sesudah send ack: "+System.nanoTime()*1000);

             }
                
            });
        
        server.addEventListener("test", Integer.class, new DataListener<Integer>(){
             @Override
             public void onData(SocketIOClient sioc, Integer t, AckRequest ar) throws Exception {
                 for(Room room : listRoom){
                     System.out.println(room.getNama()+" ===== " + room.getId_Room());
                     for(Model_Client client : room.getClients()){
                         System.out.print(client.getUser().getNama()+" ");
                     } 
                     System.out.println("");
                 }
             }
            
        });
        
        server.addEventListener("join_Room", Integer.class, new DataListener<Integer>(){
             @Override
             public void onData(SocketIOClient sioc, Integer t, AckRequest ar) throws Exception {
                   Model_Client cl = null;
                   
                   for(Room room : listRoom){
                       for(Model_Client client: room.getClients()){
                           if(client.getClient().getRemoteAddress() == sioc.getRemoteAddress()){
                               cl = client;
                               break;
                           }
                       }
                   }
                   
//                   System.out.println(cl.getUser().getNama());
                    addClientToRoom(t, cl);
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
        System.out.println("=======================================");
        System.out.println(acc.getNama());
        //Update udah diubah jadi 0
        for(int idxRoom: roomList){
            System.out.print(idxRoom+" ");
            listRoom.get(idxRoom).addClient(new Model_Client(socket,acc));
        }
        System.out.println("\n=======================================");

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
        String roomName = "";
        
        for(int idx: roomList){
            if(idx==idxRoom){       
                listRoom.get(idx).addClient(client);
                
                roomName = listRoom.get(idx).getNama();
            }
            
        }
        
        boolean result = ServiceRoom.getInstance().joinUserToRoom(idxRoom, user_Id);
        System.out.println("apakah berhasil? "+result);
        
        Model_Send_Message ms = new Model_Send_Message(client.getUser().getUserId(), idxRoom, String.format("%s telah bergabung di room %s", client.getUser().getNama(), roomName));
        broadcastToRoom(ms);
        
    }
    public void broadcastToRoom(Model_Send_Message message){
      
        int roomIdx = message.getId_Room();
        System.out.println(roomIdx);
        
        List<Model_Client> clients = listRoom.get(roomIdx).getClients();
        
        
         for(Model_Client client : clients){
//                System.out.println("Room Id pengirim: "+ roomIdx+" ,room posisi client terkini: "+currentRoom.get(client.getUser().getNama()));
                System.out.println(client.getUser().getNama() +"lagi di posisi room : "+currentRoom.get(client.getUser().getNama()) +" ,dengan roomIdx "+roomIdx );
                
                if(client.getUser().getUserId()!=message.getFromIdUser() && (currentRoom.get(client.getUser().getNama())!=null &&currentRoom.get(client.getUser().getNama()) == roomIdx) ){
                    System.out.println("==============================dapet=============================");
                    client.getClient().sendEvent("broadcast", new Model_Receive_Message(message.getFromIdUser(), message.getId_Room(), message.getText()));
            }
        }  
    }
    
    public void refreshForAll() throws InterruptedException{
        try {
            wait(500);
        } catch (Exception e) {
        }
        
        for(Room room : listRoom){
            for(Model_Client client : room.getClients() ){
                //TODO : Nanti perlu di set di client
                System.out.println("refresh client:" +client.getUser().getNama()+" di room: "+ room.getId_Room());
                client.getClient().sendEvent("refreshForAll", true);
            }System.out.println();
            
        }
    }
    
    public void setCurrentRoomForClient(Model_Client client, Integer room) {
        
        this.currentRoom.put(client.getUser().getNama(),room);        
    }
}
