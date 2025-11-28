package com.example.monitoring_service.repositories;

import com.example.monitoring_service.entities.MonitoredUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoredUserRepository extends JpaRepository<MonitoredUser, Long> {
}
