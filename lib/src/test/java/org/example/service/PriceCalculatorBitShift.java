package org.example.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.example.domain.Book;
import org.example.domain.Price;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is merely a comparison the actual code is in {@link PriceCalculator}
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PriceCalculatorBitShift {
    private static final double BOOK_PRICE = 8.0;

    public static Price calculatePrice(List<Book> books) {
        int[] freq = new int[5];
        for (Book book : books) {
            freq[book.getPartId() - 1]++;
        }
        Map<String, Double> memo = new HashMap<>();
        return Price.fromEuros(calculatePrice(freq, memo)).get();
    }

    private static double calculatePrice(int[] freq, Map<String, Double> memo) {
        String key = Arrays.toString(freq);
        if (memo.containsKey(key)) {
            return memo.get(key);
        }
        boolean isEmpty = true;
        for (int count : freq) {
            if (count != 0) {
                isEmpty = false;
                break;
            }
        }
        if (isEmpty) {
            return 0.0;
        }

        double minPrice = Double.MAX_VALUE;
        int totalCombinations = 1 << 5;
        for (int subset = 1; subset < totalCombinations; subset++) {
            int[] newFreq = Arrays.copyOf(freq, freq.length);
            int distinctBooks = 0;
            boolean valid = true;
            for (int i = 0; i < 5; i++) {
                if ((subset & (1 << i)) != 0) {
                    if (newFreq[i] == 0) {
                        valid = false;
                        break;
                    }
                    newFreq[i]--;
                    distinctBooks++;
                }
            }
            if (!valid) {
                continue;
            }
            double setPrice = BOOK_PRICE * distinctBooks * discountFactor(distinctBooks);
            double totalPrice = setPrice + calculatePrice(newFreq, memo);
            minPrice = Math.min(minPrice, totalPrice);
        }
        memo.put(key, minPrice);
        return minPrice;
    }

    private static double discountFactor(int n) {
        return switch (n) {
            case 1 -> 1.0;   // No discount
            case 2 -> 0.95;  // 5% discount
            case 3 -> 0.90;  // 10% discount
            case 4 -> 0.80;  // 20% discount
            case 5 -> 0.75;  // 25% discount
            default -> 1.0;
        };
    }
}
