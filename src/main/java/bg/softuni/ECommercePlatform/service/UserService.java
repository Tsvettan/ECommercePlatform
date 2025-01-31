package bg.softuni.ECommercePlatform.service;

import bg.softuni.ECommercePlatform.dto.UserDTO;
import bg.softuni.ECommercePlatform.enums.Role;
import bg.softuni.ECommercePlatform.model.ConfirmationToken;
import bg.softuni.ECommercePlatform.model.UserEntity;
import bg.softuni.ECommercePlatform.repository.ConfirmationTokenRepository;
import bg.softuni.ECommercePlatform.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenRepository tokenRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, ConfirmationTokenRepository tokenRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    public void updateLastLogin(UserEntity user) {
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    public void registerUser(UserEntity user) {
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token, user, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30));
        tokenRepository.save(confirmationToken);

        String link = "http://localhost:8080/confirm?token=" + token;
        emailService.sendConfirmationEmail(user.getEmail(), user.getUsername(), link);
    }


    public void confirmToken(String token) {
        ConfirmationToken confirmationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found"));

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }

        UserEntity user = confirmationToken.getUser();
        user.setRole(Role.USER);                                        // Activate user
        userRepository.save(user);

        tokenRepository.delete(confirmationToken);                      // Remove token after confirmation
    }

    private String buildEmail(String username, String link) {
        return "<p>Hi " + username + ",</p>" +
                "<p>Please confirm your email by clicking the link below:</p>" +
                "<a href=\"" + link + "\">Confirm Email</a>" +
                "<p>The link will expire in 30 minutes.</p>";
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public UserDTO getUserById(Long id) {
        return modelMapper.map(userRepository.findById(id).orElseThrow(), UserDTO.class);
    }
}
