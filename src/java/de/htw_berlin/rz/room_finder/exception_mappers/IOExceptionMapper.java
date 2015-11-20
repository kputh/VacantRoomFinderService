/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htw_berlin.rz.room_finder.exception_mappers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception handler for IOExceptions. These exceptions indicate internal errors
 * and failing communitation with the underlying webservice. Relays the
 * exception to the client.
 * 
 * @author Kai Puth
 */
@Provider
public class IOExceptionMapper implements ExceptionMapper<IOException> {

    private static final String LOGGER_NAME = IOExceptionMapper.class.getName();

    @Override
    public Response toResponse(IOException exception) {

        // log exception
        Logger logger = Logger.getLogger(LOGGER_NAME);
        logger.log(Level.SEVERE, null, exception);

        // send response with debug information
        return Response.serverError().type(MediaType.TEXT_PLAIN_TYPE).entity(
                exception.getMessage()).build();
        
        // send response without debug information
        //return Response.serverError().build();
    }

}
