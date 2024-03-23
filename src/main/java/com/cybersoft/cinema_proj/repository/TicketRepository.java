package com.cybersoft.cinema_proj.repository;

import com.cybersoft.cinema_proj.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Integer> {

    @Query("SELECT COUNT(t) FROM TicketEntity t WHERE t.showtime_id.id = :showtimeId")
    long countByShowtimeId(@Param("showtimeId") Long showtimeId);

    @Query("SELECT t FROM TicketEntity t " +
        "WHERE t.bill_id.member_id.username = :username " +
        "AND t.showtime_id.id = :showtimeId " +
        "ORDER BY t.bill_id.paymentTime DESC")
    List<TicketEntity> findByMemberIdAndShowtimeId(@Param("username") String username, int showtimeId);

    @Query("SELECT t FROM TicketEntity t " +
        "WHERE t.bill_id.member_id.id = :memberId " +
        "AND t.showtime_id.id = :showtimeId " +
        "AND DATE(t.bill_id.paymentTime) = :todayDate")
    List<TicketEntity> findByMemberIdAndShowtimeIdAndTodayDate(int memberId, int showtimeId, LocalDate todayDate);

    @Query("SELECT t FROM TicketEntity t WHERE t.bill_id.id = :billId")
    List<TicketEntity> findByBill_id(int billId);

    @Query("SELECT t.showtime_id.movie.name AS movie_name, SUM(t.price) AS total_revenue " +
        "FROM TicketEntity t " +
        "JOIN t.showtime_id s " +
        "JOIN s.movie m " +
        "WHERE m.id = :movieId " +
        "GROUP BY m.name")
    List<Object[]> getTotalRevenueByMovieId(@Param("movieId") int movieId);
}
