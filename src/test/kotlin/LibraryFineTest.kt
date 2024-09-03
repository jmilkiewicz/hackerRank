import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.util.stream.Stream
import kotlin.math.absoluteValue

class LibraryFineTest {
    @ParameterizedTest(name = "{index} book was returned on {0}.{1}.{2} but due data was {3}.{4}.{5}, fine is {6}")
    @MethodSource("provideStringsForIsBlank")
    fun test(
        d1: Int,
        m1: Int,
        y1: Int,
        d2: Int,
        m2: Int,
        y2: Int,
        expected: Int,
    ) {
        assertThat(libraryFine(d1, m1, y1, d2, m2, y2), Matchers.`is`(expected))
    }

    fun libraryFine(
        d1: Int,
        m1: Int,
        y1: Int,
        d2: Int,
        m2: Int,
        y2: Int,
    ): Int {
        val returnedData = LocalDate.of(y1, m1, d1)
        val dueDate = LocalDate.of(y2, m2, d2)

        return when {
            returnedData.isBefore(dueDate) || returnedData.isEqual(dueDate) -> 0
            y1 != y2 -> 10_000
            m1 != m2 -> 500 * (m1 - m2).absoluteValue
            else -> (d1 - d2).absoluteValue * 15
        }
    }

    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    9,
                    6,
                    2015,
                    6,
                    6,
                    2015,
                    45,
                ),
                Arguments.of(
                    1,
                    1,
                    2018,
                    31,
                    12,
                    2017,
                    10000,
                ),
                Arguments.of(
                    1,
                    1,
                    2018,
                    1,
                    1,
                    2017,
                    10000,
                ),
                Arguments.of(
                    14,
                    7,
                    2018,
                    5,
                    7,
                    2018,
                    135,
                ),
                Arguments.of(
                    14,
                    7,
                    2018,
                    14,
                    7,
                    2018,
                    0,
                ),
                Arguments.of(
                    10,
                    7,
                    2018,
                    14,
                    7,
                    2018,
                    0,
                ),
                Arguments.of(
                    1,
                    9,
                    2018,
                    31,
                    7,
                    2018,
                    1000,
                ),
            )
    }
}
