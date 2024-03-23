package com.cybersoft.cinema_proj.controller;

import com.cybersoft.cinema_proj.dto.MemberDTO;
import com.cybersoft.cinema_proj.request.*;
import com.cybersoft.cinema_proj.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/info")
    public ResponseEntity<?> getInfo() {

        List<MemberDTO> listMember = memberService.getInfo();
        return new ResponseEntity<>(listMember, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> userProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName().substring(10);

        MemberDTO memberDTO = memberService.findMemberWithCustomerByUsername(username);
        if (memberDTO == null) {
            System.out.println("MemberDTO: " + memberDTO);
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Member not found with username: " + username);
        }
        return ResponseEntity.ok(memberDTO);
    }

    @PostMapping("/update_password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordRequest request) throws Exception {
        boolean changePasswordResult = memberService.changePassword(request);

        if (changePasswordResult) {
            return ResponseEntity.ok("SUCCESS");
        } else {
            return ResponseEntity.badRequest().body("FAIL");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllMember() {
        List<MemberDTO> members = memberService.getAllMember();
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMovie(@ModelAttribute AddMemberRequest addMemberRequest) {
        if (addMemberRequest.getImage() == null) {
            return ResponseEntity.badRequest().body("Image is required");
        }
        MemberDTO memberDTO = memberService.convertToAddMemberRequestToDTO(addMemberRequest);
        MemberDTO savedMemberDTO = memberService.addMember(memberDTO);

        if (savedMemberDTO == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Member already exists");
        }
        return ResponseEntity.ok(savedMemberDTO);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateMember(@PathVariable int id, @ModelAttribute UpdateMemberRequest updateMemberRequest) {
        MemberDTO existingMemberDTO = memberService.getMemberById(id);
        if (existingMemberDTO == null) {
            return new ResponseEntity<>("Member not found", HttpStatus.NOT_FOUND);
        }
        MemberDTO memberDTO = memberService.convertToUpdateMemberRequestToDTO(updateMemberRequest, id);
        memberDTO.setId(id);
        MemberDTO updatedMemberDTO = memberService.updateMember(memberDTO);
        if (updatedMemberDTO != null) {
            return new ResponseEntity<>("Member updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to update Member", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteDayTime(@PathVariable int id) {
        memberService.deleteMember(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getMemberById(@PathVariable("id") int id) {
        MemberDTO memberDTO = memberService.getMemberById(id);
        if (memberDTO != null) {
            return new ResponseEntity<>(memberDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Member not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> countAllMembers() {
        long count = memberService.countAllMembers();
        return ResponseEntity.ok(count);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully.");
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> depositMoney(@RequestBody PaymentRequest amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName().substring(10);

        boolean result = memberService.depositMoney(username, amount.getAmount());
        if (result) {
            return ResponseEntity.ok("Money deposited successfully.");
        } else {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Failed to deposit money. Please check your account.");
        }
    }

    @PostMapping("/confirm_payment")
    public ResponseEntity<?> confirmPaymentAndDeposit(@RequestParam int id) {
        boolean result = memberService.confirmPayment(id);
        if (result) {
            return ResponseEntity.ok("Payment confirmed and money deposited successfully.");
        } else {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Failed to confirm payment and deposit money. Please check your account.");
        }
    }

    @PostMapping("/cancel_payment")
    public ResponseEntity<?> cancelPayment(@RequestParam int id) {
        boolean result = memberService.cancelPayment(id);
        if (result) {
            return ResponseEntity.ok("Payment canceled successfully.");
        } else {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Failed to cancel payment. Please check the member's status.");
        }
    }

    @GetMapping("/unconfirmed_deposits")
    public ResponseEntity<?> getMembersWithUnconfirmedDeposits() {
        List<MemberDTO> membersWithUnconfirmedDeposits = memberService.findMembersWithUnconfirmedPayments();
        return new ResponseEntity<>(membersWithUnconfirmedDeposits, HttpStatus.OK);
    }

    @PostMapping("/pay/{billId}")
    public ResponseEntity<?> payBill(@PathVariable int billId) {
        try {
            boolean paymentResult = memberService.payBill(billId);
            if (paymentResult) {
                return ResponseEntity.ok("Bill paid successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to pay the bill.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String username) {
        boolean result = memberService.resetPassword(username);
        if (result) {
            return ResponseEntity.ok("Password reset successfully.");
        } else {
            return ResponseEntity.status(404).body("User not found.");
        }
    }

    @GetMapping("/role")
    public ResponseEntity<?> getRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName().substring(10);

        String role = memberService.getRoleByUsername(username);
        return ResponseEntity.ok(role);
    }
}
