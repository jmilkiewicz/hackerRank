import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class MinimumDistanceTest {

    @ParameterizedTest(name = "{index} for s {0} ,minimum distance is {1}")
    @MethodSource("provideStringsForIsBlank")
    fun test(input: Array<Int>, output: Int) {
        assertThat(minimumDistances(input), Matchers.`is`(output))
    }

    fun minimumDistances(a: Array<Int>): Int {

        val groupBy = a.withIndex().groupBy({ ss -> ss.value }, { i -> i.index })

        return groupBy.values.asSequence().filter { it.size > 1 }
            .map { indexes -> indexes.windowed(size = 2).minOfOrNull { Math.abs(it[0] - it[1]) }!! }.minOrNull() ?: -1

    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(

                Arguments.of(
                    arrayOf(7, 1, 3, 4, 1, 7),
                    3,
                ),

                Arguments.of(
                    arrayOf(1, 2, 3, 4, 10),
                    -1,
                ),

                );
        }

    }

}
