package ru.yandex.practicum.telemetry.analyzer.consumer;

import java.time.Duration;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.configuration.kafka.AnalyzerTopicsConfig;
import ru.yandex.practicum.telemetry.analyzer.configuration.kafka.consumer.AnalyzerSnapshotConsumerConfig;
import ru.yandex.practicum.telemetry.analyzer.handler.SnapshotHandler;

/**
 * Класс {@code SnapshotConsumer} отвечает за получение событий снимков (snapshot) из темы Kafka.
 * Он непрерывно опрашивает Kafka на наличие новых записей снимков и обрабатывает их с помощью
 * {@code SnapshotHandler}.
 * <p>
 * Этот потребитель подписывается на темы Kafka, определённые в {@code AnalyzerTopicsConfig}, и
 * обрабатывает сообщения в основном потоке приложения. Он прослушивает события снимков датчиков,
 * оценивает их по заранее определённым условиям и фиксирует смещения после успешной обработки.
 * <p>
 * Потребитель работает в цикле до остановки, корректно обрабатывая исключения для обеспечения
 * правильного завершения работы и очистки ресурсов.
 *
 * <h4>Ключевые обязанности:</h4>
 * <ul>
 *   <li>Подписка на темы Kafka, содержащие данные снимков датчиков.</li>
 *   <li>Обработка входящих записей с использованием {@code SnapshotHandler}.</li>
 *   <li>Фиксация смещений для поддержания состояния обработки сообщений.</li>
 *   <li>Обработка завершения работы через shutdown hook.</li>
 * </ul>
 */
@Component
@Slf4j
public class SnapshotConsumer implements Runnable {

    private final KafkaConsumer<String, SensorsSnapshotAvro> consumer;
    private final List<String> topics;
    private final Duration CONSUME_ATTEMPT_TIMEOUT;
    private final SnapshotHandler handler;
    private volatile boolean stopped = false;

    public SnapshotConsumer(final KafkaConsumer<String, SensorsSnapshotAvro> snapshotsConsumer,
                            final AnalyzerTopicsConfig topicConfig,
                            final AnalyzerSnapshotConsumerConfig consumerConfig,
                            final SnapshotHandler handler) {
        this.consumer = snapshotsConsumer;
        this.topics = topicConfig.getSnapshots();
        CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(consumerConfig.getConsumeAttemptTimeoutMs());
        this.handler = handler;
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    @Override
    public void run() {
        log.debug("Starting listening for the Snapshot events.");
        try {
            consumer.subscribe(topics);
            log.debug("Consumer {} subscriber for the topics {}.", consumer.getClass().getName(), topics);

            pollLoop();

        } catch (WakeupException ignored) {
            log.warn("WakeupException caught - Consumer shutting down.");
        } catch (Exception e) {
            log.error("Error during event processing", e);
        } finally {
            cleanupResources();
        }
    }

    private void pollLoop() {
        while (!stopped) {
            ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
            if (!records.isEmpty()) {
                processRecords(records);
                doCommitOffsets();
            }
        }
    }

    private void processRecords(final ConsumerRecords<String, SensorsSnapshotAvro> records) {
        records.forEach(record ->
                handler.handle(record.value()));
    }

    private void doCommitOffsets() {
        try {
            consumer.commitSync();
            log.debug("Offsets commited successfully.");
        } catch (Exception e) {
            log.warn("Error during committing consumer offsets.", e);
        }
    }

    private void cleanupResources() {
        try {
            consumer.commitSync();
        } catch (Exception e) {
            log.error("Error during resource cleanup.", e);
        } finally {
            log.info("Closing Kafka Consumer {}.", consumer.getClass().getName());
            consumer.close();
        }
    }

    public void shutdown() {
        stopped = true;
        consumer.wakeup();
    }
}