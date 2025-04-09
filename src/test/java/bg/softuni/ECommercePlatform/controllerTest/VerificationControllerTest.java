package bg.softuni.ECommercePlatform.controllerTest;

import bg.softuni.ECommercePlatform.model.UserEntity;
import bg.softuni.ECommercePlatform.repository.UserRepository;
import bg.softuni.ECommercePlatform.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private EmailService emailService;

    @Test
    void verifyEmail_ShouldActivateUser_WhenTokenIsValid() throws Exception {
        UserEntity user = new UserEntity();
        user.setVerificationToken("validToken");

        when(userRepository.findByVerificationToken("validToken")).thenReturn(user);

        mockMvc.perform(get("/verify").param("token", "validToken"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?verified"));

        assertTrue(user.isVerified());
    }

    @Test
    void verifyEmail_ShouldFail_WhenTokenIsInvalid() throws Exception {
        when(userRepository.findByVerificationToken("invalidToken")).thenReturn(null);

        mockMvc.perform(get("/verify").param("token", "invalidToken"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=invalidToken"));
    }

    @Test
    void verifyEmail_ShouldRedirectOnSuccess() throws Exception {
        String email = "test@example.com";
        String username = "testUser";
        String confirmationLink = "http://localhost/verify-email?token=validToken";
        doNothing().when(emailService).sendConfirmationEmail(email, username, confirmationLink);

        mockMvc.perform(get("/verify-email").param("token", "validToken"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?verified"));
    }

    @Test
    void verifyEmail_ShouldFailForInvalidToken() throws Exception {
        doThrow(new IllegalArgumentException("Invalid token"))
                .when(emailService).sendConfirmationEmail(anyString(), anyString(), anyString());

        mockMvc.perform(get("/verify-email").param("token", "invalidToken"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register?error"));
    }
}