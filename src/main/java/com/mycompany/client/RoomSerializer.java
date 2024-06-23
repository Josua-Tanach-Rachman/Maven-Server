/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RoomSerializer {
    public static String toJSON(Room room) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(room);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
