/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htw_berlin.rz.room_finder;

import de.htw_berlin.rz.room_finder.data.InputRoom;
import de.htw_berlin.rz.room_finder.data.OutputRoom;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods to convert InputRooms and OutputRooms into each other.
 *
 * @author Kai Puth
 */
public class RoomConverter {

    private static final String DELIMITER = " ";
    private static final String PATTERN = "\\(\\w+\\)";
    private static final String EMPTY_STRING = "";

    private static final String ROOM_TYPE_WITH_COMPUTERS = "IT-Labor";

    private static final int FLOOR_DISTANCE = 1000;
    private static final int BUILDING_DISTANCE = 100 * FLOOR_DISTANCE;
    private static final int CAMPUS_DISTANCE = 10 * BUILDING_DISTANCE;

    /**
     * Converts a List of InputRooms into a List of OutputRooms. Also
     * approximated the distance from each room to the user.
     */
    public List<OutputRoom> process(List<InputRoom> in,
            String userLocation) {

        List<OutputRoom> out = new ArrayList(in.size());
        Room roomNearUser, currentRoom;
        int distance;

        roomNearUser = parse(userLocation);
        boolean isSameCampus, isSameBuilding, isSameFloor, isSameRoom,
                hasComputers;
        for (InputRoom inRoom : in) {
            currentRoom = parse(inRoom.abbreviation);

            // calculate distance between user and room
            distance = 0;

            isSameCampus = roomNearUser.campus.equals(currentRoom.campus);
            isSameBuilding = isSameCampus
                    && roomNearUser.building.equals(currentRoom.building);
            isSameFloor = isSameBuilding
                    && roomNearUser.floor == currentRoom.floor;
            isSameRoom = isSameFloor
                    && roomNearUser.nr == currentRoom.nr;
            hasComputers = inRoom.roomType.equals(ROOM_TYPE_WITH_COMPUTERS);

            if (!isSameRoom) {
                if (!isSameFloor) {
                    distance += roomNearUser.nr + currentRoom.nr;
                    if (!isSameBuilding) {
                        distance += (roomNearUser.floor + currentRoom.floor)
                                * FLOOR_DISTANCE;
                        distance += Math.abs(roomNearUser.building.charAt(0) - 
                                currentRoom.building.charAt(0))
                                * BUILDING_DISTANCE;
                        if (!isSameCampus) {
                            distance += CAMPUS_DISTANCE;
                        }
                    } else { // same building
                        distance += Math.abs(roomNearUser.floor - currentRoom.floor)
                                * FLOOR_DISTANCE;
                    }
                } else { // same floor
                    distance += Math.abs(roomNearUser.nr - currentRoom.nr);
                }
            }

            out.add(new OutputRoom(inRoom.abbreviation, distance,
                    inRoom.nrOfSeats, hasComputers, inRoom.hasProjector));
        }

        return out;
    }

    /**
     * Parses a room abbreviation into a Room object.
     */
    private Room parse(String abbr) {
        Room room = new Room();

        abbr = abbr.replaceAll(PATTERN, EMPTY_STRING).trim();
        String[] tokens = abbr.split(DELIMITER);
        room.campus = tokens[0];
        if (tokens.length == 3) {
            room.building = tokens[1];
        }
        if (tokens.length == 4) {
            room.building = tokens[1] + " " + tokens[2];
        }
        int tmp = Integer.parseInt(tokens[tokens.length - 1]);
        int basis;
        if (tmp < 999) {
            basis = 100;
        } else {
            basis = 1000;
        }
        room.floor = tmp / basis;
        room.nr = tmp - basis * room.floor;

        return room;
    }

    /**
     * Checks whether an room has a specific equipment
     */
//    private boolean hasEquipment(InputRoom room, String name) {
//        boolean hasEquipment = false;
//        
//        for (Equipment equipment : room.equipment) {
//            if (equipment.name.equals(name) && equipment.count > 0) {
//                hasEquipment = true;
//                break;
//            }
//        }
//        
//        return hasEquipment;
//    }
    /**
     * Internal representation of a room required to calculate the distance
     * between an InputRoom and a user.
     */
    private class Room {

        public String campus;
        public String building;
        public int floor;
        public int nr;
        public boolean hasProjector;
    }

}
