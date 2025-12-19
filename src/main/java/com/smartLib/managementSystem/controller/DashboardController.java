package com.smartLib.managementSystem.controller;

import com.smartLib.managementSystem.ai.AiSuggestionService;
import com.smartLib.managementSystem.service.BookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final BookService bookService;
    private final AiSuggestionService aiSuggestionService;

    public DashboardController(BookService bookService,
                               AiSuggestionService aiSuggestionService) {
        this.bookService = bookService;
        this.aiSuggestionService = aiSuggestionService;
    }

    private Long getUserId(HttpSession session) {
        Object id = session.getAttribute("userId");
        if (id == null) return null;
        if (id instanceof Long l) return l;
        if (id instanceof Integer i) return i.longValue();
        return Long.parseLong(id.toString());
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model) {

        Long userId = getUserId(session);
        if (userId == null) return "redirect:/login";

        // your normal dashboard data
        model.addAttribute("books", bookService.findByUserId(userId));
        model.addAttribute("aiSuggestions", null); // not generated yet

        return "dashboard";
    }

    @PostMapping("/suggestions")
    public String generateSuggestions(HttpSession session, Model model) {

        Long userId = getUserId(session);
        if (userId == null) return "redirect:/login";

        model.addAttribute("books", bookService.findByUserId(userId));

        model.addAttribute("aiSuggestions",
                aiSuggestionService.suggestBooksForUser(userId.intValue(), 5));

        return "dashboard";
    }
}
