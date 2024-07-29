import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class CircularArrayRotationTest {

    @ParameterizedTest(name = "{index} for input {0}, number of rotations {1} , indexes of {2} are {3}")
    @MethodSource("provideStringsForIsBlank")
    fun test(input: Array<Int>, rotations: Int, indexes: Array<Int>, expected: Array<Int>) {
        assertThat(circularArrayRotation(input, rotations, indexes), Matchers.`is`(expected))
    }


    fun circularArrayRotation(a: Array<Int>, k: Int, queries: Array<Int>): Array<Int> {
        val numberOfRotations = k % a.size
        val final = if (numberOfRotations == 0) {
            a
        } else {
            val prefix = a.takeLast(numberOfRotations).toTypedArray()
            val suffix = a.sliceArray(0.rangeTo(a.size - numberOfRotations -1))
            prefix + suffix
        }
        return queries.map { final[it] }.toTypedArray()
    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    listOf(1, 2, 3).toTypedArray(),
                    2,
                    listOf(0, 1, 2).toTypedArray(),
                    listOf(2, 3, 1).toTypedArray(),
                ),

                Arguments.of(
                    listOf(1, 2, 3).toTypedArray(),
                    3,
                    listOf(0, 1, 2).toTypedArray(),
                    listOf(1, 2, 3).toTypedArray(),
                ),

                Arguments.of(
                    listOf(3, 4, 5).toTypedArray(),
                    2,
                    listOf(1, 2).toTypedArray(),
                    listOf(5, 3).toTypedArray(),
                ),
                Arguments.of(
                    listOf(1, 2, 3, 4, 5).toTypedArray(),
                    7,
                    listOf(0, 1, 2, 3, 4).toTypedArray(),
                    listOf(4, 5, 1, 2, 3).toTypedArray(),
                ),


                )
        }

    }

}
