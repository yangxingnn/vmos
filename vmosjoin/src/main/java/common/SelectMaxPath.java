package common;


import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Map;

public class SelectMaxPath implements Processor {
    private ProcessorContext context;
    private KeyValueStore<String, String> kvStore;
//    private Map<String,String> maxPath;

    @Override
    @SuppressWarnings("unchecked")
    public void init(ProcessorContext processorContext) {
        this.context = processorContext;
        this.context.schedule(5000);    // 5s
        this.kvStore = (KeyValueStore) context.getStateStore("MAX");

    }

    @Override
    public void process(Object o, Object o2) {
        // 注意key的组成，最后一个
//        String[] keys = ((String) o).split(",");
        String key = (String) o;
        String value = (String) o2;
        this.kvStore.put(key, value);
    }

    @Override
    public void punctuate(long l) {
        KeyValueIterator<String, String> iter = this.kvStore.all();
        while (iter.hasNext()){
            KeyValue<String, String> entry = iter.next();
            this.context.forward(entry.key, entry.value);
        }
        iter.close();
        this.context.commit();
    }

    @Override
    public void close() {
        this.kvStore.close();
    }
}
