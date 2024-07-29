import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.math.abs


class FlatlandSpaceStationsTest {

    @ParameterizedTest(name = " for {0} cities , stations {1} , biggest distance is {2}")
    @MethodSource("provideStringsForIsBlank")
    fun test(n: Int, stations: Array<Int>, expected: Int) {
        assertThat(flatlandSpaceStations(n, stations), Matchers.`is`(expected))
    }


    fun flatlandSpaceStations(n: Int, c: Array<Int>): Int {
        return if (n == c.size)  0
        else {
            val sorted = c.sorted()
            val dist = sorted.windowed(2, 1).maxOfOrNull { l -> abs(l[1] - l[0]) }?.let { it / 2 }
            val result = listOfNotNull(sorted.first(), n - sorted.last() - 1, dist).max()
            result
        }


    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    6, arrayOf(0,1,2,4,3,5),
                    0
                ),
                Arguments.of(
                    3,
                    arrayOf(1),
                    1,
                ),
                Arguments.of(
                    5,
                    arrayOf(0,4),
                    2,
                ),

                Arguments.of(
                    8,
                    arrayOf(3),
                    4,
                ),

                Arguments.of(
                    8,
                    arrayOf(7),
                    7,
                ),
            );
        }

    }

}
