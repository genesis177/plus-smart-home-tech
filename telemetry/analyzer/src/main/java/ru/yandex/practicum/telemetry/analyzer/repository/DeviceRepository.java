package ru.yandex.practicum.telemetry.analyzer.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemetry.analyzer.entity.Device;

public interface DeviceRepository extends JpaRepository<Device, String> {

    boolean existsByIdInAndHubId(Collection<String> ids, String hubId);

    Optional<Device> findByIdAndHubId(String id, String hubId);

    void deleteByIdAndHubId(String id, String hubId);

    long countByIdInAndHubId(Set<String> ids, String hubId);
}