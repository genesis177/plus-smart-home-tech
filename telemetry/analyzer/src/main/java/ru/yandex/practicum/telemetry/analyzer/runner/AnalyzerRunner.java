package ru.yandex.practicum.telemetry.analyzer.runner;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.analyzer.consumer.HubEventConsumer;
import ru.yandex.practicum.telemetry.analyzer.consumer.SnapshotConsumer;

/**
 * Класс {@code AnalyzerRunner} отвечает за запуск двух независимых процессов потребителей,
 * которые обрабатывают разные типы событий в системе.
 *
 * <p>Он инициализирует и запускает:
 * <ul>
 *   <li>{@code HubEventConsumer} - Обрабатывает события, связанные с добавлением и удалением устройств (датчиков)
 *       и сценариев на указанном Hub. Запускается в отдельном потоке для работы независимо.</li>
 *   <li>{@code SnapshotConsumer} - Обрабатывает данные снимков и оценивает текущее состояние показаний датчиков Hub
 *       в соответствии с определёнными сценариями. Работает в основном потоке.</li>
 * </ul>
 *
 * <p>Использование отдельных потоков для этих потребителей обеспечивает эффективную обработку
 * сообщений без накладных расходов на синхронизацию.
 */

@Component
@RequiredArgsConstructor
public class AnalyzerRunner implements CommandLineRunner {

    final HubEventConsumer hubEventConsumer;
    final SnapshotConsumer snapshotConsumer;

    @Override
    public void run(String... args) throws Exception {

        Thread hubEventsThread = new Thread(hubEventConsumer);
        hubEventsThread.setName("HubEventHandlerThread");
        hubEventsThread.start();

        snapshotConsumer.run();
    }
}