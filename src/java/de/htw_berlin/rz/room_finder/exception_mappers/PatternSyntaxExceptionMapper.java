/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.htw_berlin.rz.room_finder.exception_mappers;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception handler for faulty room abbreviations in requests. Relays the
 * exception to the client.
 * 
 * @author Kai Puth
 */
@Provider
public class PatternSyntaxExceptionMapper implements
        ExceptionMapper<PatternSyntaxException> {

    private static final String LOGGER_NAME =
            PatternSyntaxExceptionMapper.class.getName();

    @Override
    public Response toResponse(PatternSyntaxException exception) {
        
        // log exception
        Logger logger = Logger.getLogger(LOGGER_NAME);
        logger.log(Level.INFO, null, exception);
        
        // send response with debug information
        Response.ResponseBuilder responseBuilder = 
                Response.status(Response.Status.BAD_REQUEST);
        responseBuilder.type(MediaType.TEXT_PLAIN_TYPE);
        responseBuilder.entity(exception.getMessage());
        return responseBuilder.build();
        
        // send response without debug information
        //return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
}
