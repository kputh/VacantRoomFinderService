/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htw_berlin.rz.room_finder.data;

import java.util.List;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Room data, as returned by the underlying webservice.
 * 
 * @author Kai Puth
 */
@XmlRootElement(name = "room")
@XmlAccessorType(XmlAccessType.FIELD)
public class InputRoom {
    
    @XmlElement
    int roomId;

    @XmlElement
    public String abbreviation;

    @XmlElement
    public int nrOfSeats;

    @XmlElement
    public String buildingName;
    
    @XmlElement
    public int floor;
    
    @XmlElement
    @XmlJavaTypeAdapter(BooleanAdapter.class)
    public Boolean hasProjector;
    
    @XmlElement(name = "roomtype")
    public String roomType;
    
    @XmlElement
    public List<Equipment> equipment;

    public InputRoom() {}
    
    public InputRoom(String abbreviation) {
        this.abbreviation = abbreviation;
    }
    
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(String.format("room abbreviation: %s, building name: %s, "
                + "floor: %s, room id: %s, hasProjector: %s\n",
                abbreviation, buildingName, floor, roomId, hasProjector));
        for (Equipment item : equipment) {
            str.append(item.toString());
        }
        return str.toString();
    }
}
