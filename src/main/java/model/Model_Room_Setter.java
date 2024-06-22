/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import org.json.JSONObject;

/**
 *
 * @author User
 */
public class Model_Room_Setter {
    private int idSender;
    private int curRoom;
    
    
    public Model_Room_Setter(){
    }
    
    public Model_Room_Setter(int idSender, int curRoom){
        this.idSender = idSender;
        this.curRoom = curRoom;
    }
    
    public Model_Room_Setter(Object json){
        JSONObject obj = (JSONObject) json;
        this.idSender = obj.getInt("IdSender");
        this.curRoom = obj.getInt("sendToRoom");
    }

    public int getIdSender() {
        return idSender;
    }

   

    public int getCurRoom() {
        return curRoom;
    }

    public void setIdSender(int idSender) {
        this.idSender = idSender;
    }

    public void setCurRoom(int curRoom) {
        this.curRoom = curRoom;
    }

    public JSONObject toJSONObject(){
        JSONObject json = new JSONObject();
        json.put("IdSender",idSender);
        json.put("sendToRoom", curRoom);
        return json;
    }
    
    
}
