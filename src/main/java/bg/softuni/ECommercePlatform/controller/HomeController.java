package bg.softuni.ECommercePlatform.controller;

import bg.softuni.ECommercePlatform.model.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserEntity user, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (user != null) {
            model.addAttribute("username", user.getUsername());
        }

        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getPrincipal().equals("anonymousUser")) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("username", authentication.getName());
        } else {
            model.addAttribute("isLoggedIn", false);
        }

        return "home";
    }
}
