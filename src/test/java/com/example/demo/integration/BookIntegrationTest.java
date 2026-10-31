package com.example.demo.integration;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookIntegrationTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private BookRepository bookRepository;

    @Test
    void whenCreateBook_thenBookIsSavedInDatabase() {
        // Create a book object
        Book book = new Book();
        book.setTitle("Integration Test Book");
        book.setAuthor("Integration Test Author");

        // Make POST request to create the book
        ResponseEntity<Book> createResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/books",
            book,
            Book.class
        );

        // Verify HTTP response
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().getId()).isNotNull();
        assertThat(createResponse.getBody().getTitle()).isEqualTo("Integration Test Book");
        assertThat(createResponse.getBody().getAuthor()).isEqualTo("Integration Test Author");

        // Verify book is in database
        Book savedBook = bookRepository.findById(createResponse.getBody().getId()).orElse(null);
        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Integration Test Book");
        assertThat(savedBook.getAuthor()).isEqualTo("Integration Test Author");
    }

    @Test
    void whenGetBook_thenReturnCorrectBook() {
        // Create a book in the database
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        Book savedBook = bookRepository.save(book);

        // Make GET request to retrieve the book
        ResponseEntity<Book> getResponse = restTemplate.getForEntity(
            "http://localhost:" + port + "/books/" + savedBook.getId(),
            Book.class
        );

        // Verify HTTP response
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(savedBook.getId());
        assertThat(getResponse.getBody().getTitle()).isEqualTo("Test Book");
        assertThat(getResponse.getBody().getAuthor()).isEqualTo("Test Author");
    }

    @Test
    void whenUpdateBook_thenBookIsUpdatedInDatabase() {
        // Create a book in the database
        Book book = new Book();
        book.setTitle("Original Title");
        book.setAuthor("Original Author");
        Book savedBook = bookRepository.save(book);

        // Update the book
        savedBook.setTitle("Updated Title");
        savedBook.setAuthor("Updated Author");

        // Make PUT request to update the book
        restTemplate.put("http://localhost:" + port + "/books/" + savedBook.getId(), savedBook);

        // Verify book is updated in database
        Book updatedBook = bookRepository.findById(savedBook.getId()).orElse(null);
        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedBook.getAuthor()).isEqualTo("Updated Author");
    }

    @Test
    void whenDeleteBook_thenBookIsRemovedFromDatabase() {
        // Create a book in the database
        Book book = new Book();
        book.setTitle("Book to Delete");
        book.setAuthor("Author to Delete");
        Book savedBook = bookRepository.save(book);

        // Make DELETE request
        restTemplate.delete("http://localhost:" + port + "/books/" + savedBook.getId());

        // Verify book is deleted from database
        assertThat(bookRepository.findById(savedBook.getId())).isEmpty();
    }
}
