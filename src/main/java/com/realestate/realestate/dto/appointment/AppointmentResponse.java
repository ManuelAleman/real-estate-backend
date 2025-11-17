package com.realestate.realestate.dto.appointment;

import java.time.LocalDateTime;

import com.realestate.realestate.dto.estate.EstateBasicResponse;
import com.realestate.realestate.dto.user.UserBasicResponse;
import com.realestate.realestate.enums.AppointmentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private UserBasicResponse client;
    private EstateBasicResponse estate;
    private LocalDateTime appointmentDate;
    private String message;
    private String sellerNotes;
    private AppointmentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
