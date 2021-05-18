package com.github.wansors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.wansors.quarkusconfigserver.SpringYamlConfigSource;

public class SpringYamlConfigSourceTest {

	@Test
	public void readFile() throws MalformedURLException, IOException {
		SpringYamlConfigSource config = new SpringYamlConfigSource(
				getClass().getClassLoader().getResource("SampleConfig.yaml"), 12);

		assertEquals(12, config.getOrdinal());

		Map<String, String> properties = config.getProperties();
		for (String key : properties.keySet()) {
			System.out.println(key + "=" + properties.get(key));
		}

		assertEquals(21, properties.size());

		assertEquals("first hash", config.getValue("empty.hash.a"));
		assertEquals("", config.getValue("empty.hash.b"));
		assertEquals("", config.getValue("empty.hash.c"));
		assertEquals("first element", config.getValue("empty.simple_list[0]"));
		assertEquals("second element", config.getValue("empty.simple_list[1]"));
		assertEquals("third element", config.getValue("empty.simple_list[2]"));
		assertEquals("", config.getValue("empty.simple_list[3]"));
		assertEquals("true", config.getValue("global.var_boolean1"));
		assertEquals("false", config.getValue("global.var_boolean2"));
		assertEquals("42", config.getValue("global.var_number"));
		assertEquals("value1", config.getValue("global.var_string"));
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

	}
}
