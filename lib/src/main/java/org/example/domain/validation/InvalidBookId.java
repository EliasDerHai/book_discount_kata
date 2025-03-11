package org.example.domain.validation;

public record InvalidBookId(long id) implements InputValidation {

    public String toString() {
        return "Could not construct Book due to invalid argument 'id' = %d - must be between 1 and 5".formatted(id);
    }
}
