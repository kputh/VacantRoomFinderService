/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htw_berlin.rz.room_finder.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 * A list of vacant rooms, as returned by the underlying webservice.
 * 
 * @author Kai Puth
 */
@XmlRootElement(name = "roomrequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class RoomList {

    @XmlElement(name="room")
    public List<InputRoom> rooms;

    public RoomList() {
        rooms = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Room request containing\n");

        for (InputRoom room : rooms) {
            str.append(room.toString());
        }

        return str.toString();
    }
}
