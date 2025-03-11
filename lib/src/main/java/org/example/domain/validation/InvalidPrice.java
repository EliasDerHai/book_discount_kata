package org.example.domain.validation;

public record InvalidPrice(float price) implements InputValidation {

    public String toString() {
        return "Could not construct Price due to invalid argument 'price' = %f - must be positive".formatted(price);
    }
}
