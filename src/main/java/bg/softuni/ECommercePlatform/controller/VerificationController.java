package bg.softuni.ECommercePlatform.controller;

import bg.softuni.ECommercePlatform.model.UserEntity;
import bg.softuni.ECommercePlatform.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VerificationController {
    private final UserRepository userRepository;

    public VerificationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        UserEntity user = userRepository.findByVerificationToken(token);
        if (user != null) {
            user.setVerified(true);
            user.setVerificationToken(null);    // Clear the token
            userRepository.save(user);
            return "redirect:/login?verified";
        }
        return "redirect:/login?error=invalidToken";
    }
}