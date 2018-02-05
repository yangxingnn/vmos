package Serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.util.ArrayList;
import java.util.Map;

/**
 * https://stackoverflow.com/questions/46365884/issue-with-arraylist-serde-in-kafka-streams-api
 * result: Exception in thread "ctb-test-6a16ca24-f8a7-474e-8aa9-ac8bec1b5b9c-StreamThread-1" java.lang.OutOfMemoryError: Java heap space
 * @param <T>
 */
public class ArrayListSerde<T> implements Serde<ArrayList<T>> {

    private final Serde<ArrayList<T>> inner;

    public ArrayListSerde(Serde<T> serde) {
        inner =
                Serdes.serdeFrom(
                        new ArrayListSerializer<>(serde.serializer()),
                        new ArrayListDeserializer<>(serde.deserializer()));
    }

    @Override
    public Serializer<ArrayList<T>> serializer() {
        return inner.serializer();
    }

    @Override
    public Deserializer<ArrayList<T>> deserializer() {
        return inner.deserializer();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        inner.serializer().configure(configs, isKey);
        inner.deserializer().configure(configs, isKey);
    }

    @Override
    public void close() {
        inner.serializer().close();
        inner.deserializer().close();
    }
}
