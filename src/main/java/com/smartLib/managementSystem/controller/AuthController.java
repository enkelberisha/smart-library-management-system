package com.smartLib.managementSystem.controller;

import com.smartLib.managementSystem.model.User;
import com.smartLib.managementSystem.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLogin() {
        return "auth/login";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        Optional<User> userOpt = authService.authenticate(email, password);

        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Invalid email or password");
            return "auth/login";
        }

        User user = userOpt.get();

        session.setAttribute("userId", user.getId());
        session.setAttribute("userRole", user.getRole());
        session.setAttribute("fullName", user.getName());


        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/admin";
        }

        return "redirect:/books";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        boolean success = authService.register(user);

        if (!success) {
            model.addAttribute("error", "Email already exists");
            return "auth/register";
        }

        model.addAttribute("success", "Registration successful. Please login.");
        return "auth/login";
    }


    @GetMapping("/register")
    public String showRegister() {
        return "auth/register";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }


}
