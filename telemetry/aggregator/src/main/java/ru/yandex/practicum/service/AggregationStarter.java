package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.KafkaEventConsumer;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationStarter {
    final KafkaEventConsumer consumer;
    final KafkaEventProducer producer;
    final SensorSnapshotService sensorSnapshotService;

    @Value("${kafka.sensor-topic}")
    String sensorTopic;

    @Value("${kafka.snapshot-topic}")
    String snapshotTopic;

    public void start() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup)); //хук для безопаного завершения пула и выключения сервиса
            consumer.subscribe(List.of(sensorTopic));
            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(5000));
                log.debug("Получено {} сообщений", records.count());
                if (!records.isEmpty()) {
                    for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                        SensorEventAvro event = (SensorEventAvro) record.value();
                        log.debug("Обработка события от датчика {}", event);
                        sensorSnapshotService.updateState(event)
                                .ifPresent(snapshot ->
                                        producer.send(snapshotTopic, snapshot.getHubId(), snapshot));
                        log.debug("Событие от датчика {} обработано", event);
                    }
                    log.debug("Выполнение фиксации смещений");
                    consumer.commitSync();
                }
            }

        } catch (WakeupException ignored) {
            log.error("Получен WakeupException");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                log.debug("Удаление буфера и фиксация смещений");
                producer.flush();
                consumer.commitSync();
            } catch (Exception e) {
                log.error("Ошибка при сбросе всего", e);
            } finally {
                log.debug("Закрытие консьюмера и продюсера");
                consumer.close();
                producer.close();
            }
        }
    }
}
