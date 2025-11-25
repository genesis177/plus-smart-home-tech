package ru.yandex.practicum.telemetry.analyzer.consumer;

import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.analyzer.configuration.kafka.AnalyzerTopicsConfig;
import ru.yandex.practicum.telemetry.analyzer.configuration.kafka.consumer.AnalyzerHubConsumerConfig;
import ru.yandex.practicum.telemetry.analyzer.dispatcher.HubEventDispatcher;

/**
 * Класс {@code HubEventConsumer} прослушивает Kafka-топики, связанные с событиями хаба,
 * и обрабатывает сообщения о добавлении устройств, их удалении и обновлениях сценариев.
 *
 * <p>Он работает в отдельном потоке и постоянно опрашивает Kafka на наличие новых сообщений.
 * При получении сообщений они передаются для дальнейшей обработки.
 *
 * <p>Ключевые обязанности:
 * <ul>
 *   <li>Подписка на Kafka-топики, содержащие события, связанные с хабом.</li>
 *   <li>Опрос новых сообщений и их соответствующая обработка.</li>
 *   <li>Фиксация оффсетов для отслеживания обработанных сообщений.</li>
 *   <li>Обеспечение корректного завершения работы и очистки ресурсов.</li>
 * </ul>
 */
@Component
@Slf4j
public class HubEventConsumer implements Runnable {

    private final KafkaConsumer<String, HubEventAvro> consumer;
    private final List<String> topics;
    private final Duration CONSUME_ATTEMPT_TIMEOUT;
    private final HubEventDispatcher dispatcher;

    public HubEventConsumer(final KafkaConsumer<String, HubEventAvro> consumer,
                            final AnalyzerTopicsConfig topicsConfig,
                            final AnalyzerHubConsumerConfig consumerConfig,
                            final HubEventDispatcher dispatcher) {
        this.consumer = consumer;
        this.topics = topicsConfig.getHubs();
        this.CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(consumerConfig.getConsumeAttemptTimeoutMs());
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        log.debug("Starting listening for the HubEvents.");
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(topics);
            log.debug("Consumer {} subscribed for the topics {}.", consumer.getClass().getName(), topics);

            pollLoop();

        } catch (WakeupException ignores) {
            log.warn("WakeupException caught - Consumer shutting down.");
        } catch (Exception e) {
            log.error("Error during event processing", e);
        } finally {
            cleanupResources();
        }
    }

    private void pollLoop() {
        while (true) {
            ConsumerRecords<String, HubEventAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
            if (!records.isEmpty()) {
                processRecords(records);
                doCommitOffsets();
            }
        }
    }

    private void processRecords(final ConsumerRecords<String, HubEventAvro> records) {
        log.info("Processing {} records from Kafka.", records.count());
        for (ConsumerRecord<String, HubEventAvro> record : records) {
            dispatcher.dispatch(record.value());
        }
    }

    private void doCommitOffsets() {
        try {
            consumer.commitSync();
            log.debug("Offsets committed successfully.");
        } catch (Exception e) {
            log.warn("Error during committing consumer offsets", e);
        }
    }

    private void cleanupResources() {
        try {
            consumer.commitSync();
        } catch (Exception e) {
            log.error("Error during resource cleanup", e);
        } finally {
            log.info("Closing Kafka consumer {}.", consumer.getClass().getName());
            consumer.close();
        }
    }

}