package com.cybersoft.cinema_proj.service;

import com.cybersoft.cinema_proj.dto.*;
import com.cybersoft.cinema_proj.entity.*;
import com.cybersoft.cinema_proj.repository.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Autowired
    private SeatBookingRepository seatBookingRepository;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private SeatService seatService;

    public List<BillDTO> getAllBill() {
        List<BillEntity> billEntities = billRepository.findAll();
        List<BillDTO> billDTOs = new ArrayList<>();
        for (BillEntity billEntity : billEntities) {
            BillDTO billDTO = new BillDTO();
            BeanUtils.copyProperties(billEntity, billDTO);

            MemberEntity memberEntity = memberRepository.findById(billEntity.getMember_id().getId());
            if (memberEntity != null) {
                MemberEntity memberDTO = new MemberEntity();
                BeanUtils.copyProperties(memberEntity, memberDTO);
                billDTO.setMember_id(memberDTO);
            }

            List<TicketEntity> ticketEntities = ticketRepository.findByBill_id(billEntity.getId());
            List<TicketDTO> ticketDTOs = new ArrayList<>();
            for (TicketEntity ticketEntity : ticketEntities) {
                TicketDTO ticketDTO = new TicketDTO();
                BeanUtils.copyProperties(ticketEntity, ticketDTO);

                SeatBookingEntity seatBookingEntity = seatBookingRepository.findById(ticketEntity.getSeat_booking_id().getShowtime_id().getId()).orElse(null);
                if (seatBookingEntity != null) {
                    SeatBookingEntity seatBookingDTO = new SeatBookingEntity();
                    BeanUtils.copyProperties(seatBookingEntity, seatBookingDTO);
                    ticketDTO.setSeat_booking_id(seatBookingDTO);
                }
                ticketDTOs.add(ticketDTO);
            }
            billDTO.setTickets(ticketDTOs);
            billDTOs.add(billDTO);
        }
        return billDTOs;
    }

    public BillDTO getBillDetail(int billId) {
        BillEntity billEntity = billRepository.findById(billId)
            .orElseThrow(() -> new RuntimeException("Bill not found with id: " + billId));
        BillDTO billDTO = new BillDTO();
        BeanUtils.copyProperties(billEntity, billDTO);

        // Lấy danh sách vé trong hóa đơn
        List<TicketEntity> ticketEntities = billRepository.findTicketsByBillId(billId);
        List<TicketDTO> ticketDTOs = new ArrayList<>();
        for (TicketEntity ticketEntity : ticketEntities) {
            // Lấy chi tiết của từng vé và thêm vào danh sách
            TicketDTO ticketDTO = ticketService.getTicketDetails(ticketEntity.getId());
            ticketDTOs.add(ticketDTO);
        }
        billDTO.setTickets(ticketDTOs);

        return billDTO;
    }

    public List<BillDTO> getListBill(String username) {
        List<BillEntity> billEntities = billRepository.findByMember(username);
        List<BillDTO> billDTOs = new ArrayList<>();
        for (BillEntity billEntity : billEntities) {
            BillDTO billDTO = new BillDTO();
            BeanUtils.copyProperties(billEntity, billDTO);

            // Ánh xạ trường member từ MemberEntity vào MemberDTO
            MemberEntity memberDTO = new MemberEntity();
            BeanUtils.copyProperties(billEntity.getMember_id(), memberDTO);
            billDTO.setMember_id(memberDTO);

            List<TicketEntity> ticketEntities = ticketRepository.findByBill_id(billEntity.getId());
            List<TicketDTO> ticketDTOs = new ArrayList<>();
            for (TicketEntity ticketEntity : ticketEntities) {
                TicketDTO ticketDTO = new TicketDTO();
                BeanUtils.copyProperties(ticketEntity, ticketDTO);

                // Sử dụng seatBookingRepository để lấy thông tin showtime
                SeatBookingEntity seatBookingEntity = seatBookingRepository.findById(ticketEntity.getSeat_booking_id().getId()).orElse(null);
                if (seatBookingEntity != null) {
                    ShowTimeEntity showTimeEntity = seatBookingEntity.getShowtime_id();
                    if (showTimeEntity != null) {
                        ShowTimeEntity showTimeDTO = new ShowTimeEntity();
                        BeanUtils.copyProperties(showTimeEntity, showTimeDTO);
                        ticketDTO.setShowtime_id(showTimeDTO);
                    }
                }
                ticketDTOs.add(ticketDTO);
            }
            billDTO.setTickets(ticketDTOs);
            billDTOs.add(billDTO);
        }
        return billDTOs;
    }




    // Chức năng hủy hóa đơn
    public void cancelBill(int billId) {
        BillEntity billEntity = billRepository.findById(billId)
            .orElseThrow(() -> new RuntimeException("Bill not found with id: " + billId));

        if (!billEntity.isStatus()) {
            throw new RuntimeException("Bill is already canceled");
        }

        // Cập nhật trạng thái của hóa đơn
        billEntity.setStatus(false);
        billRepository.save(billEntity);

        // Lấy danh sách vé trong hóa đơn
        List<TicketEntity> ticketEntities = ticketRepository.findByBill_id(billId);

        // Cập nhật trạng thái của vé và ghế
        for (TicketEntity ticketEntity : ticketEntities) {
            ticketEntity.setBill_id(null);
            ticketRepository.save(ticketEntity);

            SeatEntity seatEntity = ticketEntity.getSeat_id();
            if (seatEntity != null) {
                seatService.updateSeatStatus(seatEntity.getId(), false);
            }
        }
    }

}
