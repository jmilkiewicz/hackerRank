import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class NonDivisibleSubsetTest {

    @ParameterizedTest(name = "{index} for rank {0}, scores {1} , result is {2}")
    @MethodSource("provideStringsForIsBlank")
    fun test(k: Int, s: Array<Int>, expected: Int) {
        assertThat(nonDivisibleSubset(k, s), Matchers.`is`(expected))
    }


    fun subsets(set: Set<Int>): Set<Set<Int>> {
        return if (set.size == 2) {
            setOf(set)
        } else {
            set.flatMap { elem ->

                val minus = set.minus(elem)
                subsets(minus) + setOf(set.minus(elem))

            }.toSet()
        }
    }





    fun nonDivisibleSubset(k: Int, s: Array<Int>): Int {

        val allNonDivisablePairs = combinations(s.toList(), k)

        fun go(sets: Set<Set<Int>>): Int {
            val r = if (sets.any { allPairsNotDivisibleBy(it, k) }) {
                sets.first().size
            } else {
                val x = sets.flatMap { s -> shrinkSet(s) }.toSet()
                go(x)
            }
            return r
        }

        return go(setOf(s.toSet()))

    }

    private fun shrinkSet(toSet: Set<Int>): Set<Set<Int>> = toSet.map { elem -> toSet.minus(elem) }.toSet()


    fun nonDivisibleSubset2(k: Int, s: Array<Int>): Int {
        fun go(inputSet: Set<Set<Int>>): Int {

            val input = inputSet.toList()

            val current = input.flatMapIndexed { index, elem ->
                input.subList(index + 1, input.size).map { elem2 -> elem2 + elem }
            }.filter { allPairsNotDivisibleBy(it, k) }.toSet()

            return if (current.isEmpty()) {
                input.maxBy { it.size }.size
            } else {
                go(current)
            }


        }


        val combinations = combinations(s.toList(), k)

        return go(combinations)

    }


    private fun allPairsNotDivisibleBy(set: Set<Int>, k: Int): Boolean {
        val l = set.toList()

        return !set.asSequence().flatMapIndexed { index, elem ->
            l.subList(index + 1, l.size).map { setOf(elem, it) }
        }.any { it.sum() % k == 0 }
    }


    private fun combinations(toSet: List<Int>, k: Int): Set<Set<Int>> {
        return toSet.flatMapIndexed { index, elem ->
            toSet.subList(index + 1, toSet.size).map { setOf(elem, it) }.filter {
                it.sum() % k != 0
            }
        }.toSet()
    }

//19,10, 12,24


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    4,
                    listOf(19, 10, 12, 24, 25, 22).toTypedArray(),
                    3,
                ),
//                Arguments.of(
//                    3,
//                    listOf(1, 7, 2, 4).toTypedArray(),
//                    3,
//                ),
                Arguments.of(
                    7,
                    listOf(278, 576, 496, 727, 410, 124, 338, 149, 209, 702, 282, 718, 771, 575, 436).toTypedArray(),
                    11,
                )
            );
        }

    }

}
