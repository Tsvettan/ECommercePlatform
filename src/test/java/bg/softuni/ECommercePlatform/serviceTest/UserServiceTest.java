package bg.softuni.ECommercePlatform.serviceTest;

import bg.softuni.ECommercePlatform.enums.Role;
import bg.softuni.ECommercePlatform.model.UserEntity;
import bg.softuni.ECommercePlatform.repository.UserRepository;
import bg.softuni.ECommercePlatform.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedpassword");
        testUser.setRole(Role.USER);
    }

    @Test
    void registerUser_ShouldSaveUserWithEncodedPassword() {
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";
        UserEntity user = new UserEntity();
        user.setPassword(rawPassword);

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity savedUser = invocation.getArgument(0);
            logger.info("Saving user with password: {}", savedUser.getPassword());
            return savedUser;
        });

        userService.registerUser(user);

        verify(userRepository).save(userCaptor.capture());

        UserEntity savedUser = userCaptor.getValue();

        assertEquals(encodedPassword, savedUser.getPassword(), "Password should be encoded");
    }

    @Test
    void isUsernameTaken_ShouldReturnTrue_WhenUsernameExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        assertTrue(userService.isUsernameTaken("testuser"));
    }

    @Test
    void isEmailTaken_ShouldReturnTrue_WhenEmailExists() {
        Mockito.when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(new UserEntity()));
        assertTrue(userService.isEmailTaken("test@example.com"));
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenUserExists() {
        Mockito.when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(testUser));
        UserEntity result = userService.findByUsername("testuser");
        assertEquals(testUser, result);
    }

    @Test
    void findByUsername_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.findByUsername("unknownUser"));
    }

    @Test
    void changeUserRole_ShouldUpdateRole_WhenAdminChangesRole() {
        // Arrange
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername("testuser");
        user.setRole(Role.USER);

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        // Act
        userService.changeUserRole(userId, Role.ADMIN);

        // Assert
        Assertions.assertEquals(Role.ADMIN, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void shouldEncryptPasswordBeforeSavingUser() {
        testUser.setPassword("password123");

        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.registerUser(testUser);

        assertEquals("hashedPassword", testUser.getPassword(), "Password should be hashed before saving.");
    }
}