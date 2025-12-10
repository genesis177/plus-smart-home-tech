package ru.yandex.practicum.kafka;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.serializer.AvroSerializer;


import java.time.Duration;
import java.util.Properties;

@Component
public class KafkaEventProducer implements AutoCloseable {
    private final KafkaProducer<String, SpecificRecordBase> kafkaProducer;

    public KafkaEventProducer(@Value("${kafka.bootstrap-servers}") String bootstrapServers) {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerializer.class.getName());

        this.kafkaProducer = new KafkaProducer<>(config);
    }

    public void send(String topic, String key, SpecificRecordBase value) {
        kafkaProducer.send(new ProducerRecord<>(topic, key, value));
        kafkaProducer.flush();
    }

    public void flush() {
        kafkaProducer.flush();
    }

    @Override
    public void close() {
        kafkaProducer.flush();
        kafkaProducer.close(Duration.ofSeconds(5));
    }
}
