import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class FindDigitsTest {

    @ParameterizedTest(name = "{index} for rank {0}, scores {1} , result is {2}")
    @MethodSource("provideStringsForIsBlank")
    fun test(digit: Int, expected: Int) {
        assertThat(findDigits(digit), Matchers.`is`(expected))
    }


    fun findDigits(n: Int): Int {
        val x = n.toString().toCharArray().count {
            if (it == '0') {
                false
            } else {
                n % it.digitToInt() == 0
            }
        }

        return x

    }

    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    1012,
                    3,
                ),
                Arguments.of(
                    12,
                    2
                ),
                Arguments.of(
                    124,
                    3
                )
            );
        }

    }

}
