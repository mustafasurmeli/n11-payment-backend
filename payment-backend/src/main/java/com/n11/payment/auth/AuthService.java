package com.n11.payment.auth;


import com.n11.payment.auth.dto.RegisterRequest;
import com.n11.payment.auth.dto.UserResponse;
import com.n11.payment.user.Role;
import com.n11.payment.user.User;
import com.n11.payment.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse register(RegisterRequest request){
        if(userRepository.existsByUsername(request.getUsername()))
            throw new IllegalArgumentException("Username already talen");
        if(userRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Email already registered");

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                hashedPassword,
                Role.USER
        );

        User saved = userRepository.save(user);

        return  new UserResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getRole()
        );
    }
}
