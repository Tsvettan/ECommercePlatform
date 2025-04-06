package bg.softuni.ECommercePlatform.service;

import bg.softuni.ECommercePlatform.dto.UserDTO;
import bg.softuni.ECommercePlatform.enums.Role;
import bg.softuni.ECommercePlatform.model.ConfirmationToken;
import bg.softuni.ECommercePlatform.model.UserEntity;
import bg.softuni.ECommercePlatform.repository.ConfirmationTokenRepository;
import bg.softuni.ECommercePlatform.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    public UserEntity updateUser(Long id, String username, String email) {
        UserEntity user = getUserById(id);
        user.setUsername(username);
        user.setEmail(email);
        return userRepository.save(user);
    }

    public void changeUserRole(Long id, Role newRole) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setRole(newRole);
        userRepository.save(user);
    }

    public void registerUser(UserEntity user) {
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        UserEntity newUser = new UserEntity();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setRole(Role.USER);

        userRepository.save(newUser);
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

    public void updateUserRole(Long userId, Role role) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(role);
        userRepository.save(user);
    }

    public UserEntity getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public Long getTotalUsers() {
        return userRepository.count();
    }

    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public void updateUserProfile(UserEntity updatedUser, String newPassword) {
        UserEntity user = userRepository.findByUsername(updatedUser.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + updatedUser.getUsername()));

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        userRepository.save(user);

        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(user);
    }
}
