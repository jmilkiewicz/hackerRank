import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class CountingValleysTest {

    @ParameterizedTest(name = "{index} for {0} biggest is {1}")
    @MethodSource("provideStringsForIsBlank")
    fun countingValleys(trace: String, expected: Int) {
        assertThat(countingValleys(trace), Matchers.`is`(expected))
    }


    data class Trace(val level: Int = 0, val numberOfValleys: Int = 0) {
        fun mergeMove(move: Char): Trace {
            val levelIncrement = when {
                move == 'U' -> 1
                else -> -1
            }
            val valleyIncrement = if (level == 0 && levelIncrement == -1) {
                +1
            } else {
                0
            }
            return Trace(level + levelIncrement, numberOfValleys + valleyIncrement)
        }

    }

    private fun countingValleys(trace: String): Int {
        val fold = trace.fold(Trace()) { acc, move -> acc.mergeMove(move) }
        return fold.numberOfValleys


    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("UDDDUDUU", 1),
                Arguments.of("DDUUUUDD", 1),
            );
        }
    }

}
