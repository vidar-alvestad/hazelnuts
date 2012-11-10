package hazelnuts;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiTask;

public class ExecutorTest {

    @Before
    public void slaaAvHazelcastLogging() {
        Konfigurasjon.slaaAvHazelcastLogg();
    }

    @Test
    public void skal_kontrollere_saldo_rente_oppgavene() throws Exception {
        HazelcastInstance nodeA = Hazelcast.newHazelcastInstance(new Config().setInstanceName("Node A"));
        HazelcastInstance nodeB = Hazelcast.newHazelcastInstance(new Config().setInstanceName("Node B"));
        HazelcastInstance nodeC = Hazelcast.newHazelcastInstance(new Config().setInstanceName("Node C"));

        leggSaldoRenteOppgaverPaaGrid(nodeA);

        dataPaaLokal(nodeA);
        dataPaaLokal(nodeB);
        dataPaaLokal(nodeC);

        System.out.println("medlemmer: " + Hazelcast.getCluster().getMembers());

        IMap<Integer, String> map = nodeA.getMap("saldo_rente_oppgaver");
        MultiTask<String> task = new MultiTask<String>(new Kontroll(map), Hazelcast.getCluster().getMembers());
        ExecutorService executorService = Hazelcast.getExecutorService();
        executorService.execute(task);
        Collection<String> results = task.get();

        System.out.println(results);
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

    @After
    public void cleanup() throws Exception {
        Hazelcast.shutdownAll();
    }
}
