package hazelnuts;

import hazelnuts.Konfigurasjon;
import junit.framework.Assert;

import org.junit.Test;


import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class MapStoreTest {

    @Test
    public void skalKalleMapStore() {
        Config config = Konfigurasjon.konfigurer();
        HazelcastInstance node1 = Hazelcast.newHazelcastInstance(config);

        IMap<Integer, String> map1 = node1.getMap(Konfigurasjon.TEST_MAP);
        map1.put(1, "RØD");
        map1.put(2, "BLÅ");
        map1.put(3, "GRØNN");
        map1.put(4, "GUL");
        map1.put(5, "SORT");
        Assert.assertEquals(5, map1.size());
        System.out.println("Lagt til alle data node1");

        HazelcastInstance node2 = Hazelcast.newHazelcastInstance(config);
        IMap<Integer, String> map2 = node2.getMap(Konfigurasjon.TEST_MAP);
        map2.put(6, "LILLA");
    }

}
