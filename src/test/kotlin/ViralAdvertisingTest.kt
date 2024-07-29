import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class ViralAdvertisingTest {

    @ParameterizedTest(name = "{index} for day {0}, we have  {1}")
    @MethodSource("provideStringsForIsBlank")
    fun test(n: Int, expected: Int) {
        assertThat(viralAdvertising(n), Matchers.`is`(expected))
    }


    data class Acc(val shared: Int = 5, val liked: Int = 2)

    fun viralAdvertising(n: Int): Int {
        val seq = generateSequence(Acc()) { acc ->
            val newShares = (acc.shared / 2) * 3
            Acc(liked = newShares / 2 + acc.liked, shared = newShares)
        }

        val (_, liked) = seq.drop(n-1).first()

        return liked
    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    3,
                    9,
                ),
                Arguments.of(
                    1,
                    2,
                ),
                Arguments.of(
                    5,
                    24
                ),
                Arguments.of(
                    50,
                    2068789129
                ),
            )
        }

    }

}
