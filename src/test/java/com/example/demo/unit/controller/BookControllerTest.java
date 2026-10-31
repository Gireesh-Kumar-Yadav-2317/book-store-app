package com.example.demo.unit.controller;

import com.example.demo.controller.BookController;
import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookRepository bookRepository;

    // Jackson 3 ObjectMapper
    private final ObjectMapper objectMapper = new JsonMapper();

    // 1. GET all books
    @Test
    void whenGetAllBooks_thenReturnJsonArray() throws Exception {

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");

        when(bookRepository.findAll()).thenReturn(Arrays.asList(book));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].author").value("Test Author"));
    }

    // 2. GET book by ID
    @Test
    void whenGetBookById_thenReturnBook() throws Exception {

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"));
    }

    // 3. CREATE book
    @Test
    void whenCreateBook_thenReturnCreatedBook() throws Exception {

        Book book = new Book();
        book.setTitle("New Book");
        book.setAuthor("New Author");

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle("New Book");
        savedBook.setAuthor("New Author");

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Book"))
                .andExpect(jsonPath("$.author").value("New Author"));
    }

    // 4. UPDATE book
    @Test
    void whenUpdateBook_thenReturnUpdatedBook() throws Exception {

        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Original Book");
        existingBook.setAuthor("Original Author");

        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setTitle("Updated Book");
        updatedBook.setAuthor("Updated Author");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        mockMvc.perform(put("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Book"))
                .andExpect(jsonPath("$.author").value("Updated Author"));
    }

    // 5. DELETE book
    @Test
    void whenDeleteBook_thenReturn200() throws Exception {

        doNothing().when(bookRepository).deleteById(1L);

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isOk());

        verify(bookRepository, times(1)).deleteById(1L);
    }

    // 6. GET non-existing book
    @Test
    void whenGetNonExistingBook_thenReturn200WithNull() throws Exception {

        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/books/999")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
}