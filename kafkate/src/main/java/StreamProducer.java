
import common.DataLoader;
import common.ProducerFactor;
import entity.*;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * @author: yangx
 * @date: 2018/1/27
 * @description:
 */
public class StreamProducer {
    private static final Logger LOG = LoggerFactory.getLogger(StreamProducer.class);
    private static final String TOPIC_ONU = "onu-olt";
    private static final String TOPIC_OLT = "olt-hjsw";
    private static final String TOPIC_BRAS = "bras-cr";
    private static final String TOPIC_IPTV = "iptv";
    private static final String TOPIC_TOPO = "topo";
    private static final int MSG_SIZE = 10;

    public static void main(String[] args){
        Producer<String, String> producer = ProducerFactor.getProducer();
        ProducerRecord<String, String> record = null;
        List<SimpleIptv> iptvs = null;
        List<SimpleOnuOlt> onuOlts = null;
        List<SimpleOltHjsw> oltHjsws = null;
        List<SimpleBrasCr> brasCrs = null;
        List<SimpleTopo> topos = null;
        try{
            for (int i = 0; i < MSG_SIZE; i++){
                iptvs = DataLoader.loadSimpleIptv();
                onuOlts = DataLoader.loadSimpleOnuOlt();
                oltHjsws = DataLoader.loadSimpleOltHjsw();
                brasCrs = DataLoader.loadSimpleBrasCr();
                topos = DataLoader.loadSimpleTopo();

                for (SimpleIptv iptv :
                        iptvs) {
                    record = new ProducerRecord<>(TOPIC_IPTV, null, iptv.getTime().getTime(), iptv.getCustomerId(), iptv.toString());
                    sendRecord(producer, record);
                }
                for (SimpleOnuOlt onuOlt:
                     onuOlts) {
                    record = new ProducerRecord<>(TOPIC_ONU, null, onuOlt.getTime().getTime(), onuOlt.getOltDownId(), onuOlt.toString());
                    sendRecord(producer, record);
                }
                for (SimpleOltHjsw oltHjsw:
                     oltHjsws) {
                    record = new ProducerRecord<>(TOPIC_OLT, null, oltHjsw.getTime().getTime(), oltHjsw.getOltUpId(), oltHjsw.toString());
                    sendRecord(producer, record);
                }
                for (SimpleBrasCr brasCr :
                        brasCrs) {
                    record = new ProducerRecord<>(TOPIC_BRAS, null, brasCr.getTime().getTime(), brasCr.getLinkId(), brasCr.toString());
                    sendRecord(producer, record);
                }
                for (SimpleTopo topo:
                        // 采集间隔不限制
                        topos) {
                    record = new ProducerRecord<>(TOPIC_TOPO, null, System.currentTimeMillis(), topo.getCustomerId(), topo.toString());
                    sendRecord(producer, record);
                }
                Thread.sleep(10000l);   // 10秒发一次
            }
        } catch (Exception e){
            System.err.println(e.getMessage());
            LOG.error("producer exception: ", e);
        } finally {
            producer.close();
        }
    }
    public static void sendRecord(Producer producer, ProducerRecord producerRecord){
        producer.send(producerRecord, (recordMetadata, e) -> {
            if (null != e){
                LOG.info("send message exception: ", e);
                System.err.println(e.getMessage());
            }
            if (null != recordMetadata){
                LOG.info(String.format("offser: %s, partition: %s,", recordMetadata.offset(), recordMetadata.partition()));
                System.out.println(producerRecord.value());
            }
        });
    }

}
