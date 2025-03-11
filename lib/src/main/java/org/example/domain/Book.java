package org.example.domain;

import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.domain.validation.InputValidation;
import org.example.domain.validation.InvalidBookId;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Book {
    public static final Price BOOK_BASE_PRICE_EURO_CENTS = Price.fromEuroCents(800).get();
    private final int partId;

    /**
     * @param partId represents the part of the series (must be between 1 and 5)
     */
    public static Either<InputValidation, Book> from(int partId) {
        if (partId < 1 || partId > 5) {
            return Either.left(new InvalidBookId(partId));
        }
        return Either.right(new Book(partId));
    }
}
