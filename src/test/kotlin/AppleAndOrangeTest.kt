import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class AppleAndOrangeTest {
    @ParameterizedTest(
        name = "{index} house on {0}-{1}, apple tree {2} , orange tree at {3}, falls of apples {4}, falls of oranges {5}, on house {6}",
    )
    @MethodSource("provideStringsForIsBlank")
    fun test(
        s: Int,
        t: Int,
        a: Int,
        b: Int,
        apples: Array<Int>,
        oranges: Array<Int>,
        expected: Array<Int>,
    ) {
        assertThat(countFruit(s, t, a, b, apples, oranges), Matchers.`is`(expected))
    }

    fun countFruit(
        s: Int,
        t: Int,
        a: Int,
        b: Int,
        apples: Array<Int>,
        oranges: Array<Int>,
    ): Array<Int> {
        val countOfApples = apples.count { a + it >= s && a + it <= t }
        val countOrOranges = oranges.count { b + it >= s && b + it <= t }
        return arrayOf(countOfApples, countOrOranges)
    }

    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    7,
                    10,
                    4,
                    12,
                    arrayOf(2, 3, -4),
                    arrayOf(3, -2, -4),
                    arrayOf(1, 2),
                ),
                Arguments.of(
                    7,
                    11,
                    5,
                    15,
                    arrayOf(-2, 2, 1),
                    arrayOf(5, -6),
                    arrayOf(1, 1),
                ),
            )
    }
}
