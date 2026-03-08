package com.SignUpform.form.controller;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import com.SignUpform.form.model.User;
import com.SignUpform.form.service.UserService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender mailSender;

    // OTP store
    Map<String, String> otpStorage = new HashMap<>();


    // ===================== USERS =====================

    // See all users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // See user by email
    @GetMapping("/user/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email);
    }


    // ===================== TEST =====================

    @GetMapping("/home")
    public String home() {
        return "its working";
    }


    // ===================== SIGNUP =====================

    @PostMapping("/signup")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }


    // ===================== LOGIN =====================

    @PostMapping("/login")
    public String login(@RequestBody Map<String,String> request){

        String email = request.get("email");
        String password = request.get("password");

        User existingUser = userService.findByEmail(email);

        if(existingUser == null){
            return "User not found";
        }

        // 🔴 Debug prints
        System.out.println("RAW PASSWORD: " + password);
        System.out.println("DB HASH: " + existingUser.getPassword());
        System.out.println("MATCH RESULT: " + userService.checkPassword(password, existingUser.getPassword()));

        boolean match = userService.checkPassword(
                password,
                existingUser.getPassword()
        );

        if(match){
            return "Login Successful";
        }

        return "Invalid Password";
    }
    // ===================== FORGOT PASSWORD =====================

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody Map<String, String> request) {

        String email = request.get("email");

        User user = userService.findByEmail(email);

        if (user == null) {
            return "Email not found";
        }

        String otp = String.format("%06d", new Random().nextInt(999999));

        otpStorage.put(email, otp);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP is: " + otp);

        mailSender.send(message);

        return "OTP sent to email";
    }


    // ===================== VERIFY OTP =====================

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String otp = request.get("otp");

        String storedOtp = otpStorage.get(email);

        if (storedOtp == null) {
            return "OTP expired";
        }

        if (storedOtp.equals(otp)) {
            otpStorage.remove(email);   // OTP remove after verification
            return "OTP verified";
        }

        return "Invalid OTP";
    }


    // ===================== RESET PASSWORD =====================

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String newPassword = request.get("password");

        User user = userService.findByEmail(email);

        if (user == null) {
            return "User not found";
        }

        user.setPassword(userService.encodePassword(newPassword));

        userService.save(user);   // update password

        return "Password updated successfully";
    }
}