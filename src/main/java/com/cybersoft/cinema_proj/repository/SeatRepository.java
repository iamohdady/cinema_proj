package com.cybersoft.cinema_proj.repository;

import com.cybersoft.cinema_proj.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, Integer> {

    List<SeatEntity> findByRoomId(Long roomId);
    @Modifying
    @Query("DELETE FROM SeatEntity s WHERE s.id IN :seatIds")
    void cancelSeat(@Param("seatIds") List<Long> seatIds);
}