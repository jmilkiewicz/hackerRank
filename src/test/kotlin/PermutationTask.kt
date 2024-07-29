import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class PermutationTask {

    @ParameterizedTest(name = "{index} for {0} permutations are  is {1}")
    @MethodSource("provideStringsForIsBlank")
    fun permutateTest(input: List<Int>, expected: Set<List<Int>>) {
        assertThat(permutate(input), Matchers.`is`(expected))
    }

    private fun permutate(input: List<Int>): Set<List<Int>> {
        if (input.size <= 1) {
            return setOf(input)
        }

        val set = input.toSet()

        return set.flatMap { e ->
            val subPermutations = permutate(
                set.minus(e).toList()
            )
            subPermutations.map { l -> listOf(e) + l }.toSet()
        }.toSet()
    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    listOf(1, 2),
                    setOf(listOf(1, 2), listOf(2, 1))
                ),
                Arguments.of(
                    listOf(1, 2, 3),
                    setOf(
                        listOf(1, 2, 3),
                        listOf(1, 3, 2),
                        listOf(2, 1, 3),
                        listOf(2, 3, 1),
                        listOf(3, 1, 2),
                        listOf(3, 2, 1)

                    ),
                )
            )
        }
    }

}
