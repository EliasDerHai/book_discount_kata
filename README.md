## Book Discount Kata

## Run
To run all tests:
`./gradlew test -DperformanceTests=true`

To run unit-tests only:
`./gradlew test`
This Kata is also known as [Potter Book Kata](https://codingdojo.org/kata/Potter/).

## Thoughts
The challenge lies in the not guaranteed optimal solution of building greedy sets of books (3 books at 10% discount - 4 books at 20% discount).

Fully exploring all combinations can lead to combinatorial explosion. This can partly be mitigated by applying DFS and memoization. 
Other solution have come up with swapping non-optimal sets with optimal sets, but this approach is based upon the hard coded unchangeable discounts.

I instead used DFS, memoization, and a heuristic to tackle this issue. 

My heuristic is:
**If you have an equal amount of books you can distribute them evenly into sets and derive the cheapest price from that.**
This assumes, that the biggest discount will always be given for the greatest set and that a set of 5 is cheaper than for example a set of 2 and 3 books.

In contrast to most approaches to this challenge I tried to maximize the readability and clarity of my code. 
I intentionally omitted "potential performance gains" by using tricks like bit-masks, bit-shifting, int-arrays and or overly complex nested loops and mutation.
Through performance benchmarks (run tests with `performanceTests=true` environment-variable) I showcase, 
that a functional approach and boxed types don't necessarily have a negative performance impact 
and can actually perform better if other algorithmic optimizations are explored.

## Dependencies
This project is built with gradle, java17 and following dependencies:
 - [vavr](https://vavr.io/)
 - jUnit5
 - lombok

jUnit and Lombok are commonly known. 
Vavr is a functional library for Java with lots of helpful utilities to write clean and readable code.
It can be a bit intimidating though, if one is not quite familiar with the patterns of FP.
I am happy to explain the powerful tooling, that it gives java programmers and can assure, 
that this kata was coded by me rather than an AI, because LLMs tend to be terrible at writing vavr code :)

I'm looking forward to the review!

