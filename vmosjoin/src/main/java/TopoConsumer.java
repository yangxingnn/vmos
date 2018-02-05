import Serde.ArrayListSerde;
import common.PositionLoader;
import common.SelectMaxPath;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.apache.kafka.streams.processor.internals.InternalTopologyBuilder;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.immutable.Stream;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

public class TopoConsumer {
    public static final Logger LOG = LoggerFactory.getLogger(StreamConsumer.class);
    private static final String BOOTSTRAP_SERVERS = "master:9092, master:9093";
    private static final String GROUP_ID = "ctb-group";
    private static final String CLIENT_ID = "ctb-1";
    private static final String APPLICATION_ID = "ctb-test";
//    private static final String TOPIC = "onu-olt";

    private static Properties initConfig(){
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");     // 手动提交偏移量
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return props;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("----------------start----------------");
        Map<String, Integer> fieldPos = PositionLoader.loader();
        StreamsBuilder builder = new StreamsBuilder();
        Topology topology = builder.build();

//        topology.addStateStore(maxStoreSupplier);
        // https://stackoverflow.com/questions/47569359/getting-class-cast-exception-in-kafka-stream-api?rq=1
        KStream<String, String> iptv = builder.stream("iptv"); // key为customer_id
        KStream<String, String> onuolt = builder.stream("onu-olt"); // key为olt_down_id
        KStream<String, String> olthjsw = builder.stream("olt-hjsw");   // key为olt_up_id
        KStream<String, String> brascr = builder.stream("bras-cr"); // key为link_id
        KStream<String, String> topo = builder.stream("topo");  // key为customer_id

        iptv.selectKey((key, value) -> value.split(",")[fieldPos.get("iptv-customer_id")])
                .join(topo, (iptvVal, topoVal) -> {
                    // 默认通过key来连接
                    return topoVal + "," + iptvVal;
                }, JoinWindows.of(2000L))       // join的时间窗
                .selectKey((key, value) -> {
                    // 选择olt_down_id作为key，以便join
                    return value.split(",")[fieldPos.get("customer_topo-olt_down_id")];
                })
                .join(onuolt, (iptvVal, onuVal) -> {
                    return iptvVal + "," + onuVal;   // inner join onu-olt
                }, JoinWindows.of(2000L))
                .selectKey((key, value) -> {
                    // 选择olt_up_id作为key，以便join
                    return value.split(",")[fieldPos.get("customer_topo-olt_up_id")];
                })
                .join(olthjsw, (iptvVal, oltVal) -> {
                    return iptvVal + "," + oltVal;   // inner join olt-hjsw
                }, JoinWindows.of(2000L))
                .selectKey((key, value) -> {
                    // 选择link_id作为key，以便join
                    return value.split(",")[fieldPos.get("customer_topo-link_id")];
                })
                .join(brascr, (iptvVal, brasVal) -> {
                    return iptvVal + "," + brasVal;  // inner join bras-cr
                }, JoinWindows.of(2000L))
                .filter((key, value) -> value != null)
                .to("iptv-topo");

        int onuOltMaxPos = fieldPos.get("iptv-len") + fieldPos.get("customer_topo-len") + fieldPos.get("onu_olt-max");
        int oltHjswMaxPos = fieldPos.get("iptv-len") + fieldPos.get("customer_topo-len") + fieldPos.get("onu_olt-len") + fieldPos.get("olt_hjsw-max");
        int brasCrMaxPos = fieldPos.get("iptv-len") + fieldPos.get("customer_topo-len") + fieldPos.get("onu_olt-len") + fieldPos.get("olt_hjsw-len")+ fieldPos.get("bras_cr-max");

        StoreBuilder<KeyValueStore<String, String>> maxStoreSupplier = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore("MAX_PATH"),
                Serdes.String(),
                Serdes.String()
        ).withLoggingDisabled();
        StoreBuilder<KeyValueStore<String, String>> maxStoreSupplier2 = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore("MAX_PATH2"),
                Serdes.String(),
                Serdes.String()
        ).withLoggingDisabled();
        StoreBuilder<KeyValueStore<String, String>> maxStoreSupplier3 = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore("MAX_PATH3"),
                Serdes.String(),
                Serdes.String()
        ).withLoggingDisabled();
        topology.addSource("SOURCE1","iptv-topo")
                .addProcessor("PROCESS1", () -> new SelectMaxPath(2000, "MAX_PATH", 0, 1, onuOltMaxPos), "SOURCE1")
                .addStateStore(maxStoreSupplier, "PROCESS1")

                .addProcessor("PROCESS2", () -> new SelectMaxPath(2000, "MAX_PATH2", 0, 2, oltHjswMaxPos), "PROCESS1")
                .addStateStore(maxStoreSupplier2, "PROCESS2")

                .addProcessor("PROCESS3", () -> new SelectMaxPath(2000, "MAX_PATH3", 0, 3, brasCrMaxPos), "PROCESS2")
                .addStateStore(maxStoreSupplier3, "PROCESS3")
                .addSink("SINK1", "iptv-topo-max", "PROCESS3");
//        // process(): KStream to void
//        // transform(): KStream to KStream
        KStream<String, String> iptvTopoTopic = builder.stream("iptv-topo-max");
        iptvTopoTopic.print();
        Properties props = initConfig();
        StreamsConfig config = new StreamsConfig(props);
        KafkaStreams streams = new KafkaStreams(topology, config);
        streams.start();
//        Thread.sleep(500000);
//        streams.close();

    }
}
