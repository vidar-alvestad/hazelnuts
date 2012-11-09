package hazelnuts;

import hazelnuts.Konfigurasjon;

import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;


import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.LifecycleService;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.partition.PartitionService;

public class RebalanseringTest {

    @Test
    public void skalLeggeDataPaaEnNodeStartNyNodeRebalanser() {
        Config config = Konfigurasjon.konfigurer();
        HazelcastInstance node1 = Hazelcast.newHazelcastInstance(config);

        Rebalanseringslytter rebalanseringslytter = new Rebalanseringslytter(node1);
        PartitionService partitionService = node1.getPartitionService();
        partitionService.addMigrationListener(rebalanseringslytter);

        IMap<Integer, String> map1 = node1.getMap(Konfigurasjon.TEST_MAP);
        map1.put(1, "RØD");
        map1.put(2, "BLÅ");
        map1.put(3, "GRØNN");
        map1.put(4, "GUL");
        map1.put(5, "SORT");
        Assert.assertEquals(5, map1.size());
        System.out.println("Lagt til alle data node1");

        HazelcastInstance node2 = Hazelcast.newHazelcastInstance(config);
        PartitionService partitionService2 = node2.getPartitionService();
        partitionService2.addMigrationListener(rebalanseringslytter);

        IMap<Integer, String> map2 = node2.getMap(Konfigurasjon.TEST_MAP);

        // fra utsiden ser det ut som map1 og map2 inneholder det samme
        Assert.assertEquals(map1.get(1), map2.get(1));

        // men map1 og map2 eier bare noen farver hver...
        LocalMapStats localMapStats1 = map1.getLocalMapStats();
        LocalMapStats localMapStats2 = map2.getLocalMapStats();
        System.out.println("Node 1 eier:" + localMapStats1.getOwnedEntryCount());
        System.out.println("Node 2 eier:" + localMapStats2.getOwnedEntryCount());

        System.out.println("Data på node1:");
        dataPaaLokalNode(map1);
        System.out.println("Data på node2:");
        dataPaaLokalNode(map2);

        // resten er rebalansert
        System.out.println("Node 1 backup:" + localMapStats1.getBackupEntryCount());
        System.out.println("Node 2 backup:" + localMapStats2.getBackupEntryCount());

        map2.put(6, "LILLA");
        map1.put(7, "GRÅ");
        System.out.println("Data på node1:");
        dataPaaLokalNode(map1);
        System.out.println("Data på node2:");
        dataPaaLokalNode(map2);

        LifecycleService lifecycleService = node2.getLifecycleService();
        lifecycleService.shutdown();

        System.out.println("Data på node1 etter destroy node2:");
        dataPaaLokalNode(map1);
        testAtAlleDataErTilgjengelig(map1);
    }

    private void testAtAlleDataErTilgjengelig(IMap<Integer, String> map) {
        Assert.assertEquals("RØD", map.get(1));
        Assert.assertEquals("BLÅ", map.get(2));
        Assert.assertEquals("GRØNN", map.get(3));
        Assert.assertEquals("GUL", map.get(4));
        Assert.assertEquals("SORT", map.get(5));
        Assert.assertEquals("LILLA", map.get(6));
        Assert.assertEquals("GRÅ", map.get(7));
    }

    private void dataPaaLokalNode(IMap<Integer, String> map) {
        Set<Integer> nokler = map.localKeySet();
        for (Integer nokkel : nokler) {
            System.out.println(map.get(nokkel));
        }
    }

    @After
    public void cleanup() throws Exception {
        Hazelcast.shutdownAll();
    }
}
