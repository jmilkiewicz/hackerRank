import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class ChocolateFeastTest {

    @ParameterizedTest(name = "{index} for i have {0}$, chocolate is for {1}$ " +
            "and exchange is {2} wrapps for 1 chocolate, i ate is {3}")
    @MethodSource("provideStringsForIsBlank")
    fun jumpingOnClouds(money: Int, costOfBar: Int, exchangeRation: Int, expected: Int) {
        assertThat(chocolateFeast(money, costOfBar, exchangeRation), Matchers.`is`(expected))
    }

    fun chocolateFeast(money: Int, costOfBar: Int, exchangeRation: Int): Int {

        fun go(toEat: Int, wraps: Int, eaten: Int): Int {
            return if (toEat == 0) {
                eaten
            } else {
                val newWrapps = wraps + toEat
                val exchange = newWrapps / exchangeRation
                go(exchange, newWrapps % exchangeRation, eaten + exchange)
            }

        }

        val init = money / costOfBar

        return go(init, wraps = 0, eaten = init)

    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    10, 5, 5, 2
                ),

                Arguments.of(
                    15, 3, 5, 6
                ),
                Arguments.of(
                    12,4,4,3
                ),
                Arguments.of(
                    6, 2, 2, 5
                ),
                );
        }
    }

}
