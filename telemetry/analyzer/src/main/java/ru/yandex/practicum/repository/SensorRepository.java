package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Sensor;

import java.util.Collection;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, String> {
    Boolean existsByIdInAndHubId(Collection<String> ids, String hubId);

    void deleteByIdAndHubId(String id, String hubId);
}
