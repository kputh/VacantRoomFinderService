/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.htw_berlin.rz.room_finder.data;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Adapter for JAX Binding. Used to convert between Boolean values and the their
 * custom representation returned by the underlying webservice.
 * 
 * @author Kai Puth
 */
public class BooleanAdapter extends XmlAdapter<String, Boolean> {
    
    private static final String TRUE = "y";
    private static final String FALSE = "n";
    
    @Override
    public Boolean unmarshal(String string) {
        return string.equals(TRUE);
    }

    @Override
    public String marshal(Boolean bool) {
        return bool ? TRUE : FALSE;
    }
}
