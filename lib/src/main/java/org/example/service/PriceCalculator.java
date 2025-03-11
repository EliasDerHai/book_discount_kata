package org.example.service;

import io.vavr.collection.*;
import lombok.NonNull;
import org.example.domain.Book;
import org.example.domain.Price;

public class PriceCalculator {
    // updated when new confirmed optimum found
    // memoization strategy to deal with combinatorial explosion;
    private HashMap<Array<Integer>, Price> knownOptima = HashMap.empty();

    public Price getDiscountedPrice(@NonNull List<Book> books) {
        return dfs_traverse(books, Price.fromEuroCents(0).get());
    }

    /**
     * should be invoked regularly (CRON or in otherwise) to avoid performance impact by overcaching
     */
    public void purgeCache() {
        knownOptima = HashMap.empty();
    }

    private Price dfs_traverse(List<Book> booksRemaining, Price aggregatedPrice) {
        if (booksRemaining.isEmpty()) return aggregatedPrice;

        var key = getKey(booksRemaining);

        if (knownOptima.containsKey(key)) {
            return knownOptima.get(key).get();
        }

        var occurrenceToBook = booksRemaining.groupBy(Book::getPartId);

        // if there is an equal amount of all parts of the series we know the optimum is the greedy approach
        var firstLength = occurrenceToBook.values().head().size();
        if (occurrenceToBook.values().forAll(l -> firstLength == l.size())) {
            return getPriceOfUniformShoppingBag(aggregatedPrice, occurrenceToBook, firstLength);
        }

        // a slice is between 1 and 5 books of different kinds
        var slice = occurrenceToBook.values().map(Traversable::head);

        var allPrices = slice.combinations() // because of the slicing the complexity of the combinations is limited to 2^5
                .reverse() // vavr's Combinations algorithm goes from empty set to full set, which is not great for runtime performance
                // - reverting the list helps to optimize the effect of memoization
                .filter(Traversable::nonEmpty)
                .map(set -> {
                    var aggregatedNewPrice = Book.BOOK_BASE_PRICE_EURO_CENTS
                            .multiply(set.size())
                            .applyDiscount(getDiscountMultiplier(set.size()))
                            .add(aggregatedPrice);
                    var nextRemaining = booksRemaining;
                    for (var setItem : set) {
                        nextRemaining = nextRemaining.removeFirst(remainingItem -> remainingItem == setItem);
                    }
                    return dfs_traverse(nextRemaining, aggregatedNewPrice);
                });

        var bestPrice = allPrices.min().get();
        knownOptima = knownOptima.put(key, bestPrice);
        return bestPrice;
    }

    private Price getPriceOfUniformShoppingBag(
            Price aggregatedPrice,
            Map<Integer, List<Book>> occurrenceToBook,
            int firstLength
    ) {
        return Book.BOOK_BASE_PRICE_EURO_CENTS
                .multiply(firstLength * occurrenceToBook.size())
                .applyDiscount(getDiscountMultiplier(occurrenceToBook.size()))
                .add(aggregatedPrice);
    }

    private float getDiscountMultiplier(int setSize) {
        return switch (setSize) {
            case 1 -> 1f;
            case 2 -> .95f;
            case 3 -> .9f;
            case 4 -> .8f;
            case 5 -> .75f;
            default -> throw new IllegalStateException("Unexpected setSize: %d".formatted(setSize));
        };
    }

    private Array<Integer> getKey(List<Book> books) {
        return books.groupBy(Book::getPartId)
                .mapValues(Traversable::size)
                .values()
                .sorted()
                .toArray();
    }
}
