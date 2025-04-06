package bg.softuni.ECommercePlatform.controller;

import bg.softuni.ECommercePlatform.dto.UserDTO;
import bg.softuni.ECommercePlatform.enums.Role;
import bg.softuni.ECommercePlatform.model.UserEntity;
import bg.softuni.ECommercePlatform.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/profile")
    public UserEntity getProfile(Principal principal) {
        return userService.getUserByUsername(principal.getName());
    }

    @PutMapping("/profile")
    public UserEntity updateProfile(Principal principal, @RequestParam String username, @RequestParam String email) {
        UserEntity user = getProfile(principal);
        return userService.updateUser(user.getId(), username, email);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public void changeUserRole(@PathVariable Long id, @RequestParam Role role) {
        userService.changeUserRole(id, role);
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserEntity());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") UserEntity user,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (userService.isUsernameTaken(user.getUsername())) {
            model.addAttribute("usernameError", "Username is already taken");
            return "register";
        }

        if (userService.isEmailTaken(user.getEmail())) {
            model.addAttribute("emailError", "Email is already taken");
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userService.registerUser(user);

        try {
            Authentication authRequest =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            Authentication auth = authenticationManager.authenticate(authRequest);
            SecurityContextHolder.getContext().setAuthentication(auth);

            request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("authError", "Failed to log in after registration.");
            return "register";
        }

        return "redirect:/home?registerSuccess";  // Redirect user after successful registration
    }

    @GetMapping("/confirm")
    public String confirmEmail(@RequestParam("token") String token, Model model) {
        try {
            userService.confirmToken(token);
            model.addAttribute("message", "Email confirmed successfully. You can now log in.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "confirm";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<UserDTO> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/users/{id}")
    public String userDetails(@PathVariable Long id, Model model) {
        UserEntity user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "user-details";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
