package com.cybersoft.cinema_proj.controller;

import com.cybersoft.cinema_proj.request.ListRevenueMovie;
import com.cybersoft.cinema_proj.response.MovieRevenueResponse;
import com.cybersoft.cinema_proj.service.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/revenue")
public class RevenueController {

    @Autowired
    private RevenueService revenueService;

    @GetMapping("/daily")
    public ResponseEntity<?> getDailyRevenue(@RequestParam LocalDate date) {
        double revenue = revenueService.getRevenueByDate(date);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/monthly")
    public ResponseEntity<?> getMonthlyRevenue(@RequestParam int year, @RequestParam int month) {
        double revenue = revenueService.getRevenueByMonth(year, month);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/yearly")
    public ResponseEntity<?> getYearlyRevenue(@RequestParam int year) {
        double revenue = revenueService.getRevenueByYear(year);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<?> getRevenueByMovie(@PathVariable int movieId) {
        List<Object[]> revenue = revenueService.getTotalRevenueByMovie(movieId);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/today")
    public ResponseEntity<?> getDailyRevenue() {
        LocalDate today = LocalDate.now();
        double revenue = revenueService.getRevenueByDate(today);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/daily-by-month")
    public ResponseEntity<?> getDailyRevenueByMonth(@RequestParam int year, @RequestParam int month) {
        Map<LocalDate, Double> dailyRevenueMap = revenueService.getDailyRevenueByMonth(year, month);
        return ResponseEntity.ok(dailyRevenueMap);
    }

    @GetMapping("/monthly-by-year")
    public ResponseEntity<?> getMonthlyRevenueByYear(@RequestParam int year) {
        Map<YearMonth, Double> monthlyRevenueMap = revenueService.getMonthlyRevenueByYear(year);
        return ResponseEntity.ok(monthlyRevenueMap);
    }

    @GetMapping("/hourly")
    public ResponseEntity<?> getHourlyRevenueByDate(@RequestParam LocalDate date) {
        Map<Integer, Double> hourlyRevenueMap = revenueService.getHourlyRevenueByDate(date);
        return ResponseEntity.ok(hourlyRevenueMap);
    }

    @PostMapping("/movie-and-date")
    public ResponseEntity<List<MovieRevenueResponse>> getTotalRevenueByMovieAndDate(@RequestBody ListRevenueMovie revenueMovie) {
        Date paymentDate = revenueMovie.getPaymentTime();
        List<Object[]> revenueData = revenueService.getTotalRevenueByMovieAndDate(paymentDate);
        List<MovieRevenueResponse> response = revenueData.stream()
            .map(data -> new MovieRevenueResponse(Long.valueOf((Integer) data[0]), (String) data[1], (Double) data[2]))
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}