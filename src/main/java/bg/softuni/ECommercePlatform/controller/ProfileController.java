package bg.softuni.ECommercePlatform.controller;

import bg.softuni.ECommercePlatform.model.UserEntity;
import bg.softuni.ECommercePlatform.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getProfilePage(Model model, Principal principal) {
        String username = principal.getName();
        UserEntity user = userService.getUserByUsername(username);
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute UserEntity user, Principal principal) {
        userService.updateUserProfile(user, principal.getName());
        return "redirect:/profile?success";
    }
}
