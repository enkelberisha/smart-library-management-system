package com.smartLib.managementSystem.ai;

import com.fasterxml.jackson.databind.*;
import com.smartLib.managementSystem.model.dto.BookRow;
import com.smartLib.managementSystem.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AiSuggestionService {

    private final BookRepository bookRepo;
    private final OpenRouterClient openRouter;
    private final ObjectMapper mapper;

    public AiSuggestionService(BookRepository bookRepo, OpenRouterClient openRouter) {
        this.bookRepo = bookRepo;
        this.openRouter = openRouter;
        this.mapper = new ObjectMapper();
    }

    public List<Suggestion> suggestBooksForUser(long userId, int k) {

        List<BookRow> books = bookRepo.findUserBooksForSuggestions((int) userId);
        if (books.isEmpty()) return List.of();

        List<BookRow> candidates = books.stream()
                .filter(b -> "want_to_read".equals(b.status()) || "reading".equals(b.status()))
                .toList();

        if (candidates.isEmpty()) candidates = books;

        String booksJson;
        try {
            booksJson = mapper.writeValueAsString(
                    candidates.stream().map(b -> Map.of(
                            "title", b.title(),
                            "author", b.author(),
                            "genre", (b.genre() == null || b.genre().isBlank()) ? "Unknown" : b.genre(),
                            "status", b.status()
                    )).toList()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize books for AI", e);
        }

        String system = """
You are a book recommendation assistant.
You must recommend books based ONLY on the user's library.
You may suggest NEW books that are NOT in the list,
but they must logically match the user's genres/authors.
Return ONLY valid JSON.
No explanations. No extra text.
""";


        String user = """
USER_LIBRARY (books the user owns):
%s

Task:
Based on the user's reading preferences (genres, authors, patterns),
recommend up to %d NEW books the user might like.

Rules:
- Do NOT repeat books already in USER_LIBRARY
- Recommendations must be realistic, well-known books
- Prefer similar genres or authors
- Keep reasons short (max 12 words)

Output EXACTLY this JSON:
{
  "suggestions": [
    {
      "title": "Book Title",
      "author": "Author Name",
      "reason": "Why it fits the user's taste"
    }
  ]
}

Return ONLY JSON.
""".formatted(booksJson, k);


        String ai = openRouter.chat(system, user);

        return parse(ai, k);

    }

    private List<Suggestion> parse(String json, int k) {
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode arr = root.get("suggestions");
            if (arr == null || !arr.isArray()) return List.of();

            List<Suggestion> out = new ArrayList<>();

            for (JsonNode n : arr) {
                String title = n.path("title").asText("").trim();
                String reason = n.path("reason").asText("").trim();

                if (!title.isBlank()) {
                    out.add(new Suggestion(title, reason));
                }
            }

            return out.stream().limit(k).toList();
        } catch (Exception e) {
            return List.of();
        }
    }



    public record Suggestion(String title, String reason) {}
}
