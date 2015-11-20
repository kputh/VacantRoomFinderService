/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htw_berlin.rz.room_finder;

import de.htw_berlin.rz.room_finder.data.RoomList;
import de.htw_berlin.rz.room_finder.data.OutputRoom;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.bind.JAXB;
import javax.xml.ws.WebServiceException;

/**
 * REST Web Service
 * This ressource can provide a list of vacant rooms ordered ascending by their
 * distance to a given room abbreviation.
 *
 * @author Kai Puth
 */
@Path("vacant_rooms")
public class RoomFinderResource {

    private static final String START_TAG = "&lt;roomrequest&gt;";
    private static final String END_TAG = "&lt;/roomrequest&gt;";

    // validation patterns
    private static final String INPUT_PATTER_STRING
            = "(TA|WH) [A-Z]{1,3} ([A-Z0-9]+ )?\\d{3,4}[A-Z]?";
    private static final Pattern INPUT_PATTERN
            = Pattern.compile(INPUT_PATTER_STRING);

    // default values for requests
    private static final long DEFAULT_DURATION = 5400000;
    private static final String DEFAULT_SEATING = "3";
    private static final String DEFAULT_PROJECTORS = "false";
    private static final String DEFAULT_COMPUTERS = "false";

    // error messages
    private static final String USER_LOCATION_NULL_ERROR_MESSAGE
            = "Parameter \"near\" must not be null.";
    private static final String INPUT_FORMAT_ERROR_MESSAGE
            = "Parameter \"near\" does not match expected format.";
    private static final String TIME_ERROR_MESSAGE
            = "Start time is before end time.";
    private static final String SEATING_ERROR_MESSAGE
            = "Less than one seat requested.";
    private static final String REMOTE_SERVICE_ERROR_MESSAGE
            = "Response code of remote service %s: %s.";

    private static final int RESULT_LIMIT = 25;
    private static final String SERVICE_ADDRESS
            = "http://ananke.rz.htw-berlin.de:14080/qisserver/services/"
            + "dbinterface";

    private static final String BODY_TEMPLATE
            = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<SOAP-ENV:Envelope\n"
            + " xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"
            + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + " xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"\n"
            + " SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n"
            + "<SOAP-ENV:Body>\n"
            + "<getDataXML>\n"
            + "<arg0 xsi:type=\"xsd:string\">\n"
            + "&lt;SOAPDataService&gt;\n"
            + "	&lt;general&gt;\n"
            + "	 &lt;object&gt;roomrequest&lt;/object&gt;\n"
            + "	 &lt;group&gt;&lt;/group&gt;\n"
            + "	&lt;/general&gt;\n"
            + "	&lt;condition&gt;\n"
            + "	&lt;campus&gt;%s&lt;/campus&gt;\n" // Campus
            + "	&lt;seat&gt;%d&lt;/seat&gt;\n" // Sitzplätze
            + "	&lt;fom&gt;%tR&lt;/fom&gt;\n" // Startzeit
            + "	&lt;to&gt;%tR&lt;/to&gt;\n" // Endzeit
            + "	&lt;/condition&gt;\n"
            + "	&lt;user-auth&gt;\n"
            + "		&lt;username&gt;&lt;/username&gt;\n"
            + "		&lt;password&gt;&lt;/password&gt;\n"
            + "	&lt;/user-auth&gt;\n"
            + "	&lt;search&gt;\n"
            + "	&lt;/search&gt;\n"
            + "	&lt;limit&gt;%d&lt;/limit&gt;\n" // Maximale Ergebniszahl
            + "	&lt;offset&gt;0&lt;/offset&gt;\n"
            + "	&lt;language&gt;de-DE&lt;/language&gt;\n"
            + "&lt;/SOAPDataService&gt;\n"
            + "				</arg0></getDataXML>\n"
            + "</SOAP-ENV:Body>\n"
            + "</SOAP-ENV:Envelope>";

    private final RoomConverter converter;
    private final Comparator<OutputRoom> comparator;

    /**
     * Creates a new instance of OutputRoomFinderResource
     */
    public RoomFinderResource() {
        this.converter = new RoomConverter();
        this.comparator = new RoomComparator();
    }

    /**
     * Handles a request for vacant rooms. The request is an XML document while
     * the answer is JSON.
     * 
     * @param roomAbbr
     * @param from
     * @param to
     * @param seatingFor
     * @param withProjector
     * @param withComputers
     * @return
     * @throws IOException 
     */
    @GET
    @Produces("application/json")
    public List<OutputRoom> findVacantRooms(
            @QueryParam("near") String roomAbbr,
            @QueryParam("from") Date from,
            @QueryParam("to") Date to,
            @DefaultValue(DEFAULT_SEATING) @QueryParam("seatingFor") int seatingFor,
            @DefaultValue(DEFAULT_PROJECTORS) @QueryParam("withProjector") boolean withProjector,
            @DefaultValue(DEFAULT_COMPUTERS) @QueryParam("withComputers") boolean withComputers)
            throws IOException {

        // validate input
        if (roomAbbr == null) {
            throw new IllegalArgumentException(USER_LOCATION_NULL_ERROR_MESSAGE);
        }
        roomAbbr = roomAbbr.toUpperCase();
        if (!INPUT_PATTERN.matcher(roomAbbr).matches()) {
            throw new PatternSyntaxException(
                    INPUT_FORMAT_ERROR_MESSAGE, INPUT_PATTER_STRING, -1);
        }
        if (from != null && to != null && from.getTime() > to.getTime()) {
            throw new IllegalArgumentException(TIME_ERROR_MESSAGE);
        }
        if (seatingFor < 1) {
            throw new IllegalArgumentException(SEATING_ERROR_MESSAGE);
        }

        // insert missing default values
        if (from == null) {
            from = new Date();
        }
        if (to == null) {
            to = new Date(from.getTime() + DEFAULT_DURATION);
        }

        // prepare request to underlying webservice
        String campus = roomAbbr.substring(0, 2);
        String body = String.format(BODY_TEMPLATE, campus, seatingFor, from, to,
                RESULT_LIMIT);
        StringBuilder strBuilder;

        // prepare connection
        URL url = new URL(SERVICE_ADDRESS);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestProperty("Content-Type", "text/html");
        connection.setRequestProperty("Content-Length",
                String.valueOf(body.length()));
        connection.setRequestProperty("SOAPAction", "\"\"");

        // send request
        OutputStreamWriter writer = new OutputStreamWriter(
                connection.getOutputStream());
        writer.write(body);
        writer.flush();
        writer.close();

        // check response code
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new WebServiceException(String.format(
                    REMOTE_SERVICE_ERROR_MESSAGE,
                    connection.getResponseCode(),
                    connection.getResponseMessage()));
        }

        // read reply
        strBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            strBuilder.append(line);
        }
        reader.close();
        connection.disconnect();

        // get properly formated response document
        int startIndex = strBuilder.indexOf(START_TAG);
        int endIndex = strBuilder.lastIndexOf(END_TAG) + END_TAG.length();
        String replyBody = strBuilder.substring(startIndex, endIndex);

        replyBody = replyBody.replaceAll("&lt;", "<");
        replyBody = replyBody.replaceAll("&gt;", ">");

        // parse response
        RoomList requestNode = JAXB.unmarshal(new StringReader(replyBody),
                RoomList.class);

        // convert InputRooms into OutputRooms
        List<OutputRoom> outRooms = this.converter.process(requestNode.rooms,
                roomAbbr);

        // filter rooms and order by distance to user location
        int requiredComputers = withComputers ? seatingFor : 0;
        outRooms = filterBy(requiredComputers, withProjector, outRooms);
        Collections.sort(outRooms, this.comparator);

        return outRooms;
    }

    /**
     * Filter rooms by requested equipment.
     * 
     * @param requiredComputers
     * @param projectorRequired
     * @param list
     * @return 
     */
    private List<OutputRoom> filterBy(int requiredComputers, boolean projectorRequired,
            List<OutputRoom> list) {

        List<OutputRoom> filteredList = new ArrayList<OutputRoom>(list.size());

        for (OutputRoom room : list) {
            if (projectorRequired && !room.hasProjector) {
                continue;
            }
            if (requiredComputers > 0 && (!room.hasComputerWorkplaces ||
                    room.seatingFor < requiredComputers)) {
                continue;
            }
            filteredList.add(room);
        }

        return filteredList;
    }

}
