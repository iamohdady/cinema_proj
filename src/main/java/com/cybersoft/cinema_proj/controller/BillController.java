package com.cybersoft.cinema_proj.controller;

import com.cybersoft.cinema_proj.dto.BillDTO;
import com.cybersoft.cinema_proj.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bill")
public class BillController {

    @Autowired
    public BillService billService;

    @GetMapping("/all")
    public ResponseEntity<?> getMovie() {
        List<BillDTO> listBill = billService.getAllBill();
        return new ResponseEntity<>(listBill, HttpStatus.OK);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getBillDetail(@PathVariable("id") int billId) {
        BillDTO billDetail = billService.getBillDetail(billId);
        return new ResponseEntity<>(billDetail, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getListTickets() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName().substring(10);

        List<BillDTO> bill = billService.getListBill(username);
        return new ResponseEntity<>(bill, HttpStatus.OK);
    }

    @PostMapping("/cancel/{billId}")
    public ResponseEntity<?> cancelBill(@PathVariable int billId) {
        billService.cancelBill(billId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
