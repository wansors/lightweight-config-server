package com.github.wansors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.wansors.lightweightconfigserver.SpringYamlConfigSource;

public class SpringYamlConfigSourceTest {

    @Test
    void readFile() throws MalformedURLException, IOException {
	final SpringYamlConfigSource config = new SpringYamlConfigSource(this.getClass()
		.getClassLoader()
		.getResource("SampleConfig.yaml"), 12);

	assertEquals(12, config.getOrdinal());

	final Map<String, String> properties = config.getProperties();

	for (final String x : properties.values()) {
	    System.out.println(x);
	}

	assertEquals(54, properties.size());

	assertEquals("first hash", config.getValue("empty.hash.a"));
	assertEquals("", config.getValue("empty.hash.b"));
	assertEquals("", config.getValue("empty.hash.c"));
	assertEquals("first element", config.getValue("empty.simple_list[0]"));
	assertEquals("second element", config.getValue("empty.simple_list[1]"));
	assertEquals("third element", config.getValue("empty.simple_list[2]"));
	assertEquals("", config.getValue("empty.simple_list[3]"));
	assertEquals("[]", config.getValue("empty.list"));
	assertEquals("true", config.getValue("global.var_boolean1"));
	assertEquals("false", config.getValue("global.var_boolean2"));
	assertEquals("42", config.getValue("global.var_number"));
	assertEquals("123123123123123123", config.getValue("global.var_long"));
	assertEquals("value1", config.getValue("global.var_string"));
	assertEquals("07760", config.getValue("global.var_string_number"));
	assertEquals("123", config.getValue("inline.list[0]"));
	assertEquals("345", config.getValue("inline.list[1]"));
	assertEquals("456", config.getValue("inline.list[2]"));
	assertEquals("123", config.getValue("inline.var"));
	assertEquals("filter 0", config.getValue("var.complex_list[0].filters[0][0]"));
	assertEquals("false", config.getValue("var.complex_list[0].filters[0][1]"));
	assertEquals("node0", config.getValue("var.complex_list[0].node"));
	assertEquals("filter 1", config.getValue("var.complex_list[1].filters[0][0]"));
	assertEquals("true", config.getValue("var.complex_list[1].filters[0][1]"));
	assertEquals("node1", config.getValue("var.complex_list[1].node"));
	assertEquals("[]", config.getValue("string.list"));
	assertEquals("true", config.getValue("string.boolean"));
	assertEquals("42", config.getValue("string.number"));

	assertEquals("java.lang.String", config.getValueType("empty.hash.a"));
	assertEquals("java.lang.String", config.getValueType("empty.hash.b"));
	assertEquals("java.lang.String", config.getValueType("empty.hash.c"));
	assertEquals("java.lang.String", config.getValueType("empty.simple_list[0]"));
	assertEquals("java.lang.String", config.getValueType("empty.simple_list[1]"));
	assertEquals("java.lang.String", config.getValueType("empty.simple_list[2]"));
	assertEquals("java.lang.String", config.getValueType("empty.simple_list[3]"));
	assertEquals("java.util.List", config.getValueType("empty.list"));
	assertEquals("java.lang.Boolean", config.getValueType("global.var_boolean1"));
	assertEquals("java.lang.Boolean", config.getValueType("global.var_boolean2"));
	assertEquals("java.lang.Integer", config.getValueType("global.var_number"));
	assertEquals("java.lang.Long", config.getValueType("global.var_long"));
	assertEquals("java.lang.String", config.getValueType("global.var_string"));
	assertEquals("java.lang.String", config.getValueType("global.var_string_number"));
	assertEquals("java.lang.Integer", config.getValueType("inline.list[0]"));
	assertEquals("java.lang.Integer", config.getValueType("inline.list[1]"));
	assertEquals("java.lang.Integer", config.getValueType("inline.list[2]"));
	assertEquals("java.lang.Integer", config.getValueType("inline.var"));
	assertEquals("java.lang.String", config.getValueType("var.complex_list[0].filters[0][0]"));
	assertEquals("java.lang.Boolean", config.getValueType("var.complex_list[0].filters[0][1]"));
	assertEquals("java.lang.String", config.getValueType("var.complex_list[0].node"));
	assertEquals("java.lang.String", config.getValueType("var.complex_list[1].filters[0][0]"));
	assertEquals("java.lang.Boolean", config.getValueType("var.complex_list[1].filters[0][1]"));
	assertEquals("java.lang.String", config.getValueType("var.complex_list[1].node"));
	assertEquals("java.lang.String", config.getValueType("string.list"));
	assertEquals("java.lang.String", config.getValueType("string.boolean"));
	assertEquals("java.lang.String", config.getValueType("string.number"));

    }
}
