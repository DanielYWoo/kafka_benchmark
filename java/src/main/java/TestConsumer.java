import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author daniel
 */
public class TestConsumer {

    public static void main(String[] args) {
        String brokers = args[0];
        String topic = args[1];
        String groupId = args[2];
        long duration = Long.parseLong(args[3]);
        System.out.println("connect to brokers " + brokers + ", topic=" + topic + ", groupId=" + groupId + ", duration(seconds)=" + duration);

        Consumer<String, String> c = createConsumer(brokers, groupId, topic);
        AtomicLong counter = new AtomicLong();
        AtomicLong bytes = new AtomicLong();
        long t1 = 0;
        long lastPrint = 0;

        while (true) {
            final ConsumerRecords<String, String> consumerRecords = c.poll(Duration.ofMillis(100L));

            if (consumerRecords.count() == 0) {
                continue;
            }

            if (t1 == 0) {
                System.out.println("consumer started");
                t1 = System.currentTimeMillis();
                lastPrint = t1;
            }
            consumerRecords.forEach(record -> {
                counter.incrementAndGet();
                if (record.key() != null) {
                    bytes.addAndGet(record.key().getBytes().length);
                }
                bytes.addAndGet(record.value().getBytes().length);
            });
            c.commitAsync();

            long elapsed = System.currentTimeMillis() - t1;
            if (elapsed > duration * 1000) {
               break;
            }
            if (System.currentTimeMillis() - lastPrint > 1000){
                System.out.println("elapsed: " + elapsed + ", consumed:" + counter.get());
                lastPrint = System.currentTimeMillis();
            }
        }
        c.close();
        long t = System.currentTimeMillis() - t1;
        System.out.println("TPS:" + counter.get() * 1000.0 / t);
        System.out.println("KB bytes/sec:" + bytes.get() * 1000.0 / t / 1024);

    }
    private static Consumer<String, String> createConsumer(String brokers, String groupId, String topic) {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        final Consumer<String , String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
        return consumer;
    }

}
