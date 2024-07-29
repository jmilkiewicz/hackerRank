import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class SquaresTest {

    @ParameterizedTest(name = "{index} from  {0} - {1}, we have {2} ")
    @MethodSource("provideStringsForIsBlank")
    fun test(from: Int, to:Int, expected: Int) {
        assertThat(squares(from, to), Matchers.`is`(expected))
    }

    private fun squares(from: Int, to: Int): Int {
        val start = Math.ceil(Math.sqrt(from.toDouble())).toInt()


        val till = Math.floor(Math.sqrt(to.toDouble())).toInt()

        return Math.max(till - start + 1, 0)
    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    24,49,
                    3
                ),
                Arguments.of(
                    3,9,
                    2
                ),
                Arguments.of(
                    17,24,
                    0
                ),
                Arguments.of(
                    17,63,
                    3
                ),
            );
        }

    }

}
