import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.math.abs
import kotlin.math.max


class PickingNumbersTest {

    @ParameterizedTest(name = "{index} for {0} biggest is {1}")
    @MethodSource("provideStringsForIsBlank")
    fun pickingNumbers(input: Array<Int>, expected: Int) {
        assertThat(pickingNumbers(input), Matchers.`is`(expected))
    }


    data class Acc(val x: List<Int>, val bestResult: Int) {
        fun merge(v: Int): Acc {
            return if (x.isEmpty()) {
                Acc(listOf(v), bestResult)
            } else {
                val diff = abs(x.first() - v)
                if (diff <= 1) {
                    Acc(x + v, bestResult)
                } else {
                    val dropWhile = x.dropWhile { Math.abs(it - v) > 1 }
                    val best = max(x.size, bestResult)
                    Acc(dropWhile + v, best)
                }
            }
        }

    }

    private fun pickingNumbers(a: Array<Int>): Int {
        val sorted = a.sorted()

        val fold = sorted.fold(Acc(emptyList(), 0)) { acc, v ->
            acc.merge(v)
        }

        return Math.max(fold.x.size, fold.bestResult)


    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(listOf(4, 6, 5, 3, 3, 1).toTypedArray(), 3),
                Arguments.of(listOf(1, 2, 2, 3, 1, 2).toTypedArray(), 5),
            );
        }
    }

}
