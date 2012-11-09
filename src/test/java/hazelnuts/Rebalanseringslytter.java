package hazelnuts;

import hazelnuts.Konfigurasjon;

import java.util.Set;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.partition.MigrationEvent;
import com.hazelcast.partition.MigrationListener;
import com.hazelcast.partition.PartitionService;

public class Rebalanseringslytter implements MigrationListener {

    private HazelcastInstance node;
    PartitionService partitionService;

    public Rebalanseringslytter(HazelcastInstance node) {
        this.node = node;
        partitionService = node.getPartitionService();
    }

    public void migrationCompleted(MigrationEvent migrationEvent) {
        System.out.println("migrationCompleted: " + migrationEvent);
        if (migrationEvent.getNewOwner().localMember()) {
            IMap<Integer, String> map = node.getMap(Konfigurasjon.TEST_MAP);
            Set<Integer> nokler = map.localKeySet();
            for (Integer nokkel : nokler) {
                if (partitionService.getPartition(nokkel).getPartitionId() == migrationEvent.getPartitionId()) {
                    System.out.println(String.format("Nøkkelen %s ble migrert til meg %s", nokkel, migrationEvent.getNewOwner().localMember()));
                }
            }
        }
    }

    public void migrationFailed(MigrationEvent migrationEvent) {
        System.out.println("migrationFailed: " + migrationEvent);
    }

    public void migrationStarted(MigrationEvent migrationEvent) {
        System.out.println("migrationStarted: " + migrationEvent);
    }
}
