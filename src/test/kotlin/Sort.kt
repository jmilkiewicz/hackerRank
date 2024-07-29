import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream


class Sort {

    @ParameterizedTest(name = "{index} for input {0} sorted is {1}")
    @MethodSource("provideStringsForIsBlank")
    fun <T : Comparable<T>> quickSort(input: List<T>, expected: List<T>) {
        assertThat(quickSort(input), `is`(expected))
    }

    @ParameterizedTest(name = "{index} for input {0} sorted is {1}")
    @MethodSource("provideStringsForIsBlank")
    fun <T : Comparable<T>> mergeSort(input: List<T>, expected: List<T>) {
        assertThat(mergeSort(input), `is`(expected))
    }

    private fun <T : Comparable<T>> quickSort(input: List<T>): List<T> {
        return if (input.size <= 1) {
            input
        } else {
            val pivot = input.first()
            val (smaller, bigger) = input.drop(1).partition { it.compareTo(pivot) < 0 }
            quickSort(smaller) + pivot + quickSort(bigger)
        }
    }


    private fun <T : Comparable<T>> mergeSort(input: List<T>): List<T> {
        return when (input.size) {
            0 -> input
            1 -> input
            2 -> {
                if (input[0].compareTo(input[1]) < 0) {
                    input
                } else {
                    input.reversed()
                }
            }
            else -> {
                val middle = input.size / 2
                val left = mergeSort(input.subList(0, middle))
                val right = mergeSort(input.subList(middle, input.size))
                merge2(left, right)
            }

        }
    }

    private fun <T : Comparable<T>> merge2(left: List<T>, right: List<T>): List<T> {
        fun go(left: List<T>, right: List<T>, result: List<T>): List<T> {
            return when {
                left.isEmpty() -> result + right
                right.isEmpty() -> result + left
                else -> if (left.first().compareTo(right.first()) <= 0) {
                    go(left.drop(1), right, result + left.first())
                } else {
                    go(left, right.drop(1), result + right.first())
                }
            }
        }
        return go(left, right, emptyList())
    }


    companion object {
        @JvmStatic
        fun provideStringsForIsBlank(): Stream<Arguments> {
            val someInput = listOf(1, 2, 5, 7, 3, 98, 1)
            return Stream.of(
                Arguments.of(listOf(2, 1), listOf(1, 2)),
                Arguments.of(listOf(1, 1, 2, 1), listOf(1, 1, 1, 2)),
                Arguments.of(someInput.shuffled(), someInput.sorted()),
            );
        }
    }

}
