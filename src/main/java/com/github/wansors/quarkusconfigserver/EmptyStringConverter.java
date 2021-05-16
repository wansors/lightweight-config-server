package com.github.wansors.quarkusconfigserver;

import org.eclipse.microprofile.config.spi.Converter;

public class EmptyStringConverter implements Converter<String> {

    @Override
    public String convert(String value) throws IllegalArgumentException, NullPointerException {
        if(value!=null){
            return value;
        }
        return "";
    }
    
}
