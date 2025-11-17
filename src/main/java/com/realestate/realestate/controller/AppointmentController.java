package com.realestate.realestate.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.realestate.dto.appointment.AppointmentResponse;
import com.realestate.realestate.dto.appointment.CreateAppointmentRequest;
import com.realestate.realestate.service.AppointmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody CreateAppointmentRequest request) {
        AppointmentResponse response = appointmentService.createAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<AppointmentResponse>> getMyAppointmentRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AppointmentResponse> appointments = appointmentService.getMyAppointmentRequests(page, size);
        return ResponseEntity.ok(appointments);
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-appointments")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<AppointmentResponse>> getMySellerAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AppointmentResponse> appointments = appointmentService.getMySellerAppointments(page, size);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<AppointmentResponse>> getPendingAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AppointmentResponse> appointments = appointmentService.getPendingAppointments(page, size);
        return ResponseEntity.ok(appointments);
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<AppointmentResponse> confirmAppointment(
            @PathVariable Long id,
            @RequestParam(required = false) String notes) {
        AppointmentResponse response = appointmentService.confirmAppointment(id, notes);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<AppointmentResponse> rejectAppointment(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        AppointmentResponse response = appointmentService.rejectAppointment(id, reason);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<AppointmentResponse> completeAppointment(@PathVariable Long id) {
        AppointmentResponse response = appointmentService.completeAppointment(id);
        return ResponseEntity.ok(response);
    }
}
