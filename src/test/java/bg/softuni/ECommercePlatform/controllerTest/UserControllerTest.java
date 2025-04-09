package bg.softuni.ECommercePlatform.controllerTest;

import bg.softuni.ECommercePlatform.controller.UserController;
import bg.softuni.ECommercePlatform.model.UserEntity;
import bg.softuni.ECommercePlatform.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private UserController userController;

    @Mock
    private HttpServletRequest request;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new UserEntity();
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("password123");
    }

    @Test
    void registerUser_ShouldRedirectToLogin_WhenSuccessful() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String result = userController.registerUser(testUser, bindingResult, model, request);

        assertEquals("redirect:/login", result);
        verify(userService, times(1)).registerUser(testUser);
    }

    @Test
    void registerUser_ShouldReturnRegisterPage_WhenValidationFails() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = userController.registerUser(testUser, bindingResult, model, request);

        assertEquals("register", result);
        verify(userService, never()).registerUser(testUser);
    }
}
