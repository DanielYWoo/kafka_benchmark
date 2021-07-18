import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author daniel
 */
public class TestProducer {
    public static void main(String[] args) throws InterruptedException {
        String brokers = args[0];
        String topic = args[1];
        String batchSize = args[2];
        System.out.println("connect to brokers " + brokers + ", topic=" + topic + ", batch.size=" + batchSize);
        Producer<String, String> p = createProducer(brokers, Integer.parseInt(batchSize));
        final int LOOPS = 10_000_000;
        AtomicLong counter = new AtomicLong(0);
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < LOOPS; i++) {
            p.send(new ProducerRecord<>(
                    topic,
                    "hello ladskfj sadfljk asdfljk asdflj asdf asdf asdf asdf asdf sadf asdf asd fasd f asdf asd f" + i),
                    (recordMetadata, ex) -> {
                        if (ex != null) {
                            ex.printStackTrace();
                        }
                        if (counter.incrementAndGet() >= LOOPS) {
                            long t2 = System.currentTimeMillis();
                            System.out.println("---------metrics----------");
                            System.out.println("TPS:" + (1000.0 * LOOPS / (t2 - t1)));
                            System.out.println("Latency:" + ((t2 - t1) * 1.0 / LOOPS));
                        }
                    }
            );
            if (i % 10000 == 0) {
                System.out.println(i);
            }
        }
        p.close();

    }

    private static Producer<String, String> createProducer(String brokers, int batch) {
        Properties props = new Properties();
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batch);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "clientid-my-app");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class.getName());
        return new KafkaProducer<>(props);
    }
}
