package hazelnuts;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class MapStoreTest {

    @Before
    public void slaaAvHazelcastLogging() {
        Konfigurasjon.slaaAvHazelcastLogg();
    }

    @Test
    public void skalKalleMapStore() {
        Config config = Konfigurasjon.konfigurer();
        HazelcastInstance node1 = Hazelcast.newHazelcastInstance(config);

        IMap<Integer, String> map1 = node1.getMap("saldo_rente_oppgaver");
        map1.put(1, "SR-1");
        map1.put(2, "SR-2");
        map1.put(3, "SR-3");
        map1.put(4, "SR-4");
        map1.put(5, "SR-5");
        Assert.assertEquals(5, map1.size());
        System.out.println("Lagt til alle data node1");

        HazelcastInstance node2 = Hazelcast.newHazelcastInstance(config);
        IMap<Integer, String> map2 = node2.getMap("saldo_rente_oppgaver");
        map2.put(6, "SR-6");
    }

}
