import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class CombinationWithoutRepetitionTask {

    @ParameterizedTest(name = "{index} for {0} combinations without repetition are  is {1}")
    @MethodSource("withoutRepetitions")
    fun combinationWithoutRepetitionTest(input: List<Int>, expected: Set<List<Int>>) {
        assertThat(combinationWithoutRepetition(input), Matchers.`is`(expected))
    }

    @ParameterizedTest(name = "{index} for {0} combinations with repetition are  is {1}")
    @MethodSource("withRepetitions")
    fun combinationWithRepetitionTest(input: List<Int>, expected: Set<List<Int>>) {
        assertThat(combinationWithRepetition(input), Matchers.`is`(expected))
    }

    private fun <T> combinationWithoutRepetition(input: List<T>): Set<Pair<T, T>> {
        return input.flatMapIndexed { index, t1 ->
            input.subList(index + 1, input.size).map { t2 -> t1 to t2 }
        }.toSet()
    }


    private fun <T> combinationWithRepetition(input: List<T>): Set<Pair<T, T>> {
        return input.flatMap { t1 ->
            input.filter { it != t1 }.map { t2 -> t1 to t2 }
        }.toSet()
    }


    companion object {
        @JvmStatic
        fun withoutRepetitions(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    listOf(1, 2, 3,4),
                    setOf(
                        Pair(1, 2),
                        Pair(1, 3),
                        Pair(2, 3),
                    ),
                )
            )
        }

        @JvmStatic
        fun withRepetitions(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    listOf(1, 2, 3),
                    setOf(
                        Pair(1, 2),
                        Pair(1, 3),
                        Pair(2, 1),
                        Pair(2, 3),
                        Pair(3, 1),
                        Pair(3, 2),
                    ),
                )
            )
        }

    }

}
