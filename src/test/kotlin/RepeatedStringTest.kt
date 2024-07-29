import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.lang.Integer.reverse
import java.util.stream.Stream


class RepeatedStringTest {

    @ParameterizedTest(name = "{index} for  repeated {0} and n {1} number of 'a' is {2}")
    @MethodSource("provideStringsForIsBlank")
    fun test(str: String, n: Long, expected: Long) {
        assertThat(repeatedString(str, n), Matchers.`is`(expected))
    }


    fun repeatedString(s: String, n: Long): Long {
        val numberOfA = s.count { it == 'a' }

        val rest = s.take((n % s.length).toInt()).count { it == 'a' }

        val result = numberOfA * (n / s.length) + rest

        return result

    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "aba",
                    10,
                    7
                ),
                Arguments.of(
                    "bbbbb",
                    10000,
                    0
                ),
                Arguments.of(
                    "ab",
                    100,
                    50
                ),

                Arguments.of(
                    "ab",
                    99,
                    50
                ),

                Arguments.of(
                    "ab",
                    101,
                    51
                ),

                Arguments.of(
                    "abaccc",
                    3,
                    2
                ),

                Arguments.of(
                    "a",
                    1000000000000,
                    1000000000000
                )
            );
        }

    }

}
