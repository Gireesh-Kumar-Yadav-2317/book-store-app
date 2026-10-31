
package com.example.demo.repository;

import com.example.demo.model.Book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import jakarta.persistence.EntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    // Test 1: Find a book by ID
    @Test
    void whenFindById_thenReturnBook() {

        // Given
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");

        entityManager.persist(book);
        entityManager.flush();

        // When
        Optional<Book> found =
                bookRepository.findById(book.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("Test Book", found.get().getTitle());
        assertEquals("Test Author", found.get().getAuthor());
    }

    // Test 2: Save a book
    @Test
    void whenSaveBook_thenReturnSavedBook() {

        // Given
        Book book = new Book();
        book.setTitle("Save Test Book");
        book.setAuthor("Save Test Author");

        // When
        Book savedBook = bookRepository.save(book);
        entityManager.flush();

        // Then
        assertNotNull(savedBook.getId());
        assertEquals("Save Test Book", savedBook.getTitle());
        assertEquals("Save Test Author", savedBook.getAuthor());
    }

    // Test 3: Delete a book
    @Test
    void whenDeleteBook_thenBookShouldNotExist() {

        // Given
        Book book = new Book();
        book.setTitle("Delete Test Book");
        book.setAuthor("Delete Test Author");

        entityManager.persist(book);
        entityManager.flush();

        // When
        bookRepository.deleteById(book.getId());
        entityManager.flush();

        // Then
        Optional<Book> deletedBook =
                bookRepository.findById(book.getId());

        assertFalse(deletedBook.isPresent());
    }
}
