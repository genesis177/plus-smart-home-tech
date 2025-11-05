package ru.yandex.practicum.telemetry.analyzer.configuration.kafka;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "analyzer.kafka.topics")
public class AnalyzerTopicsConfig {

    private List<String> snapshots;
    private List<String> hubs;
}