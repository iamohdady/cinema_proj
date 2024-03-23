package com.cybersoft.cinema_proj.service;

//import com.cybersoft.cinema_proj.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//    @Override
//    public boolean insertUser(SignupRequest signupRequest) {
//
//        boolean isSuccess = false;
//
//        UserEntity user = new UserEntity();
//        user.setEmail(signupRequest.getEmail());
//        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
//        user.setUsername(signupRequest.getUsername());
//
//        try {
//            userRepository.save(user);
//            isSuccess =  true;
//        }catch (Exception e){
//            System.out.println("Them that bai: " + e.getLocalizedMessage());
//            isSuccess =  false;
//        }
//        return isSuccess;
//
//    }
}
