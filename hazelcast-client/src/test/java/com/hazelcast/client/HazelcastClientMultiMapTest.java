/*
 * Copyright (c) 2007-2008, Hazel Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.hazelcast.client;

import org.junit.After;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import static org.junit.Assert.*;
import com.hazelcast.core.*;
import static com.hazelcast.client.TestUtility.getHazelcastClient;
import com.hazelcast.client.impl.Values;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertTrue;

public class HazelcastClientMultiMapTest {

    @Test(expected = NullPointerException.class)
    public void testPutNull(){
        HazelcastClient hClient = getHazelcastClient();
        final MultiMap map = hClient.getMultiMap("testPutNull");
        map.put(1, null);
    }


    @Test
    public void putToMultiMap(){
        HazelcastClient hClient = getHazelcastClient();
        MultiMap multiMap = hClient.getMultiMap("putToMultiMap");
        assertTrue(multiMap.put("a",1));
    }
    @Test
    public void removeFromMultiMap(){
        HazelcastClient hClient = getHazelcastClient();
        MultiMap multiMap = hClient.getMultiMap("removeFromMultiMap");
        assertTrue(multiMap.put("a",1));
        assertTrue(multiMap.remove("a",1));
    }

    @Test
    public void containsKey(){
        HazelcastClient hClient = getHazelcastClient();
        MultiMap multiMap = hClient.getMultiMap("containsKey");
        assertFalse(multiMap.containsKey("a"));
        assertTrue(multiMap.put("a",1));
        assertTrue(multiMap.containsKey("a"));

    }
    @Test
    public void containsValue(){
        HazelcastClient hClient = getHazelcastClient();
        MultiMap multiMap = hClient.getMultiMap("containsValue");
        assertFalse(multiMap.containsValue(1));
        assertTrue(multiMap.put("a",1));
        assertTrue(multiMap.containsValue(1));
    }
    @Test
    public void containsEntry(){
        HazelcastClient hClient = getHazelcastClient();
        MultiMap multiMap = hClient.getMultiMap("containsEntry");
        assertFalse(multiMap.containsEntry("a",1));
        assertTrue(multiMap.put("a",1));
        assertTrue(multiMap.containsEntry("a",1));
        assertFalse(multiMap.containsEntry("a",2));
    }
    @Test
    public void size(){
        HazelcastClient hClient = getHazelcastClient();
        MultiMap multiMap = hClient.getMultiMap("size");
        assertEquals(0, multiMap.size());
        assertTrue(multiMap.put("a",1));
        assertEquals(1, multiMap.size());

        assertTrue(multiMap.put("a",2));
        assertEquals(2, multiMap.size());
    }

    @Test
    public void get() throws InterruptedException {
        HazelcastClient hClient = getHazelcastClient();
        MultiMap multiMap = hClient.getMultiMap("get");
        assertTrue(multiMap.put("a",1));
        assertTrue(multiMap.put("a",2));
        Map<Integer, CountDownLatch> map = new HashMap<Integer, CountDownLatch>();
        map.put(1, new CountDownLatch(1));
        map.put(2, new CountDownLatch(1));
        Collection collection = multiMap.get("a");
        assertEquals(Values.class, collection.getClass());
        assertEquals(2, collection.size());
        for(Iterator it = collection.iterator();it.hasNext();){
            Object o = it.next();
            map.get((Integer)o).countDown();
        }
        assertTrue(map.get(1).await(10, TimeUnit.MILLISECONDS));
        assertTrue(map.get(2).await(10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void removeKey() throws InterruptedException {
        HazelcastClient hClient = getHazelcastClient();
        MultiMap multiMap = hClient.getMultiMap("removeKey");
        assertTrue(multiMap.put("a",1));
        assertTrue(multiMap.put("a",2));
        Map<Integer, CountDownLatch> map = new HashMap<Integer, CountDownLatch>();
        map.put(1, new CountDownLatch(1));
        map.put(2, new CountDownLatch(1));
        Collection collection = multiMap.remove("a");
        assertEquals(Values.class, collection.getClass());
        assertEquals(2, collection.size());
        for(Iterator it = collection.iterator();it.hasNext();){
            Object o = it.next();
            map.get((Integer)o).countDown();
        }
        assertTrue(map.get(1).await(10, TimeUnit.MILLISECONDS));
        assertTrue(map.get(2).await(10, TimeUnit.MILLISECONDS));
    }
    @Test
    public void keySet() throws InterruptedException {
        HazelcastClient hClient = getHazelcastClient();
        MultiMap multiMap = hClient.getMultiMap("keySet");
        int count =100;
        for (int i=0;i<count;i++){
            for(int j=0;j<=i;j++){
                multiMap.put(""+i,""+j);
            }
        }

        assertEquals(count*(count+1)/2, multiMap.size());

        Set set = multiMap.keySet();
        assertEquals(count, set.size());
        Set s = new HashSet();
        for(int i=0;i<count;i++){
            s.add(""+i);
        }
        assertEquals(s, set);
    }
    @Test
    public void entrySet() throws InterruptedException {
        HazelcastClient hClient = getHazelcastClient();
        MultiMap multiMap = hClient.getMultiMap("entrySet");
        Map<String, List<String>> keyValueListMap = new HashMap<String, List<String>>();
        int count =100;
        for (int i=0;i<count;i++){
            for(int j=0;j<=i;j++){
                String key = ""+i;
                String value = ""+j;
                multiMap.put(key,value);
                if(keyValueListMap.get(key)==null){
                    keyValueListMap.put(key, new ArrayList<String>());
                }
                keyValueListMap.get(key).add(value);

            }
        }

        assertEquals(count*(count+1)/2, multiMap.size());

        Set set = multiMap.entrySet();
        assertEquals(count*(count+1)/2, set.size());

        for (Iterator<MapEntry> iterator = set.iterator(); iterator.hasNext();) {
            Map.Entry o =  iterator.next();
            assertTrue(Integer.valueOf((String)o.getValue())<count);
            assertTrue(keyValueListMap.get((String)o.getKey()).contains((String)o.getValue()));

        }
    }

    @Test
    public void values() throws InterruptedException {
        HazelcastClient hClient = getHazelcastClient();
        MultiMap multiMap = Hazelcast.getMultiMap("entrySet");
        Map<String, List<String>> valueKeyListMap = new HashMap<String, List<String>>();
        int count =100;
        for (int i=0;i<count;i++){
            for(int j=0;j<=i;j++){
                String key = ""+i;
                String value = ""+j;
                multiMap.put(key,value);
                if(valueKeyListMap.get(value)==null){
                    valueKeyListMap.put(value, new ArrayList<String>());
                }
                valueKeyListMap.get(value).add(key);

            }
        }

        assertEquals(count*(count+1)/2, multiMap.size());

        Collection collection = multiMap.values();
        assertEquals(count*(count+1)/2, collection.size());

        for (Iterator<String> iterator = collection.iterator(); iterator.hasNext();) {
            String value =  iterator.next();
            assertNotNull(valueKeyListMap.get(value).remove(0));
            if(valueKeyListMap.get(value).size()==0){
                valueKeyListMap.remove(value);
            }
        }

        assertTrue(valueKeyListMap.isEmpty());
    }


    @Test
    public void testMultiMapPutAndGet() {
        HazelcastClient hClient = getHazelcastClient();
        MultiMap<String, String> map = hClient.getMultiMap("testMultiMapPutAndGet");
        map.put("Hello", "World");
        Collection<String> values = map.get("Hello");
        assertEquals("World", values.iterator().next());
        map.put("Hello", "Europe");
        map.put("Hello", "America");
        map.put("Hello", "Asia");
        map.put("Hello", "Africa");
        map.put("Hello", "Antartica");
        map.put("Hello", "Australia");
        values = map.get("Hello");
        assertEquals(7, values.size());
    }

    @Test
    public void testMultiMapGetNameAndType() {
        HazelcastClient hClient = getHazelcastClient();
        MultiMap<String, String> map = hClient.getMultiMap("testMultiMapGetNameAndType");
        assertEquals("testMultiMapGetNameAndType", map.getName());
        Instance.InstanceType type = map.getInstanceType();
        assertEquals(Instance.InstanceType.MULTIMAP, type);
    }

    @Test
    public void testMultiMapClear() {
        HazelcastClient hClient = getHazelcastClient();
        MultiMap<String, String> map = hClient.getMultiMap("testMultiMapClear");
        map.put("Hello", "World");
        assertEquals(1, map.size());
        map.clear();
        assertEquals(0, map.size());
    }

    @Test
    public void testMultiMapContainsKey() {
        HazelcastClient hClient = getHazelcastClient();
        MultiMap<String, String> map = hClient.getMultiMap("testMultiMapContainsKey");
        map.put("Hello", "World");
        Assert.assertTrue(map.containsKey("Hello"));
    }

    @Test
    public void testMultiMapContainsValue() {
        HazelcastClient hClient = getHazelcastClient();
        MultiMap<String, String> map = hClient.getMultiMap("testMultiMapContainsValue");
        map.put("Hello", "World");
        Assert.assertTrue(map.containsValue("World"));
    }

    @Test
    public void testMultiMapContainsEntry() {
        HazelcastClient hClient = getHazelcastClient();
        MultiMap<String, String> map = hClient.getMultiMap("testMultiMapContainsEntry");
        map.put("Hello", "World");
        Assert.assertTrue(map.containsEntry("Hello", "World"));
    }

    @Test
    public void testMultiMapKeySet() {
        HazelcastClient hClient = getHazelcastClient();
        MultiMap<String, String> map = hClient.getMultiMap("testMultiMapKeySet");
        map.put("Hello", "World");
        map.put("Hello", "Europe");
        map.put("Hello", "America");
        map.put("Hello", "Asia");
        map.put("Hello", "Africa");
        map.put("Hello", "Antartica");
        map.put("Hello", "Australia");
        Set<String> keys = map.keySet();
        assertEquals(1, keys.size());
    }

    @Test
    public void testMultiMapValues() {
        HazelcastClient hClient = getHazelcastClient();
        MultiMap<String, String> map = hClient.getMultiMap("testMultiMapValues");
        map.put("Hello", "World");
        map.put("Hello", "Europe");
        map.put("Hello", "America");
        map.put("Hello", "Asia");
        map.put("Hello", "Africa");
        map.put("Hello", "Antartica");
        map.put("Hello", "Australia");
        Collection<String> values = map.values();
        assertEquals(7, values.size());
    }

    @Test
    public void testMultiMapRemove() {
        HazelcastClient hClient = getHazelcastClient();
        MultiMap<String, String> map = hClient.getMultiMap("testMultiMapRemove");
        map.put("Hello", "World");
        map.put("Hello", "Europe");
        map.put("Hello", "America");
        map.put("Hello", "Asia");
        map.put("Hello", "Africa");
        map.put("Hello", "Antartica");
        map.put("Hello", "Australia");
        assertEquals(7, map.size());
        assertEquals(1, map.keySet().size());
        Collection<String> values = map.remove("Hello");
        assertEquals(7, values.size());
        assertEquals(0, map.size());
        assertEquals(0, map.keySet().size());
        map.put("Hello", "World");
        assertEquals(1, map.size());
        assertEquals(1, map.keySet().size());
    }

    @Test
    public void testMultiMapRemoveEntries() {
        HazelcastClient hClient = getHazelcastClient();
        MultiMap<String, String> map = hClient.getMultiMap("testMultiMapRemoveEntries");
        map.put("Hello", "World");
        map.put("Hello", "Europe");
        map.put("Hello", "America");
        map.put("Hello", "Asia");
        map.put("Hello", "Africa");
        map.put("Hello", "Antartica");
        map.put("Hello", "Australia");
        boolean removed = map.remove("Hello", "World");
        Assert.assertTrue(removed);
        assertEquals(6, map.size());
    }

    @Test
    public void testMultiMapEntrySet() {
        HazelcastClient hClient = getHazelcastClient();
        MultiMap<String, String> map = hClient.getMultiMap("testMultiMapEntrySet");
        map.put("Hello", "World");
        map.put("Hello", "Europe");
        map.put("Hello", "America");
        map.put("Hello", "Asia");
        map.put("Hello", "Africa");
        map.put("Hello", "Antartica");
        map.put("Hello", "Australia");
        Set<Map.Entry<String, String>> entries = map.entrySet();
        assertEquals(7, entries.size());
        int itCount = 0;
        for (Map.Entry<String, String> entry : entries) {
            assertEquals("Hello", entry.getKey());
            itCount++;
        }
        assertEquals(7, itCount);
    }

    @Test
    public void testMultiMapValueCount() {
        HazelcastClient hClient = getHazelcastClient();
        MultiMap<Integer, String> map = hClient.getMultiMap("testMultiMapValueCount");
        map.put(1, "World");
        map.put(2, "Africa");
        map.put(1, "America");
        map.put(2, "Antartica");
        map.put(1, "Asia");
        map.put(1, "Europe");
        map.put(2, "Australia");
        assertEquals(4, map.valueCount(1));
        assertEquals(3, map.valueCount(2));
    }
}
