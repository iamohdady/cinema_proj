package com.cybersoft.cinema_proj.repository;

import com.cybersoft.cinema_proj.entity.RoomBookingEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomBookingRepository extends JpaRepository<RoomBookingEntity, Integer> {

    RoomBookingEntity findById(Long id);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO room_booking (showtime_id, room_id, is_booked) " +
        "VALUES (:showtimeId, :roomId, :isBooked)", nativeQuery = true)
    void insertRoomBooking(@Param("showtimeId") int showtimeId,
                           @Param("roomId") int roomId,
                           @Param("isBooked") boolean isBooked);

//    @Modifying
//    @Transactional
//    @Query("UPDATE RoomBookingEntity rb " +
//        "SET rb.showtime_id.id = :showtimeId, rb.room_id.id = :roomId, rb.isBooked = :isBooked " +
//        "WHERE rb.room_id.id = :roomId")
//    void updateRoomBooking(int showtimeId, int roomId, boolean isBooked);

//    @Transactional
//    @Modifying
//    @Query("INSERT INTO RoomBookingEntity (showtime_id, room_id, isBooked) " +
//        "VALUES (:showtimeId, :roomId, :isBooked)")
//    void insertRoomBooking(@Param("showtimeId") int showtimeId,
//                           @Param("roomId") int roomId,
//                           @Param("isBooked") boolean isBooked);
}
