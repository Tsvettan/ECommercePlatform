package bg.softuni.ECommercePlatform.controllerTest;

import bg.softuni.ECommercePlatform.controller.LoginController;
import bg.softuni.ECommercePlatform.controller.ProfileController;
import bg.softuni.ECommercePlatform.controller.RegisterController;
import bg.softuni.ECommercePlatform.model.UserEntity;
import bg.softuni.ECommercePlatform.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProfileTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProfileController profileController;

    private LoginController loginController;

    private RegisterController registerController;

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        profileController = new ProfileController(userService);
        loginController = new LoginController();
        registerController = new RegisterController();
        mockMvc = MockMvcBuilders.standaloneSetup(profileController, loginController, registerController).build();
    }

    @Test
    void loginPage_ShouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void registerPage_ShouldReturnRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void getProfilePage_ShouldReturnProfileViewWithUser() throws Exception {
        UserEntity user = new UserEntity();
        user.setUsername("testUser");
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");
        when(userService.findByUsername("testUser")).thenReturn(user);

        mockMvc.perform(get("/profile").principal(principal))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void updateProfile_ShouldRedirectToProfilePage() throws Exception {
        UserEntity user = new UserEntity();
        user.setUsername("testUser");
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");
        doNothing().when(userService).updateUserProfile(user, "testUser");

        mockMvc.perform(post("/profile/update").flashAttr("user", user).principal(principal))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?success"));
    }

    @Test
    void getProfilePage_ShouldReturnProfileView() throws Exception {
        Principal mockPrincipal = () -> "testUser";
        UserEntity user = new UserEntity();
        user.setUsername("testUser");
        when(userService.findByUsername("testUser")).thenReturn(user);

        Model model = mock(Model.class);
        String viewName = profileController.getProfilePage(model, mockPrincipal);

        assertEquals("profile", viewName);
        verify(model).addAttribute("user", user);
    }

    @Test
    void updateProfile_ShouldRedirectOnSuccess() throws Exception {
        UserEntity user = new UserEntity();
        user.setUsername("testUser");
        Principal mockPrincipal = () -> "testUser";

        mockMvc.perform(post("/profile/update").flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?success"));
    }

    @Test
    void updateProfile_ShouldFailForInvalidData() throws Exception {
        UserEntity user = new UserEntity();
        user.setUsername(""); // Invalid username
        Principal mockPrincipal = () -> "testUser";

        mockMvc.perform(post("/profile/update").flashAttr("user", user))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?error"));
    }

    @Test
    void updateProfile_ShouldFailForUnauthorizedUser() throws Exception {
        UserEntity user = new UserEntity();
        user.setUsername("anotherUser");
        Principal mockPrincipal = () -> "testUser";

        doThrow(new SecurityException("Unauthorized"))
                .when(userService).updateUserProfile(any(), eq("testUser"));

        mockMvc.perform(post("/profile/update").flashAttr("user", user))
                .andExpect(status().isForbidden());
    }
}