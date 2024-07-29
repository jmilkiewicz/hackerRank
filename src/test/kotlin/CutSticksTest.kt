import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class CutSticksTest {

    @ParameterizedTest(name = "{index} for sticks {0} we have {1}")
    @MethodSource("provideStringsForIsBlank")
    fun test(sticks: Array<Int>, expected: Array<Int>) {
        assertThat(cutTheSticks(sticks), Matchers.`is`(expected))
    }

    fun cutTheSticks(arr: Array<Int>): Array<Int> {
        if (arr.isEmpty()) {
            return emptyArray()
        }

        if (arr.size == 1) {
            return arrayOf(1)
        }

        val min = arr.min()
        val rest = arr.filter { it - min > 0 }
        return arrayOf(arr.size) + cutTheSticks(rest.toTypedArray())
    }

    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    listOf<Int>().toTypedArray(),
                    listOf<Int>().toTypedArray(),
                ),
                Arguments.of(
                    listOf<Int>(3).toTypedArray(),
                    listOf<Int>(1).toTypedArray(),
                ),
                Arguments.of(
                    listOf<Int>(1, 10).toTypedArray(),
                    listOf<Int>(2, 1).toTypedArray(),
                ),
                Arguments.of(
                    listOf<Int>(1, 1).toTypedArray(),
                    listOf<Int>(2).toTypedArray(),
                ),
                Arguments.of(
                    listOf<Int>(1, 2, 3).toTypedArray(),
                    listOf<Int>(3, 2, 1).toTypedArray(),
                ),

                Arguments.of(
                    listOf<Int>(5, 4, 4, 2, 2, 8).toTypedArray(),
                    listOf<Int>(6, 4, 2, 1).toTypedArray(),
                ),

                );
        }

    }

}
