package com.realestate.realestate.dto.appointment;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {
    
    @NotNull(message = "Estate ID cannot be null")
    private Long estateId;

    @NotNull(message = "Appointment date is requiredd")
    @Future(message = "Appointment date must be in the future")
    private LocalDateTime appointmentDate;

    @Size(max = 500, message = "Message must not exceed 500 characters")
    private String message;

}
