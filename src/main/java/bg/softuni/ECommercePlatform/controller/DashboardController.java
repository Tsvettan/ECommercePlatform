package bg.softuni.ECommercePlatform.controller;

import bg.softuni.ECommercePlatform.model.UserEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserEntity user, Model model) {
        model.addAttribute("username", user.getUsername());
        // Add additional user-specific data here
        return "dashboard";
    }
}
