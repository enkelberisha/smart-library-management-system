package com.smartLib.managementSystem.repository;

import com.smartLib.managementSystem.config.DBConnector;
import com.smartLib.managementSystem.model.Book;
import com.smartLib.managementSystem.model.dto.BookAndUserDTO;
import com.smartLib.managementSystem.model.dto.BookRow;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository {

    public void     save(Book book) {
        String sql =
                "INSERT INTO books (title, author, genre, status, user_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getGenre());
            ps.setString(4, book.getStatus());
            ps.setLong(5, book.getUserId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save book", e);
        }
    }

    public List<Book> findByUserId(Long userId) {
        String sql = "SELECT * FROM books WHERE user_id = ? ORDER BY id";
        List<Book> books = new ArrayList<>();

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list books for user", e);
        }

        return books;
    }

    public List<Book> findAll() {
        String sql = "SELECT * FROM books ORDER BY id";
        List<Book> books = new ArrayList<>();

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                books.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list all books", e);
        }

        return books;
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete book", e);
        }
    }

    public Optional<Book> findById(Long id) {
        String sql = "SELECT * FROM books WHERE id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find book by id", e);
        }

        return Optional.empty();
    }

    public void update(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, genre = ?, status = ? WHERE id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getGenre());
            ps.setString(4, book.getStatus());
            ps.setLong(5, book.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update book", e);
        }
    }


    public List<BookAndUserDTO> findAllWithUserInfo() {

        String sql = """
        SELECT 
            b.id,
            b.title,
            b.author,
            b.genre,
            b.status,
            u.id    AS user_id,
            u.name  AS user_name,
            u.email AS user_email
        FROM books b
        JOIN users u ON u.id = b.user_id
        ORDER BY b.id
    """;

        List<BookAndUserDTO> books = new ArrayList<>();

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                BookAndUserDTO dto = new BookAndUserDTO();

                dto.setId(rs.getLong("id"));
                dto.setTitle(rs.getString("title"));
                dto.setAuthor(rs.getString("author"));
                dto.setGenre(rs.getString("genre"));
                dto.setStatus(rs.getString("status"));

                dto.setUserId(rs.getLong("user_id"));
                dto.setUserName(rs.getString("user_name"));
                dto.setUserEmail(rs.getString("user_email"));

                books.add(dto);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to list all books with users", e);
        }

        return books;
    }

    public List<Book> findByUserIdAndGenre(Long userId, String genre) {
        String sql = "SELECT * FROM books WHERE user_id = ? AND genre = ? ORDER BY id";
        List<Book> books = new ArrayList<>();

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setString(2, genre);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list books by genre", e);
        }

        return books;
    }

    public List<String> findGenresByUserId(Long userId) {
        String sql = "SELECT DISTINCT genre FROM books WHERE user_id = ? AND genre IS NOT NULL AND genre <> '' ORDER BY genre";
        List<String> genres = new ArrayList<>();

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    genres.add(rs.getString("genre"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list genres", e);
        }

        return genres;
    }

    public List<BookAndUserDTO> findAllWithUserInfoByGenre(String genre) {

        String sql = """
        SELECT
            b.id,
            b.title,
            b.author,
            b.genre,
            b.status,
            u.id    AS user_id,
            u.name  AS user_name,
            u.email AS user_email
        FROM books b
        JOIN users u ON u.id = b.user_id
        WHERE b.genre = ?
        ORDER BY b.id
    """;

        List<BookAndUserDTO> list = new ArrayList<>();

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, genre);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BookAndUserDTO dto = new BookAndUserDTO();

                    dto.setId(rs.getLong("id"));
                    dto.setTitle(rs.getString("title"));
                    dto.setAuthor(rs.getString("author"));
                    dto.setGenre(rs.getString("genre"));
                    dto.setStatus(rs.getString("status"));

                    dto.setUserId(rs.getLong("user_id"));
                    dto.setUserName(rs.getString("user_name"));
                    dto.setUserEmail(rs.getString("user_email"));

                    list.add(dto);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to list all books with users by genre", e);
        }

        return list;
    }


    public List<String> findAllGenres() {
        String sql = "SELECT DISTINCT genre FROM books WHERE genre IS NOT NULL AND genre <> '' ORDER BY genre";
        List<String> genres = new ArrayList<>();

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                genres.add(rs.getString("genre"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list genres", e);
        }

        return genres;
    }


    private Book mapRow(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setId(rs.getLong("id"));
        b.setTitle(rs.getString("title"));
        b.setAuthor(rs.getString("author"));
        b.setGenre(rs.getString("genre"));
        b.setStatus(rs.getString("status"));
        b.setUserId(rs.getLong("user_id"));
        return b;
    }
    public List<BookRow> findUserBooksForSuggestions(int userId) {

        String sql = """
                SELECT id, title, author, genre, status
                FROM books
                WHERE user_id = ?
                ORDER BY
                  CASE status
                    WHEN 'reading' THEN 1
                    WHEN 'want_to_read' THEN 2
                    WHEN 'completed' THEN 3
                  END, title
                """;

        List<BookRow> books = new ArrayList<>();

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(new BookRow(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("genre"),
                            rs.getString("status")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user books for AI suggestions", e);
        }

        return books;
    }

}
