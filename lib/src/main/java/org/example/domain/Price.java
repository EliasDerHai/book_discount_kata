package org.example.domain;

import io.vavr.control.Either;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.domain.validation.InputValidation;
import org.example.domain.validation.InvalidPrice;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Price implements Comparable<Price> {
    private final long priceInEuroCents;

    public static Either<InputValidation, Price> fromEuroCents(long priceInEuroCents) {
        if (priceInEuroCents < 0) {
            return Either.left(new InvalidPrice(priceInEuroCents));
        }
        return Either.right(new Price(priceInEuroCents));
    }

    public static Either<InputValidation, Price> fromEuros(double priceInEuros) {
        if (priceInEuros < 0) {
            return Either.left(new InvalidPrice((float) priceInEuros));
        }
        var priceInEuroCents = Math.round(priceInEuros * 100);
        return Either.right(new Price(priceInEuroCents));
    }

    public static Either<InputValidation, Price> fromEuros(float priceInEuros) {
        if (priceInEuros < 0) {
            return Either.left(new InvalidPrice(priceInEuros));
        }
        var priceInEuroCents = Math.round(priceInEuros * 100);
        return Either.right(new Price(priceInEuroCents));
    }

    public Price add(Price other) {
        return Price.fromEuroCents(this.priceInEuroCents + other.getPriceInEuroCents()).get();
    }

    public Price addEuroCents(long euroCents) {
        return Price.fromEuroCents(this.priceInEuroCents + euroCents).get();
    }

    public Price applyDiscount(float discountMultiplier) {
        float newCents = this.priceInEuroCents * discountMultiplier / 100;
        return Price.fromEuros(newCents).get();
    }

    @Override
    public int compareTo(Price otherPrice) {
        return Long.compare(this.priceInEuroCents, otherPrice.priceInEuroCents);
    }

    public Price multiply(int multiplier) {
        return Price.fromEuroCents(this.priceInEuroCents * multiplier).get();
    }
}
