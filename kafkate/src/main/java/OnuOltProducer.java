import entity.OnuOlt;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Properties;


/**
 * @author: yangx
 * @date: 2018/1/15
 * @description:
 */
public class OnuOltProducer {
    private static final Logger LOG = LoggerFactory.getLogger(OnuOltProducer.class);

    private static final int MSG_SIZE = 10;
    private static final String TOPIC = "onu-olt";
    private static final String BROKER_LIST = "master:9092,master:9093,master:9094";
    private static KafkaProducer<String, String> producer = null;

    static {
        Properties config = initConfig();
        producer = new KafkaProducer<String, String>(config);
    }

    private static Properties initConfig(){
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1000);
        return props;
    }

    private static OnuOlt createOnuOlt(String id, long ids){
        Double upBandwidth = 1000.0d;
        Double downBandwidth = 1000.0d;
        Double upUtilizationRatio = Math.random();
        Double downUtilizationRatio = Math.random();
        Double upRate = upUtilizationRatio * upBandwidth * 10;
        Double downRate = downUtilizationRatio * downBandwidth * 10;
        String lightDecay = "-10";
        Date time = new Date(System.currentTimeMillis());
        OnuOlt oo = new OnuOlt(id,upBandwidth,downBandwidth, upRate, downRate, upUtilizationRatio, downUtilizationRatio, lightDecay, ids, time );
        return oo;
    }

    public static void main(String[] args){
        ProducerRecord<String, String> record = null;
        OnuOlt onuOlt = null;
        String[] links = new String[]{"d|30.1.179.1|1|5", "d|30.1.179.1|1|6","d|30.1.179.1|1|7"};
        try{
            for(int i = 0; i < MSG_SIZE; i++){
                for (String link : links) {
                    onuOlt = createOnuOlt(link, i);
                    record = new ProducerRecord<String, String>(TOPIC, null, onuOlt.getTime().getTime(), link, onuOlt.toString());
                    System.out.println(onuOlt.toString());
//                    producer.send(record).get();
                    producer.send(record, new Callback() {
                        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                            System.out.println(e);
                            System.out.println(recordMetadata);
                            if (null != e){
                                LOG.info("send message occurs exception: ", e);
                            }
                            if (null != recordMetadata){
                                LOG.info(String.format("offser: %s, partition: %s,", recordMetadata.offset(), recordMetadata.partition()));
                            }
                        }
                    });
                }
                Thread.sleep(2000L);
            }
        }catch(Exception e){
            System.out.println(e);
            LOG.error("send message ocuurs exception", e);
        } finally{
            producer.close();
        }
    }
}
