package com.realestate.realestate.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.realestate.realestate.entity.Appointment;
import com.realestate.realestate.entity.Seller;
import com.realestate.realestate.entity.User;
import com.realestate.realestate.enums.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Page<Appointment> findByClientOrderByAppointmentDateDesc(User client, Pageable pageable);

    Page<Appointment> findBySellerOrderByAppointmentDateDesc(Seller seller, Pageable pageable);

    Page<Appointment> findBySellerAndStatusOrderByAppointmentDateAsc(Seller seller, AppointmentStatus status,
            Pageable pageable);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE " +
            "a.seller = :seller AND " +
            "a.appointmentDate = :appointmentDate AND " +
            "a.status IN ('PENDING', 'CONFIRMED')")
    boolean existsConflictingAppointment(
            @Param("seller") Seller seller,
            @Param("appointmentDate") LocalDateTime appointmentDate);

    long countBySellerAndStatus(Seller seller, AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE " +
            "a.client = :client AND " +
            "a.status = 'CONFIRMED' AND " +
            "a.appointmentDate >= :now " +
            "ORDER BY a.appointmentDate ASC")
    List<Appointment> findUpcomingAppointments(
            @Param("client") User client,
            @Param("now") LocalDateTime now);
}
