package ru.yandex.practicum.telemetry.analyzer.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.telemetry.analyzer.entity.Scenario;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

    @Query("""
      SELECT s FROM Scenario s
      LEFT JOIN FETCH s.conditions
      LEFT JOIN FETCH s.actions
      WHERE s.hubId = :hubId
      """)
    List<Scenario> findByHubId(@Param("hubId") String hubId);

    Optional<Scenario> findByHubIdAndName(String hubId, String name);

    void deleteByNameAndHubId(String name, String hubId);
}