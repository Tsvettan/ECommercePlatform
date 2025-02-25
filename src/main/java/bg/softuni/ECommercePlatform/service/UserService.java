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

    private UserDTO convertToDTO(UserEntity user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Update user profile
    public UserEntity updateUser(Long id, String username, String email) {
        UserEntity user = getUserById(id);
        user.setUsername(username);
        user.setEmail(email);
        return userRepository.save(user);
    }

    // Change Role (Admin only)
    public void changeUserRole(Long id, Role newRole) {
        UserEntity user = new UserEntity();
        user.setRole(newRole);
        userRepository.save(user);
    }

    // Register a new user
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

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
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

    public UserEntity getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
