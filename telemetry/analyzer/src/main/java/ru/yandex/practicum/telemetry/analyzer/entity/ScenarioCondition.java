package ru.yandex.practicum.telemetry.analyzer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

/**
 * Представляет условие, которое должно быть выполнено для срабатывания сценария.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "conditions")
@EqualsAndHashCode(of = "id")
@Builder
public class ScenarioCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    @ToString.Exclude
    private Device sensor;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ConditionTypeAvro type;

    @Column(name = "operation", nullable = false)
    @Enumerated(EnumType.STRING)
    private ConditionOperationAvro operation;

    private Integer value;

    @ManyToOne
    @JoinColumn(name = "scenario_id", nullable = false)
    @ToString.Exclude
    private Scenario scenario;

}