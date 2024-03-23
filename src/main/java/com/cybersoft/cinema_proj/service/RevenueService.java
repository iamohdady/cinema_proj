package com.cybersoft.cinema_proj.service;

import com.cybersoft.cinema_proj.dto.BillDTO;
import com.cybersoft.cinema_proj.dto.TicketDTO;
import com.cybersoft.cinema_proj.entity.BillEntity;
import com.cybersoft.cinema_proj.entity.TicketEntity;
import com.cybersoft.cinema_proj.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
public class RevenueService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private MovieRepository movieRepository;  //


    // Thống kê doanh thu theo ngày
    public double getRevenueByDate(LocalDate date) {
        List<BillEntity> bills = billRepository.findByPaymentTimeBetween(
            date.atStartOfDay(),
            date.plusDays(1).atStartOfDay()
        );
        return bills.stream().mapToDouble(BillEntity::getTotal_amount).sum();
    }

    // Thống kê doanh thu theo tháng
    public double getRevenueByMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        List<BillEntity> bills = billRepository.findByPaymentTimeBetween(
            yearMonth.atDay(1).atStartOfDay(),
            yearMonth.plusMonths(1).atDay(1).atStartOfDay()
        );
        return bills.stream().mapToDouble(BillEntity::getTotal_amount).sum();
    }

    // Thống kê doanh thu theo năm
    public double getRevenueByYear(int year) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate startOfNextYear = startOfYear.plusYears(1);
        List<BillEntity> bills = billRepository.findByPaymentTimeBetween(
            startOfYear.atStartOfDay(),
            startOfNextYear.atStartOfDay()
        );
        return bills.stream().mapToDouble(BillEntity::getTotal_amount).sum();
    }

    public List<Object[]> getTotalRevenueByMovie(int movieId) {
        return ticketRepository.getTotalRevenueByMovieId(movieId);
    }

    public Map<LocalDate, Double> getDailyRevenueByMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Map<LocalDate, Double> dailyRevenueMap = new HashMap<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            double dailyRevenue = billRepository.findByPaymentTimeBetween(
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay()
            ).stream().mapToDouble(BillEntity::getTotal_amount).sum();
            dailyRevenueMap.put(date, dailyRevenue);
        }

        return dailyRevenueMap;
    }

    // Thống kê doanh thu theo từng tháng trong năm
    public Map<YearMonth, Double> getMonthlyRevenueByYear(int year) {
        Map<YearMonth, Double> monthlyRevenueMap = new HashMap<>();
        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(year, month);
            double monthlyRevenue = billRepository.findByPaymentTimeBetween(
                yearMonth.atDay(1).atStartOfDay(),
                yearMonth.plusMonths(1).atDay(1).atStartOfDay()
            ).stream().mapToDouble(BillEntity::getTotal_amount).sum();
            monthlyRevenueMap.put(yearMonth, monthlyRevenue);
        }
        return monthlyRevenueMap;
    }

    public Map<Integer, Double> getHourlyRevenueByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59); // Lấy thời điểm cuối cùng trong ngày
        Map<Integer, Double> hourlyRevenueMap = new TreeMap<>(); // Sử dụng TreeMap để sắp xếp theo thứ tự giờ
        for (int hour = 0; hour < 24; hour++) {
            LocalDateTime startOfHour = startOfDay.plusHours(hour);
            LocalDateTime endOfHour = startOfHour.plusHours(1);
            double hourlyRevenue = billRepository.findByPaymentTimeBetween(
                startOfHour,
                endOfHour
            ).stream().mapToDouble(BillEntity::getTotal_amount).sum();
            hourlyRevenueMap.put(hour, hourlyRevenue);
        }
        return hourlyRevenueMap;
    }

    public List<Object[]> getTotalRevenueByMovieAndDate(Date paymentDate) {
        return billRepository.getTotalRevenueByMovieAndDate(paymentDate);
    }
}
