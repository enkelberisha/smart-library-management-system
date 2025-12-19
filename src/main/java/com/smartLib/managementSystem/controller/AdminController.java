package com.smartLib.managementSystem.controller;

import com.smartLib.managementSystem.model.User;
import com.smartLib.managementSystem.service.BookService;
import com.smartLib.managementSystem.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final BookService bookService;

    public AdminController(UserService userService, BookService bookService) {
        this.userService = userService;
        this.bookService = bookService;
    }

    private boolean isAdmin(HttpSession session) {
        Object role = session.getAttribute("userRole");
        return role != null && "ADMIN".equalsIgnoreCase(role.toString());
    }

    @GetMapping
    public String adminDashboard(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "userBooks") String mode,
            @RequestParam(required = false) String genre,
            HttpSession session,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        List<User> users = userService.findAll();
        users.removeIf(u -> "ADMIN".equalsIgnoreCase(u.getRole()));
        model.addAttribute("users", users);
        model.addAttribute("mode", mode);

        model.addAttribute("genres", bookService.findAllGenres());
        model.addAttribute("selectedGenre", genre == null ? "" : genre);

        if ("allBooks".equalsIgnoreCase(mode)) {

            if (genre != null && !genre.isBlank()) {

                model.addAttribute("allBooks", bookService.findAllByGenre(genre));
            } else {
                model.addAttribute("allBooks", bookService.findAllWithUserInfo());
            }

            model.addAttribute("books", null);
            model.addAttribute("selectedUser", null);
            return "admin";
        }

        if (userId != null) {

            if (genre != null && !genre.isBlank()) {
                model.addAttribute("books", bookService.findByUserIdAndGenre(userId, genre));
            } else {
                model.addAttribute("books", bookService.findByUserId(userId));
            }

            model.addAttribute("selectedUser",
                    userService.findById(userId).orElse(null));
        } else {
            model.addAttribute("books", null);
            model.addAttribute("selectedUser", null);
        }

        model.addAttribute("allBooks", null);
        return "admin";
    }

    @GetMapping("/users/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model, HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        User user = userService.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/admin";
        }

        model.addAttribute("user", user);
        return "user/user-edit";
    }

    @PostMapping("/users/{id}/edit")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("user") User user,
                             HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        user.setId(id);
        userService.updateUser(user);

        return "redirect:/admin?successMsg=User updated successfully";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        userService.deleteById(id);
        return "redirect:/admin?successMsg=User deleted";
    }

    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("userForm", new User());
        return "user/add-user";
    }


    @PostMapping("/users")
    public String createUser(@ModelAttribute("userForm") User form,
                             RedirectAttributes ra,
                             HttpSession session) {

        if (!isAdmin(session)) return "redirect:/login";

        boolean created = userService.createUser(form);

        if (created) ra.addFlashAttribute("successMsg", "User created!");
        else ra.addFlashAttribute("errorMsg", "Email already exists!");

        return "redirect:/admin/users/new";
    }



}
