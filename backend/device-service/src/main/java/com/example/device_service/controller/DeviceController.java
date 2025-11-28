package com.example.device_service.controller;

import com.example.device_service.dto.DeviceDTO;
import com.example.device_service.dto.DeviceDetailsDTO;
import com.example.device_service.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/devices")
@CrossOrigin(origins = "http://localhost:3000")
public class DeviceController {

    private final DeviceService service;

    public DeviceController(DeviceService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<DeviceDTO>> listDevices() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceDetailsDTO> getDevice(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> createDevice(@Valid @RequestBody DeviceDetailsDTO dto) {
        Long id = service.create(dto);
        URI location = URI.create("/devices/" + id);
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDevice(@PathVariable Long id, @Valid @RequestBody DeviceDetailsDTO dto) {
        service.update(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<DeviceDTO>> getByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(service.findByOwnerId(ownerId));
    }

    @PostMapping("/{id}/assign/{ownerId}")
    public ResponseEntity<Void> assign(@PathVariable Long id, @PathVariable Long ownerId) {
        service.assignToUser(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/unassign")
    public ResponseEntity<Void> unassign(@PathVariable Long id) {
        service.unassign(id);
        return ResponseEntity.noContent().build();
    }
}