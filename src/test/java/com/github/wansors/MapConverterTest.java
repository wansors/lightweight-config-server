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

import com.github.wansors.quarkusconfigserver.utils.MapConverter;

public class MapConverterTest {

	@Test()
	public void convertTest() {

		Map<String, String> testMap = new HashMap<>();
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

		Map<String, Object> resultMap = MapConverter.convert(testMap);

		assertEquals("test", resultMap.get("test"));
		Map<String, Object> nodo = (Map<String, Object>) resultMap.get("nodo");
		assertNotNull(nodo, "Map nodo does not exist");
		Map<String, Object> subnodo1 = (Map<String, Object>) nodo.get("subnodo1");
		assertNotNull(subnodo1, "Map subnodo1 does not exist");
		assertEquals(true, subnodo1.get("a"));
		assertEquals("b", subnodo1.get("b"));
		assertEquals(-123L, subnodo1.get("c"));
		assertEquals(false, subnodo1.get("d"));
		Map<String, Object> subnodo2 = (Map<String, Object>) nodo.get("subnodo2");
		assertNotNull(subnodo2, "Map subnodo2 does not exist");
		assertEquals("a", subnodo2.get("a"));
		assertEquals("b", subnodo2.get("b"));
		assertEquals((float) 123.45, subnodo2.get("c"));
		assertEquals(false, subnodo2.get("d"));
		Map<String, Object> subnodo3 = (Map<String, Object>) nodo.get("subnodo3");
		assertNotNull(subnodo3, "Map subnodo3 does not exist");
		assertEquals(true, subnodo3.get("a"));
		assertEquals(Long.valueOf(-24), subnodo3.get("b"));
		assertEquals(123.45, subnodo3.get("c"));
		assertEquals(false, subnodo3.get("d"));
	}

	@Test
	public void convertToPropertiesFormatStringTest() {

		Map<String, String> testMap = new HashMap<>();
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

		String resultString = MapConverter.convertToPropertiesFormatString(testMap);

		assertTrue(resultString.contains("test=test\n"));
		assertTrue(resultString.contains("nodo.subnodo1.a=tRue\n"));
		assertTrue(resultString.contains("odo.subnodo1.b=b\n"));
		assertTrue(resultString.contains("odo.subnodo2.a=a\n"));
		assertTrue(resultString.contains("nodo.subnodo2.d=no\n"));
		assertTrue(resultString.contains("nodo.subnodo3.c=123.45\n"));
		assertTrue(resultString.contains("odo.subnodo3.d=false\n"));
		assertTrue(resultString.contains("odo.subnodo1.c=-123\n"));
		assertTrue(resultString.contains("odo.subnodo2.b=b\n"));
		assertTrue(resultString.contains("nodo.subnodo3.a=true\n"));
		assertTrue(resultString.contains("odo.subnodo1.d=no\n"));
		assertTrue(resultString.contains("nodo.subnodo2.c=123.45\n"));
	}

	@Test()
	public void convertTestArray() {

		Map<String, String> testMap = new HashMap<>();
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

		Map<String, Object> resultMap = MapConverter.convert(testMap);

		List<Object> lista = (List<Object>) resultMap.get("subnodo");
		assertEquals(3, lista.size());
		Map<String, Object> subnodo = (Map<String, Object>) lista.get(0);
		assertEquals("a0", subnodo.get("a"));
		assertEquals("b0", subnodo.get("b"));
		subnodo = (Map<String, Object>) lista.get(1);
		Map<String, Object> subnodo1 = (Map<String, Object>) lista.get(1);
		assertEquals("a1", subnodo.get("a"));
		subnodo = (Map<String, Object>) lista.get(2);
		Map<String, Object> subnodo2 = (Map<String, Object>) lista.get(2);
		assertEquals("b2", subnodo.get("b"));

		lista = (List<Object>) resultMap.get("nodo");
		assertEquals(2, lista.size());

	}

	@Test()
	public void convertSampleConfigPropertiesFile() throws IOException {
		Properties properties = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream stream = loader.getResourceAsStream("SampleConfig.properties");

		properties.load(stream);

		Map<String, String> map = new HashMap<>();
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			map.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
		}

		Map<String, Object> testMap = MapConverter.convert(map);
	}
}
