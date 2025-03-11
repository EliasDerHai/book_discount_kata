package org.example.service;

import io.vavr.collection.List;
import io.vavr.control.Either;
import org.example.domain.Book;
import org.example.domain.Price;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.*;

class PriceCalculatorTest {

    @Test
    void shouldGetLowestPrice_whenGivenSituationWhereGreedyDoesNotSuffice_9books() {
        // arrange
        var books = getBooks(1, 1, 2, 2, 3, 3, 4, 5);

        // act
        var price = new PriceCalculator().getDiscountedPrice(books);

        // assert
        assertEquals(51_20, price.getPriceInEuroCents());
    }

    @Test
    void shouldGetLowestPrice_whenGivenSituationWhereGreedyDoesNotSuffice_11books() {
        // arrange
        var books = getBooks(1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 5);

        // act
        var price = new PriceCalculator().getDiscountedPrice(books);

        // assert
        assertEquals(51_20 + 3 * 800 * .9, price.getPriceInEuroCents());
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForCorrectPriceCalculation")
    void shouldFindTheCheapestPrice(List<Integer> input, double expected) {
        // arrange
        var books = input.map(id -> Book.from(id).get());

        // act
        var calculatedPrice = new PriceCalculator().getDiscountedPrice(books);

        // assert
        assertEquals(Price.fromEuros((float) expected).get(), calculatedPrice);
    }

    private static Stream<Arguments> provideTestCasesForCorrectPriceCalculation() {
        return Stream.of(
                Arguments.of(List.of(1), 8),
                Arguments.of(List.of(1, 2), 8 * 2 * .95),
                Arguments.of(List.of(1, 2, 3), 8 * 3 * .9),
                Arguments.of(List.of(1, 2, 3, 4), 8 * 4 * .8),
                Arguments.of(List.of(1, 2, 3, 4, 5), 8 * 5 * .75),
                Arguments.of(List.empty(), 0),
                Arguments.of(List.of(1, 1, 2), 8 * 2 * .95 + 8),
                Arguments.of(List.of(5, 5, 5, 2, 2, 2, 3), 8 * 2 * .95 * 2 + 8 * 3 * .9)
        );
    }

    public static List<Book> getBooks(int... ids) {
        return List.ofAll(ids)
                .map(Book::from)
                .map(Either::get);
    }

    @Test
    void shouldFindOptimalSolutionInReasonableTime_whenGivenBiggerShoppingBag() {
        // arrange
        var bigOrder = List.fill(20, Book.from(1))
                .appendAll(List.fill(20, Book.from(2)))
                .appendAll(List.fill(20, Book.from(3)))
                .appendAll(List.fill(20, Book.from(4)))
                .appendAll(List.fill(20, Book.from(5)))
                .map(Either::get);

        // act
        var price = new PriceCalculator().getDiscountedPrice(bigOrder);

        // assert
        assertEquals(Price.fromEuros((float) (8 * .75 * 5 * 20)).get(), price);
    }

    @Test
    void shouldThrow_whenHandedNull(){
        assertThrows(NullPointerException.class, () -> new PriceCalculator().getDiscountedPrice(null));
    }

    @Nested
    @EnabledIfEnvironmentVariable(named = "performanceTests", matches = "true")
    class PerformanceTests {

        @Test
        void shouldFindOptimalSolutionInReasonableTime_whenGivenBiggerShoppingBag_tricky() {
            // arrange
            var bigOrder = List.fill(20, Book.from(1))
                    .appendAll(List.fill(20, Book.from(2)))
                    .appendAll(List.fill(20, Book.from(3)))
                    .appendAll(List.fill(19, Book.from(4)))
                    .appendAll(List.fill(20, Book.from(5)))
                    .map(Either::get);

            // act
            var price = assertTimeout(ofSeconds(10), () ->
                    new PriceCalculator().getDiscountedPrice(bigOrder)
            );

            // assert
            assertEquals(Price.fromEuros((float) ((8 * .75 * 5 * 19) + 8 * .8 * 4)).get(), price);
        }

        @Test
        void shouldBeFasterThanBitShifting() {
            // arrange
            var bigOrder = List.fill(20, Book.from(1))
                    .appendAll(List.fill(20, Book.from(2)))
                    .appendAll(List.fill(20, Book.from(3)))
                    .appendAll(List.fill(19, Book.from(4)))
                    .appendAll(List.fill(20, Book.from(5)))
                    .map(Either::get);

            // act
            long startProduction = System.currentTimeMillis();
            var productionCodeResult = new PriceCalculator().getDiscountedPrice(bigOrder);
            long productionRuntimeInMillis = System.currentTimeMillis() - startProduction;

            long startComparison = System.currentTimeMillis();
            var comparisonCodeResult = PriceCalculatorBitShift.calculatePrice(bigOrder.asJava());
            long comparisonRuntimeInMillis = System.currentTimeMillis() - startComparison;

            // assert
            assertEquals(comparisonCodeResult, productionCodeResult);
            System.out.printf("Prod took %dms\n", productionRuntimeInMillis);
            System.out.printf("Comp took %dms\n", comparisonRuntimeInMillis);
            System.out.printf("Advantage is %.2f percent faster\n",
                    (1d - (double) productionRuntimeInMillis / (double) comparisonRuntimeInMillis) * 100d);
            assertTrue(productionRuntimeInMillis < comparisonRuntimeInMillis);
        }
    }
}