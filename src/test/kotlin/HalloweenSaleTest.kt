import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.math.max


class HalloweenSaleTest {

    @ParameterizedTest(name = "{index} for init price {0}, step {1} and min {2}. Buget of {3} allows {4} games")
    @MethodSource("provideStringsForIsBlank")
    fun test(nominalPrice: Int, discountStep: Int, minimum: Int, budget: Int, expected: Int) {
        assertThat(howManyGames(nominalPrice, discountStep, minimum, budget), Matchers.`is`(expected))
    }

    data class Acc(val price: Int, val budget: Int)

    fun howManyGames(nominalPrice: Int, discountStep: Int, minimum: Int, budget: Int): Int {
        val prices =
            generateSequence(Acc(nominalPrice, budget - nominalPrice)) { prev ->
                val newPrice = max(prev.price - discountStep, minimum)
                Acc(newPrice, prev.budget - newPrice)
            }.takeWhile { (_, budget) ->
                budget >= 0
            }

        return prices.count()


    }

    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    10, 1, 10, 10, 1
                ),
                Arguments.of(
                    10, 1, 8, 19, 2
                ),
                Arguments.of(
                    12, 1, 10, 10, 0
                ),
                Arguments.of(
                    10, 1, 8, 28, 3
                ),

                Arguments.of(
                    7, 2, 7, 40, 5
                ),
            );
        }

    }

}
