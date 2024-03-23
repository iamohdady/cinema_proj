package com.cybersoft.cinema_proj.controller;

import com.cybersoft.cinema_proj.dto.TicketDTO;
import com.cybersoft.cinema_proj.request.ListTicketRequest;
import com.cybersoft.cinema_proj.request.TicketRequest;
import com.cybersoft.cinema_proj.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/book")
    public ResponseEntity<?> bookTickets(@RequestBody List<TicketRequest> ticketRequests) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName().substring(10);

        List<TicketDTO> bookedTickets = ticketService.bookTickets(ticketRequests, username);
        return new ResponseEntity<>(bookedTickets, HttpStatus.CREATED);
    }

    @GetMapping("/detail/{ticketIds}")
    public ResponseEntity<?> getTicketDetails(@PathVariable int ticketIds) {
        TicketDTO ticketDTO = ticketService.getTicketDetails(ticketIds);
        return ResponseEntity.ok().body(ticketDTO);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getMovie(){
        List<TicketDTO> list = ticketService.getAllTicket();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/list/member/{showtimeId}")
    public ResponseEntity<?> getListTickets(
        @PathVariable int showtimeId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName().substring(10);

        List<TicketDTO> tickets = ticketService.getListTickets(showtimeId, username);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @PostMapping("/list/member/today")
    public ResponseEntity<?> getListTicketsToday(@RequestBody ListTicketRequest request) {
        List<TicketDTO> tickets = ticketService.getListTicketsToday(request);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @GetMapping("/bill/{billId}")
    public ResponseEntity<?> getTicketsByBillId(@PathVariable int billId) {
        List<TicketDTO> tickets = ticketService. getTicketsByBillId(billId);
        return ResponseEntity.ok().body(tickets);
    }

    @PostMapping("/cancel/{ticketId}")
    public ResponseEntity<?> cancelTicket(@PathVariable int ticketId) {
        ticketService.cancelTicket(ticketId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}