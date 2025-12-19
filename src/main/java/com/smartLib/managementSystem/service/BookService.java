package com.smartLib.managementSystem.service;

import com.smartLib.managementSystem.model.Book;
import com.smartLib.managementSystem.model.dto.BookAndUserDTO;
import com.smartLib.managementSystem.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void save(Book book) {
        bookRepository.save(book);
    }

    public void update(Book book) {
        bookRepository.update(book);
    }

    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    public List<Book> findByUserId(Long userId) {
        return bookRepository.findByUserId(userId);
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public List<BookAndUserDTO> findAllWithUserInfo() {
        return bookRepository.findAllWithUserInfo();
    }
    public List<Book> findByUserIdAndGenre(Long userId, String genre) {
        return bookRepository.findByUserIdAndGenre(userId, genre);
    }

    public List<String> findGenresByUserId(Long userId) {
        return bookRepository.findGenresByUserId(userId);
    }

    public List<BookAndUserDTO> findAllByGenre(String genre) {
        return bookRepository.findAllWithUserInfoByGenre(genre);
    }

    public List<String> findAllGenres() {
        return bookRepository.findAllGenres();
    }

}
