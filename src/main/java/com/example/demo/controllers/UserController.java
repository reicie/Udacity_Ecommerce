package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static Logger logger;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    public UserController(UserRepository userRepository, CartRepository cartRepository, PasswordEncoder bcryptEncoder) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.bcryptEncoder = bcryptEncoder;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        // log username that is for every user created
        logger.info("Username set with ", createUserRequest.getUsername());

        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);

        // check that the length of the password is correct
        if(createUserRequest.getPassword().length() < 7 || !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
            // log the password length being created
            logger.debug("Password length created ", createUserRequest.getPassword().length());
            logger.debug("Password versus confirmed password ",createUserRequest.getPassword(), createUserRequest.getConfirmPassword());
            return ResponseEntity.badRequest().build();
        }

        user.setPassword(bcryptEncoder.encode(createUserRequest.getPassword()));

        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
}