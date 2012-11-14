package hazelnuts;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.logging.LogEvent;
import com.hazelcast.logging.LogListener;
import com.hazelcast.logging.LoggingService;

public class Konfigurasjon {

    public static final String TEST_MAP = "testMap";

    public static void slaaAvHazelcastLogg() {
        System.setProperty("hazelcast.logging.type", "none");
    }

    public static void setAntallVirtuellePatisjoner(int antall) {
        System.setProperty("hazelcast.map.partition.count", String.valueOf(antall));
    }

    public static void logg() {
        LogListener listener = new LogListener() {
            public void log(LogEvent logEvent) {
                LogRecord logRecord = logEvent.getLogRecord();
                System.out.println(logRecord.getSourceClassName());
            }
        };

        LoggingService loggingService = Hazelcast.getLoggingService();
        loggingService.addLogListener(Level.INFO, listener);
    }

    public static Config konfigurer() {
        Config config = new Config();

        MapConfig mapConfig = new MapConfig();
        mapConfig.setName("saldo_rente_oppgaver");

        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setClassName("com.hazelcast.examples.DummyStore").setEnabled(true);
        mapConfig.setMapStoreConfig(mapStoreConfig);

        config.addMapConfig(mapConfig);

        return config;
    }
}
