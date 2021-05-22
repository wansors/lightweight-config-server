package com.github.wansors.lightweightconfigserver;

import org.eclipse.microprofile.config.spi.Converter;

public class EmptyStringConverter implements Converter<String> {

	private static final long serialVersionUID = 5045598718194541748L;

	@Override
	public String convert(String value) throws IllegalArgumentException, NullPointerException {
		if (value != null) {
			return value;
		}
		return "";
	}

}
