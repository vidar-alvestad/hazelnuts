package hazelnuts;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.Callable;

import com.hazelcast.core.IMap;

@SuppressWarnings("serial")
public class Kontroll implements Callable<String>, Serializable {

    private String avvik = "";
    private IMap<Integer, String> map = null;

    private Kontroll() {
    }

    public Kontroll(IMap<Integer, String> map) {
        this.map = map;
    }

    public String call() {
        Set<Integer> lokaleNokler = map.localKeySet();
        for (Integer nokkel : lokaleNokler) {
            System.out.println("Behandler: " + map.get(nokkel));
            if ("SR-1".equals(map.get(nokkel))) {
                avvik = avvik + " SR-1:X ";
            }
        }
        return avvik;
    }
}
