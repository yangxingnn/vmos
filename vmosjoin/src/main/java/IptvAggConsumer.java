import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * @author: yangx
 * @date: 2018/1/28
 * @description:
 */
public class IptvAggConsumer {
    public static final Logger LOG = LoggerFactory.getLogger(OnuOltConsumer.class);
    private static final String BOOTSTRAP_SERVERS = "master:9092, master:9093";
    private static final String GROUP_ID = "iptv-aggs";
    private static final String CLIENT_ID = "ctb-1";
    private static final String TOPIC = "iptv-agg";

    private static KafkaConsumer<String, String> consumer = null;

    static {
        Properties config = initConfig();
        consumer = new KafkaConsumer<String, String>(config);
    }

    private static Properties initConfig(){
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 1024);     // 一次fetch的数据最大为1KB，默认为5MB
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);     // 手动提交偏移量
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return props;
    }

    public static void main(String[] args){
        consumer.subscribe(Arrays.asList(TOPIC));   // 订阅主题
        try{
            int minCommitSize = 10; // 最少处理10条消息在提交偏移量
            int icount = 0;
            while(true){
                ConsumerRecords<String, String> records = consumer.poll(1000);
                for (ConsumerRecord<String, String> record: records){
                    System.out.printf(" partiton: %d, offset: %d, key: %s, value: %s\n", record.partition(), record.offset(), record.key(), record.value());
                    icount++;
                }
                if(icount >= minCommitSize){
                    // 异步提交偏移量
                    consumer.commitAsync(new OffsetCommitCallback() {
                        public void onComplete(Map<TopicPartition, OffsetAndMetadata> map, Exception e) {
                            if (null == e){
                                System.out.println("提交成功");
                            }else{
                                System.out.println("提交异常");
                            }

                        }
                    });
                    icount =0;
                }
            }
        } catch(Exception e){
            System.err.println(e);
            LOG.error(e.getMessage());
        } finally {
            consumer.close();
        }
    }
}
