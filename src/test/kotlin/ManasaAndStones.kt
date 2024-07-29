import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Stream


class ManasaAndStones {

    @ParameterizedTest(name = "{index} for n {0}, a {1} , b {2} result is {3}")
    @MethodSource("provideStringsForIsBlank")
    fun test(n: Int, a: Int, b: Int, expected: Array<Int>) {
        assertThat(stones(n, a, b), Matchers.`is`(expected))
    }


    fun stones(n: Int, a: Int, b: Int): Array<Int> {
        //fajne ale nie efektywne
        val stones = stones(0, n - 1, setOf(a, b))

        return stones.sorted().toTypedArray()

    }


    fun stones2(n: Int, a: Int, b: Int): Array<Int> {
        val result = (0..n - 1).map { x -> x * a + (n - 1 - x) * b }.toSet()
        return result.sorted().toTypedArray()

    }

    private fun stones(start: Int, n: Int, diffs: Set<Int>): Set<Int> {
        val xx = when (n) {
            0 -> setOf(start)
            else -> diffs.flatMap { diff -> stones(start + diff, n - 1, diffs) }.toSet()
        }

        return xx
    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    3, 1, 2,
                    arrayOf(
                        2, 3, 4
                    )
                ),

                Arguments.of(
                    4, 10, 100,
                    arrayOf(
                        30, 120, 210, 300
                    )
                ),

                Arguments.of(
                    6, 4, 7,
                    arrayOf(
                        20, 23, 26, 29, 32, 35
                    )
                ),

                Arguments.of(
                    7, 9, 11,
                    arrayOf(
                        54, 56, 58, 60, 62, 64, 66

                    )
                ),

                Arguments.of(
                    4, 8, 16,
                    arrayOf(
                        24, 32, 40, 48

                    )
                ),

                );
        }

    }

}
