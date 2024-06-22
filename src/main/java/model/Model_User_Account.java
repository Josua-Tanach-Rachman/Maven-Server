/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import com.corundumstudio.socketio.SocketIOClient;

/**
 *
 * @author User
 */
public class Model_User_Account {

    private String nama;
    private int userId;
    private int curRoom;
    
    public Model_User_Account(){
    }
    
    public Model_User_Account(int userId, String nama){
        this.nama = nama;
        this.userId = userId;
        this.curRoom=-1;
    }

    public String getNama() {
        return nama;
    }

    public int getUserId() {
        return userId;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCurRoom() {
        return curRoom;
    }

    public void setCurRoom(int curRoom) {
        this.curRoom = curRoom;
    }
    
    
    
}
