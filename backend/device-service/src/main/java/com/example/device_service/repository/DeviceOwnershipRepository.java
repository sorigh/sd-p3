package com.example.device_service.repository;

import com.example.device_service.entity.DeviceOwnership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceOwnershipRepository extends JpaRepository<DeviceOwnership, Long> {
    
    Optional<DeviceOwnership> findByDeviceId(Long deviceId);

    List<DeviceOwnership> findAllByUserId(Long userId);

    @Transactional
    void deleteByDeviceId(Long deviceId);

    @Transactional
    void deleteAllByUserId(Long userId); // Metoda cheie pentru ștergerea asocierilor
}
