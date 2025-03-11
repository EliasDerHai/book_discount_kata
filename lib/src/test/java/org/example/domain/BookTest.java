package org.example.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookTest {

    @Test
    void shouldCreateBook_whenGivenValidId() {
        assertInstanceOf(Book.class, Book.from(1).get());
        assertInstanceOf(Book.class, Book.from(2).get());
        assertInstanceOf(Book.class, Book.from(3).get());
        assertInstanceOf(Book.class, Book.from(4).get());
        assertInstanceOf(Book.class, Book.from(5).get());
    }

    @Test
    void shouldNotCreateBook_whenGivenInvalidId() {
        assertTrue(Book.from(-1).isLeft());
        assertTrue(Book.from(0).isLeft());
        assertTrue(Book.from(6).isLeft());
        assertTrue(Book.from(Integer.MAX_VALUE).isLeft());
        assertTrue(Book.from(Integer.MIN_VALUE).isLeft());
    }
}