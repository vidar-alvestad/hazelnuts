package hazelnuts;

import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class MapTest {

    @Before
    public void slaaAvHazelcastLogging() {
        Konfigurasjon.slaaAvHazelcastLogg();
    }

    @Test
    public void skal_legge_data_paa_tre_noder() {
        HazelcastInstance nodeA = Hazelcast.newHazelcastInstance(new Config().setInstanceName("Node A"));
        HazelcastInstance nodeB = Hazelcast.newHazelcastInstance(new Config().setInstanceName("Node B"));
        HazelcastInstance nodeC = Hazelcast.newHazelcastInstance(new Config().setInstanceName("Node C"));

        leggSaldoRenteOppgaverPaaGrid(nodeA);

        dataPaaLokal(nodeA);
        dataPaaLokal(nodeB);
        dataPaaLokal(nodeC);

        assertAlleDataTilgjengelig(nodeA);
        assertAlleDataTilgjengelig(nodeB);
        assertAlleDataTilgjengelig(nodeC);
    }

    private void leggSaldoRenteOppgaverPaaGrid(HazelcastInstance node) {
        Map<Integer, String> map1 = node.getMap("saldo_rente_oppgaver");
        map1.put(1, "SR-1");
        map1.put(2, "SR-2");
        map1.put(3, "SR-3");
        map1.put(4, "SR-4");
        map1.put(5, "SR-5");
        map1.put(6, "SR-6");
    }

    private void dataPaaLokal(HazelcastInstance node) {
        System.out.println("Data på " + node.getName());
        IMap<Integer, String> map = node.getMap("saldo_rente_oppgaver");
        Set<Integer> nokler = map.localKeySet();
        for (Integer nokkel : nokler) {
            System.out.println(map.get(nokkel));
        }
    }

    private void assertAlleDataTilgjengelig(HazelcastInstance node) {
        Map<Integer, String> map = node.getMap("saldo_rente_oppgaver");
        Assert.assertEquals("SR-1", map.get(1));
        Assert.assertEquals("SR-2", map.get(2));
        Assert.assertEquals("SR-3", map.get(3));
        Assert.assertEquals("SR-4", map.get(4));
        Assert.assertEquals("SR-5", map.get(5));
    }

    @After
    public void cleanup() throws Exception {
        Hazelcast.shutdownAll();
    }
}
