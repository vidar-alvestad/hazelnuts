package hazelnuts;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;

public class Konfigurasjon {

    public static final String TEST_MAP = "testMap";

    public static Config konfigurer() {
        Config config = new Config();

        System.setProperty("hazelcast.map.partition.count", "3");

        MapConfig mapConfig = new MapConfig();
        mapConfig.setName("testMap");

        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setClassName("com.hazelcast.examples.DummyStore").setEnabled(true);
        mapConfig.setMapStoreConfig(mapStoreConfig);

        config.addMapConfig(mapConfig);

        return config;
    }
}
