package com.smartLib.managementSystem.service;

import com.smartLib.managementSystem.ai.OpenRouterClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AiQueryService {

    private final OpenRouterClient ai;
    private final JdbcTemplate jdbc;

    public AiQueryService(OpenRouterClient ai, JdbcTemplate jdbc) {
        this.ai = ai;
        this.jdbc = jdbc;
    }

    public AiResult run(String question) {

        try {
            String sql = generateSql(question);

            if (!sql.toLowerCase().startsWith("select")) {
                return AiResult.error("Only SELECT queries are allowed.");
            }

            List<Map<String, Object>> data = jdbc.queryForList(sql);

            if (data.isEmpty()) {
                return new AiResult(
                        "Query executed successfully. No results found.",
                        List.of(),
                        List.of(),
                        null
                );
            }

            List<String> columns = new ArrayList<>(data.get(0).keySet());
            List<List<Object>> rows = new ArrayList<>();

            for (Map<String, Object> row : data) {
                rows.add(new ArrayList<>(row.values()));
            }

            return new AiResult(
                    "Query executed successfully.",
                    columns,
                    rows,
                    null
            );

        } catch (Exception e) {
            return AiResult.error(e.getMessage());
        }
    }

    private String generateSql(String question) {

        String systemPrompt = """
        You are an AI that generates PostgreSQL SQL queries.

        Database schema:
        users(id, name, email, password, role)
        books(id, title, author, genre, status, user_id)

        Rules:
        - roles are ONLY: ADMIN, USER
        - book status ONLY: completed, reading, want_to_read
        - Use JOIN only when needed
        - NEVER use tables or columns not listed
        - Return ONLY raw SQL
        - Only SELECT queries allowed
        """;

        return ai.chat(systemPrompt, question)
                .replace("```sql", "")
                .replace("```", "")
                .trim();
    }

    public record AiResult(
            String answerText,
            List<String> columns,
            List<List<Object>> rows,
            String error
    ) {
        public static AiResult error(String msg) {
            return new AiResult(null, null, null, msg);
        }
    }
}
