package bg.softuni.ECommercePlatform.controllerTest;

import bg.softuni.ECommercePlatform.controller.ProfileController;
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

import java.security.Principal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class ProfileTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProfileController profileController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(profileController).build();
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
}
