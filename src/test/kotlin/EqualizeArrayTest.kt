import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class EqualizeArrayTest {

    @ParameterizedTest(name = "{index} for  {0} , moves : {1} ")
    @MethodSource("provideStringsForIsBlank")
    fun test(array: Array<Int>, expected: Int) {
        assertThat(equalizeArray(array), Matchers.`is`(expected))
    }


    fun equalizeArray(arr: Array<Int>): Int {
        val map = arr.groupingBy { it }.eachCount()
        val (key, value) = map.maxBy { entry -> entry.value }
        val minus = map.minus(key)
        return minus.values.sum()
    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    listOf(3, 3, 2, 1, 3).toTypedArray(),
                    2
                ),
                Arguments.of(
                    listOf(1, 2, 2, 3).toTypedArray(),
                    2
                )
            );
        }

    }

}
