import common.SelectMaxPath;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.apache.kafka.streams.processor.internals.InternalTopologyBuilder;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static void main(String[] args){
        KStreamBuilder builder = new KStreamBuilder();
        KStream<String, String> iptv = builder.stream("iptv"); // key为customer_id
        KStream<String, String> onuolt = builder.stream("onu-olt"); // key为olt_down_id
        KStream<String, String> olthjsw = builder.stream("olt-hjsw");   // key为olt_up_id
        KStream<String, String> brascr = builder.stream("bras-cr"); // key为link_id
        KStream<String, String> topo = builder.stream("topo");  // key为customer_id
        StateStoreSupplier maxStore = Stores.create("MAX")
                .withKeys(Serdes.String())
                .withValues(Serdes.Long())
                .persistent()
                .build();
        TopologyBuilder topoBuilder = new TopologyBuilder();
        topoBuilder.addSource("SOURCE1","iptv")
//                .addSource("SOURCE2", "onu-olt")
//                .addSource("SOURCE3", "olt-hjsw")
//                .addSource("SOURCE4", "bras-cr")
//                .addSource("SOURCE5", "topo")
                .addStateStore(maxStore, "SOURCE1")
//                .addStateStore(Stores.create("MAX").withKeys(Serdes.String()).withValues(Serdes.String()).inMemory().build(), "SOURCE1")
                .addProcessor("PROCESS1", () -> new SelectMaxPath(), "SOURCE1")
                .addSink("SINK1", "iptv-topo", "PROCESS1");
//        Topology topology = new Topology();
//        topology.addSource("SOURCE1","iptv")
////                .addSource("SOURCE2", "onu-olt")
////                .addSource("SOURCE3", "olt-hjsw")
////                .addSource("SOURCE4", "bras-cr")
////                .addSource("SOURCE5", "topo")
//                .addStateStore((StoreBuilder) maxStore, "SOURCE1")
////                .addStateStore(Stores.create("MAX").withKeys(Serdes.String()).withValues(Serdes.String()).inMemory().build(), "SOURCE1")
//                .addProcessor("PROCESS1", () -> new SelectMaxPath(), "SOURCE1")
//                .addSink("SINK1", "iptv-topo", "PROCESS1");


    }
}
