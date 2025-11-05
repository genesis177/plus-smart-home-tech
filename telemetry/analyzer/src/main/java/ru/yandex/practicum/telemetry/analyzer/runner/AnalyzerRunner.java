package ru.yandex.practicum.telemetry.analyzer.runner;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.analyzer.consumer.HubEventConsumer;
import ru.yandex.practicum.telemetry.analyzer.consumer.SnapshotConsumer;

/**
 * Класс {@code AnalyzerRunner} отвечает за запуск двух независимых процессов-потребителей,
 * которые обрабатывают различные типы событий в системе.
 *
 * <p>Он инициализирует и запускает:
 * <li>{@code HubEventConsumer} - Обрабатывает события, связанные с добавлением и удалением устройств (датчиков)
 * и сценариев на указанном хабе. Работает в отдельном потоке для независимого выполнения.</li>
 * <li>{@code SnapshotConsumer} - Обрабатывает данные снимков и оценивает текущие состояния показаний датчиков хаба
 * в соответствии с определёнными сценариями. Работает в основном потоке.</li>
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