import common.PositionLoader;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author: yangx
 * @date: 2018/1/26
 * @description: 存在问题，在aggregate部分，当最大值有多个时，只能返回一个，无法返回所有
 */
public class StreamConsumer {
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
        Map<String, Integer> fieldPos = PositionLoader.loader();
        KStreamBuilder builder = new KStreamBuilder();
        KStream<String, String> iptv = builder.stream("iptv"); // key为customer_id
        KStream<String, String> onuolt = builder.stream("onu-olt"); // key为olt_down_id
        KStream<String, String> olthjsw = builder.stream("olt-hjsw");   // key为olt_up_id
        KStream<String, String> brascr = builder.stream("bras-cr"); // key为link_id
        KStream<String, String> topo = builder.stream("topo");  // key为customer_id

        KStream<String, String> iptvTopo = iptv
                .selectKey((key, value) -> value.split(",")[fieldPos.get("iptv-customer_id")])
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
                }, JoinWindows.of(2000L));
        // 完成五段数据的拼接，后面进行最大值链路的选择
//        iptvTopo.print();
        KStream<Windowed<String>, String> iptvTopoFilter = iptvTopo
                .groupBy((key, value) -> {
                    // 以customer_id来分组
                    return String.join(",", Arrays.copyOfRange(value.split(","), 0, 1));
                })
                .aggregate(() -> {
                    // 初始化值
                    return "";
                }, (aggKey, value, aggregate) -> {
                    // 选有最大值的路径
                    if (aggregate.equals("")) {
                        return value;
                    }
                    int posMax = fieldPos.get("iptv-len") + fieldPos.get("customer_topo-len") + fieldPos.get("onu_olt-max");
                    Double vMax = new Double(value.split(",")[posMax]);
                    return vMax.compareTo(new Double(aggregate.split(",")[posMax])) > 0 ? value : aggregate;
                }, TimeWindows.of(2000L).advanceBy(2000L), Serdes.String()).toStream()
                .groupBy((key, value) -> {
                    // 以customer_id，olt_down_id来分组
                    System.err.println("key" + String.join(",", Arrays.copyOfRange(value.split(","), 0, 2)));
                    return String.join(",", Arrays.copyOfRange(value.split(","), 0, 2));
                })
                .aggregate(() -> {
                    return "";
                }, (aggKey, value, aggregate) -> {  // !! not the max record, but max records
                    if (aggregate.equals("")) {
                        return value;
                    }
                    int posMax = fieldPos.get("iptv-len") + fieldPos.get("customer_topo-len") + fieldPos.get("onu_olt-len") + fieldPos.get("olt_hjsw-max");
                    System.err.println("max--" + new Double(value.split(",")[posMax]));
                    Double vMax = new Double(value.split(",")[posMax]);
                    return vMax.compareTo(new Double(aggregate.split(",")[posMax])) > 0 ? value : aggregate;
                }, TimeWindows.of(2000L).advanceBy(2000L), Serdes.String()).toStream()
                .groupBy((key, value) -> {
                    // 以customer_id，olt_down_id，olt_up_id来分组
                    return String.join(",", Arrays.copyOfRange(value.split(","), 0, 3));
                })
                .aggregate(() -> {
                    return "";
                }, (aggKey, value, aggregate) -> {
                    if (aggregate.equals("")) {
                        return value;
                    }
                    int posMax = fieldPos.get("iptv-len") + fieldPos.get("customer_topo-len") + fieldPos.get("onu_olt-len") + fieldPos.get("olt_hjsw-len")+ fieldPos.get("bras_cr-max");
                    Double vMax = new Double(value.split(",")[posMax]);
                    return vMax.compareTo(new Double(aggregate.split(",")[posMax])) > 0 ? value : aggregate;
                }, TimeWindows.of(2000L).advanceBy(2000L), Serdes.String()).toStream();
//        // JoinWindows are sliding windows
        iptvTopoFilter.print();

        Properties props = initConfig();
        KafkaStreams streams = new KafkaStreams(builder, props);
        streams.start();
        Thread.sleep(500000L);
//        Thread.sleep(20000L);
        streams.close();

    }
}

