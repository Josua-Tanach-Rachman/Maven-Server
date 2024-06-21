    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author User
 */
public class Model_Receive_Message {
    
    private String text;
    private int fromIdUser;
    private int id_Room;
    
    public Model_Receive_Message(int fromIdUser,int id_Room, String text){
        this.fromIdUser = fromIdUser;
        this.id_Room = id_Room;
        this.text = text;
    }
    
    public Model_Receive_Message(Object json){
        JSONObject obj = (JSONObject)json;
        this.fromIdUser = obj.getInt("fromIdUser");
        this.id_Room = obj.getInt("id_Room");
        this.text = obj.getString("text");
        
    }
    
    public JSONObject toJSONObject(){
        JSONObject json = new JSONObject();
        json.put("fromIdUser", fromIdUser);
        json.put("id_Room",id_Room);
        json.put("text",text);
        return json;
    }

    public int getFromIdUser() {
        return fromIdUser;
    }

    public int getId_Room() {
        return id_Room;
    }

    public String getText() {
        return text;
    }

    public void setFromIdUser(int fromIdUser) {
        this.fromIdUser = fromIdUser;
    }

    public void setId_Room(int id_Room) {
        this.id_Room = id_Room;
    }

    public void setText(String text) {
        this.text = text;
    }
    
}
