/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htw_berlin.rz.room_finder.exception_mappers;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception handler for faulty requests to this webservice. Relays the
 * exception to the client.
 * 
 * @author Kai Puth
 */
@Provider
public class IllegalArgumentExceptionMapper implements
        ExceptionMapper<IllegalArgumentException> {

    private static final String LOGGER_NAME
            = IllegalArgumentExceptionMapper.class.getName();

    @Override
    public Response toResponse(IllegalArgumentException exception) {

        // log exception
        Logger logger = Logger.getLogger(LOGGER_NAME);
        logger.log(Level.INFO, null, exception);

        // send response with debug information
        Response.ResponseBuilder responseBuilder =
                Response.status(Response.Status.BAD_REQUEST);
        responseBuilder.type(MediaType.TEXT_PLAIN_TYPE);
        responseBuilder.entity(exception.getMessage());
        return responseBuilder.build();
    }

}
