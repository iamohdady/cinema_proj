package com.cybersoft.cinema_proj.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "ticket")
@Entity
@NoArgsConstructor
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double price;

    private String code_qr;

    private String description;

    @ManyToOne
    @JoinColumn(name = "showtime_id", referencedColumnName = "id")
    private ShowTimeEntity showtime_id;

    @ManyToOne
    @JoinColumn(name = "bill_id", referencedColumnName = "id")
    private BillEntity bill_id;

    @ManyToOne
    @JoinColumn(name = "seat_id", referencedColumnName = "id")
    private SeatEntity seat_id;

    @ManyToOne
    @JoinColumn(name = "seat_booking_id", referencedColumnName = "id")
    private SeatBookingEntity seat_booking_id;
}
