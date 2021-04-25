package com.github.wansors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import com.github.wansors.quarkusconfigserver.utils.MapConverter;

import org.junit.jupiter.api.Test;

public class MapConverterTest {
    
    @Test()
    public void convertTest() {

        Map<String, Object> testMap = new HashMap<>();
        testMap.put("test", "test");
        testMap.put("nodo.subnodo1.a", "tRue");
        testMap.put("nodo.subnodo1.b", "b");
        testMap.put("nodo.subnodo1.c", "-123");
        testMap.put("nodo.subnodo1.d", "no");
        testMap.put("nodo.subnodo2.a", "a");
        testMap.put("nodo.subnodo2.b", "b");
        testMap.put("nodo.subnodo2.c", "123.45");
        testMap.put("nodo.subnodo2.d", "no");
        testMap.put("nodo.subnodo3.a", true);
        testMap.put("nodo.subnodo3.b", -24);
        testMap.put("nodo.subnodo3.c", 123.45);
        testMap.put("nodo.subnodo3.d", false);

        Map<String, Object> resultMap = MapConverter.convert(testMap);

        assertEquals("test", resultMap.get("test"));
        Map<String, Object> nodo = (Map<String, Object>) resultMap.get("nodo");
        assertNotNull(nodo, "Map nodo does not exist");
        Map<String, Object> subnodo1 = (Map<String, Object>) nodo.get("subnodo1");
        assertNotNull(subnodo1, "Map subnodo1 does not exist");
        assertEquals(true, subnodo1.get("a"));
        assertEquals("b", subnodo1.get("b"));
        assertEquals(-123, subnodo1.get("c"));
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
        assertEquals(-24, subnodo3.get("b"));
        assertEquals(123.45, subnodo3.get("c"));
        assertEquals(false, subnodo3.get("d"));
    }

    @Test
    public void convertToPropertiesFormatStringTest() {

        Map<String, Object> testMap = new HashMap<>();
        testMap.put("test", "test");
        testMap.put("nodo.subnodo1.a", "tRue");
        testMap.put("nodo.subnodo1.b", "b");
        testMap.put("nodo.subnodo1.c", "-123");
        testMap.put("nodo.subnodo1.d", "no");
        testMap.put("nodo.subnodo2.a", "a");
        testMap.put("nodo.subnodo2.b", "b");
        testMap.put("nodo.subnodo2.c", "123.45");
        testMap.put("nodo.subnodo2.d", "no");
        testMap.put("nodo.subnodo3.a", true);
        testMap.put("nodo.subnodo3.b", -24);
        testMap.put("nodo.subnodo3.c", 123.45);
        testMap.put("nodo.subnodo3.d", false);
        
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
}
