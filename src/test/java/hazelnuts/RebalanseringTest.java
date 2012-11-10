package hazelnuts;

import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.partition.PartitionService;

@SuppressWarnings("deprecation")
public class RebalanseringTest {

    @Before
    public void konfig() {
        Konfigurasjon.slaaAvHazelcastLogg();
        Konfigurasjon.setAntallVirtuellePatisjoner(3);
    }

    @Test
    public void skalLeggeDataPaaEnNodeStartNyNodeRebalanser() {
        HazelcastInstance nodeA = Hazelcast.newHazelcastInstance(null);
        leggPaaRebalanseringslytter(nodeA);

        IMap<Integer, String> map1 = leggSaldoRenteOppgaverPaaGrid(nodeA);

        HazelcastInstance nodeB = Hazelcast.newHazelcastInstance(null);
        leggPaaRebalanseringslytter(nodeB);

        IMap<Integer, String> map2 = nodeB.getMap("saldo_rente_oppgaver");

        // fra utsiden ser det ut som map1 og map2 inneholder det samme
        Assert.assertEquals(map1.get(1), map2.get(1));

        // men map1 og map2 eier bare noen farver hver...
        System.out.println("Data på node1:");
        dataPaaLokalNode(map1);
        System.out.println("Data på node2:");
        dataPaaLokalNode(map2);

        LocalMapStats localMapStats1 = map1.getLocalMapStats();
        LocalMapStats localMapStats2 = map2.getLocalMapStats();
        System.out.println("Node 1 eier:" + localMapStats1.getOwnedEntryCount());
        System.out.println("Node 1 backup:" + localMapStats1.getBackupEntryCount());
        System.out.println("Node 2 eier:" + localMapStats2.getOwnedEntryCount());
        System.out.println("Node 2 backup:" + localMapStats2.getBackupEntryCount());

        System.out.println("medlemmer før kill: " + Hazelcast.getCluster().getMembers());
        nodeB.shutdown();
        System.out.println("medlemmer etter kill: " + Hazelcast.getCluster().getMembers());

        System.out.println("Data på node1 etter kill node2:");
        dataPaaNode(map1);
        testAtAlleDataErTilgjengelig(map1);
    }

    private void leggPaaRebalanseringslytter(HazelcastInstance node) {
        Rebalanseringslytter rebalanseringslytter = new Rebalanseringslytter(node);
        PartitionService partitionService = node.getPartitionService();
        partitionService.addMigrationListener(rebalanseringslytter);
    }

    private IMap<Integer, String> leggSaldoRenteOppgaverPaaGrid(HazelcastInstance node) {
        IMap<Integer, String> map = node.getMap("saldo_rente_oppgaver");
        map.put(1, "SR-1");
        map.put(2, "SR-2");
        map.put(3, "SR-3");
        map.put(4, "SR-4");
        map.put(5, "SR-5");
        map.put(6, "SR-6");
        return map;
    }

    private void testAtAlleDataErTilgjengelig(IMap<Integer, String> map) {
        Assert.assertEquals("SR-1", map.get(1));
        Assert.assertEquals("SR-2", map.get(2));
        Assert.assertEquals("SR-3", map.get(3));
        Assert.assertEquals("SR-4", map.get(4));
        Assert.assertEquals("SR-5", map.get(5));
        Assert.assertEquals("SR-6", map.get(6));
    }

    private void dataPaaLokalNode(IMap<Integer, String> map) {
        Set<Integer> nokler = map.localKeySet();
        for (Integer nokkel : nokler) {
            System.out.println(map.get(nokkel));
        }
    }

    private void dataPaaNode(IMap<Integer, String> map) {
        Set<Integer> nokler = map.keySet();
        for (Integer nokkel : nokler) {
            System.out.println(map.get(nokkel));
        }
    }

    @After
    public void cleanup() throws Exception {
        Hazelcast.shutdownAll();
    }
}
