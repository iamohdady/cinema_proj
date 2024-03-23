package com.cybersoft.cinema_proj.repository;

import com.cybersoft.cinema_proj.entity.SeatBookingEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatBookingRepository extends JpaRepository<SeatBookingEntity, Integer> {

    @Query("SELECT s.id, s.name, sb.isBooked " +
        "FROM SeatBookingEntity sb " +
        "JOIN SeatEntity s ON sb.seat_id.id = s.id " +
        "WHERE sb.showtime_id.id = :showtime_id")
    List<Object[]> getListSeat(@Param("showtime_id") Long showtime_id);

    @Query("SELECT sb FROM SeatBookingEntity sb WHERE sb.seat_id.id = :seatId AND sb.showtime_id.id = :showtimeId")
    Optional<SeatBookingEntity> findBySeatIdAndShowtimeId(@Param("seatId") Long seatId, @Param("showtimeId") Long showtimeId);

    @Transactional
    @Modifying
    @Query("UPDATE SeatBookingEntity sb " +
        "SET sb.isBooked = TRUE " +
        "WHERE sb.showtime_id.id = :showtimeId AND sb.seat_id.id = :seatId")
    void bookSeat(@Param("showtimeId") Long showtimeId, @Param("seatId") Long seatId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO seat_booking (showtime_id, seat_id, is_booked) " +
        "SELECT :showtimeId, s.id, FALSE " +
        "FROM seat s " +
        "WHERE s.room_id = :roomId", nativeQuery = true)
    void insertSeatBooking(@Param("showtimeId") Long showtimeId, @Param("roomId") Integer roomId);

    @Transactional
    @Modifying
    @Query("DELETE FROM SeatBookingEntity sb WHERE sb.showtime_id.id = :showtimeId")
    void deleteByShowtimeId(@Param("showtimeId") Long showtimeId);

}


