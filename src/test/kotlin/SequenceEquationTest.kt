import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class SequenceEquationTest {

    @ParameterizedTest(name = "{index} for input {0},we have {1}")
    @MethodSource("provideStringsForIsBlank")
    fun test(st: Array<Int>, expected: Array<Int>) {
        assertThat(permutationEquation(st), Matchers.`is`(expected))
    }


    fun permutationEquation(p: Array<Int>): Array<Int> {
        val sorted = p.withIndex().sortedBy { it.value }

        val result = sorted.map { v ->
            val value = v.index
            sorted[value].index + 1
        }
        return result.toTypedArray()


    }

    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    listOf(4, 3, 5, 1, 2).toTypedArray(),
                    arrayOf(1, 3, 5, 4, 2)
                ),
                Arguments.of(
                    listOf(5, 2, 1, 3, 4).toTypedArray(),
                    arrayOf(4, 2, 5, 1, 3)
                ),
            );
        }

    }

}
