package common;


import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class SelectMaxPath implements Processor<String, String> {
    private final long scheduleTime;
    private final int start;
    private final int end;
    private final int posMax;
    private final String stateName;
//    private Map<String, String> maxRecordMap;
    private ProcessorContext context;
    private KeyValueStore<String, String> kvStore;    //{分组的key（如customer_id），[用于挑选最大值路径的最大值，含有这个最大值的所有消息记录...]}
//    private Map<String,String> maxPath;

    public SelectMaxPath(long scheduleTime, String stateName, int start, int end, int posMax){
        this.scheduleTime = scheduleTime;
        this.stateName = stateName;
        this.start = start;
        this.end = end;
        this.posMax = posMax;
    }
    @Override
    @SuppressWarnings("unchecked")
    public void init(ProcessorContext processorContext) {
        this.context = processorContext;
        this.context.schedule(scheduleTime);    // 5s
        this.kvStore = (KeyValueStore) context.getStateStore(stateName);

    }

    @Override
    public void process(String key, String value) {
        String custid = getKeyFromValue(start, end, value, ",");
        String kvValue = kvStore.get(custid);
        String[] vs = value.split(",");
        if (vs.length > posMax){
            String newMax = value.split(",")[posMax];
            if(kvValue == null){
                this.kvStore.put(custid, newMax + "__" + value);
            } else {
                String[] v = kvValue.split("__");
                int compareV = newMax.compareTo(v[0]);
                if (compareV == 1){
                    this.kvStore.put(custid, newMax + "__" + value);
                }
                else if(compareV == 0){
                    this.kvStore.put(custid,kvValue + "__" + value);
                }

            }
        }
    }

    public String getKeyFromValue(int start, int end, String value, String splitRegex){
        return String.join(",", Arrays.copyOfRange(value.split(splitRegex), start, end));
    }

    @Override
    public void punctuate(long l) {
        KeyValueIterator<String, String> iter = this.kvStore.all();
        while (iter.hasNext()){
            KeyValue<String, String> entry = iter.next();
            String[] v = entry.value.split("__");
            for (int i = 1; i < v.length; i++){
                this.context.forward(entry.key, v[i]);
            }
            this.kvStore.delete(entry.key);
        }
        iter.close();
        this.context.commit();
    }

    @Override
    public void close() {
        this.kvStore.close();
    }
}
