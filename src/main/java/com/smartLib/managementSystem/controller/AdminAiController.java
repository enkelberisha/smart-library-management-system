package com.smartLib.managementSystem.controller;

import com.smartLib.managementSystem.service.AiQueryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/ai-user")
public class AdminAiController {

    private final AiQueryService service;

    public AdminAiController(AiQueryService service) {
        this.service = service;
    }

    private boolean isAdmin(HttpSession session) {
        Object role = session.getAttribute("userRole");
        return role != null && "ADMIN".equals(role.toString());
    }

    @GetMapping
    public String page(Model model, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        model.addAttribute("q", "");
        model.addAttribute("answerText", null);
        model.addAttribute("columns", null);
        model.addAttribute("rows", null);
        model.addAttribute("error", null);

        return "ai-user";
    }

    @PostMapping
    public String ask(@RequestParam(name = "q", required = false) String q,
                      Model model,
                      HttpSession session) {

        if (!isAdmin(session)) return "redirect:/login";

        q = (q == null) ? "" : q.trim();

        model.addAttribute("q", q);

        if (q.isBlank()) {
            model.addAttribute("answerText", null);
            model.addAttribute("columns", null);
            model.addAttribute("rows", null);
            model.addAttribute("error", "Please enter a question.");
            return "ai-user";
        }

        try {
            var result = service.run(q);

            model.addAttribute("answerText", result.answerText());
            model.addAttribute("columns", result.columns());
            model.addAttribute("rows", result.rows());
            model.addAttribute("error", result.error());

        } catch (Exception e) {
            model.addAttribute("answerText", null);
            model.addAttribute("columns", null);
            model.addAttribute("rows", null);
            model.addAttribute("error", "AI error: " + e.getMessage());
        }

        return "ai-user";
    }
}
