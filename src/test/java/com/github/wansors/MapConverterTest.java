package com.github.wansors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import com.github.wansors.lightweightconfigserver.utils.MapConverter;

public class MapConverterTest {

    @Test()
    public void convertTest() {

	final Map<String, Object> testMap = new HashMap<>();
	testMap.put("test", "test");
	testMap.put("nodo.subnodo1.a", "tRue");
	testMap.put("nodo.subnodo1.b", "b");
	testMap.put("nodo.subnodo1.c", "-123");
	testMap.put("nodo.subnodo1.d", "no");
	testMap.put("nodo.subnodo2.a", "a");
	testMap.put("nodo.subnodo2.b", "b");
	testMap.put("nodo.subnodo2.c", "123.45");
	testMap.put("nodo.subnodo2.d", "no");
	testMap.put("nodo.subnodo3.a", "true");
	testMap.put("nodo.subnodo3.b", "-24");
	testMap.put("nodo.subnodo3.c", "123.45");
	testMap.put("nodo.subnodo3.d", "false");

	final Map<String, Object> resultMap = MapConverter.expandMap(testMap);

	assertEquals("test", resultMap.get("test"));
	final Map<String, Object> nodo = (Map<String, Object>) resultMap.get("nodo");
	assertNotNull(nodo, "Map nodo does not exist");
	final Map<String, Object> subnodo1 = (Map<String, Object>) nodo.get("subnodo1");
	assertNotNull(subnodo1, "Map subnodo1 does not exist");
	assertEquals("tRue", subnodo1.get("a"));
	assertEquals("b", subnodo1.get("b"));
	assertEquals("-123", subnodo1.get("c"));
	assertEquals("no", subnodo1.get("d"));
	final Map<String, Object> subnodo2 = (Map<String, Object>) nodo.get("subnodo2");
	assertNotNull(subnodo2, "Map subnodo2 does not exist");
	assertEquals("a", subnodo2.get("a"));
	assertEquals("b", subnodo2.get("b"));
	assertEquals("123.45", subnodo2.get("c"));
	assertEquals("no", subnodo2.get("d"));
	final Map<String, Object> subnodo3 = (Map<String, Object>) nodo.get("subnodo3");
	assertNotNull(subnodo3, "Map subnodo3 does not exist");
	assertEquals("true", subnodo3.get("a"));
	assertEquals("-24", subnodo3.get("b"));
	assertEquals("123.45", subnodo3.get("c"));
	assertEquals("false", subnodo3.get("d"));
    }

    @Test
    public void convertToPropertiesFormatStringTest() {

	final Map<String, String> testMap = new HashMap<>();
	testMap.put("test", "test");
	testMap.put("nodo.subnodo1.a", "tRue");
	testMap.put("nodo.subnodo1.b", "b");
	testMap.put("nodo.subnodo1.c", "-123");
	testMap.put("nodo.subnodo1.d", "no");
	testMap.put("nodo.subnodo2.a", "a");
	testMap.put("nodo.subnodo2.b", "b");
	testMap.put("nodo.subnodo2.c", "123.45");
	testMap.put("nodo.subnodo2.d", "no");
	testMap.put("nodo.subnodo3.a", "true");
	testMap.put("nodo.subnodo3.b", "-24");
	testMap.put("nodo.subnodo3.c", "123.45");
	testMap.put("nodo.subnodo3.d", "false");

	final String resultString = MapConverter.convertToPropertiesFormatString(testMap);

	assertTrue(resultString.contains("test: test\n"));
	assertTrue(resultString.contains("nodo.subnodo1.a: tRue"));
	assertTrue(resultString.contains("odo.subnodo1.b: b"));
	assertTrue(resultString.contains("odo.subnodo2.a: a"));
	assertTrue(resultString.contains("nodo.subnodo2.d: no"));
	assertTrue(resultString.contains("nodo.subnodo3.c: 123.45"));
	assertTrue(resultString.contains("odo.subnodo3.d: false"));
	assertTrue(resultString.contains("odo.subnodo1.c: -123"));
	assertTrue(resultString.contains("odo.subnodo2.b: b"));
	assertTrue(resultString.contains("nodo.subnodo3.a: true"));
	assertTrue(resultString.contains("odo.subnodo1.d: no"));
	assertTrue(resultString.contains("nodo.subnodo2.c: 123.45"));
    }

    @Test()
    public void convertTestArray() {

	final Map<String, Object> testMap = new HashMap<>();
	testMap.put("subnodo[0].a", "a0");
	testMap.put("subnodo[0].b", "b0");
	testMap.put("subnodo[1].a", "a1");
	testMap.put("subnodo[2].b", "b2");

	testMap.put("nodo[0].subnodo[0].a", "a00");
	testMap.put("nodo[0].subnodo[0].b", "b00");
	testMap.put("nodo[0].subnodo[1].a", "a01");
	testMap.put("nodo[0].subnodo[2].b", "b02");

	testMap.put("nodo[1].subnodo[0].a", "a10");
	testMap.put("nodo[1].subnodo[0].b", "b10");
	testMap.put("nodo[1].subnodo[1].a", "a11");
	testMap.put("nodo[1].subnodo[2].b", "b12");

	testMap.put("array[0]", "00");
	testMap.put("array[1]", "01");
	testMap.put("array[2]", "02");

	final Map<String, Object> resultMap = MapConverter.expandMap(testMap);

	List<Object> lista = (List<Object>) resultMap.get("subnodo");
	assertEquals(3, lista.size());
	Map<String, Object> subnodo = (Map<String, Object>) lista.get(0);
	assertEquals("a0", subnodo.get("a"));
	assertEquals("b0", subnodo.get("b"));
	subnodo = (Map<String, Object>) lista.get(1);
	final Map<String, Object> subnodo1 = (Map<String, Object>) lista.get(1);
	assertEquals("a1", subnodo.get("a"));
	subnodo = (Map<String, Object>) lista.get(2);
	final Map<String, Object> subnodo2 = (Map<String, Object>) lista.get(2);
	assertEquals("b2", subnodo.get("b"));

	lista = (List<Object>) resultMap.get("nodo");
	assertEquals(2, lista.size());

    }

    @Test()
    public void convertSampleConfigPropertiesFile() throws IOException {
	final Properties properties = new Properties();
	final ClassLoader loader = Thread.currentThread()
		.getContextClassLoader();
	final InputStream stream = loader.getResourceAsStream("SampleConfig.properties");

	properties.load(stream);

	final Map<String, Object> map = new HashMap<>();
	for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
	    map.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
	}

	final Map<String, Object> testMap = MapConverter.expandMap(map);
    }

    @Test()
    public void convertTestArray2() {

	final Map<String, Object> testMap = new HashMap<>();
	testMap.put("subnodo[0].a", "a0");
	testMap.put("subnodo[0].b", "b0");
	testMap.put("subnodo[1].a", "a1");
	testMap.put("subnodo[2].b", "b2");
	testMap.put("subnodo[3].a", "a1");
	testMap.put("subnodo[4].b", "b2");
	testMap.put("subnodo[5].a", "a1");
	testMap.put("subnodo[6].b", "b2");
	testMap.put("subnodo[7].a", "a1");
	testMap.put("subnodo[8].b", "b2");
	testMap.put("subnodo[9].a", "a1");
	testMap.put("subnodo[10].b", "b10");
	testMap.put("subnodo[11].a", "a11");
	testMap.put("subnodo[12].b", "b12");

	final Map<String, Object> resultMap = MapConverter.expandMap(testMap);

	final List<Object> lista = (List<Object>) resultMap.get("subnodo");
	assertEquals(13, lista.size());
	Map<String, Object> subnodo = (Map<String, Object>) lista.get(10);
	assertEquals("b10", subnodo.get("b"));
	subnodo = (Map<String, Object>) lista.get(11);
	assertEquals("a11", subnodo.get("a"));
	subnodo = (Map<String, Object>) lista.get(12);
	assertEquals("b12", subnodo.get("b"));

    }
}
