package com.cybersoft.cinema_proj.repository;

import com.cybersoft.cinema_proj.entity.BillEntity;
import com.cybersoft.cinema_proj.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<BillEntity, Integer>  {
    @Query("SELECT t FROM TicketEntity t WHERE t.bill_id.id = :billId")
    List<TicketEntity> findTicketsByBillId(@Param("billId") int billId);

    List<BillEntity> findByPaymentTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT m.id AS movie_id, m.name AS movie_name, SUM(t.price) AS total_revenue " +
        "FROM MovieEntity m " +
        "JOIN ShowTimeEntity s ON m.id = s.movie.id " +
        "JOIN TicketEntity t ON s.id = t.seat_booking_id.showtime_id.id " +
        "JOIN BillEntity b ON t.bill_id.id = b.id " +
        "WHERE DATE(b.paymentTime) = :paymentDate " +
        "GROUP BY m.id, m.name")
    List<Object[]> getTotalRevenueByMovieAndDate(@Param("paymentDate") Date paymentDate);

    @Query("SELECT b " +
        "FROM BillEntity b " +
        "JOIN MemberEntity m ON b.member_id.id = m.id " +
        "WHERE m.username = :username")
    List<BillEntity> findByMember(@Param("username") String username);
}


