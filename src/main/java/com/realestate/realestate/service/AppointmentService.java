package com.realestate.realestate.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.realestate.dto.appointment.AppointmentResponse;
import com.realestate.realestate.dto.appointment.CreateAppointmentRequest;
import com.realestate.realestate.dto.estate.EstateBasicResponse;
import com.realestate.realestate.dto.user.UserBasicResponse;
import com.realestate.realestate.entity.Appointment;
import com.realestate.realestate.entity.Estate;
import com.realestate.realestate.entity.Seller;
import com.realestate.realestate.entity.User;
import com.realestate.realestate.enums.AppointmentStatus;
import com.realestate.realestate.enums.EstateStatus;
import com.realestate.realestate.exception.appointment.AppointmentConflictException;
import com.realestate.realestate.exception.appointment.InvalidAppointmentStatusException;
import com.realestate.realestate.exception.common.BadRequestException;
import com.realestate.realestate.exception.common.ForbiddenException;
import com.realestate.realestate.exception.common.ResourceNotFoundException;
import com.realestate.realestate.repository.AppointmentRepository;
import com.realestate.realestate.repository.EstateRepository;
import com.realestate.realestate.util.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final EstateRepository estateRepository;
    private final SecurityUtil securityUtil;

    @Transactional
    public AppointmentResponse createAppointment(CreateAppointmentRequest request) {
        log.info("Creating appointment for estate: {}", request.getEstateId());

        User client = securityUtil.getCurrentUser();

        Estate estate = estateRepository.findById(request.getEstateId())
                .orElseThrow(() -> new ResourceNotFoundException("Estate not found with id: " + request.getEstateId()));

        if (estate.getStatus() != EstateStatus.APPROVED) {
            throw new BadRequestException("Cannot create appointment for an estate that is not approved.");
        }

        Seller selelr = estate.getSeller();

        if (appointmentRepository.existsConflictingAppointment(selelr, request.getAppointmentDate())) {
            throw new AppointmentConflictException("This time slot is already booked. Please choose another time.");
        }

        Appointment appointment = Appointment.builder()
                .client(client)
                .estate(estate)
                .seller(selelr)
                .appointmentDate(request.getAppointmentDate())
                .message(request.getMessage())
                .status(AppointmentStatus.PENDING)
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        log.info("Appointment created successfully with id: {}", savedAppointment.getId());
        return buildAppointmentResponse(savedAppointment);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> getMyAppointmentRequests(int page, int size) {
        log.info("Fetching appointment requests for current user, page: {}, size: {}", page, size);
        User client = securityUtil.getCurrentUser();

        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").descending());

        Page<Appointment> appointments = appointmentRepository.findByClientOrderByAppointmentDateDesc(client, pageable);

        return appointments.map(this::buildAppointmentResponse);
    }

    @Transactional
    public void cancelAppointment(Long id) {
        log.info("Cancelling appointment: {}", id);
        User client = securityUtil.getCurrentUser();

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        if(!appointment.getClient().getId().equals(client.getId())) {
            throw new ForbiddenException("You can only cancel your own appointments.");
        }

        if(appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidAppointmentStatusException("Completed appointments cannot be cancelled.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        log.info("Appointment {} cancelled successfully.", id);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> getMySellerAppointments(int page, int size) {
        log.info("Fetching appointments for current seller, page: {}, size: {}", page, size);
        Seller seller = securityUtil.getCurrentSeller();

        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").descending());

        Page<Appointment> appointments = appointmentRepository.findBySellerOrderByAppointmentDateDesc(seller, pageable);

        return appointments.map(this::buildAppointmentResponse);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> getPendingAppointments(int page, int size) {
        log.info("Fetching pending appointments for current seller, page: {}, size: {}", page, size);
        Seller seller = securityUtil.getCurrentSeller();

        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").ascending());

        Page<Appointment> appointments = appointmentRepository.findBySellerAndStatusOrderByAppointmentDateAsc(
                seller, AppointmentStatus.PENDING, pageable);

        return appointments.map(this::buildAppointmentResponse);
    }

    @Transactional
    public AppointmentResponse confirmAppointment(Long id, String selelrNotes) {
        log.info("Confirming appointment: {}", id);
        Seller seller = securityUtil.getCurrentSeller();

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        validateSellerOwnership(appointment, seller);

        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new InvalidAppointmentStatusException("Only pending appointments can be confirmed.");
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setSellerNotes(selelrNotes);
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        log.info("Appointment {} confirmed successfully.", id);
        return buildAppointmentResponse(updatedAppointment);
    }

    @Transactional
    public AppointmentResponse rejectAppointment(Long id, 
    String selelrNotes) {
        log.info("Rejecting appointment: {}", id);
        Seller seller = securityUtil.getCurrentSeller();

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        validateSellerOwnership(appointment, seller);

        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new InvalidAppointmentStatusException("Only pending appointments can be rejected.");
        }

        appointment.setStatus(AppointmentStatus.REJECTED);
        appointment.setSellerNotes(selelrNotes);
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        log.info("Appointment {} rejected successfully.", id);
        return buildAppointmentResponse(updatedAppointment);
    }

    @Transactional
    public AppointmentResponse completeAppointment(Long id) {
        log.info("Marking appointment as completed: {}", id);
        Seller seller = securityUtil.getCurrentSeller();

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        validateSellerOwnership(appointment, seller);

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new InvalidAppointmentStatusException("Only confirmed appointments can be marked as completed.");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        log.info("Appointment {} marked as completed successfully.", id);
        return buildAppointmentResponse(updatedAppointment);
    }

    private void validateSellerOwnership(Appointment appointment, Seller seller) {
        if (!appointment.getSeller().getId().equals(seller.getId())) {
            throw new ForbiddenException("Seller does not own this appointment.");
        }
    }

    private AppointmentResponse buildAppointmentResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .client(buildUserBasicResponse(appointment.getClient()))
                .estate(buildEstateBasicResponse(appointment.getEstate()))
                .appointmentDate(appointment.getAppointmentDate())
                .message(appointment.getMessage())
                .sellerNotes(appointment.getSellerNotes())
                .status(appointment.getStatus())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }

    private UserBasicResponse buildUserBasicResponse(User user) {
        return UserBasicResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getContactNumber())
                .build();
    }

    private EstateBasicResponse buildEstateBasicResponse(Estate estate) {
        String mainImageUrl = estate.getImages().isEmpty() ? null : estate.getImages().get(0).getS3url();

        return EstateBasicResponse.builder()
                .id(estate.getId())
                .name(estate.getName())
                .price(estate.getPrice())
                .type(estate.getType())
                .city(estate.getCity())
                .address(estate.getAddress())
                .mainImageUrl(mainImageUrl)
                .build();
    }
}
