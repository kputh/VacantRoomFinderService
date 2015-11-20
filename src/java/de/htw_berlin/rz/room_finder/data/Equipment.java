/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htw_berlin.rz.room_finder.data;

import javax.xml.bind.annotation.*;

/**
 * Equipment data, as returned by the underlying webservice.
 * 
 * @author Kai Puth
 */
@XmlRootElement(name = "room")
@XmlAccessorType(XmlAccessType.FIELD)
public class Equipment {

    @XmlElement
    public String name;

    @XmlElement
    public int count;

    public Equipment() {}
    
    @Override
    public String toString() {
        return String.format("equipment name: %s\n", name);
    }
}
