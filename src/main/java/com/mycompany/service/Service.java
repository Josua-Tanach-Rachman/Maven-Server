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
import com.mycompany.connection.DataBaseConnection;
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
    
    public static Service getInstance(JTextArea txtArea) {
        if(instance == null) {
            instance = new Service(txtArea);
        }
        return instance;
    }

    private Service(JTextArea txtArea) {
            this.txtArea = txtArea;
    }
     
    public void startServer(){
         Configuration config = new Configuration();
         config.setPort(PORT_NUMBER);
         
         server = new SocketIOServer(config);
         
         
         server.addConnectListener(new ConnectListener(){
             @Override
             public void onConnect(SocketIOClient sioc) {
                 txtArea.append("Client has connected succesfully\n");
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
                    txtArea.append("User Registered, Username : "+ t.getUsername() +", Password : "+ t.getPass()+"\n");
             }
         });
         
         server.start();
         
         txtArea.append("Connected to Server Port :"+ PORT_NUMBER +"\n");
         
    }
    
}
