package com.cybersoft.cinema_proj.service;

import com.cybersoft.cinema_proj.dto.MemberDTO;
import com.cybersoft.cinema_proj.entity.BillEntity;
import com.cybersoft.cinema_proj.entity.MemberEntity;
import com.cybersoft.cinema_proj.repository.BillRepository;
import com.cybersoft.cinema_proj.repository.MemberRepository;
import com.cybersoft.cinema_proj.request.AddMemberRequest;
import com.cybersoft.cinema_proj.request.RegisterRequest;
import com.cybersoft.cinema_proj.request.UpdateMemberRequest;
import com.cybersoft.cinema_proj.request.UpdatePasswordRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<MemberDTO> getInfo() {
        List<MemberEntity> members = memberRepository.findAll();
        List<MemberDTO> dtos = new ArrayList<>();
        for (MemberEntity member : members) {
            MemberDTO dto = new MemberDTO();
            BeanUtils.copyProperties(member, dto);
            dtos.add(dto);
        }
        return dtos;
    }

    public MemberDTO findMemberWithCustomerByUsername(String username) {
        System.out.println("username: " + username);
        MemberEntity memberEntity = memberRepository.findMemberWithCustomerByUsername(username);
        if (memberEntity != null) {
            MemberDTO memberDTO = new MemberDTO();
            BeanUtils.copyProperties(memberEntity, memberDTO);
            return memberDTO;
        }
        return null;
    }


    public boolean changePassword(UpdatePasswordRequest request) throws Exception {
        if (!request.isValid()) {
            throw new BadRequestException("New password and confirm password do not match");
        }

        MemberEntity memberEntity = memberRepository.findByUsername(request.getUsername());
        if (memberEntity != null) {
            if (passwordEncoder.matches(request.getOldPassword(), memberEntity.getPassword())) {
                if (request.getNewPassword().equals(request.getConfirmPassword())) {
                    memberEntity.setPassword(passwordEncoder.encode(request.getNewPassword()));
                    memberRepository.save(memberEntity);
                    return true;
                }
            }
        }
        throw new BadRequestException("Update password unsuccess !");
    }

    public List<MemberDTO> getAllMember() {
        List<MemberEntity> memberEntities = memberRepository.findAll();
        List<MemberDTO> memberDTOS = new ArrayList<>();
        for (MemberEntity entity : memberEntities) {
            MemberDTO dto = new MemberDTO();
            BeanUtils.copyProperties(entity, dto);
            memberDTOS.add(dto);
        }
        return memberDTOS;
    }

    public MemberDTO addMember(MemberDTO memberDTO) {
        if (memberDTO == null) {
            return null;
        }
        MemberEntity existingMember = memberRepository.findByUsername(memberDTO.getUsername());
        if (existingMember != null) {
            return null;
        }
        MemberEntity memberEntity = new MemberEntity();
        BeanUtils.copyProperties(memberDTO, memberEntity);

        memberEntity.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
        MemberEntity savedMemberEntity = memberRepository.save(memberEntity);
        MemberDTO savedMemberDTO = new MemberDTO();
        BeanUtils.copyProperties(savedMemberEntity, savedMemberDTO);
        return savedMemberDTO;
    }

    public MemberDTO convertToAddMemberRequestToDTO(AddMemberRequest addMemberRequest) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setUsername(addMemberRequest.getUsername());
        MultipartFile imageFile = addMemberRequest.getImage();

        if (imageFile != null) {
            memberDTO.setImage(imageFile.getOriginalFilename());
        } else {
            memberDTO.setImage(null);
        }
        memberDTO.setEmail(addMemberRequest.getEmail());
        memberDTO.setPassword(addMemberRequest.getPassword());
        memberDTO.setBirthday(addMemberRequest.getBirthday());
        memberDTO.setAddress(addMemberRequest.getAddress());
        memberDTO.setFullname(addMemberRequest.getFullname());
        memberDTO.setPhone(addMemberRequest.getPhone());
        memberDTO.setRole(addMemberRequest.getRole());
        memberDTO.setPoint(addMemberRequest.getPoint());

        return memberDTO;
    }

    public MemberDTO updateMember(MemberDTO memberDTO) {
        Optional<MemberEntity> optionalExistingMember = Optional.ofNullable(memberRepository.findById(memberDTO.getId()));
        if (optionalExistingMember.isPresent()) {
            MemberEntity existingMember = optionalExistingMember.get();
            BeanUtils.copyProperties(memberDTO, existingMember);

            MemberEntity updatedMemberEntity = memberRepository.save(existingMember);
            MemberDTO updateMemberDTO = new MemberDTO();
            BeanUtils.copyProperties(updatedMemberEntity, updateMemberDTO);
            return updateMemberDTO;
        } else {
            return null;
        }
    }

    public MemberDTO convertToUpdateMemberRequestToDTO(UpdateMemberRequest updateMemberRequest, int id) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(id);
        memberDTO.setUsername(updateMemberRequest.getUsername());
        MultipartFile imageFile = updateMemberRequest.getImage();

        if (imageFile != null) {
            memberDTO.setImage(imageFile.getOriginalFilename());
        } else {
            memberDTO.setImage(null);
        }
        memberDTO.setEmail(updateMemberRequest.getEmail());
        memberDTO.setPassword(updateMemberRequest.getPassword());
        memberDTO.setBirthday(updateMemberRequest.getBirthday());
        memberDTO.setAddress(updateMemberRequest.getAddress());
        memberDTO.setFullname(updateMemberRequest.getFullname());
        memberDTO.setPhone(updateMemberRequest.getPhone());
        memberDTO.setRole(updateMemberRequest.getRole());

        return memberDTO;
    }

    public void deleteMember(int id) {
        memberRepository.deleteById(id);
    }

    public MemberDTO getMemberById(int id) {
        MemberEntity memberEntity = memberRepository.findById(id);
        if (memberEntity != null) {
            MemberDTO memberDTO = new MemberDTO();
            BeanUtils.copyProperties(memberEntity, memberDTO);
            return memberDTO;
        }
        return null;
    }

    public long countAllMembers() {
        return memberRepository.countAllMembers();
    }

    public MemberDTO register(MemberDTO memberDTO) {
        if (memberRepository.findByUsername(memberDTO.getUsername()) != null) {
            return null;
        }

        String encodedPassword = passwordEncoder.encode(memberDTO.getPassword());
        memberDTO.setPassword(encodedPassword);
        memberDTO.setRole("MEMBER");

        MemberEntity newMemberEntity = new MemberEntity();
        BeanUtils.copyProperties(memberDTO, newMemberEntity);
        memberRepository.save(newMemberEntity);

        return memberDTO;
    }

    public MemberDTO convertToRegisterRequestToDTO(RegisterRequest registerRequest) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setUsername(registerRequest.getUsername());
        MultipartFile imageFile = registerRequest.getImage();

        if (imageFile != null) {
            memberDTO.setImage(imageFile.getOriginalFilename());
        } else {
            memberDTO.setImage(null);
        }
        memberDTO.setEmail(registerRequest.getEmail());
        memberDTO.setPassword(registerRequest.getPassword());
        memberDTO.setBirthday(registerRequest.getBirthday());
        memberDTO.setAddress(registerRequest.getAddress());
        memberDTO.setFullname(registerRequest.getFullname());
        memberDTO.setPhone(registerRequest.getPhone());
        memberDTO.setRole(registerRequest.getRole());
        return memberDTO;
    }

    public boolean depositMoney(String username, double amount) {
        MemberEntity memberEntity = memberRepository.findByUsername(username);
        if (memberEntity != null) {
            memberEntity.setStatus_payment(false);
            memberEntity.setAmount(amount);
            memberRepository.save(memberEntity);
            return true;
        }
        return false;
    }

    public boolean confirmPayment(int id) {
        MemberEntity memberEntity = memberRepository.findById(id);
        if (memberEntity != null && !memberEntity.isStatus_payment()) {
            memberEntity.setStatus_payment(true);
            double currentMoney = memberEntity.getMoney();
            double amount = memberEntity.getAmount();
            memberEntity.setMoney(currentMoney + amount);
            memberEntity.setAmount(0);
            memberRepository.save(memberEntity);
            return true;
        }
        return false;
    }

    public List<MemberDTO> findMembersWithUnconfirmedPayments() {
        List<MemberEntity> members = memberRepository.findMembersWithUnconfirmedPayments();
        List<MemberDTO> dtos = new ArrayList<>();
        for (MemberEntity member : members) {
            MemberDTO dto = new MemberDTO();
            BeanUtils.copyProperties(member, dto);
            dtos.add(dto);
        }
        return dtos;
    }

    public boolean cancelPayment(int id) {
        Optional<MemberEntity> optionalMember = Optional.ofNullable(memberRepository.findById(id));

        if (optionalMember.isPresent()) {
            MemberEntity memberEntity = optionalMember.get();

            if (!memberEntity.isStatus_payment()) {
                memberEntity.setStatus_payment(false);
                memberEntity.setAmount(0);
                memberRepository.save(memberEntity);
                return true;
            }
        }
        return false;
    }


    public boolean payBill(int billId) {
        BillEntity billEntity = billRepository.findById(billId)
            .orElseThrow(() -> new RuntimeException("Bill not found with id: " + billId));

        MemberEntity memberEntity = billEntity.getMember_id();

        double totalAmount = billEntity.getTotal_amount();
        double currentBalance = memberEntity.getMoney();
        if (currentBalance < totalAmount) {
            throw new RuntimeException("Insufficient balance.");
        }

        double newBalance = currentBalance - totalAmount;
        memberEntity.setMoney(newBalance);

        memberRepository.save(memberEntity);

        billEntity.setPaymentTime(new Date());
        billEntity.setStatus(true);
        billRepository.save(billEntity);

        return true;
    }

    public boolean resetPassword(String username) {
        MemberEntity member = memberRepository.findByUsername(username);
        if (member != null) {
            String encodedPassword = passwordEncoder.encode("123456");
            member.setPassword(encodedPassword);
            memberRepository.save(member);
            return true;
        }
        return false;
    }

    public String getRoleByUsername(String username) {
        return memberRepository.findRoleByUsername(username);
    }

}
