package com.SignUpform.form.service;

import java.util.List;   // ✅ ye import add karo

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.SignUpform.form.model.User;
import com.SignUpform.form.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    // SIGNUP
    public User register(User user){

        User existingUser = userRepository.findByEmail(user.getEmail());

        if(existingUser != null){
            throw new RuntimeException("Email already registered");
        }

        user.setPassword(encoder.encode(user.getPassword()));

        return userRepository.save(user);
    }


    // FIND USER BY EMAIL
    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }


    // CHECK PASSWORD
    public boolean checkPassword(String rawPassword,String encodedPassword){
        return encoder.matches(rawPassword, encodedPassword);
    }


    // ENCODE PASSWORD
    public String encodePassword(String password){
        return encoder.encode(password);
    }


    // GET ALL USERS
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    // SAVE USER (for password reset)
    public User save(User user){
        return userRepository.save(user);
    }

}