package com.smartLib.managementSystem.controller;

import com.smartLib.managementSystem.model.Book;
import com.smartLib.managementSystem.service.BookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.smartLib.managementSystem.ai.AiSuggestionService;

import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final BookService bookService;

    private final AiSuggestionService aiSuggestionService;

    public BooksController(BookService bookService, AiSuggestionService aiSuggestionService) {
        this.bookService = bookService;
        this.aiSuggestionService = aiSuggestionService;
    }


    private boolean isAdmin(HttpSession session) {
        Object role = session.getAttribute("userRole");
        return role != null && "ADMIN".equalsIgnoreCase(role.toString());
    }

    private Long requireUserId(HttpSession session) {
        Object id = session.getAttribute("userId");
        return (id instanceof Long) ? (Long) id : null;
    }


    @GetMapping
    public String listBooks(@RequestParam(required = false) String genre,
                            HttpSession session,
                            Model model) {

        Long userId = requireUserId(session);
        if (userId == null) return "redirect:/login";

        boolean admin = isAdmin(session);

        if (admin) {
            model.addAttribute("genres", bookService.findAllGenres());
        } else {
            model.addAttribute("genres", bookService.findGenresByUserId(userId));
        }

        model.addAttribute("selectedGenre", genre == null ? "" : genre);

        if (genre != null && !genre.isBlank()) {
            if (admin) {
                model.addAttribute("books", bookService.findAllByGenre(genre));
            } else {
                model.addAttribute("books", bookService.findByUserIdAndGenre(userId, genre));
            }
        } else {
            if (admin) {
                model.addAttribute("books", bookService.findAll());
            } else {
                model.addAttribute("books", bookService.findByUserId(userId));
            }
        }

        return "books";
    }

    @GetMapping("/new")
    public String showAddForm(HttpSession session, Model model) {
        Long userId = requireUserId(session);
        if (userId == null) return "redirect:/login";

        Book book = new Book();
        book.setUserId(userId);
        model.addAttribute("book", book);

        return "books/new";
    }


    @PostMapping("/new")
    public String createBook(@ModelAttribute Book book,
                             HttpSession session,
                             Model model) {

        Long userId = requireUserId(session);
        if (userId == null) return "redirect:/login";

        book.setUserId(userId);

        try {
            bookService.save(book);
            return "redirect:/books";
        } catch (Exception e) {
            model.addAttribute("errorMsg", "Failed to add book: " + e.getMessage());
            model.addAttribute("book", book);
            return "books/new";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               HttpSession session,
                               Model model) {
        Long userId = requireUserId(session);
        if (userId == null) return "redirect:/login";

        Optional<Book> bookOpt = bookService.findById(id);
        if (bookOpt.isEmpty()) return "redirect:/books";

        Book book = bookOpt.get();

        if (!isAdmin(session) && !userId.equals(book.getUserId())) {
            return "redirect:/books";
        }

        model.addAttribute("book", book);
        return "books/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateBook(@PathVariable Long id,
                             @ModelAttribute Book formBook,
                             HttpSession session,
                             Model model) {

        Long userId = requireUserId(session);
        if (userId == null) return "redirect:/login";

        boolean isAdmin = isAdmin(session);
        String redirectTarget = isAdmin ? "redirect:/admin" : "redirect:/books";

        Optional<Book> bookOpt = bookService.findById(id);
        if (bookOpt.isEmpty()) return redirectTarget;

        Book existing = bookOpt.get();

        if (!isAdmin && !userId.equals(existing.getUserId())) {
            return redirectTarget;
        }

        existing.setTitle(formBook.getTitle());
        existing.setAuthor(formBook.getAuthor());
        existing.setGenre(formBook.getGenre());
        existing.setStatus(formBook.getStatus());

        try {
            bookService.update(existing);
            return redirectTarget;
        } catch (Exception e) {
            model.addAttribute("errorMsg", "Failed to update book: " + e.getMessage());
            model.addAttribute("book", existing);

            return isAdmin ? "admin/edit-book" : "books/edit";
        }
    }


    @PostMapping("/{id}/delete")
    public String deleteBook(@PathVariable Long id,
                             HttpSession session) {
        Long userId = requireUserId(session);
        if (userId == null) return "redirect:/login";

        Optional<Book> bookOpt = bookService.findById(id);
        if (bookOpt.isEmpty()) return "redirect:/books";

        Book book = bookOpt.get();

        if (!isAdmin(session) && !userId.equals(book.getUserId())) {
            return "redirect:/books";
        }

        bookService.deleteById(id);
        return "redirect:/books";
    }

    @PostMapping("/ai-suggestions")
    public String generateAiSuggestions(@RequestParam(required = false) String genre,
                                        HttpSession session,
                                        Model model) {

        Long userId = requireUserId(session);
        if (userId == null) return "redirect:/login";

        boolean admin = isAdmin(session);

        if (admin) {
            model.addAttribute("genres", bookService.findAllGenres());
        } else {
            model.addAttribute("genres", bookService.findGenresByUserId(userId));
        }

        model.addAttribute("selectedGenre", genre == null ? "" : genre);

        if (genre != null && !genre.isBlank()) {
            if (admin) model.addAttribute("books", bookService.findAllByGenre(genre));
            else model.addAttribute("books", bookService.findByUserIdAndGenre(userId, genre));
        } else {
            if (admin) model.addAttribute("books", bookService.findAll());
            else model.addAttribute("books", bookService.findByUserId(userId));
        }

        model.addAttribute("aiSuggestions",
                aiSuggestionService.suggestBooksForUser(userId.intValue(), 5));

        return "books";
    }


}
