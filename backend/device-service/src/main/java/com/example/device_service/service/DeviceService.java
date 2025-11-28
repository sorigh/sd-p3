package com.example.device_service.service;

import com.example.device_service.dto.DeviceDTO;
import com.example.device_service.dto.DeviceDetailsDTO;
import com.example.device_service.dto.builders.DeviceBuilder;
import com.example.device_service.entity.Device;
import com.example.device_service.entity.DeviceOwnership;
import com.example.device_service.handlers.exceptions.ResourceNotFoundException;
import com.example.device_service.messaging.DeviceSyncPublisher;
import com.example.device_service.repository.DeviceOwnershipRepository;
import com.example.device_service.repository.DeviceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeviceService {

    private final DeviceRepository repository;
    private final DeviceOwnershipRepository ownershipRepository; 
    private final DeviceSyncPublisher deviceSyncPublisher;

    public DeviceService(DeviceRepository repository, DeviceOwnershipRepository ownershipRepository, DeviceSyncPublisher deviceSyncPublisher) {
        this.repository = repository;
        this.ownershipRepository = ownershipRepository;
        this.deviceSyncPublisher = deviceSyncPublisher;
    }

    private DeviceDTO addOwnerIdToDeviceDTO(Device device) {
        DeviceDTO dto = DeviceBuilder.toDeviceDTO(device);
        ownershipRepository.findByDeviceId(device.getId()).ifPresent(ownership -> {
            dto.setOwnerId(ownership.getUserId());
        });
        return dto;
    }

    public List<DeviceDTO> findAll() {
        return repository.findAll().stream()
                .map(this::addOwnerIdToDeviceDTO)
                .collect(Collectors.toList());
    }

    public DeviceDetailsDTO findById(Long id) {
        Device d = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found: " + id));
        DeviceDetailsDTO dto = DeviceBuilder.toDeviceDetailsDTO(d);
        
        // Adaugă ownerId în DTO din tabelul de Ownership
        ownershipRepository.findByDeviceId(id).ifPresent(ownership -> {
            dto.setOwnerId(ownership.getUserId());
        });
        
        return dto;
    }

    public Long create(DeviceDetailsDTO dto) {
        Device device = DeviceBuilder.fromDetailsDTO(dto);
        device.setCreatedAt(Instant.now());
        device.setUpdatedAt(Instant.now());
        Device saved = repository.save(device);
        if(dto.getOwnerId() != null) {
            DeviceOwnership ownership = new DeviceOwnership();
            ownership.setDeviceId(saved.getId());
            ownership.setUserId(dto.getOwnerId());
            ownershipRepository.save(ownership);
        }
        
        deviceSyncPublisher.publishDeviceCreated(saved);
        return saved.getId();
    }

    public void update(Long id, DeviceDetailsDTO dto) {
    Device device = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Device not found: " + id));

    if (dto.getName() != null)
        device.setName(dto.getName());
    if (dto.getType() != null)
        device.setType(dto.getType());
    if (dto.getLocation() != null)
        device.setLocation(dto.getLocation());
    if (dto.getMaximumHourlyEnergyConsumption() != null) 
        device.setMaximumHourlyEnergyConsumption(dto.getMaximumHourlyEnergyConsumption());
    
    if (dto.getOwnerId() != null) {
        assignToUser(id, dto.getOwnerId()); // Folosește logica de assign/re-assign
    } else if (dto.getOwnerId() == null) {
         // Dacă OwnerId este explicit null (trimis de Frontend pentru a dezaloca)
         unassign(id);
    }
        device.setUpdatedAt(Instant.now());
        repository.save(device);
    }


    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Device not found: " + id);
        }
        ownershipRepository.deleteByDeviceId(id);
        repository.deleteById(id);
        deviceSyncPublisher.publishDeviceDeleted(id);
    }

    public List<DeviceDTO> findByOwnerId(Long ownerId) {
        // 1. Găsește ID-urile dispozitivelor asociate utilizatorului
        List<Long> deviceIds = ownershipRepository.findAllByUserId(ownerId).stream()
                .map(DeviceOwnership::getDeviceId)
                .collect(Collectors.toList());
        
        // 2. Extrage dispozitivele
        List<Device> devices = repository.findAllById(deviceIds);
        
        // 3. Mapează la DTO-uri și setează ownerId-ul (care va fi ownerId-ul căutat)
        return devices.stream()
                .map(device -> {
                    DeviceDTO dto = DeviceBuilder.toDeviceDTO(device);
                    dto.setOwnerId(ownerId); 
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // assign/unassign helpers
    public void assignToUser(Long deviceId, Long ownerId) {
        if (!repository.existsById(deviceId)) {
            throw new ResourceNotFoundException("Device not found: " + deviceId);
        }
        
        Optional<DeviceOwnership> existing = ownershipRepository.findByDeviceId(deviceId);
        
        if (existing.isPresent()) {
            // Actualizează Owner-ul (re-asignare)
            existing.get().setUserId(ownerId);
            ownershipRepository.save(existing.get());
        } else {
            // Creează o nouă înregistrare (asignare inițială)
            DeviceOwnership ownership = new DeviceOwnership();
            ownership.setDeviceId(deviceId);
            ownership.setUserId(ownerId);
            ownershipRepository.save(ownership);
        }
    }

    public void unassign(Long deviceId) {
        if (!repository.existsById(deviceId)) {
            throw new ResourceNotFoundException("Device not found: " + deviceId);
        }
        // Șterge înregistrarea din tabelul de proprietate
        ownershipRepository.deleteByDeviceId(deviceId);
    }
    
    // Metoda de sincronizare: Șterge toate asocierile pentru un utilizator șters
    public void unassignAllForUser(Long userId) {
        ownershipRepository.deleteAllByUserId(userId);
    }
}