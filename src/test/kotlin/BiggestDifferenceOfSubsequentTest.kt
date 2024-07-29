import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class BiggestDifferenceOfSubsequentTest {

    @ParameterizedTest(name = "{index} for {0} biggest is {1}")
    @MethodSource("provideStringsForIsBlank")
    fun biggestDifferenceOfSubsequentTest(input: List<Int>, expected: Int?) {
        assertThat(biggestDifferenceOfSubsequent(input), Matchers.`is`(expected))
    }


    private fun biggestDifferenceOfSubsequent(input: List<Int>): Int? {
        //val chunked = input.windowed(2, 1)

        val pair = input.zipWithNext().maxByOrNull { (p1, p2) -> Math.abs(p1 - p2) }
        return pair?.let { (p1, p2) -> Math.abs(p1 - p2) }
    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(listOf(1, 1, 1), 0),
                Arguments.of(listOf(1), null),
                Arguments.of(listOf(1, 2, 3), 1),
                Arguments.of(listOf(1, 5, 3), 4),
                Arguments.of(listOf(10, 2, 3), 8),
            );
        }
    }
}