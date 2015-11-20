/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htw_berlin.rz.room_finder;

import de.htw_berlin.rz.room_finder.data.OutputRoom;
import java.util.Comparator;

/**
 * A comparator used to order OutputRooms by their distance to a user.
 * 
 * @author Kai Puth
 */
public class RoomComparator implements Comparator {
    
    @Override
    public int compare(Object o1, Object o2) {

        OutputRoom room1 = (OutputRoom) o1;
        OutputRoom room2 = (OutputRoom) o2;
        
        return Integer.compare(room1.distanceToUser, room2.distanceToUser);
    }
}
