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
public class Model_Message {
    private String message;
    private boolean result;
    
    public Model_Message(){
    }
    
    public Model_Message(String message, boolean result) {
        this.message = message;
        this.result = result;
    }
    
    public Model_Message(Object json){
        JSONObject obj = (JSONObject)json;
        try{
            message = obj.getString("message");
            result = obj.getBoolean("result");
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public String getMessage() {
        return message;
    }
    
   public boolean getResult(){
       return result;
   }

   
    public void setMessage(String message) {
        this.message = message;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
   
   public JSONObject toJSONObject(){
        try {
            JSONObject json = new JSONObject();
            json.put("message", message);
            json.put("result", result);
         
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
