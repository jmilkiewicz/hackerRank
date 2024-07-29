import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.lang.Integer.reverse
import java.util.stream.Stream


class BeautifulDaysTest {

    @ParameterizedTest(name = "{index} for  {0} .. {1} and k {2} result is {3}")
    @MethodSource("provideStringsForIsBlank")
    fun test(i: Int, j: Int, k: Int, expected: Int) {
        assertThat(beautifulDays(i,j,k), Matchers.`is`(expected))
    }


    fun beautifulDays(i: Int, j: Int, k: Int): Int {
        return (i..j).count { (it - reverseIt(it)) % k == 0 }
    }

    private fun reverseIt(it: Int): Int {
        return it.toString().reversed().toInt()
    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    20,
                    23,
                    6,
                    2
                ),
            );
        }

    }

}
