package com.cybersoft.cinema_proj.service;

import com.cybersoft.cinema_proj.dto.TicketDTO;
import com.cybersoft.cinema_proj.entity.*;
import com.cybersoft.cinema_proj.repository.*;
import com.cybersoft.cinema_proj.request.ListTicketRequest;
import com.cybersoft.cinema_proj.request.TicketRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;


    @Autowired
    private SeatBookingRepository seatBookingRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SeatService seatService;

    public List<TicketDTO> bookTickets(List<TicketRequest> ticketRequests, String username) {
        MemberEntity memberEntity = memberRepository.findByUsername(username);
        if (memberEntity == null) {
            throw new RuntimeException("Không tìm thấy thông tin thành viên.");
        }

        BillEntity billEntity = new BillEntity();
        billEntity.setPaymentTime(new Date());

        double totalPrice = 0.0;
        for (TicketRequest ticketRequest : ticketRequests) {
            Optional<SeatBookingEntity> seatBookingOptional = seatBookingRepository.findBySeatIdAndShowtimeId(ticketRequest.seatId, ticketRequest.showtimeId);
            if (!seatBookingOptional.isPresent()) {
                throw new RuntimeException("Không tìm thấy thông tin đặt ghế với seatId: " + ticketRequest.seatId + " và showtimeId: " + ticketRequest.showtimeId);
            }
            SeatBookingEntity seatBooking = seatBookingOptional.get();

            ShowTimeEntity showTime = seatBooking.getShowtime_id();
            totalPrice += showTime.getMovie().getPrice();
        }
        billEntity.setTotal_amount(totalPrice);
        billEntity.setMember_id(memberEntity);
        billEntity.setStatus(true);

        billEntity = billRepository.save(billEntity);

        List<TicketDTO> bookedTickets = new ArrayList<>();
        for (TicketRequest ticketRequest : ticketRequests) {
            TicketDTO bookedTicket = bookSingleTicket(ticketRequest, memberEntity, billEntity);
            bookedTickets.add(bookedTicket);
        }
        return bookedTickets;
    }

    public TicketDTO bookSingleTicket(TicketRequest ticketRequest, MemberEntity memberEntity, BillEntity billEntity) {
        seatBookingRepository.bookSeat(ticketRequest.showtimeId, ticketRequest.seatId);

        long bookedTicketsCount = ticketRepository.countByShowtimeId(ticketRequest.showtimeId);
        Optional<SeatBookingEntity> optionalSeatBooking = seatBookingRepository.findBySeatIdAndShowtimeId(ticketRequest.seatId, ticketRequest.showtimeId);
        SeatBookingEntity seatBooking = optionalSeatBooking.isPresent() ? optionalSeatBooking.get() : null;

        if (seatBooking == null) {
            throw new RuntimeException("Không tìm thấy thông tin đặt ghế với seatId: " + ticketRequest.seatId + " và showtimeId: " + ticketRequest.showtimeId);
        }
        if (bookedTicketsCount >= seatBooking.getShowtime_id().getRoom().getCapacity()) {
            throw new RuntimeException("Lịch chiếu đã hết chỗ trống.");
        }

        TicketEntity ticketEntity = new TicketEntity();
        ticketEntity.setPrice(seatBooking.getShowtime_id().getMovie().getPrice());
        ticketEntity.setBill_id(billEntity);
        ticketEntity.setSeat_booking_id(seatBooking);
        ticketEntity = ticketRepository.save(ticketEntity);

        TicketDTO ticketDTO = new TicketDTO();
        BeanUtils.copyProperties(ticketEntity, ticketDTO);
        return ticketDTO;
    }

    public TicketDTO getTicketDetails(int ticketId) {
        Optional<TicketEntity> ticketOptional = ticketRepository.findById(ticketId);
        if (ticketOptional.isPresent()) {
            TicketDTO ticketDTO = new TicketDTO();
            BeanUtils.copyProperties(ticketOptional.get(), ticketDTO);
            return ticketDTO;
        } else {
            throw new RuntimeException("Không tìm thấy vé với ID: " + ticketId);
        }
    }

    public List<TicketDTO> getAllTicket(){
        List<TicketEntity> list = ticketRepository.findAll();
        List<TicketDTO> listTicketDTO = new ArrayList<>();
        for (TicketEntity data : list){
            TicketDTO listDTO = new TicketDTO();
            BeanUtils.copyProperties(data, listDTO);
            listTicketDTO.add(listDTO);
        }
        return listTicketDTO;
    }

    public List<TicketDTO> getListTickets(Integer showtimeId, String username) {
        List<TicketEntity> ticketEntities = ticketRepository.findByMemberIdAndShowtimeId(username, showtimeId);
        List<TicketDTO> ticketDTOs = new ArrayList<>();
        for (TicketEntity ticketEntity : ticketEntities) {
            TicketDTO ticketDTO = new TicketDTO();
            BeanUtils.copyProperties(ticketEntity, ticketDTO);
            ticketDTOs.add(ticketDTO);
        }
        return ticketDTOs;
    }

    public List<TicketDTO> getListTicketsToday(ListTicketRequest request) {
        LocalDate todayDate = LocalDate.now();
        List<TicketEntity> ticketEntities = ticketRepository.findByMemberIdAndShowtimeIdAndTodayDate(request.memberId, request.showtimeId, todayDate);
        List<TicketDTO> ticketDTOs = new ArrayList<>();
        for (TicketEntity ticketEntity : ticketEntities) {
            TicketDTO ticketDTO = new TicketDTO();
            BeanUtils.copyProperties(ticketEntity, ticketDTO);
            ticketDTOs.add(ticketDTO);
        }
        return ticketDTOs;
    }

    public void cancelTicket(int ticketId) {
        Optional<TicketEntity> ticketOptional = ticketRepository.findById(ticketId);
        if (ticketOptional.isPresent()) {
            TicketEntity ticket = ticketOptional.get();
            // Đánh dấu ghế là chưa đặt chỗ
            seatService.updateSeatStatus(ticket.getSeat_booking_id().getSeat_id().getId(), false);
            // Xóa vé
            ticketRepository.delete(ticket);
        } else {
            throw new RuntimeException("Vé không tồn tại với ID: " + ticketId);
        }
    }

    public List<TicketDTO> getTicketsByBillId(int billId) {
        List<TicketEntity> ticketEntities = ticketRepository.findByBill_id(billId);
        List<TicketDTO> ticketDTOs = new ArrayList<>();

        for (TicketEntity ticketEntity : ticketEntities) {
            TicketDTO ticketDTO = new TicketDTO();
            BeanUtils.copyProperties(ticketEntity, ticketDTO);
            ticketDTOs.add(ticketDTO);
        }
        return ticketDTOs;
    }
}
