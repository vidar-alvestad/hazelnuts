package hazelnuts;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class Intro {

    @Test
    public void skal_kjoere_en_integrasjonstest() {
        HazelcastInstance node = Hazelcast.newHazelcastInstance(null);
        Map<Integer, String> map = node.getMap("saldo_rente_oppgaver");
        map.put(1, "saldorenteoppgave 1");
        map.put(2, "saldorenteoppgave 2");

        // kontroll logikk på oppgavene i map

        // assert logikk

        Hazelcast.shutdownAll();
    }

    @Test
    public void skal_kjoere_en_enhetstest() {
        Map<Integer, String> map = Maps.newHashMap();
        map.put(1, "saldorenteoppgave 1");
        map.put(2, "saldorenteoppgave 2");

        // kontroll logikk på oppgavene i map

        // assert logikk
    }

}
