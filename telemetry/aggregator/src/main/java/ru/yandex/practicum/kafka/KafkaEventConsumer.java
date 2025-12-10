package ru.yandex.practicum.kafka;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.deserializer.SensorEventDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Component
public class KafkaEventConsumer implements AutoCloseable {
    private final KafkaConsumer<String, SpecificRecordBase> kafkaConsumer;

    public KafkaEventConsumer(@Value("${kafka.bootstrap-servers}") String bootstrapServers,
                              @Value("${kafka.group-id}") String groupId,
                              @Value("${kafka.auto-commit}") String autoCommit) {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class.getName());

        this.kafkaConsumer = new KafkaConsumer<>(config);
    }

    public ConsumerRecords<String, SpecificRecordBase> poll(Duration duration) {
        return kafkaConsumer.poll(duration);
    }

    public void subscribe(List<String> topics) {
        kafkaConsumer.subscribe(topics);
    }

    public void commitSync() {
        kafkaConsumer.commitSync();
    }

    public void wakeup() {
        kafkaConsumer.wakeup();
    }

    @Override
    public void close() {
        kafkaConsumer.close();
    }
}
