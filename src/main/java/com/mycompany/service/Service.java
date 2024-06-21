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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JTextArea;
import model.Model_Data;

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
   
    
    private List<Room> listRoom;
    private List<SocketIOClient> clients;
    
    public static Service getInstance(JTextArea txtArea, JTextArea clientArea) {
        if(instance == null) {
            
            instance = new Service(txtArea, clientArea);
        }
        return instance;
    }

    private Service(JTextArea txtArea, JTextArea clientArea) {
            this.txtArea = txtArea;
            this.clientArea = clientArea;
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
                 clients.add(sioc);
                 
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
                    boolean successLogin = sU.login(t);
                    ar.sendAckData(successLogin);
                    txtArea.append("User Login, Username : "+ t.getUsername() +", Password : "+ t.getPass()+"\n");
             }
         });
         
         //Server add listener buat room
         
         server.addEventListener("getRoom", Model_Data.class, new DataListener<Model_Data>() {
            @Override
            public synchronized void onData(SocketIOClient sioc, Model_Data t, AckRequest ar) throws Exception {
                ServiceRoom sR = ServiceRoom.getInstance();

                List<Room> result = sR.getRoom(t);
//                ar.sendAckData(result);
                sioc.sendEvent("getRoom", result.toArray());
//                txtArea.append("User : "+t.getUsername()+", Room : "+result.toString()+"\n");
            }
         });
         
         //Server listener untuk pengambilan user dalam suatu room
         //Perlu ditentukan apakah privilege lewat event listener atau 
         server.addEventListener("getUserInRoom", Model_Data.class, new DataListener<Model_Data>() {
             @Override
             public synchronized void onData(SocketIOClient sioc, Model_Data t, AckRequest ar) throws Exception {
                 
             }
         });
         
//         server.addEventListener("broadcast", eventClass, listener);
         
         server.start();
         
         txtArea.append("Connected to Server Port :"+ PORT_NUMBER +"\n");
         
    }
    
    public void broadcast(String message){
        //Client emit ke server, server menerima "broadcast" dan sendEvent/send ke masing" sioc 
        
        //send Event ke masing" client
        //Broadcast ke ruangan yang dituju?
        
        for(SocketIOClient sioc : clients){
            sioc.sendEvent("broadcast", message);
        }
    }
}
