/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.htw_berlin.rz.room_finder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Kai
 */
@XmlRootElement
public class Room {
    
    public final String roomAbbr;
    
    public Room() {
        this.roomAbbr = "default";
    }
    
    public Room(String abbr) {
        this.roomAbbr = abbr;
    }
}
