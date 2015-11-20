/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htw_berlin.rz.room_finder.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Room data, as required by the client.
 * 
 * @author Kai Puth
 */
@XmlRootElement
public class OutputRoom {

    public final String roomAbbr;

    @XmlTransient
    public final int distanceToUser;
    
    public final int seatingFor;

    @XmlTransient
    public final boolean hasComputerWorkplaces;

    @XmlTransient
    public final boolean hasProjector;

    public OutputRoom() {
        this.roomAbbr = "default";
        this.distanceToUser = -1;
        this.seatingFor = -1;
        this.hasComputerWorkplaces = false;
        this.hasProjector = false;
    }

    public OutputRoom(String abbr, int distance, int seatingFor,
            boolean hasComputerWorkplaces, boolean hasProjector) {

        this.roomAbbr = abbr.toUpperCase();
        this.distanceToUser = distance;
        this.seatingFor = seatingFor;
        this.hasComputerWorkplaces = hasComputerWorkplaces;
        this.hasProjector = hasProjector;
    }

}
