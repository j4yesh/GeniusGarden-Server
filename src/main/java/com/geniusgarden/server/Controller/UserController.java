package com.geniusgarden.server.Controller;

import com.geniusgarden.server.Model.AuthUser;
import com.geniusgarden.server.Model.Otp;
import com.geniusgarden.server.Pages.getHtml;
import com.geniusgarden.server.Repository.AuthUserRepository;
import com.geniusgarden.server.Repository.OtpRepository;
import com.geniusgarden.server.Service.EmailSender;
import com.geniusgarden.server.Service.JWTServiceImpl;
import com.geniusgarden.server.Service.OtpDeletionService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@AllArgsConstructor
public class UserController {
    private static int otpLength = 4;

    private final AuthUserRepository userRepository;
    private final OtpRepository otpRepository;

    @Autowired
    private JWTServiceImpl jwtService;

    @Autowired
    private OtpDeletionService otpDeletionService;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    EmailSender emailSender;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody AuthUser user){
        try {
            System.out.println(user);
            if(userRepository.findByUsername(user.getUsername()).isPresent()){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists.");
            } else {
                if(otpRepository.findByOtp(user.getOtp()).isPresent()) {
                    System.out.println("instance");
                    user.setPassword(passwordEncoder().encode(user.getPassword()));
                    user.setActive(true);
                    userRepository.save(user);
                    return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
                }else{
                    return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body("Enter valid otp.");
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/getotp")
    public ResponseEntity<String> getOtp(@RequestBody AuthUser user) {
        try {
            System.out.println(user);
            String otp = EmailSender.generateOtp(otpLength);

            while (otpRepository.findByOtp(otp).isPresent()) {
                otp = EmailSender.generateOtp(otpLength);
            }

            Date now = new Date();
            Date expiryTime = new Date(now.getTime() + 5 * 60 * 1000);

            Otp otpEntity = new Otp(otp, user.getEmail(), now, expiryTime);
            otpRepository.save(otpEntity);
//            otpDeletionService.scheduleOtpDeletion(otp,5*60*1000);
            String htmlBody = getHtml.getOtpPage(otp);

            emailSender.sendmail(user.getEmail(), "Verification Otp : GeniusGarden", htmlBody);

            return ResponseEntity.ok("OTP sent successfully to " + user.getEmail());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }


    @PostMapping("/login")
    public String loginUser(@RequestBody AuthUser user){
        Authentication authentication =
                 authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
        if (authentication.isAuthenticated()){
                return jwtService.generateToken(user.getUsername());
        }else{
            return "bad";
        }
    }


}
