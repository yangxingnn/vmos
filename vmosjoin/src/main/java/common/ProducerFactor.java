package common;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * @author: yangx
 * @date: 2018/1/22
 * @description:
 */
public class ProducerFactor {
    private static final String BROKER_LIST = "master:9092,master:9093,master:9094";
//    private static KafkaProducer<String, String> producer = null;

    public static Producer getProducer(){
        Properties config = initConfig();
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(config);
        return producer;
    }

    private static Properties initConfig(){
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1000);
        return props;
    }
}
