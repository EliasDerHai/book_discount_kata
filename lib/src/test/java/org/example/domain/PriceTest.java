package org.example.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PriceTest {

    @Test
    void shouldNotCreatePrice_whenGivenInvalidPrice() {
        assertTrue(Price.fromEuros(-.000000001f).isLeft());
        assertTrue(Price.fromEuros(Float.NEGATIVE_INFINITY).isLeft());
        assertTrue(Price.fromEuroCents(Long.MIN_VALUE).isLeft());
    }

    @Test
    void shouldCreatePrice_whenGivenValidPrice() {
        assertInstanceOf(Price.class, Price.fromEuroCents(0).get());
        assertInstanceOf(Price.class, Price.fromEuroCents(1).get());
        assertInstanceOf(Price.class, Price.fromEuroCents(Long.MAX_VALUE).get());
        assertInstanceOf(Price.class, Price.fromEuros(0f).get());
        assertInstanceOf(Price.class, Price.fromEuros((float) Math.PI).get());
        assertInstanceOf(Price.class, Price.fromEuros(Float.MAX_VALUE).get());
    }

    @Test
    void shouldRecognizePriceAsEqual_whenGivenValidPrice() {
        var fromCents = Price.fromEuroCents(123).get();
        var fromEuros = Price.fromEuros(1.23f).get();

        assertEquals(fromCents, fromEuros);
    }


    @Test
    void shouldRoundPrice_whenGivenOverlyPrecise() {
        var fromEuros = Price.fromEuros(9.99999999f).get();

        assertEquals(1_000, fromEuros.getPriceInEuroCents());
    }
}