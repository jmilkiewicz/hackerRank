import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class KangarooTest {
    @ParameterizedTest(
        name = "{index} kang1 starts at position {0} and jumps by {1}, kang2 starts at position {2} and jumps by {3}, will they meet: {4} ?",
    )
    @MethodSource("provideStringsForIsBlank")
    fun test(
        x1: Int,
        v1: Int,
        x2: Int,
        v2: Int,
        expected: Boolean,
    ) {
        assertThat(kangaroo(x1, v1, x2, v2), Matchers.`is`(expected))
    }

    fun kangaroo(
        x1: Int,
        v1: Int,
        x2: Int,
        v2: Int,
    ): Boolean =
        if (v1 == v2 && x1 != x2) {
            false
        } else {
            val startDiff = (x2 - x1).toDouble()
            val velocityDiff = (v1 - v2).toDouble()
            startDiff / velocityDiff >= 0 && startDiff.toInt() % velocityDiff.toInt() == 0
        }

    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    2,
                    1,
                    1,
                    2,
                    true,
                ),
                Arguments.of(
                    0,
                    3,
                    4,
                    2,
                    true,
                ),
                Arguments.of(
                    0,
                    2,
                    5,
                    3,
                    false,
                ),
                Arguments.of(
                    0,
                    4,
                    1,
                    2,
                    false,
                ),
            )
    }
}
